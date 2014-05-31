package com.castlebravostudios.theguide.text

import net.minecraft.client.gui.FontRenderer

trait TextSizeCalculator {
  def stringWidth( str : String ) : Int
}
class DefaultTextSizeCalculator( renderer : FontRenderer ) extends TextSizeCalculator {
  def stringWidth( str : String ) : Int = renderer.getStringWidth( str )
}
class TestTextSizeCalculator extends TextSizeCalculator {
  def stringWidth( str : String ) : Int =
    str.replaceAll("§.", "").length()
}