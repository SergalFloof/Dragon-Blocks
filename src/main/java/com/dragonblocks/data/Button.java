package com.dragonblocks.data;

import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Button implements ISided {

    /**
     * 16-bit data components:
     *
     * [000000000] [0]      [0]   [000]
     * Unused      Polarity State Dir
     */

    public static final byte POLARITY_POSITIVE = 0;
    public static final byte POLARITY_NEGATIVE = 1;

    public static final byte STATE_OFF = 0;
    public static final byte STATE_ON  = 1;

    /**
     * Returns direction.
     */
    @Override
   	public EnumFacing getDirection(TEBase TE) {
   		return EnumFacing.getFront(TE.getData() & 0x7);
   	}

    /**
     * Sets direction.
     */
    @Override
    public boolean setDirection(TEBase TE, EnumFaceDirection dir)
    {
        int temp = (TE.getData() & ~0x7) | dir.ordinal();
        return TE.setData(temp);
    }

    /**
     * Returns state.
     */
    public int getState(TEBase TE)
    {
        return (TE.getData() & 0x8) >> 3;
    }

    /**
     * Sets state.
     */
    public void setState(TEBase TE, int state, boolean playSound)
    {
        int temp = (TE.getData() & ~0x8) | (state << 3);
        World world = TE.getWorld();

        if (!world.isRemote && BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).getMaterial() != Material.CLOTH && playSound && getState(TE) != state) {
            world.playSoundEffect(TE.getPos().getX() + 0.5D, TE.getPos().getY() + 0.5D, TE.getPos().getZ() + 0.5D, "random.click", 0.3F, getState(TE) == STATE_ON ? 0.5F : 0.6F);
        }

        TE.setData(temp);
    }

    /**
     * Returns polarity.
     */
    public int getPolarity(TEBase TE)
    {
        return (TE.getData() & 0x10) >> 4;
    }

    /**
     * Sets polarity.
     */
    public void setPolarity(TEBase TE, int polarity)
    {
        int temp = (TE.getData() & ~0x10) | (polarity << 4);
        TE.setData(temp);
    }

}
