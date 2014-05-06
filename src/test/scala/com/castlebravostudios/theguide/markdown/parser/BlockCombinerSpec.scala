package com.castlebravostudios.theguide.markdown.parser

import org.scalatest.FlatSpec
import com.castlebravostudios.theguide.markdown.EmptyLine
import com.castlebravostudios.theguide.markdown.MarkdownBlock
import com.castlebravostudios.theguide.markdown.TextLine
import com.castlebravostudios.theguide.markdown.Paragraph

class BlockCombinerSpec extends FlatSpec {

  import BlockCombiner.combineLines

  "BlockCombiner" should "return an empty sequence when given only an empty line" in {
    assert( combineLines( Seq( EmptyLine ) ) == Right( Seq[MarkdownBlock]() ) )
  }

  it should "return a paragraph when given a text line followed by an empty line" in {
    assert( combineLines( Seq( TextLine( "Lorem Ipsum" ), EmptyLine ) ) ==
      Right( Seq( Paragraph( "Lorem Ipsum" ) ) ) )
  }

  it should "return a paragraph when given a text line at the end of input" in {
    assert( combineLines( Seq( TextLine( "Lorem Ipsum" ) ) ) ==
      Right( Seq( Paragraph( "Lorem Ipsum" ) ) ) )
  }

  it should "combine all consecutive text lines into one paragraph" in {
    val text = Seq( "Lorem ipsum", "dolor sit amet,", "adipiscing elit." )
    val lines = text.map( str => TextLine(str) )
    val expectedText = text.mkString(" ")
    assert( combineLines( lines ) == Right( Seq( Paragraph( expectedText ) ) ) )
  }

  it should "combine all paragraphs in the input" in {
    val text = List( "Lorem ipsum", "dolor sit amet,", "adipiscing elit." )
    val textLines = text.map( str => TextLine(str) )
    val lines = textLines ::: EmptyLine :: textLines ::: EmptyLine :: textLines
    val expectedText = text.mkString(" ")
    assert( combineLines( lines ) ==
      Right( Seq.fill(3)( Paragraph( expectedText ) ) ) )
  }
}