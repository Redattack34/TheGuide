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

class TheGuideGui extends GuiScreen {

  private[this] val foreground = TheGuide.texture( "textures/gui/guide-foreground.png" )
  private[this] val background = TheGuide.texture( "textures/gui/guide-background.png" )

  private[this] val foregroundXSize = 135
  private[this] val foregroundYSize = 180

  private[this] val backgroundXSize = 106
  private[this] val backgroundYSize = 153

  private[this] def textXSize = 190
  private[this] def textYSize = 280

  private[this] val text = Paragraph( Seq( TextSpan( """Lorem ipsum dolor sit amet,
 consectetur adipiscing elit. Vivamus vitae bibendum nulla, in consectetur
 lorem. Phasellus eget libero vitae lorem lacinia mollis ut id nibh. Suspendisse
 at purus mauris. Pellentesque sed risus in nisl consectetur iaculis. Donec
 consequat mollis elementum. Fusce metus dolor, eleifend eu neque eget, egestas
 luctus augue. Proin ornare accumsan eleifend. Aliquam non tristique purus.
 Curabitur eget ullamcorper est. Curabitur tempus neque vitae est iaculis
 ultricies. Pellentesque placerat vel justo sit amet sodales. Mauris quis
 elementum lacus, sit amet tempor mi. Nullam et aliquet enim, vel fermentum
 metus. """.filter( c => c != '\n' && c != '\r' ) ) ) )

  private[this] var document : RenderableDocument = _

  //Color is in 8-bit RGB. Hence hex. This is a sort of very dark grey.
  private[this] val color = 0x404040

  override def drawScreen( mouseX : Int, mouseY : Int, param3 : Float ): Unit = {
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    mc.renderEngine.bindTexture( background )
    drawCenteredRect( backgroundXSize, backgroundYSize )

    GL11.glPushMatrix()
    GL11.glScaled(0.5d, 0.5d, 0.5d)

    if ( document == null ) {
      document = RenderableDocument( Seq( text ), textXSize,
          new DefaultTextSizeCalculator( fontRenderer ) )
    }
    document.render( (width - textXSize/2) - 5, (height - textYSize/2),
        textXSize, textYSize, fontRenderer)

    GL11.glPopMatrix();

    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    mc.renderEngine.bindTexture( foreground )
    drawCenteredRect( foregroundXSize, foregroundYSize )

    super.drawScreen(mouseX, mouseY, param3)
  }

  private def drawCenteredRect( xSize : Int, ySize : Int): Unit = {
    val x = (width - xSize) / 2
    val y = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
  }
}