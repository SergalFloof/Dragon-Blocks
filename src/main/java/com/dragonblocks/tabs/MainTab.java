package com.dragonblocks.tabs;

import com.dragonblocks.util.registry.ItemInit;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MainTab extends CreativeTabs{
	
	public MainTab(String label) {
		
		super("dragonblocks");
		
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ItemInit.HAMMER);
	}

}
