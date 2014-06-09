package com.castlebravostudios.theguide.text

import net.minecraft.client.gui.FontRenderer

trait TextSizeCalculator {
  def stringWidth( str : String ) : Int
  def textHeight : Int
}
class DefaultTextSizeCalculator( renderer : FontRenderer ) extends TextSizeCalculator {
  def stringWidth( str : String ) : Int = renderer.getStringWidth( str )
  def textHeight : Int = renderer.FONT_HEIGHT
}
class TestTextSizeCalculator extends TextSizeCalculator {
  def stringWidth( str : String ) : Int =
    str.replaceAll("§.", "").length()
  def textHeight : Int = 9
}