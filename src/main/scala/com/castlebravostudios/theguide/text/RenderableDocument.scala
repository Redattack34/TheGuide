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