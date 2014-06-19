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

private object BlockCombiner {

  private sealed trait CombineResult
  private case class Success( result : MarkdownBlock, rest : Seq[MarkdownLine] ) extends CombineResult
  private case class Failure( error : String ) extends CombineResult
  private case object Finished extends CombineResult

  private def combineTextLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine( text ) :: rest => combineTextLines( rest ) match {
      case Success( Paragraph( pText ), remaining ) => Success( Paragraph( (text + " " + pText).trim ), remaining )
      case result : CombineResult => result
    }
    case _ => Success( Paragraph( "" ), lines )
  }

  private def doCombineLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine( text ) :: HeaderRule( level ) :: rest => Success( Header( level, text ), rest )
    case HeaderRule( _ ) :: rest => Failure( "A header rule must be preceeded by a text line." )
    case TextLine(_) :: rest => combineTextLines( lines )
    case (a@Header( _, _ )) :: rest => Success( a, rest )
    case EmptyLine :: rest => doCombineLines( rest )
    case Nil => Finished
  }

  def combineLines( lines : Seq[MarkdownLine] ) : Either[String, Seq[MarkdownBlock]] = {
    val buffer = ListBuffer[MarkdownBlock]()
    var remainingLines = lines
    while ( !remainingLines.isEmpty ) {
      val result = doCombineLines( remainingLines )
      result match {
        case Finished => remainingLines = Seq()
        case Success( result, rest ) => {
          buffer += result
          remainingLines = rest
        }
        case Failure( msg ) => return Left( msg )
      }
    }
    return Right( buffer.toSeq )
  }
}