package com.castlebravostudios.theguide.markdown.parser

import org.scalatest.FlatSpec
import com.castlebravostudios.theguide.markdown._

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

  it should "convert a textline followed by a header rule to a header" in {
    assert( combineLines( Seq( TextLine("Lorem ipsum"), HeaderRule(2) ) ) ===
      Right( Seq( Header( 2, "Lorem ipsum") ) ) )
  }

  it should "return a failure on a header rule with no preceeding textline" in {
    assert( combineLines( Seq( HeaderRule( 1 ) ) ).isLeft )
  }

  it should "return headers unchanged" in {
    assert( combineLines( Seq( Header( 1, "Lorem ipsum" ) ) ) ==
      Right( Seq( Header( 1, "Lorem ipsum" ) ) ) )
  }
}