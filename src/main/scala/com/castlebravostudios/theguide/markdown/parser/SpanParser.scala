package com.castlebravostudios.theguide.markdown.parser

import com.castlebravostudios.theguide.markdown.{ MarkdownBlock => SpanBlock }
import com.castlebravostudios.theguide.markdown.{ Header => SpanHeader }
import com.castlebravostudios.theguide.markdown.{ Paragraph => SpanParagraph }
import com.castlebravostudios.theguide.markdown.TextSpan
import scala.util.parsing.combinator.RegexParsers
import com.castlebravostudios.theguide.markdown.MarkdownSpan
import scala.util.parsing.combinator._
import net.minecraft.util.ResourceLocation
import com.castlebravostudios.theguide.markdown.LinkSpan
import scala.collection.mutable.ListBuffer

private object SpanParser extends RegexParsers {

  private val text : Parser[String] = """[^\[\]]+""".r

  private val resourceLocation : Parser[ResourceLocation] =
    "[0-9a-zA-Z]*".r ~ ":" ~ """[0-9a-zA-Z\\]*""".r ^^ {
    case domain ~ ":" ~ path => new ResourceLocation( domain, path )
  }

  private val textSpan : Parser[TextSpan] = text ^^ {
    case str => TextSpan( str )
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

  override def skipWhitespace = false

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