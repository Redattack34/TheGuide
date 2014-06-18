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
import net.minecraft.util.ResourceLocation
import com.castlebravostudios.theguide.mod.PlayerHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.client.gui.GuiButton
import com.castlebravostudios.theguide.mod.Config
import com.castlebravostudios.theguide.text.IndexPageRegistry

class TheGuideGui( player : EntityPlayer ) extends GuiScreen {

  private[this] val foreground = TheGuide.texture( "textures/gui/guide-foreground.png" )
  private[this] val background = TheGuide.texture( "textures/gui/guide-background.png" )

  private[this] val fontSizeMult = Config.fontSizeMultiplier

  private[this] val foregroundXSize = 504 / 2
  private[this] val foregroundTopYStart = 0
  private[this] val foregroundTopYEnd = 54 / 2
  private[this] val foregroundBottomYStart = 54 / 2
  private[this] val foregroundBottonYEnd = 90 / 2

  private[this] val backgroundXSize = 512 / 2
  private[this] val backgroundYSize = 512 / 3

  private[this] val textXSize = (150 / fontSizeMult).toInt
  private[this] val textYSize = (125 / fontSizeMult).toInt

  private[this] def scrollThumbBaseXOffset = 72
  private[this] def scrollThumbBaseYOffset = -80
  private[this] def scrollbarWidth = 4
  private[this] def scrollbarHeight = 150

  private[this] val color = 0xFF404040

  private[this] var document : RenderableDocument = _

  private[this] var scroll = 0

  override def drawScreen( mouseX : Int, mouseY : Int, param3 : Float ): Unit = {
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    if ( document == null ) {
      val stats = PlayerHandler.getPlayerStats( player )
      stats.foreach { st => st.getLastRead match {
        case Some( loc ) => {
          loadPage( loc )
          scroll = st.lastScrollPos
          }
        case None => loadHomePage()
        }
      }
    }

    drawBackground()

    GL11.glPushMatrix()
    GL11.glScaled(fontSizeMult, fontSizeMult, fontSizeMult)

    def scaleAndCenter( screenSize : Int, textSize : Int ) : Int = {
      ( ( (screenSize / fontSizeMult) - textSize ) / 2 ).toInt
    }

    document.render( scaleAndCenter( width - 10, textXSize ),
        scaleAndCenter( height, textYSize ),
        textXSize, textYSize, scroll, fontRenderer)

    GL11.glPopMatrix();

    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    drawForeground()
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
    scroll = (scroll - pixels).max( 0 ).min( document.size - textYSize ).max( 0 )
    PlayerHandler.getPlayerStats(player).foreach( st => st.lastScrollPos = scroll )
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
      case Keyboard.KEY_HOME => changeScroll( Integer.MIN_VALUE )
      case Keyboard.KEY_END => changeScroll( Integer.MAX_VALUE )
      case _ => ()
    }
    super.keyTyped(keyChar, keyNum)
  }

  override protected def mouseClicked( x : Int, y : Int, button : Int ) : Unit = {
    if ( isOnScreen( x, y ) ) {
      val ( docX, docY ) = toDocumentCoords( x, y )
      document.clicked(docX, docY, this)
    }

    if ( isOnHomeButton( x, y ) ) {
      loadHomePage()
    }

    super.mouseClicked(x, y, button)
  }

  private def loadHomePage() : Unit = {
    document = RenderableDocument( IndexPageRegistry.globalIndexPage, textXSize,
          new DefaultTextSizeCalculator( fontRenderer ) )
    changeScroll( Integer.MIN_VALUE )
    PlayerHandler.getPlayerStats(player).foreach( _.clearLastRead )
  }

  private def isOnHomeButton( x : Int, y : Int ) : Boolean = {
    val xMin = (width - foregroundXSize) / 2 + 25
    val xMax = xMin + 7

    val yMin = (height + 150) / 2 + 5
    val yMax = yMin + 7

    xMin <= x && x <= xMax && yMin <= y && y <= yMax
  }

  private def drawCenteredRect( xSize : Int, ySize : Int): Unit = {
    val x = (width - xSize) / 2
    val y = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
  }

  private def isOnScreen(x: Int, y: Int) : Boolean = {
    val minX = ( width - 10 - textXSize * fontSizeMult ) / 2
    val maxX = ( width - 10 + textXSize * fontSizeMult ) / 2

    val minY = ( height - 40 - textYSize * fontSizeMult ) / 2
    val maxY = ( height + 25 + textYSize * fontSizeMult ) / 2

    minX < x && x < maxX && minY < y && y < maxY
  }

  private def toDocumentCoords( x : Int, y : Int ) : (Int, Int) = {
    val xOnScreen = x - ( width - 10 - textXSize * fontSizeMult ) / 2
    val yOnScreen = y - ( height - textYSize * fontSizeMult ) / 2

    val documentX = xOnScreen / fontSizeMult
    val documentY = yOnScreen / fontSizeMult + scroll
    (documentX.toInt, documentY.toInt)
  }

  private def drawForeground() = {
    val x = (width - foregroundXSize) / 2

    mc.renderEngine.bindTexture( foreground )
    val tex = mc.renderEngine.getTexture( foreground )
    drawTexturedModalRect(x, (height - 200) / 2, 0,
        foregroundTopYStart, foregroundXSize, (foregroundTopYEnd - foregroundTopYStart) )
    drawTexturedModalRect(x, (height + 150) / 2, 0,
        foregroundBottomYStart, foregroundXSize, foregroundBottonYEnd - foregroundBottomYStart )
  }

  private def drawBackground() = {
    mc.renderEngine.bindTexture( background )
    drawCenteredRect( backgroundXSize, backgroundYSize )
  }

  private def drawScrollbar() = {
    val x1 = ( width.toFloat / 2).ceil.toInt + scrollThumbBaseXOffset
    val x2 = x1 + scrollbarWidth

    val baseY = (height/2) + scrollThumbBaseYOffset
    val y1Percent = ( scroll.toFloat / document.size ).max( 0.0f )
    val y1 = baseY + (y1Percent * scrollbarHeight).toInt
    val y2Percent = ((scroll + textYSize).toFloat / document.size).min( 1.0f )
    val y2 = baseY + ( y2Percent * scrollbarHeight).toInt

    drawVerticalLine(x1, baseY, baseY + scrollbarHeight, color)
    drawVerticalLine(x2, baseY, baseY + scrollbarHeight, color)
    drawHorizontalLine(x1, x2, baseY, color)
    drawHorizontalLine(x1, x2, baseY + scrollbarHeight, color)
    Gui.drawRect(x1, y1, x2, y2, color)
  }

  def loadPage(target: ResourceLocation) : Unit = {
    val text = Parser.load( target )

    document = RenderableDocument( text, textXSize,
          new DefaultTextSizeCalculator( fontRenderer ) )
    changeScroll( Integer.MIN_VALUE )

    PlayerHandler.getPlayerStats(player).foreach( _.setLastRead( target ) )
  }
}