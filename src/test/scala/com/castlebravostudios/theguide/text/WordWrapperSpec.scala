/*
 * Copyright (c) 2014, Brook 'redattack34' Heisler
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the ModularRayguns team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.castlebravostudios.theguide.text

import org.scalatest.FlatSpec
import net.minecraft.util.ResourceLocation
import org.scalatest.Outcome

class WordWrapperSpec extends FlatSpec {

  val calc = new TestTextSizeCalculator( )
  var wrapper : WordWrapper = _

  override def withFixture( test : NoArgTest ) : Outcome = {
    wrapper = new WordWrapper(calc, 100 )
    super.withFixture(test)
  }

  "WordWrapper" should "leave short lines unmodified" in {
    val text = "Lorem ipsum dolor sit amet"
    wrapper.appendString( text )
    assert( wrapper.build === Seq( TextLine( text, Set() ) ) )
  }

  it should "wrap longer lines" in {
    val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
        "Maecenas mattis consequat ipsum sed auctor. Fusce blandit convallis " +
        "luctus. Ut vel euismod risus, vel mattis justo. Sed vitae odio non" +
        "metus sagittis gravida non blandit purus."
    val expected = Seq(
          TextLine( "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
              "Maecenas mattis consequat ipsum sed", Set() ),
          TextLine( "auctor. Fusce blandit convallis luctus. Ut vel euismod risus, vel " +
              "mattis justo. Sed vitae odio", Set() ),
          TextLine( "nonmetus sagittis gravida non blandit purus.", Set() )
        )
    wrapper.appendString(text)
    assert(wrapper.build === expected)
  }

  it should "surround links with formatting characters" in {
    wrapper.appendString("Lorem")
    wrapper.startLink( new ResourceLocation( "test", "test" ) )
    wrapper.appendString( " ipsum dolor sit amet" )
    wrapper.endLink()
    assert( wrapper.build.head.text === "Lorem§9§n ipsum dolor sit amet§r" )
  }

  it should "return a text line containing the appropriate link object" in {
    val loc = new ResourceLocation( "test", "test" )
    wrapper.appendString("Lorem")
    wrapper.startLink( loc )
    wrapper.appendString( " ipsum dolor sit amet" )
    wrapper.endLink()
    assert( wrapper.build.head ===
      TextLine( "Lorem§9§n ipsum dolor sit amet§r",
          Set( Link( loc, 5, 26 ) ) ) )
  }

  it should "split links across two lines if necessary" in {
    val loc = new ResourceLocation( "test", "test" )
    wrapper.appendString( "Lorem ipsum dolor sit amet, " )
    wrapper.startLink( loc )
    wrapper.appendString( "consectetur adipiscing elit. " +
              "Maecenas mattis consequat ipsum sed auctor. " )
    wrapper.appendString( "Fusce blandit convallis luctus. " )
    wrapper.endLink()
    wrapper.appendString( "Ut vel euismod risus, vel " +
              "mattis justo. Sed vitae odio nonmetus" )

    val firstLine = TextLine( "Lorem ipsum dolor sit amet, §9§nconsectetur adipiscing elit. " +
              "Maecenas mattis consequat ipsum sed§r",
              Set( Link( loc, 28, 92 ) ) )
    val secondLine = TextLine( "§9§nauctor. Fusce blandit convallis luctus. §rUt vel euismod risus, vel " +
              "mattis justo. Sed vitae odio",
              Set( Link( loc, 0, 40 ) ) )
    val thirdLine = TextLine( "nonmetus", Set() )
    assert( wrapper.build === Seq( firstLine, secondLine, thirdLine ) )
  }

  it should "emit two links if two are in the text" in {
    val loc1 = new ResourceLocation( "test1", "test1" )
    val loc2 = new ResourceLocation( "test2", "test2" )

    wrapper.startLink( loc1 )
    wrapper.appendString("Lorem ipsum")
    wrapper.endLink()
    wrapper.startLink( loc2 )
    wrapper.appendString( " dolor sit amet" )
    wrapper.endLink()

    assert( wrapper.build === Seq(
        TextLine( "§9§nLorem ipsum§r§9§n dolor sit amet§r",
            Set( Link( loc1, 0, 11 ), Link( loc2, 11, 26 ) ) ) ) )
  }

  it should "not insert a space where none existed in the markdown" in {
    wrapper.appendString("Lorem Ipsum")
    wrapper.appendString(".")
    wrapper.appendString(" Dolor Sit Amet.")

    assert( wrapper.build === Seq( new TextLine( "Lorem Ipsum. Dolor Sit Amet.", Set() ) ) )
  }
}