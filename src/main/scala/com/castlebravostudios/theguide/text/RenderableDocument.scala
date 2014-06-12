/*
 * Copyright (c) 2014, Brook 'redattack34' Heisler
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the ModularRayguns team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.castlebravostudios.theguide.text

import com.castlebravostudios.theguide.markdown.MarkdownBlock
import com.castlebravostudios.theguide.markdown.Paragraph
import com.castlebravostudios.theguide.markdown.Header
import com.castlebravostudios.theguide.markdown.TextSpan
import com.castlebravostudios.theguide.markdown.LinkSpan
import scala.collection.immutable.SortedMap
import net.minecraft.client.gui.FontRenderer
import scala.collection.mutable
import com.google.common.annotations.VisibleForTesting
import com.castlebravostudios.theguide.gui.TheGuideGui

class RenderableDocument( renderables : SortedMap[Int, RenderableElement], val size : Int ) {

  def render( x : Int, y : Int, width : Int, height : Int, scroll : Int, renderer: FontRenderer ) : Unit = {
    val start = renderables.to( scroll - 20 ).lastOption.map( _._1 ).getOrElse( renderables.firstKey )
    val end = renderables.from( scroll + height ).headOption.map( _._1 ).getOrElse( renderables.lastKey )
    renderables.range( start, end ).foreach { case (offset, renderable) =>
      renderable.render(x, y + offset - scroll, renderer)
    }
  }

  @VisibleForTesting
  private[text] val elements : Seq[RenderableElement] = renderables.values.toList

  def clicked( x : Int, y : Int, gui : TheGuideGui ) : Unit = {
    val key = renderables.to( y ).lastOption.map( _._1 ).getOrElse( renderables.firstKey )
    val line = renderables( key )
    line.clicked( x, y, gui )
  }
}

object RenderableDocument {
  def apply( blocks : Seq[MarkdownBlock], width : Int, calc : TextSizeCalculator ) : RenderableDocument = {
    val renderables = blocks.map {
      case p : Paragraph => wrapParagraph( p, width, calc )
      case Header( level, TextSpan( text ) ) => Seq( RenderableHeader( text, level ) )
    }
    val withBlankLines = insertBlanks( renderables ).flatten
    val offsets = withBlankLines.scanLeft(0){
      case ( offset, renderable ) => offset + renderable.height( calc )
    }
    val renderableMap = SortedMap( offsets.zip(withBlankLines) :_* )
    val size = renderableMap.last._1 + renderableMap.last._2.height( calc )
    return new RenderableDocument( renderableMap, size )
  }

  private def insertBlanks( items : Seq[Seq[RenderableElement]] ) : Seq[Seq[RenderableElement]] = items match {
    case Seq() => Seq()
    case block +: Seq() => Seq( block )
    case block +: rest => block +: Seq( BlankLine ) +: insertBlanks( rest )
  }

  private def wrapParagraph( p: Paragraph, width : Int, calc : TextSizeCalculator ) : Seq[RenderableElement] = {
    val wrapper = new WordWrapper( calc, width )
    p.text.foreach {
      case TextSpan(str) => wrapper.appendString( str )
      case LinkSpan( text, link ) => {
        wrapper.startLink( link )
        wrapper.appendString(text)
        wrapper.endLink()
      }
    }
    wrapper.build
  }
}