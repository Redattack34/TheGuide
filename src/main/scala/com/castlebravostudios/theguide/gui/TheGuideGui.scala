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

package com.castlebravostudios.theguide.gui

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.GL11
import com.castlebravostudios.theguide.mod.TheGuide
import com.castlebravostudios.theguide.markdown.Paragraph
import com.castlebravostudios.theguide.markdown.TextSpan
import com.castlebravostudios.theguide.text.WordWrapper
import com.castlebravostudios.theguide.text.DefaultTextSizeCalculator
import com.castlebravostudios.theguide.text.TextLine
import com.castlebravostudios.theguide.text.RenderableDocument
import com.castlebravostudios.theguide.markdown.Header
import com.castlebravostudios.theguide.markdown.parser.Parser
import org.lwjgl.input.Mouse
import org.lwjgl.input.Keyboard
import net.minecraft.client.gui.Gui

class TheGuideGui extends GuiScreen {

  private[this] val foreground = TheGuide.texture( "textures/gui/guide-foreground.png" )
  private[this] val background = TheGuide.texture( "textures/gui/guide-background.png" )

  private[this] val foregroundXSize = 135
  private[this] val foregroundYSize = 180

  private[this] val backgroundXSize = 106
  private[this] val backgroundYSize = 153

  private[this] val textXSize = 180
  private[this] val textYSize = 290

  private[this] val scrollThumbBaseXOffset = 43
  private[this] val scrollThumbBaseYOffset = -70
  private[this] val scrollbarWidth = 5
  private[this] val scrollbarHeight = 140

  private[this] val text = Parser.load( TheGuide.document( "markdown/Test.md" ) )

  private[this] val color = 0xFF404040

  private[this] var document : RenderableDocument = _

  private[this] var scroll = 0

  override def drawScreen( mouseX : Int, mouseY : Int, param3 : Float ): Unit = {
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    mc.renderEngine.bindTexture( background )
    drawCenteredRect( backgroundXSize, backgroundYSize )

    GL11.glPushMatrix()
    GL11.glScaled(0.5d, 0.5d, 0.5d)

    if ( document == null ) {
      document = RenderableDocument( text, textXSize,
          new DefaultTextSizeCalculator( fontRenderer ) )
    }
    document.render( (width - textXSize/2) - 5, (height - textYSize/2),
        textXSize, textYSize, scroll, fontRenderer)

    GL11.glPopMatrix();

    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    mc.renderEngine.bindTexture( foreground )
    drawCenteredRect( foregroundXSize, foregroundYSize )
    drawScrollbar()

    super.drawScreen(mouseX, mouseY, param3)
  }

  override def handleMouseInput() : Unit = {
    super.handleMouseInput()

    if ( Mouse.hasWheel ) {
      val dWheel = Mouse.getEventDWheel()
      if ( dWheel != 0 ) {
        mouseWheelMoved( dWheel )
      }
    }
  }

  private def changeScroll( pixels : Int ) : Unit = {
    scroll = (scroll - pixels).max( 0 ).min( document.size - textYSize )
  }

  private def mouseWheelMoved( dist : Int ) {
    changeScroll( dist / 12 )
  }

  override protected def keyTyped( keyChar : Char, keyNum : Int ) : Unit = {
    keyNum match {
      case Keyboard.KEY_DOWN => changeScroll( -10 )
      case Keyboard.KEY_UP => changeScroll( 10 )
      case Keyboard.KEY_NEXT => changeScroll( -100 )
      case Keyboard.KEY_PRIOR => changeScroll( 100 )
      case Keyboard.KEY_HOME => scroll = 0
      case Keyboard.KEY_END => scroll = document.size - textYSize
      case _ => ()
    }
    super.keyTyped(keyChar, keyNum)
  }

  private def drawCenteredRect( xSize : Int, ySize : Int): Unit = {
    val x = (width - xSize) / 2
    val y = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
  }

  private def drawScrollbar() = {
    val x1 = (width/2) + scrollThumbBaseXOffset
    val x2 = x1 + scrollbarWidth

    val baseY = (height/2) + scrollThumbBaseYOffset
    val y1 = baseY + ((scroll.toFloat / document.size) * scrollbarHeight).toInt
    val y2 = baseY + (((scroll + textYSize).toFloat / document.size) * scrollbarHeight).toInt

    Gui.drawRect(x1, y1, x2, y2, color)
  }
}