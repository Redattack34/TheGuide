package com.castlebravostudios.theguide.text

import net.minecraft.util.ResourceLocation
import net.minecraft.client.gui.FontRenderer

case class Link( target : ResourceLocation, startX : Int, endX : Int )

trait RenderableElement {
  def height( renderer: FontRenderer ) : Int
  def render( x : Int, y : Int, renderer : FontRenderer ) : Unit

  val color = 0x404040
}

case class RenderableHeader( text: String, level : Int ) extends RenderableElement {
  def height( renderer: FontRenderer ) : Int = 0
  def render( x: Int, y : Int, renderer: FontRenderer ) : Unit = ()
}

case class TextLine( text : String, links: Set[Link] ) extends RenderableElement {
  def height( renderer: FontRenderer ) : Int = renderer.FONT_HEIGHT + 1
  def render(x : Int, y : Int, renderer : FontRenderer ) : Unit =
    renderer.drawString(text, x, y, color)
}

case object BlankLine extends RenderableElement {
  def height( renderer: FontRenderer ) : Int = renderer.FONT_HEIGHT + 1
  def render(x : Int, y : Int, renderer : FontRenderer ) : Unit = ()
}
