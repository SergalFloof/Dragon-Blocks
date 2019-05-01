package com.dragonblocks.objects.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import com.dragonblocks.api.IHammer;
import com.dragonblocks.util.interfaces.IHasModel;
import com.dragonblocks.util.registry.ItemInit;
import com.dragonblocks.Main;

public class ItemHammer extends Item implements IHammer, IHasModel {

    public ItemHammer(String name)
    {
    	setUnlocalizedName(name);
		setRegistryName(name);
        setMaxStackSize(1);
        setCreativeTab(Main.dragonblocks);

        if (ItemInit.itemCarpentersToolsDamageable) {
            setMaxDamage(ItemInit.itemCarpentersToolsUses);
        }
        
        ItemInit.ITEMS.add(this);
    }
    
    @Override
	public void registerModels() {
		
		registerItemRenderer(this, 0, "inventory");
		
	}
	
	public void registerItemRenderer(Item item, int meta, String id) {
		Main.proxy.registerItemRenderer(this, 0, "Inventory");
	}

    @Override
    public void onHammerUse(World world, EntityPlayer entityPlayer)
    {
    
//        entityPlayer.getCurrentEquippedItem().damageItem(1, entityPlayer);
    }

    @Override
    public boolean canUseHammer(World world, EntityPlayer entityPlayer)
    {
        return true;
    }

//    @Override
//    public boolean func_150897_b(Block blockToBeHarvested)
//    {
//        return blockToBeHarvested instanceof BlockCoverable;
//    }

}
