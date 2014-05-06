package com.castlebravostudios.theguide.markdown

sealed trait MarkdownBlock
case class Paragraph( text : String ) extends MarkdownBlock