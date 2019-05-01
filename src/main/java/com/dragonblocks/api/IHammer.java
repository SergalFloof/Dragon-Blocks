package com.dragonblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IHammer {

    public void onHammerUse(World world, EntityPlayer player);

    public boolean canUseHammer(World world, EntityPlayer player);

}
