package com.dragonblocks.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSlope extends ItemBlock {

    public ItemBlockSlope(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setUnlocalizedName("blockCarpentersSlope");
    }

    @Override
    public int getMetadata(int damageValue)
    {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        if (itemStack.getItemDamage() >= BlockSlope.slopeType.length) {
            return this.getUnlocalizedName();
        } else {
            return getUnlocalizedName() + "." + BlockSlope.slopeType[itemStack.getItemDamage()];
        }
    }

}
