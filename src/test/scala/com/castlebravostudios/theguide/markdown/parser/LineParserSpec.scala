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

package com.castlebravostudios.theguide.markdown.parser

import org.scalatest.FlatSpec

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