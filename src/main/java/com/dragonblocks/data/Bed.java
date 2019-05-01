package com.dragonblocks.data;

import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.EntityLivingUtil;
import com.dragonblocks.util.registry.BlockInit;
import com.dragonblocks.util.registry.BlockRegistry;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Bed {

    /**
     * 16-bit data components:
     *
     * [0]     [00]       [00000000]  [0]         [0000]
     * isHead  Direction  Unused      isOccupied  Type
     */

    public final static byte TYPE_NORMAL   = 0;

    /**
     * Returns type.
     */
    public final static int getType(TEBase TE)
    {
        return TE.getData() & 0xf;
    }

    /**
     * Sets type.
     */
    public static void setType(TEBase TE, int type)
    {
        int temp = (TE.getData() & ~0xf) | type;
        TE.setData(temp);
    }

    /**
     * Returns whether bed is occupied.
     */
    public static boolean isOccupied(TEBase TE)
    {
        return (TE.getData() & 0x10) != 0;
    }

    /**
     * Sets occupation.
     */
    public static void setOccupied(TEBase TE, boolean isOccupied)
    {
        int temp = TE.getData() & ~0x10;

        if (isOccupied) {
            temp |= 1 << 4;
        }

        TE.setData(temp);
    }

    /**
     * Returns TE for opposite piece.
     * Will return null if opposite piece doesn't exist (when creating or destroying block, for instance).
     */
    public static TEBase getOppositeTE(TEBase TE)
    {
    	EnumFacing dir = getDirection(TE);
        int x = TE.xCoord;
        int z = TE.zCoord;

        if (isHeadOfBed(TE)) {
            x = TE.xCoord + dir.offsetX;
            z = TE.zCoord + dir.offsetZ;
        } else {
            x = TE.xCoord - dir.offsetX;
            z = TE.zCoord - dir.offsetZ;
        }

        World world = TE.getWorld();

        if (world.getBlock(x, TE.yCoord, z).equals(BlockInit.enableBed)) {
            return (TEBase) world.getTileEntity(x, TE.yCoord, z);
        } else {
            return null;
        }
    }

    /**
     * Returns whether block is head of bed.
     */
    public static boolean isHeadOfBed(TEBase TE)
    {
        return (TE.getData() & 0x8000) != 0;
    }

    /**
     * Sets block as head of bed.
     */
    public static void setHeadOfBed(TEBase TE)
    {
        int temp = (TE.getData() & ~0x8000) | (1 << 15);
        TE.setData(temp);
    }

    /**
     * Returns direction of bed piece.
     */
    public static EnumFacing getDirection(TEBase TE)
    {
        int rot = (TE.getData() & 0x6000) >> 13;
        return EntityLivingUtil.getRotationFacing(rot).getOpposite();
    }

    /**
     * Sets direction of bed piece.
     * Stored as player facing from 0 to 3.
     */
    public static void setDirection(TEBase TE, int facing)
    {
        int temp = (TE.getData() & ~0x6000) | (facing << 13);
        TE.setData(temp);
    }

}
