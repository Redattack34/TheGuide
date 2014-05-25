package com.castlebravostudios.theguide.gui

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.GL11
import com.castlebravostudios.theguide.mod.TheGuide

class TheGuideGui extends GuiScreen {

  private val foreground = TheGuide.texture( "textures/gui/guide-foreground.png" )
  private val background = TheGuide.texture( "textures/gui/guide-background.png" )

  private val foregroundXSize = 135
  private val foregroundYSize = 180

  private val backgroundXSize = 106
  private val backgroundYSize = 153

  //Color is in 8-bit RGB. Hence hex. This is a sort of very dark grey.
  private[this] val color = 0x404040

  override def drawScreen( mouseX : Int, mouseY : Int, param3 : Float ): Unit = {
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

    mc.renderEngine.bindTexture( background )
    drawCenteredRect( backgroundXSize, backgroundYSize )

    mc.renderEngine.bindTexture( foreground )
    drawCenteredRect( foregroundXSize, foregroundYSize )

    fontRenderer.drawString("Test Dummy GUI", 120, 60, color)

    super.drawScreen(mouseX, mouseY, param3)
  }

  private def drawCenteredRect( xSize : Int, ySize : Int): Unit = {
    val x = (width - xSize) / 2
    val y = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
  }
}