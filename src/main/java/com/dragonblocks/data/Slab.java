package com.dragonblocks.data;

import java.util.Arrays;

import com.dragonblocks.tileentity.TEBase;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.util.EnumFacing;

public class Slab implements ISided {

    public final static byte BLOCK_FULL = 0;
    public final static byte SLAB_X_NEG = 1; // 4
    public final static byte SLAB_X_POS = 2; // 5
    public final static byte SLAB_Y_NEG = 3; // 0
    public final static byte SLAB_Y_POS = 4; // 1
    public final static byte SLAB_Z_NEG = 5; // 2
    public final static byte SLAB_Z_POS = 6; // 3

    /** For compatibility with old direction values. */
    private static final Integer[] DIR_MAP = { 4, 5, 0, 1, 2, 3 };

    @Override
	public boolean setDirection(TEBase TE, EnumFaceDirection dir) {
		int data = TE.getData();
        int newData = Arrays.asList(DIR_MAP).indexOf(dir.ordinal()) + 1;
        if (data != newData) {
            TE.setData(newData);
            return true;
        }

        return false;
	}

    public boolean setFullCube(TEBase TE)
    {
        return TE.setData(0);
    }

    @Override
	public EnumFacing getDirection(TEBase TE) {
		if (isFullCube(TE)) {
            return null;
        }

        int data = TE.getData();
        return EnumFacing.getFront(DIR_MAP[data - 1]);
	}

    public boolean isFullCube(TEBase TE)
    {
        return TE.getData() == 0;
    }

	

	

}
