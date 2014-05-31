package com.castlebravostudios.theguide.markdown.parser

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import com.castlebravostudios.theguide.markdown.{ MarkdownBlock => SpanBlock }
import com.castlebravostudios.theguide.markdown.TextSpan
import com.castlebravostudios.theguide.markdown
import java.io.IOException
import net.minecraft.util.ResourceLocation
import net.minecraft.client.Minecraft

object Parser {

  private def errorMsg( text : String ) = Seq( markdown.Paragraph( Seq( TextSpan( text ) ) ) )

  def parse( input: InputStream ) : Either[String, Seq[SpanBlock]] = {
    val src = Source.fromInputStream(input, "UTF-8")
    val lines = src.getLines()
    for {
      lines <- LineParser.parseLines( lines.toSeq ).right
      blocks <- BlockCombiner.combineLines( lines ).right
      result <- SpanParser.parseSpans( blocks ).right
    } yield result
  }

  def load( location: ResourceLocation ) : Seq[SpanBlock] = {
    try {
      val resource = Minecraft.getMinecraft().getResourceManager().getResource(location)
      val parsed = parse( resource.getInputStream() )
      parsed.fold(
          str => errorMsg( "Failed to parse resource: " + str ),
          identity )
    }
    catch {
      case io : IOException => errorMsg( "Failed to load resource: " + io.getMessage() )
    }
  }
}