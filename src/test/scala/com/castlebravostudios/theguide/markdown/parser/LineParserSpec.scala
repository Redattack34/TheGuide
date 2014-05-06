package com.castlebravostudios.theguide.markdown.parser

import org.scalatest.FlatSpec
import com.castlebravostudios.theguide.markdown.EmptyLine
import com.castlebravostudios.theguide.markdown.MarkdownLine
import com.castlebravostudios.theguide.markdown.TextLine
import com.castlebravostudios.theguide.markdown.HeaderRule
import com.castlebravostudios.theguide.markdown.Header

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

  it should "return a level 1 header rule for any number of equals" in {
    assert( parseLines( Seq("===", "======") ) == Right( Seq( HeaderRule( 1 ), HeaderRule( 1 ) ) ) )
  }

  it should "return a level 2 header rule for any number of dashes" in {
    assert( parseLines( Seq("---", "------") ) == Right( Seq( HeaderRule( 2 ), HeaderRule( 2 ) ) ) )
  }

  it should "return a text line rather than a header line if the line contains any other character" in {
    assert( parseLines( Seq("---Test", "===Test") ) ==
      Right( Seq( TextLine( "---Test" ), TextLine( "===Test" ) ) ) )
  }

  it should "return a header of level 1 if the line begins with one hash" in {
    assert( parseLines( Seq( "# test" ) ) == Right( Seq( Header( 1, "test" ) ) ) )
  }

  it should "return a header of level n if the line begins with n hashes" in {
    assert( parseLines( Seq( "## test", "#### header" ) ) ==
      Right( Seq( Header( 2, "test" ), Header( 4, "header" ) ) ) )
  }

  it should "return a textline if the line begins with more than 6 hashes" in {
    assert( parseLines( Seq( "####### test" ) ) == Right( Seq( TextLine( "####### test" ) ) ) )
  }

  it should "trim hashes off the end of a header line" in {
    assert( parseLines( Seq( "# test ####" ) ) == Right( Seq( Header( 1, "test" ) ) ) )
  }
}