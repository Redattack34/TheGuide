package com.castlebravostudios.theguide.text

import net.minecraft.util.ResourceLocation

case class Link( target : ResourceLocation, startX : Int, endX : Int )

trait RenderableElement {

}

case class RenderableHeader( text: String, level : Int ) extends RenderableElement
case class TextLine( text : String, links: Set[Link] ) extends RenderableElement
case object BlankLine extends RenderableElement
