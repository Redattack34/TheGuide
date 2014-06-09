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
import com.castlebravostudios.theguide.markdown.{Header => SpanHeader}
import com.castlebravostudios.theguide.markdown.{Paragraph => SpanParagraph}
import com.castlebravostudios.theguide.markdown.TextSpan
import com.castlebravostudios.theguide.markdown.LinkSpan
import net.minecraft.util.ResourceLocation

class SpanParserSpec extends FlatSpec {

  import SpanParser.parseSpans

  "SpanParser" should "return a header when given a header" in {
    assert( parseSpans( Seq( Header( 1, "Lorem Ipsum" ) ) ) ==
      Right( Seq( SpanHeader( 1, TextSpan( "Lorem Ipsum" ) ) ) ) )
  }

  it should "return a paragraph with a textspan when given a paragraph with no links" in {
    assert( parseSpans( Seq( Paragraph( "Lorem ipsum dolor sit amet" ) ) ) ==
      Right( Seq( SpanParagraph( Seq( TextSpan( "Lorem ipsum dolor sit amet" ) ) ) ) ) )
  }

  it should "return a link span when given a paragraph consisting of a link" in {
    assert( parseSpans( Seq( Paragraph( "[this is a link](theguide:testResourceLocation)" ) ) ) ==
      Right( Seq( SpanParagraph( Seq( LinkSpan( "this is a link", new ResourceLocation( "theguide:testResourceLocation" ) ) ) ) ) ) )
  }

  it should "return paragraphs and links as they are found" in {
    assert( parseSpans( Seq( Paragraph( "Lorem ipsum dolor sit amet, " +
        "[consectetur adipiscing elit.](theguide:testResourceLocation) Ut " +
        "et ultrices quam. ") ) ) ==
          Right( Seq( SpanParagraph( Seq(
              TextSpan( "Lorem ipsum dolor sit amet, " ),
              LinkSpan( "consectetur adipiscing elit.", new ResourceLocation( "theguide:testResourceLocation" ) ),
              TextSpan( " Ut et ultrices quam. " ) ) ) ) ) )
  }
}