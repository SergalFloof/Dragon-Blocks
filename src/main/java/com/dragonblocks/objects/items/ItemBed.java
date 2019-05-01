package com.dragonblocks.objects.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import com.dragonblocks.data.Bed;
import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;
import com.dragonblocks.util.EntityLivingUtil;
import com.dragonblocks.util.interfaces.IHasModel;
import com.dragonblocks.util.registry.BlockRegistry;
import com.dragonblocks.util.registry.ItemInit;
import com.dragonblocks.Main;

public class ItemBed extends ItemBlock implements IHasModel{

    public ItemBed(String name)
    {
    	setUnlocalizedName(name);
		setRegistryName(name);
        setMaxStackSize(64);
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

   
//    @Override
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
//    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
//    {
//        if (side == 1) {
//
//            ++y;
//
//            int rot = EntityLivingUtil.getRotationValue(entityPlayer);
//            ForgeDirection dir = EntityLivingUtil.getRotationFacing(rot).getOpposite();
//
//            int x_offset = x - dir.offsetX;
//            int z_offset = z - dir.offsetZ;
//
//            if (
//                    entityPlayer.canPlayerEdit(x, y, z, side, itemStack)                                                &&
//                    entityPlayer.canPlayerEdit(x_offset, y, z_offset, side, itemStack)                                  &&
//                    world.isAirBlock(x, y, z)                                                                           &&
//                    world.isAirBlock(x_offset, y, z_offset)                                                             &&
//                    World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)                                              &&
//                    World.doesBlockHaveSolidTopSurface(world, x_offset, y - 1, z_offset)                                &&
//                    placeBlock(world, BlockRegistry.blockCarpentersBed, entityPlayer, itemStack, x, y, z)               &&
//                    placeBlock(world, BlockRegistry.blockCarpentersBed, entityPlayer, itemStack, x_offset, y, z_offset)
//                    )
//            {
//
//                /* Foot of bed. */
//
//                TEBase TE_foot = (TEBase) world.getTileEntity(x, y, z);
//                Bed.setDirection(TE_foot, rot);
//
//                /* Head of bed. */
//
//                TEBase TE_head = (TEBase) world.getTileEntity(x_offset, y, z_offset);
//                Bed.setHeadOfBed(TE_head);
//                Bed.setDirection(TE_head, rot);
//
//                BlockProperties.playBlockSound(world, new ItemStack(BlockRegistry.blockCarpentersBed), x, y, z, false);
//
//                if (!entityPlayer.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
//                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
//                }
//
//                return true;
//            }
//
//        }
//
//        return false;
//    }

}
