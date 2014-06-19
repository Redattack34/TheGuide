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

import net.minecraft.util.ResourceLocation
import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11
import com.castlebravostudios.theguide.gui.TheGuideGui

case class Link( target : ResourceLocation, startX : Int, endX : Int )

trait RenderableElement {
  def height( calc: TextSizeCalculator ) : Int = calc.textHeight + 1
  def render( x : Int, y : Int, renderer : FontRenderer ) : Unit
  def clicked( x : Int, y : Int, gui : TheGuideGui ) : Unit = ()

  val color = 0x404040
}

case class RenderableHeader( text: String, level : Int ) extends RenderableElement {
  val formattedText = level match {
    case 1 => "§l§n" + text + "§r"
    case _ => "§n" + text + "§r"
  }

  def render( x: Int, y : Int, renderer: FontRenderer ) : Unit = {
    val textWidth = renderer.getStringWidth(formattedText)
    val horizOffset = ( 190 - textWidth ) / 2
    renderer.drawString(formattedText, x + horizOffset, y, color)
  }
}

case class TextLine( text : String, links: Set[Link] ) extends RenderableElement {
  def render(x : Int, y : Int, renderer : FontRenderer ) : Unit =
    renderer.drawString(text, x, y, color)

  override def clicked( x : Int, y : Int, gui : TheGuideGui ) : Unit =
    links.find( link => link.startX < x && x < link.endX )
      .foreach( link => gui.loadPage( link.target ) )
}

case object BlankLine extends RenderableElement {
  def render(x : Int, y : Int, renderer : FontRenderer ) : Unit = ()
}
