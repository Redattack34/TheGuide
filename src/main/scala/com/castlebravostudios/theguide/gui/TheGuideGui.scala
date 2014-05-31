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

class TheGuideGui extends GuiScreen {

  private[this] val foreground = TheGuide.texture( "textures/gui/guide-foreground.png" )
  private[this] val background = TheGuide.texture( "textures/gui/guide-background.png" )

  private[this] val foregroundXSize = 135
  private[this] val foregroundYSize = 180

  private[this] val backgroundXSize = 106
  private[this] val backgroundYSize = 153

  private[this] def textXSize = 190
  private[this] def textYSize = 280

  private[this] val text = Parser.load( TheGuide.document( "markdown/Test.md" ) )

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
    scroll = (scroll - pixels).max( 0 ).min( 800 )//TODO: Get max height from document
  }

  private def mouseWheelMoved( dist : Int ) {
    changeScroll( dist / 12 )
  }

  override protected def keyTyped( keyChar : Char, keyNum : Int ) : Unit = keyNum match {
    case Keyboard.KEY_DOWN => changeScroll( -10 )
    case Keyboard.KEY_UP => changeScroll( 10 )
    case Keyboard.KEY_NEXT => changeScroll( -100 )
    case Keyboard.KEY_PRIOR => changeScroll( 100 )
    case Keyboard.KEY_HOME => scroll = 0
    case Keyboard.KEY_END => scroll = 800
  }

  private def drawCenteredRect( xSize : Int, ySize : Int): Unit = {
    val x = (width - xSize) / 2
    val y = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
  }
}