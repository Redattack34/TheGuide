package com.castlebravostudios.theguide.markdown.parser

import java.nio.file.Paths
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import java.io.Reader
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import com.castlebravostudios.theguide.markdown.MarkdownLine
import com.castlebravostudios.theguide.markdown.EmptyLine
import com.castlebravostudios.theguide.markdown.TextLine
import scala.collection.mutable.ListBuffer
import com.castlebravostudios.theguide.markdown.HeaderRule
import com.castlebravostudios.theguide.markdown.Header


object LineParser extends RegexParsers {

  private val rest = new Parser[String] {
    def apply( in : Input ) : ParseResult[String] = in.first match {
      case x if in.atEnd => Success( "", in.rest )
      case x => apply( in.rest ).map( x + _ )
    }
  }

  private val emptyLine : Parser[MarkdownLine] = "[ \t]*$".r ^^^ EmptyLine

  private val textLine : Parser[MarkdownLine] = rest ^^ ( text => TextLine( text.trim ) )

  private val header1Rule : Parser[MarkdownLine] = "=+$".r ^^^ HeaderRule( 1 )
  private val header2Rule : Parser[MarkdownLine] = "-+$".r ^^^ HeaderRule( 2 )

  private val headerRuleLine : Parser[MarkdownLine] = header1Rule | header2Rule

  private val headerLine : Parser[MarkdownLine] = "#{1,6} ".r ~ rest ^^ {
    case level ~ text if level.length <= 7 => Header( level.trim.length,
        text.trim.replaceAll( "#*$", "" ).trim )
  }

  private val lineParser : Parser[MarkdownLine] = emptyLine | headerRuleLine | headerLine | textLine

  private def parseLine( str : String ) : Either[String, MarkdownLine] =
    parseAll( lineParser, str ) match {
      case Success( result, _ ) => Right( result )
      case Failure( msg, _ ) => Left( msg )
      case Error( msg, _ ) => Left( msg )
    }

  def parseLines(lines: Seq[String]) : Either[String, Seq[MarkdownLine]] = {
    val buffer = ListBuffer[MarkdownLine]( )
    val iter = lines.iterator
    while ( iter.hasNext ) {
      val parsed = parseLine( iter.next() )
      if ( parsed.isLeft ) return Left( parsed.left.get )
      buffer += parsed.right.get
    }
    return Right( buffer.toSeq )
  }
}