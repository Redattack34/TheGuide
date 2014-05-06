package com.castlebravostudios.theguide.markdown.parser

import com.castlebravostudios.theguide.markdown.EmptyLine
import com.castlebravostudios.theguide.markdown.MarkdownBlock
import com.castlebravostudios.theguide.markdown.MarkdownLine
import com.castlebravostudios.theguide.markdown.Paragraph
import com.castlebravostudios.theguide.markdown.TextLine
import scala.collection.mutable.ListBuffer

object BlockCombiner {

  private sealed trait CombineResult
  private case class Success( result : MarkdownBlock, rest : Seq[MarkdownLine] ) extends CombineResult
  private case class Failure( error : String ) extends CombineResult
  private case object EndOfInput extends CombineResult

  private def combineTextLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine( text ) :: rest => combineTextLines( rest ) match {
      case Success( Paragraph( pText ), remaining ) => Success( Paragraph( (text + " " + pText).trim ), remaining )
      case result => result
    }
    case _ => Success( Paragraph( "" ), lines )
  }

  private def doCombineLines( lines : Seq[MarkdownLine] ) : CombineResult = lines match {
    case TextLine(_) :: rest => combineTextLines( lines )
    case EmptyLine :: rest => doCombineLines( rest )
    case _ => EndOfInput
  }

  def combineLines( lines : Seq[MarkdownLine] ) : Either[String, Seq[MarkdownBlock]] = {
    val buffer = ListBuffer[MarkdownBlock]()
    var remainingLines = lines
    while ( !remainingLines.isEmpty ) {
      val result = doCombineLines( remainingLines )
      result match {
        case Success( result, rest ) => {
          buffer += result
          remainingLines = rest
        }
        case Failure( msg ) => return Left( msg )
        case EndOfInput => remainingLines = Seq()
      }
    }
    return Right( buffer.toSeq )
  }
}