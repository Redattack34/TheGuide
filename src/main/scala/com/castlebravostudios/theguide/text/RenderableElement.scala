package com.castlebravostudios.theguide.text

import net.minecraft.util.ResourceLocation
import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11

case class Link( target : ResourceLocation, startX : Int, endX : Int )

trait RenderableElement {
  def height( renderer: FontRenderer ) : Int = renderer.FONT_HEIGHT + 1
  def render( x : Int, y : Int, renderer : FontRenderer ) : Unit

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
}

case object BlankLine extends RenderableElement {
  def render(x : Int, y : Int, renderer : FontRenderer ) : Unit = ()
}
