package com.dragonblocks.objects.items;

import com.dragonblocks.Main;
import com.dragonblocks.util.interfaces.IHasModel;
import com.dragonblocks.util.registry.ItemInit;

import net.minecraft.item.Item;

public class ItemBase extends Item  implements IHasModel{

	public ItemBase(String name) {
		
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.dragonblocks);
		
		ItemInit.ITEMS.add(this);
		
	}

	@Override
	public void registerModels() {
		
		registerItemRenderer(this, 0, "inventory");
		
	}
	
	public void registerItemRenderer(Item item, int meta, String id) {
		Main.proxy.registerItemRenderer(this, 0, "Inventory");
	}

}
