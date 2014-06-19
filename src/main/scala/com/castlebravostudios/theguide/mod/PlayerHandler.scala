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

import java.util.UUID

import scala.collection.concurrent

import com.castlebravostudios.theguide.items.Guide

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent

object PlayerHandler {

  private val playerStats = concurrent.TrieMap[UUID, PlayerStats]()

  @SubscribeEvent
  def onPlayerLogin(event : PlayerLoggedInEvent): Unit = {
    val player = event.player
    val stats = PlayerStats( player )
    playerStats += player.getUniqueID() -> stats
    stats.writeToNBT( player )

    if ( !stats.hasGuide ) {
      player.inventory.addItemStackToInventory( new ItemStack( Guide.guideItem ) )
      stats.hasGuide = true
    }
  }

  @SubscribeEvent
  def onPlayerLogout(event: PlayerLoggedOutEvent): Unit = {
    val player = event.player
    getPlayerStats( player ).foreach( _.writeToNBT( player ) )
    playerStats -= player.getUniqueID()
  }

  def getPlayerStats( player : EntityPlayer ) : Option[PlayerStats] =
    playerStats.get( player.getUniqueID() )

}