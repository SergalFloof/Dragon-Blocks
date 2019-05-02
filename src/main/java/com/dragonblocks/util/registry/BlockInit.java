package com.dragonblocks.util.registry;

import java.util.ArrayList;
import java.util.List;

import com.dragonblocks.objects.blocks.BlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockInit {
	
	/* Block states. */

    public static boolean enableBarrier          = true;
    public static boolean enableBed              = true;
    public static boolean enableButton           = true;
    public static boolean enableDoor             = true;
    public static boolean enableFlowerPot        = true;
    public static boolean enableGate             = true;
    public static boolean enableHatch            = true;
    public static boolean enableLadder           = true;
    public static boolean enableLever            = true;
    public static boolean enablePressurePlate    = true;
    public static boolean enableSafe             = true;
    public static boolean enableSlope            = true;
    public static boolean enableStairs           = true;
    public static boolean enableTorch            = true;
	
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	//Blocks
	public static final Block MAIN_BLOCK = new BlockBase("main_block", Material.WOOD);
	public static final Block BLOCK_SLOPE = new BlockBase("block_slope", Material.WOOD);

	

}
