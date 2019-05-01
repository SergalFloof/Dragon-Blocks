package com.dragonblocks.objects.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.dragonblocks.data.Button;
import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.handler.ChatHandler;
import com.dragonblocks.util.registry.BlockRegistry;

public class BlockButton extends BlockSided {

    private static Button data = new Button();

    public BlockButton(Material material)
    {
        super(material, data);
    }

    @Override
    /**
     * Alters polarity.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        int polarity = data.getPolarity(TE) == data.POLARITY_POSITIVE ? data.POLARITY_NEGATIVE : data.POLARITY_POSITIVE;

        data.setPolarity(TE, polarity);
        notifyBlocksOfPowerChange(TE.getWorld(), TE.xCoord, TE.yCoord, TE.zCoord);

        if (polarity == data.POLARITY_POSITIVE) {
            ChatHandler.sendMessageToPlayer("message.polarity_pos.name", entityPlayer);
        } else {
            ChatHandler.sendMessageToPlayer("message.polarity_neg.name", entityPlayer);
        }

        return true;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    	return null;
    }

    @Override
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    
    public void setBlockBoundsBasedOnState(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            setBlockBounds(0.5F - 0.1875F, 0.375F, 0.0F, 0.5F + 0.1875F, 0.625F, isDepressed(TE) ? 0.0625F : 0.125F, data.getDirection(TE));
        }
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ, ActionResult actionResult)
    {
        if (!isDepressed(TE)) {
            World world = TE.getWorld();
            data.getDirection(TE);
            data.setState(TE, data.STATE_ON, true);
            notifyBlocksOfPowerChange(world, TE.xCoord, TE.yCoord, TE.zCoord);
            world.scheduleBlockUpdate(TE.xCoord, TE.yCoord, TE.zCoord, this, tickRate(world));
            actionResult.setAltered();
        }
    }

    @Override
    /**
     * Ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	Block block;
    	TEBase TE = getSimpleTileEntity(world, pos);

        if (TE != null) {
            if (isDepressed(TE)) {
                notifyBlocksOfPowerChange(world, x, y, z);
            }
        }

        super.breakBlock(world, x, y, z, block);
    }
    /**
     * Returns whether button is in depressed state
     */
    private boolean isDepressed(TEBase TE)
    {
        return data.getState(TE) == data.STATE_ON;
    }

    /**
     * Returns power level (0 or 15)
     */
    @Override
    public int getPowerOutput(TEBase TE)
    {
        int polarity = data.getPolarity(TE);

        if (isDepressed(TE)) {
            return polarity == data.POLARITY_POSITIVE ? 15 : 0;
        } else {
            return polarity == data.POLARITY_NEGATIVE ? 15 : 0;
        }
    }

    @Override
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(IBlockState state) {
    	return true;
    }

    @Override
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	if (!world.isRemote) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null) {
                data.setState(TE, data.STATE_OFF, true);
                notifyBlocksOfPowerChange(world, x, y, z);
            }
        }
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public EnumBlockRenderType getRenderType(IBlockState state) {
    	return BlockRegistry.ButtonRenderID;
    }

}
