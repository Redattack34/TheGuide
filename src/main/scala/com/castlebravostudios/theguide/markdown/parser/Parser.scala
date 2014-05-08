package com.castlebravostudios.theguide.markdown.parser

import java.io.InputStream
import java.nio.file.Files

import java.nio.file.Paths

import scala.io.Source
import com.castlebravostudios.theguide.markdown.{ MarkdownBlock => SpanBlock }

object Parser {

  def parse( input: InputStream ) : Either[String, Seq[SpanBlock]] = {
    val src = Source.fromInputStream(input, "UTF-8")
    val lines = src.getLines()
    for {
      lines <- LineParser.parseLines( lines.toSeq ).right
      blocks <- BlockCombiner.combineLines( lines ).right
      result <- SpanParser.parseSpans( blocks ).right
    } yield result
  }
}