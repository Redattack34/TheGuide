package com.castlebravostudios.theguide.text

import scala.collection.mutable.ListBuffer
import net.minecraft.util.ResourceLocation
import scala.collection.mutable

class WordWrapper( calc : TextSizeCalculator, maxWidth : Int ) {
  private val lines = ListBuffer[TextLine]()
  private var currentLine : String = ""
  private var currentLinkStart : Int = 9
  private var currentLinkLocation : Option[ResourceLocation] = None
  private val currentLineLinks = mutable.Set[Link]()

  def appendString(text: String) = {
    text.split(" ").foreach( appendWord )
  }

  private def appendWord( word : String ) : Unit = {
    val newLine = if ( currentLineNoFormat.isEmpty() ) currentLine + word
        else currentLine + " " + word
    val width = calc.stringWidth( newLine )

    if ( width <= maxWidth ) {
      currentLine = newLine
    }
    else {
      startNewLine()
      currentLine += word
    }
  }

  private def startNewLine() = {
    val linkLoc = currentLinkLocation

    if ( linkLoc.isDefined ) {
      endLink()
    }

    lines += TextLine( currentLine, currentLineLinks.toSet )
    currentLine = ""
    currentLineLinks.clear()
    linkLoc.foreach( startLink )
  }

  def build : Seq[TextLine] = {
    if ( !currentLineNoFormat.isEmpty() ) {
      startNewLine()
    }
    lines.toVector
  }

  private def currentLineNoFormat: String = {
    currentLine.replaceAll("§.", "")
  }

  def startLink( loc : ResourceLocation ) : Unit = {
    if ( currentLinkLocation.isDefined ) {
      throw new IllegalStateException( "Already formatting a link.")
    }
    currentLinkStart = calc.stringWidth(currentLine)
    currentLine += "§o§9"
    println(currentLine)
    currentLinkLocation = Some( loc )
  }

  def endLink( ) : Unit = {
    if ( currentLinkLocation.isEmpty ) {
      throw new IllegalStateException( "Not formatting a link." )
    }
    cutLink()
    currentLine += "§r"
    currentLinkLocation = None
  }

  private def cutLink() : Unit = {
    val start = currentLinkStart
    val end = calc.stringWidth(currentLine)
    currentLineLinks += ( new Link( currentLinkLocation.get, start, end ) )
  }
}