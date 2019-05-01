package com.dragonblocks.util.flowerpot;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;

public class FlowerPotProperties {

    /**
     * Will return block from ItemStack. This is to be used for plants only.
     */
    public static Block toBlock(ItemStack itemStack)
    {
        Block block = FlowerPotHandler.itemPlant.get(itemStack.getItem());

        if (block != null) {
            return block;
        } else {
            return BlockProperties.toBlock(itemStack);
        }
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns plant color.
     */
    public static int getPlantColor(TEBase TE)
    {
        ItemStack itemStack = TE.getAttribute(TE.ATTR_PLANT);
        Block block = toBlock(itemStack);

        TE.setMetadata(itemStack.getItemDamage());
        int color1 = block.getBlockColor();
        int color2 = block.colorMultiplier(TE.getWorldObj(), TE.xCoord, TE.yCoord, TE.zCoord);
        TE.restoreMetadata();

        return color1 < color2 ? color1 : color2;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns whether plant can be colored - leaves, grass, etc.
     */
    public static boolean isPlantColorable(TEBase TE)
    {
        return FlowerPotProperties.getPlantColor(TE) != 16777215;
    }

    /**
     * Returns whether ItemStack contains soil.
     */
    public static boolean isSoil(ItemStack itemStack)
    {
        if (itemStack.getItem() instanceof ItemBlock) {
            Block block = BlockProperties.toBlock(itemStack);
            if (!block.hasTileEntity(itemStack.getItemDamage())) {
                Material material = block.getMaterial();
                return material.equals(Material.GRASS) || material.equals(Material.GROUND) || material.equals(Material.SAND);
            }
        }

        return false;
    }

    /**
     * Returns whether ItemStack contains a plant.
     */
    public static boolean isPlant(ItemStack itemStack)
    {
        Block block = BlockProperties.toBlock(itemStack);

        if (!block.equals(Blocks.AIR)) {
            if (!block.hasTileEntity(itemStack.getItemDamage())) {
                return block instanceof IPlantable || block instanceof IShearable;
            } else {
                return false;
            }
        } else {
            return FlowerPotHandler.itemPlant.containsKey(itemStack.getItem());
        }
    }

}
