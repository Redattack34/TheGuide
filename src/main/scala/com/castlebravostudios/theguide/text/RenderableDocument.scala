package com.castlebravostudios.theguide.text

import com.castlebravostudios.theguide.markdown.MarkdownBlock
import com.castlebravostudios.theguide.markdown.Paragraph
import com.castlebravostudios.theguide.markdown.Header
import com.castlebravostudios.theguide.markdown.TextSpan
import com.castlebravostudios.theguide.markdown.LinkSpan
import scala.collection.immutable.SortedMap
import net.minecraft.client.gui.FontRenderer
import scala.collection.mutable

class RenderableDocument( renderables : Seq[RenderableElement] ) {

  def render( x : Int, y : Int, width : Int, height : Int, scroll : Int, renderer: FontRenderer ) : Unit = {
    var offset : Int = 0
    renderables.foreach { r =>
      val newOffset = offset - scroll
      val lineHeight = r.height(renderer)
      if ( newOffset + lineHeight * 2 > 0 && newOffset < height ) {
        r.render(x, y + newOffset, renderer)
      }
      offset += lineHeight
    }
  }
}

object RenderableDocument {
  def apply( blocks : Seq[MarkdownBlock], width : Int, calc : TextSizeCalculator ) : RenderableDocument = {
    val renderables = blocks.flatMap {
      case p : Paragraph => wrapParagraph( p, width, calc )
      case Header( level, TextSpan( text ) ) => Seq( RenderableHeader( text, level ) )
    }
    return new RenderableDocument( renderables )
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
    wrapper.build ++ Seq( BlankLine )
  }
}