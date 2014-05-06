package com.castlebravostudios.theguide.markdown

sealed trait MarkdownBlock
sealed trait MarkdownLine

case class Paragraph( text : String ) extends MarkdownBlock
case class Header( val level : Int, val text : String ) extends MarkdownBlock with MarkdownLine

case class TextLine( val text : String ) extends MarkdownLine
case class HeaderRule( val level : Int ) extends MarkdownLine
case object EmptyLine extends MarkdownLine with MarkdownBlock