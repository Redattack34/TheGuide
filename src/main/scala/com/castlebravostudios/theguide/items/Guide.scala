package com.castlebravostudios.theguide.items

import net.minecraft.item.Item
import com.castlebravostudios.theguide.mod.Config
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import com.castlebravostudios.theguide.mod.TheGuide

class Guide extends Item( Config.guideItemId ) {

  setUnlocalizedName("theguide.TheGuide")
  setTextureName("theguide:theguide")
  setCreativeTab(CreativeTabs.tabMisc)
  setMaxStackSize(1)


  override def onItemRightClick( stack : ItemStack, world : World, player : EntityPlayer ) : ItemStack = {
    player.openGui( TheGuide, TheGuide.theGuideScreenId, world,
      player.posX.toInt, player.posY.toInt, player.posZ.toInt)
    stack
  }
}