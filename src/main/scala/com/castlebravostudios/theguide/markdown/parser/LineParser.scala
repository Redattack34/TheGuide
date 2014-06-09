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

import scala.collection.mutable.ListBuffer
import scala.util.parsing.combinator._

private object LineParser extends RegexParsers {

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