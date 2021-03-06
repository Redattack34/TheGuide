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

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import com.castlebravostudios.theguide.items.Guide
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent
import com.castlebravostudios.theguide.text.IndexPageRegistry
import org.apache.logging.log4j.Logger
import cpw.mods.fml.common.FMLCommonHandler
import net.minecraftforge.common.MinecraftForge

@Mod(modid="mod_TheGuide", version="1.0-alpha1", modLanguage="scala", useMetadata=true)
object TheGuide {

  val theGuideScreenId : Int = 1

  private var _logger : Logger = _
  def logger : Logger = _logger

  @SidedProxy(clientSide="com.castlebravostudios.theguide.mod.ClientProxy",
      serverSide="com.castlebravostudios.theguide.mod.CommonProxy")
  var proxy : CommonProxy = null

  @EventHandler
  def preInit( event : FMLPreInitializationEvent ) : Unit = {
    _logger = event.getModLog()
    Config.load( event.getSuggestedConfigurationFile() )
  }

  @EventHandler
  def load( event : FMLInitializationEvent ) : Unit = {
    IndexPageRegistry.register( "Lorem Ipsum", document( "markdown/Test.md" ) )
    IndexPageRegistry.register( "Major General's Song", document( "markdown/MajorGeneral.md" ) )

    proxy.registerRenderers()
    proxy.loadTextures()

    Guide.register()
    Guide.registerRecipe()

    NetworkRegistry.INSTANCE.registerGuiHandler(TheGuide, proxy)
    FMLCommonHandler.instance().bus().register(PlayerHandler)
    MinecraftForge.EVENT_BUS.register(PlayerHandler)
  }

  @EventHandler
  def handleIMCMessage( event : IMCEvent ) : Unit = {
    val iter = event.getMessages().iterator()
    while( iter.hasNext() ) {
      IMCHandler.handle( iter.next )
    }
  }

  @EventHandler
  def postInit( event : FMLPostInitializationEvent ) : Unit = {

  }

  def texture( path : String ) : ResourceLocation =
    new ResourceLocation( "theguide", path )
  def document( path : String ) : ResourceLocation =
    new ResourceLocation( "theguide", path )
}
