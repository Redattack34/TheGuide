package com.castlebravostudios.theguide.markdown.parser

import org.scalatest.FlatSpec
import com.castlebravostudios.theguide.markdown.EmptyLine
import com.castlebravostudios.theguide.markdown.MarkdownLine
import com.castlebravostudios.theguide.markdown.TextLine

class LineParserSpec extends FlatSpec {

  def parseLines( input : Seq[String] ) : Either[String, Seq[MarkdownLine]] =
    LineParser.parseLines( input )

  "LineParser" should "return an EmptyLine for the empty string" in {
    assert( parseLines( Seq( "" ) ) == Right( Seq( EmptyLine ) ) )
  }

  it should "return multiple EmptyLines for multiple empty strings" in {
    assert( parseLines( Seq( "", "" ) ) == Right( Seq( EmptyLine, EmptyLine ) ) )
  }

  it should "return an EmptyLine for strings that have only whitespace" in {
    assert( parseLines( Seq( " ", "\t", " \t " ) ) == Right( Seq( EmptyLine, EmptyLine, EmptyLine ) ) )
  }

  it should "return a TextLine for strings that are not whitespace-only" in {
    assert( parseLines( Seq( "Lorem Ipsum" ) ) == Right( Seq( TextLine( "Lorem Ipsum" ) ) ) )
  }

  it should "trim the text of TextLines" in {
    assert( parseLines( Seq( "    Lorem Ipsum\t" ) ) == Right( Seq( TextLine( "Lorem Ipsum" ) ) ) )
  }

  it should "return a mixture of TextLines and EmptyLines when given a mix of strings" in {
    assert(
        parseLines( Seq( " ", "\t", "Lorem Ipsum", " \t \t\t\t   ", "dolor sit amet" ) ) ==
          Right( Seq( EmptyLine, EmptyLine, TextLine( "Lorem Ipsum" ), EmptyLine, TextLine( "dolor sit amet" ) ) ) )
  }
}