package com.castlebravostudios.theguide.markdown.parser

import com.castlebravostudios.theguide.markdown.MarkdownLine
import java.nio.file.Paths
import java.nio.file.Files
import java.io.InputStream
import scala.io.Source
import com.castlebravostudios.theguide.markdown.MarkdownBlock

object Parser {

  def parse( input: InputStream ) : Either[String, Seq[MarkdownBlock]] = {
    val src = Source.fromInputStream(input, "UTF-8")
    val lines = src.getLines()
    for {
      lines <- LineParser.parseLines( lines.toSeq ).right
      blocks <- BlockCombiner.combineLines( lines ).right
    } yield blocks
  }

  def main(args: Array[String]) {
    val path = Paths.get( "C:/Users/Redattack34/Desktop/My Dropbox/workspace/TheGuide/src/main/resources/assets/theguide/markdown/Test.md" )
    val stream = Files.newInputStream(path)
    println( parse( stream ) )
  }
}