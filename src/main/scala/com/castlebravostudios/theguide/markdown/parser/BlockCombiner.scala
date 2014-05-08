package com.castlebravostudios.theguide.markdown.parser

import scala.collection.mutable.ListBuffer

private object BlockCombiner {

  private sealed trait CombineResult
  private case class Success( result : MarkdownBlock, rest : Seq[MarkdownLine] ) extends CombineResult
  private case class Failure( error : String ) extends CombineResult

  private def combineTextLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine( text ) :: rest => combineTextLines( rest ) match {
      case Success( Paragraph( pText ), remaining ) => Success( Paragraph( (text + " " + pText).trim ), remaining )
      case result => result
    }
    case _ => Success( Paragraph( "" ), lines )
  }

  private def doCombineLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine( text ) :: HeaderRule( level ) :: rest => Success( Header( level, text ), rest )
    case HeaderRule( _ ) :: rest => Failure( "A header rule must be preceeded by a text line." )
    case TextLine(_) :: rest => combineTextLines( lines )
    case (a@Header( _, _ )) :: rest => Success( a, rest )
    case EmptyLine :: rest => Success( EmptyLine, rest )
  }

  def combineLines( lines : Seq[MarkdownLine] ) : Either[String, Seq[MarkdownBlock]] = {
    val buffer = ListBuffer[MarkdownBlock]()
    var remainingLines = lines
    while ( !remainingLines.isEmpty ) {
      val result = doCombineLines( remainingLines )
      result match {
        case Success( EmptyLine, rest ) => remainingLines = rest
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