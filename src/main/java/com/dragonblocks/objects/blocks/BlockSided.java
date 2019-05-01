package com.dragonblocks.objects.blocks;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.dragonblocks.data.ISided;
import com.dragonblocks.tileentity.TEBase;

public class BlockSided extends BlockCoverable {

    private ISided data = null;

    public BlockSided(Material material, ISided data)
    {
        super(material);
        this.data = data;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBox(blockState, world, pos);
    }

    @Override
    /**
     * Checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
    	
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	if (canAttachToSide(side)) {
            EnumFacing dir = EnumFaceDirection.getOrientation(side);
            return world.getBlock(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ).isSideSolid(world, x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, dir);
        } else {
            return false;
        }
    }

    @Override
    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        return side;
    }

    @Override
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving,
    		ItemStack stack) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	super.onBlockPlacedBy(world, pos, state, entityLiving, stack);

        if (!ignoreSidePlacement()) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null) {
                EnumFaceDirection dir = getPlacementDirection(world, pos, entityLiving);
                data.setDirection(TE, dir);
            }
        }
    }

    /**
     * Gets placement direction when first placed in world.
     *
     * @param world the {@link World}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the {@link ForgeDirection}
     */
    protected EnumFacing getPlacementDirection(World world, BlockPos pos, EntityLivingBase entityLiving)
    {
        IBlockState meta = world.getBlockState(pos);
        return EnumFacing.getOrientation(meta);
    }

    /**
     * Whether side block placed against influences initial direction of block.
     *
     * @return <code>true</code> if initial placement direction ignored
     */
    protected boolean ignoreSidePlacement()
    {
        return false;
    }

    /**
     * Called after a block is placed
     */
    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int metadata)
    {
        /*
         * Part of world.setBlock() involves updating neighbors.  Since we
         * prevent this in ItemBlockSided, we'll invoke it here.
         */

        world.notifyBlocksOfNeighborChange(x, y, z, this);
    }

    @Override
    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world)
    {
        return 20;
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        if (!world.isRemote) {
            TEBase TE = getTileEntity(world, x, y, z);
            if (TE != null && !canPlaceBlockOnSide(world, x, y, z, data.getDirection(TE).ordinal()) && !canFloat()) {
                destroyBlock(world, x, y, z, true);
            }
        }
    }

    /**
     * Notifies relevant blocks of a change in power output.
     *
     * @param  world
     * @param  x
     * @param  y
     * @param  z
     * @return nothing
     */
    public void notifyBlocksOfPowerChange(World world, int x, int y, int z)
    {
        /* Notify strong power change. */

        world.notifyBlockChange(x, y, z, this);

        /* Notify weak power change. */

        if (canProvidePower()) {
            TEBase TE = getTileEntity(world, x, y, z);
            if (TE != null) {
                ForgeDirection dir = data.getDirection(TE);
                world.notifyBlocksOfNeighborChange(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, this);
            } else {

                /* When block is destroyed, notify neighbors in all directions. */

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    world.notifyBlocksOfNeighborChange(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, this);
                }

            }
        }
    }

    @Override
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        int power = super.isProvidingWeakPower(blockAccess, x, y, z, side);

        if (canProvidePower()) {
            TEBase TE = getTileEntity(blockAccess, x, y, z);
            if (TE != null) {
                int tempPower = getPowerOutput(TE);
                if (tempPower > power) {
                    power = tempPower;
                }
            }
        }

        return power;
    }

    @Override
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        int power = super.isProvidingStrongPower(blockAccess, x, y, z, side);

        if (canProvidePower()) {
            TEBase TE = getTileEntity(blockAccess, x, y, z);
            if (TE != null) {
                if (side == data.getDirection(TE).ordinal()) {
                    int tempPower = getPowerOutput(TE);
                    if (tempPower > power) {
                        power = tempPower;
                    }
                }
            }
        }

        return power;
    }

    @Override
    /**
     * Ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    public void breakBlock(World world, int x, int y, int z, Block block)
    {
        if (canProvidePower()) {
            notifyBlocksOfPowerChange(world, x, y, z);
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }

    /**
     * Gets block-specific power level from 0 to 15.
     *
     * @param  TE  the {@link TEBase}
     * @return the power output
     */
    public int getPowerOutput(TEBase TE)
    {
        return 0;
    }

    /**
     * Whether block can be attached to specified side of another block.
     *
     * @param  side the side
     * @return whether side is supported
     */
    public boolean canAttachToSide(EnumFacing side)
    {
        return true;
    }

    /**
     * Whether block requires an adjacent block with solid side for support.
     *
     * @return whether block can float freely
     */
    public boolean canFloat()
    {
        return false;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
    {
        if (Arrays.asList(getRotationAxes()).contains(axis)) {
            TEBase TE = getTileEntity(world, x, y, z);
            if (TE != null) {
                ForgeDirection dir = data.getDirection(TE);
                return data.setDirection(TE, dir.getRotation(axis));
            }
        }

        return false;
    }

    /**
     * Get supported axes of rotation.
     *
     * @return an array of {@link ForgeDirection} enums.
     */
    protected ForgeDirection[] getRotationAxes()
    {
        return new ForgeDirection[] { ForgeDirection.DOWN, ForgeDirection.UP };
    }

}
