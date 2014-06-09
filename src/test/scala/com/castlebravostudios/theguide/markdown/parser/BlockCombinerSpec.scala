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

class BlockCombinerSpec extends FlatSpec {
  private def combineLines( lines : Seq[MarkdownLine]) = BlockCombiner.combineLines(lines)

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