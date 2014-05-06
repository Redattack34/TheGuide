package com.castlebravostudios.theguide.markdown

sealed trait MarkdownLine

case class TextLine( val text : String ) extends MarkdownLine
case object EmptyLine extends MarkdownLine