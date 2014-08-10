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

package com.castlebravostudios.theguide.mod

import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage
import com.castlebravostudios.theguide.text.IndexPageRegistry
import net.minecraft.util.ResourceLocation

object IMCHandler {

  private val REGISTER_FILE_KEY = "RegisterIndexFile"

  private val nameKey = "name"
  private val locationKey = "location"

  def handle( message : IMCMessage ) : Unit = message.key match {
    case REGISTER_FILE_KEY => handleRegisterFileEvent( message )
    case _ => logError( message, "Unknown Message Key" )
  }

  private def handleRegisterFileEvent( message : IMCMessage ) : Unit = {
    if ( !message.isNBTMessage() ) {
      logError( message, "Unexpected data type - must be an NBT Message" )
      return
    }

    val tag = message.getNBTValue()
    val name = tag.getString( nameKey )
    val location = tag.getString( locationKey )

    if ( name == "" || location == "" ) {
      logError( message,
          "Expected NBT Tag compound with name and resource location. Got: " + tag )
      return
    }

    IndexPageRegistry.register( name, new ResourceLocation( location ) )
  }

  private def logError( message : IMCMessage, error : String ) : Unit = {
    val messageString = s"Key: ${message.key}, Sender: ${message.getSender()}"
    TheGuide.logger.error(s"Failed to handle IMC message ($messageString). Reason: $error" )
  }
}