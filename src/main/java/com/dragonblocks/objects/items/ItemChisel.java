package com.dragonblocks.objects.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import com.dragonblocks.Main;
import com.dragonblocks.api.IChisel;
import com.dragonblocks.util.interfaces.IHasModel;
import com.dragonblocks.util.registry.ItemInit;

public class ItemChisel extends Item implements IChisel, IHasModel {

    public ItemChisel(String name)
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

//    @Override
//    public void onChiselUse(World world, EntityPlayer entityPlayer)
//    {
//        entityPlayer.getCurrentEquippedItem().damageItem(1, entityPlayer);
//    }

    @Override
    public boolean canUseChisel(World world, EntityPlayer entityPlayer)
    {
        return true;
    }

	@Override
	public void onChiselUse(World world, EntityPlayer entityPlayer) {
		// TODO Auto-generated method stub
		
	}

//    @Override
//    public boolean func_150897_b(Block blockToBeHarvested)
//    {
//        return blockToBeHarvested instanceof BlockCoverable;
//    }

}
