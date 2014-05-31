package com.castlebravostudios.theguide.text

import org.scalatest.FlatSpec

import com.castlebravostudios.theguide.markdown.Header
import com.castlebravostudios.theguide.markdown.MarkdownBlock
import com.castlebravostudios.theguide.markdown.Paragraph
import com.castlebravostudios.theguide.markdown.TextSpan

class RenderableDocumentSpec extends FlatSpec {
  val calc = new TestTextSizeCalculator
  val width = 100

  private def document( contents : Seq[MarkdownBlock] ) : RenderableDocument =
    RenderableDocument( contents, width, calc )

  "RenderableDocument" should "wrap given blocks" in {
    assert( document( Seq( paragraph( 10 ) ) ).elements === wrappedLines( 10 ) )
  }

  it should "accept headers" in {
    assert( document( Seq( Header( 1, TextSpan( "Test" ) ) ) ).elements ===
      Seq( RenderableHeader( "Test", 1 ) ) )
  }

  it should "insert a BlankLine between elements" in {
    val header = Header( 1, TextSpan( "Test" ) )
    val renderable = RenderableHeader( "Test", 1 )
    assert( document( Seq.fill( 3 )(header) ).elements ===
      Seq( renderable, BlankLine, renderable, BlankLine, renderable ) )
  }

  it should "calculate document size correctly" in {
    assert( document( Seq( paragraph( 10 ) ) ).documentHeight === 100 )
  }

  private def wrappedLines( count : Int ) : Seq[TextLine] = {
    val word = "x" * ( width - 10 )
    val line = TextLine( word, Set() )
    Seq.fill(count)(line)
  }

  private def paragraph( lines : Int ) : Paragraph = {
    val word = "x" * ( width - 10 )
    val allWords = Seq.fill( lines )( word )
    val text = allWords.mkString(" ")
    return Paragraph( Seq( TextSpan( text ) ) )
  }

}