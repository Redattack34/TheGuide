package com.castlebravostudios.theguide.markdown.parser

private[parser] sealed trait MarkdownBlock
private[parser] sealed trait MarkdownLine

private[parser] case class Paragraph( text : String ) extends MarkdownBlock
private[parser] case class Header( val level : Int, val text : String ) extends MarkdownBlock with MarkdownLine

private[parser] case class TextLine( val text : String ) extends MarkdownLine
private[parser] case class HeaderRule( val level : Int ) extends MarkdownLine
private[parser] case object EmptyLine extends MarkdownLine with MarkdownBlock