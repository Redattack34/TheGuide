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