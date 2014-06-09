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