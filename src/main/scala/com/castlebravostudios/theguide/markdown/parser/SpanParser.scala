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

import com.castlebravostudios.theguide.markdown.{ MarkdownBlock => SpanBlock }
import com.castlebravostudios.theguide.markdown.{ Header => SpanHeader }
import com.castlebravostudios.theguide.markdown.{ Paragraph => SpanParagraph }
import com.castlebravostudios.theguide.markdown.TextSpan
import scala.util.parsing.combinator.RegexParsers
import com.castlebravostudios.theguide.markdown.MarkdownSpan
import scala.util.parsing.combinator.RegexParsers
import net.minecraft.util.ResourceLocation
import com.castlebravostudios.theguide.markdown.LinkSpan
import scala.collection.mutable.ListBuffer

private object SpanParser extends RegexParsers {

  private val text : Parser[String] = """[^\[\]]+""".r

  private val resourceLocation : Parser[ResourceLocation] =
    "[0-9a-zA-Z]*".r ~ ":" ~ """[\.0-9a-zA-Z\\]*""".r ^^ {
    case domain ~ ":" ~ path => new ResourceLocation( domain, path )
  }

  private val textSpan : Parser[TextSpan] = text ^^ {
    case str : String => TextSpan( str )
  }

  private val linkSpan : Parser[LinkSpan] =
    "[" ~ text ~ "]" ~ "(" ~ resourceLocation ~ ")" ^^ {
    case "[" ~ text ~ "]" ~ "(" ~ location ~ ")" => LinkSpan( text, location )
  }

  private val span : Parser[MarkdownSpan] = linkSpan | textSpan

  private val spans : Parser[Seq[MarkdownSpan]] = span*

  private def doParse[T]( parser : Parser[T], text : String ) : Either[String, T] =
    parseAll( parser, text ) match {
      case Success( result, _ ) => Right( result )
      case Failure( msg, _ ) => Left( msg )
      case Error( msg, _ ) => Left( msg )
    }

  override def skipWhitespace : Boolean = false

  private def parseSpansInBlock( block : MarkdownBlock ) : Either[String, SpanBlock] = block match {
    case Header( level, text ) => doParse( textSpan, text ).right
        .map( textSpan => SpanHeader( level, textSpan ) )
    case Paragraph( text ) => doParse( spans, text ).right
      .map( spans => SpanParagraph( spans ) )
  }

  def parseSpans( blocks: Seq[MarkdownBlock] ) : Either[String, Seq[SpanBlock]] = {
    val buffer = ListBuffer[SpanBlock]( )
    val iter = blocks.iterator
    while ( iter.hasNext ) {
      val parsed = parseSpansInBlock( iter.next() )
      if ( parsed.isLeft ) return Left( parsed.left.get )
      buffer += parsed.right.get
    }
    return Right( buffer.toSeq )
  }
}