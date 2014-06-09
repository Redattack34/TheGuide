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
    assert( document( Seq( paragraph( 10 ) ) ).size === 100 )
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