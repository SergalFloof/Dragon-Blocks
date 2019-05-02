package com.dragonblocks.data;

import com.dragonblocks.tileentity.TEBase;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.util.EnumFacing;

public class Ladder implements ISided {

    /**
     * 16-bit data components:
     *
     * [000000000] [0000] [000]
     * Unused      Type   Dir
     */

    public static final byte DIR_ON_X  = 0;
    public static final byte DIR_ON_Z  = 1;

    public static final byte TYPE_DEFAULT = 0;
    public static final byte TYPE_RAIL    = 1;
    public static final byte TYPE_POLE    = 2;

    @Override
   	public EnumFacing getDirection(TEBase TE) {
   		return EnumFacing.getFront(TE.getData() & 0x7);
   	}

    @Override
    public boolean setDirection(TEBase TE, EnumFaceDirection dir)
    {
        int temp = (TE.getData() & ~0x7) | dir.ordinal();
        return TE.setData(temp);
    }

    /**
     * Returns true if ladder is not connected to side of a block.
     */
    public boolean isFreestanding(TEBase TE)
    {
        return getDirection(TE).ordinal() < 2;
    }

    public int getType(TEBase TE)
    {
        return (TE.getData() & 0x78) >>> 3;
    }

    public void setType(TEBase TE, int type)
    {
        int temp = (TE.getData() & ~0x78) | (type << 3);
        TE.setData(temp);
    }

}
