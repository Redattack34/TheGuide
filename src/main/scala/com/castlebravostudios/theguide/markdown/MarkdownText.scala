package com.castlebravostudios.theguide.markdown

import net.minecraft.util.ResourceLocation

sealed trait MarkdownSpan
case class TextSpan( text: String ) extends MarkdownSpan
case class LinkSpan( text: String, location : ResourceLocation ) extends MarkdownSpan

sealed trait MarkdownBlock
case class Header( level : Int, text : TextSpan ) extends MarkdownBlock
case class Paragraph( text : Seq[MarkdownSpan] ) extends MarkdownBlock
