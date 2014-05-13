package com.castlebravostudios.theguide.gui

import net.minecraft.client.gui.GuiScreen

class TheGuideGui extends GuiScreen {

    //Color is in 8-bit RGB. Hence hex. This is a sort of very dark grey.
  private[this] val color = 0x404040

  override def drawScreen( mouseX : Int, mouseY : Int, param3 : Float ) : Unit = {
    fontRenderer.drawString("Test Dummy GUI", 8, 6, color)
    super.drawScreen(mouseX, mouseY, param3)
  }
}