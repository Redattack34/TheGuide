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

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import PlayerStats.{ tagName, lastReadKey, lastScrollPosKey, hasGuideKey }
import net.minecraft.util.ResourceLocation

class PlayerStats {


  private[this] var lastPageRead : String = ""
  var lastScrollPos : Int = _
  var hasGuide : Boolean = false

  def getLastRead : Option[ResourceLocation] = Some( lastPageRead )
    .filter( _ != "" )
    .map( s => new ResourceLocation( s ) )
  def clearLastRead : Unit = lastPageRead = null
  def setLastRead( loc : ResourceLocation ) : Unit = lastPageRead = loc.toString

  def readFromNBT(player : EntityPlayer) : Unit = {
    val tags = player.getEntityData()
    if ( !tags.hasKey(tagName) ) tags.setTag( tagName, new NBTTagCompound() )

    val tag = tags.getCompoundTag( tagName )
    lastPageRead = tag.getString(lastReadKey)
    lastScrollPos = tag.getInteger(lastScrollPosKey)
    hasGuide = tag.getBoolean(hasGuideKey)
  }

  def writeToNBT(player: EntityPlayer) : Unit = {
    val tags = player.getEntityData()
    if ( !tags.hasKey(tagName) ) tags.setTag( tagName, new NBTTagCompound() )

    val tag = tags.getCompoundTag(tagName)
    tag.setString(lastReadKey, lastPageRead)
    tag.setInteger(lastScrollPosKey, lastScrollPos)
    tag.setBoolean(hasGuideKey, hasGuide)
  }
}
object PlayerStats {

  val tagName = "TheGuide"

  val lastReadKey = "LastRead"
  val lastScrollPosKey = "ScrollPosition"
  val hasGuideKey = "HasGuide"

  def apply( player : EntityPlayer ) : PlayerStats = {
    val stats = new PlayerStats()
    stats.readFromNBT(player)
    return stats
  }
}