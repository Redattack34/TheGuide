package com.castlebravostudios.theguide.items

import net.minecraft.item.Item
import com.castlebravostudios.theguide.mod.Config
import net.minecraft.creativetab.CreativeTabs

class Guide extends Item( Config.guideItemId ) {

    setUnlocalizedName("theguide.TheGuide")
    setTextureName("theguide:theguide")
    setCreativeTab(CreativeTabs.tabMisc)
    setMaxStackSize(1)

}