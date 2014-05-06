package com.castlebravostudios.theguide.markdown

sealed trait MarkdownBlock
case class Paragraph( text : String ) extends MarkdownBlock

sealed trait MarkdownLine
case class TextLine( val text : String ) extends MarkdownLine
case object EmptyLine extends MarkdownLine with MarkdownBlock