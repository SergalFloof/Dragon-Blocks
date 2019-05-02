package com.dragonblocks.data;

import com.dragonblocks.tileentity.TEBase;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.util.EnumFacing;

public interface ISided {

    /**
     * Sets block direction.
     *
     * @param  TE the {@link TEBase}
     * @param  side the side
     * @return <code>true</code> if direction not already set
     */
    public boolean setDirection(TEBase TE, EnumFaceDirection dir);

    /**
     * Gets block direction.
     *
     * @param  TE the {@link TEBase}
     * @return the {@link ForgeDirection}
     */
    public EnumFacing getDirection(TEBase TE);

}
