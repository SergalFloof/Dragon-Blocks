package com.dragonblocks.objects.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dragonblocks.api.IChisel;
import com.dragonblocks.api.IHammer;
import com.dragonblocks.api.IWrappableBlock;
import com.dragonblocks.renderer.helper.ParticleHelper;
import com.dragonblocks.renderer.helper.RoutableFluidsHelper;
import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;
import com.dragonblocks.util.EntityLivingUtil;
import com.dragonblocks.util.handler.DesignHandler;
import com.dragonblocks.util.handler.EventHandler;
import com.dragonblocks.util.handler.OverlayHandler;
import com.dragonblocks.util.handler.OverlayHandler.Overlay;
import com.dragonblocks.util.protection.PlayerPermissions;
import com.dragonblocks.util.protection.ProtectedObject;
import com.dragonblocks.util.registry.FeatureRegistry;
import com.dragonblocks.util.registry.ItemInit;

import io.netty.handler.codec.http2.Http2FrameLogger.Direction;

public class BlockCoverable extends BlockContainer {

    /** Block drop event for dropping attribute. */
    public static int EVENT_ID_DROP_ATTR = 0;

    /** Indicates during getDrops that block instance should not be dropped. */
    protected final int METADATA_DROP_ATTR_ONLY = 16;

    /**
     * Stores actions taken on a block in order to properly play sounds,
     * decrement player inventory, and to determine if a block was altered.
     */
    protected class ActionResult {

        public ItemStack itemStack;
        public boolean playSound = true;
        public boolean altered = false;
        public boolean decInv = false;

        public ActionResult setSoundSource(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public ActionResult setNoSound() {
            playSound = false;
            return this;
        }

        public ActionResult setAltered() {
            altered = true;
            return this;
        }

        public ActionResult decInventory() {
            decInv = true;
            return this;
        }

    }

    /**
     * Class constructor.
     *
     * @param material
     */
    public BlockCoverable(Material material)
    {
        super(material);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    /**
//     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
//     * is the only chance you get to register icons.
//     */
//    public void registerBlockIcons(IIconRegister iconRegister) { }
//
//    @SideOnly(Side.CLIENT)
//    /**
//     * Returns a base icon that doesn't rely on blockIcon, which
//     * is set prior to texture stitch events.
//     *
//     * @return default icon
//     */
//    public IIcon getIcon()
//    {
//        return IconRegistry.icon_uncovered_solid;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    /**
//     * Returns the icon on the side given the block metadata.
//     * <p>
//     * Due to the amount of control needed over this, vanilla calls will always return an invisible icon.
//     */
//    public IIcon getIcon(int side, int metadata)
//    {
//        if (BlockProperties.isMetadataDefaultIcon(metadata)) {
//            return getIcon();
//        }
//
//        /*
//         * This icon is a mask (or something) for redstone wire.
//         * We use it here because it renders an invisible icon.
//         *
//         * Using an invisible icon is important because sprint particles are
//         * hard-coded and will always grab particle icons using this method.
//         * We'll throw our own sprint particles in EventHandler.class.
//         */
//
//        return BlockRedstoneWire.getRedstoneWireIcon("cross_overlay");
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    /**
//     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
//     */
//    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
//    {
//        TEBase TE = getTileEntity(blockAccess, x, y, z);
//        ItemStack itemStack = BlockProperties.getCover(TE, 6);
//        Block block = BlockProperties.toBlock(itemStack);
//
//        return block instanceof BlockCoverable ? getIcon() : getWrappedIcon(block, blockAccess, x, y, z, side, itemStack.getItemDamage());
//    }
//
//    private static IIcon getWrappedIcon(Block b, IBlockAccess iba, int x, int y, int z, int side, int meta)
//    {
//        return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getIcon(iba, x, y, z, side, b, meta) : b.getIcon(side, meta);
//    }

    /**
     * For South-sided blocks, rotates and sets the block bounds using
     * the provided EnumFacing.
     *
     * @param  dir the rotated {@link EnumFacing}
     */
    protected void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, EnumFacing dir)
    {
        switch (dir) {
            case DOWN:
                setBlockBounds(minX, 1.0F - maxZ, minY, maxX, 1.0F - minZ, maxY, dir);
                break;
            case UP:
                setBlockBounds(minX, minZ, minY, maxX, maxZ, maxY, dir);
                break;
            case NORTH:
                setBlockBounds(1.0F - maxX, minY, 1.0F - maxZ, 1.0F - minX, maxY, 1.0F - minZ, dir);
                break;
            case EAST:
                setBlockBounds(minZ, minY, 1.0F - maxX, maxZ, maxY, 1.0F - minX, dir);
                break;
            case WEST:
                setBlockBounds(1.0F - maxZ, minY, minX, 1.0F - minZ, maxY, maxX, dir);
                break;
            default:
                setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ, dir);
                break;
        }
    }

    /**
     * Called when block event is received.
     *<p>
     * For the context of this mod, this is used for dropping block attributes
     * like covers, overlays, dyes, or any other ItemStack.
     *<p>
     * In order for external classes to call the protected method
     * {@link Block#dropBlockAsItem(World,int,int,int,ItemStack) dropBlockAsItem},
     * they create a block event with parameters itemId and metadata, allowing
     * the {@link ItemStack} to be recreated and dropped.
     *
     * @param  world the {@link World}
     * @param  x the x coordinate
     * @param  y the y coordinate
     * @param  z the z coordinate
     * @param  itemId the eventId, repurposed
     * @param  metadata the event parameter, repurposed
     * @return true if event was handled
     */

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
    	if (!world.isRemote && id == EVENT_ID_DROP_ATTR)
        {
    		int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
            TEBase TE = getSimpleTileEntity(world, pos);

            if (TE != null && TE.hasAttribute((byte) param))
            {
                ItemStack itemStack = TE.getAttributeForDrop((byte) param);
                dropBlockAsItem(world, x, y, z, itemStack);
                TE.onAttrDropped((byte) param);
                return true;
            }
        }

        return super.eventReceived(state, world, pos, id, param);
    }
    
    

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack getItemDrop(World world, IBlockState metadata)
    {
        int fortune = 1;
        return new ItemStack(getItemDropped(metadata, world.rand, fortune));
    }

    /**
     * Returns adjacent, similar tile entities that can be used for duplicating
     * block properties like dye color, pattern, style, etc.
     *
     * @param  world the world reference
     * @param  x the x coordinate
     * @param  y the y coordinate
     * @param  z the z coordinate
     * @return an array of adjacent, similar tile entities
     * @see {@link TEBase}
     */
    protected TEBase[] getAdjacentTileEntities(World world, BlockPos pos)
    {
        return new TEBase[] {
            getSimpleTileEntity(world, pos)
        };
    }

    /**
     * Returns tile entity if block tile entity is instanceof TEBase.
     *
     * Used for generic purposes such as getting pattern, dye color, or
     * cover of another Carpenter's block.  Is also used if block
     * no longer exists, such as when breaking a block and ejecting
     * attributes.
     */
    protected TEBase getSimpleTileEntity(IBlockAccess blockAccess, BlockPos pos)
    {
        TileEntity TE = blockAccess.getTileEntity(pos);
        return (TE instanceof TEBase) ? (TEBase) TE : null;
    }

    /**
     * Returns tile entity if block tile entity is instanceof TEBase and
     * also belongs to this block type.
     */
    protected TEBase getTileEntity(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getSimpleTileEntity(blockAccess, pos);
        return TE != null && blockAccess.getBlockState(pos).equals(this) ? TE : null;
    }

    /**
     * Returns whether player is allowed to activate this block.
     */
    protected boolean canPlayerActivate(TEBase TE, EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	if (world.isRemote) {
            return;
        }

        TEBase TE = getTileEntity(world, pos);

        if (TE == null) {
            return;
        } else if (!PlayerPermissions.hasElevatedPermission(TE, player, false)) {
            return;
        }

        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack == null) {
            return;
        }

        int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[EventHandler.eventFace]) ? EventHandler.eventFace : 6;
        Item item = itemStack.getItem();

        if (item instanceof IHammer && ((IHammer)item).canUseHammer(world, player)) {

            ActionResult actionResult = new ActionResult();
            preOnBlockClicked(TE, world, x, y, z, player, actionResult);

            if (!actionResult.altered) {
                if (player.isSneaking()) {
                    popAttribute(TE, effectiveSide);
                } else {
                    onHammerLeftClick(TE, player);
                }
                actionResult.setAltered();
            } else {
                onNeighborChange(world, pos, pos, world);
                world.notifyBlocksOfNeighborChange(pos, this);
            }

        } else if (item instanceof IChisel && ((IChisel)item).canUseChisel(world, player)) {

            if (player.isSneaking() && TE.hasChiselDesign(effectiveSide)) {
                TE.removeChiselDesign(effectiveSide);
            } else if (TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                onChiselClick(TE, effectiveSide, true);
            }

        }
    }
    

    /**
     * Pops attribute in hard-coded order.
     *
     * @param TE
     * @param side
     */
    private void popAttribute(TEBase TE, int side)
    {
        if (TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
            TE.createBlockDropEvent(TE.ATTR_ILLUMINATOR);
        } else if (TE.hasAttribute(TE.ATTR_OVERLAY[side])) {
            TE.createBlockDropEvent(TE.ATTR_OVERLAY[side]);
        } else if (TE.hasAttribute(TE.ATTR_DYE[side])) {
            TE.createBlockDropEvent(TE.ATTR_DYE[side]);
        } else if (TE.hasAttribute(TE.ATTR_COVER[side])) {
            TE.removeChiselDesign(side);
            TE.createBlockDropEvent(TE.ATTR_COVER[side]);
        }
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
    		EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (world.isRemote) {
            return true;
        }

        TEBase TE = getTileEntity(world, pos);

        if (TE == null) {
            return false;
        }

        if (!canPlayerActivate(TE, player)) {
            return false;
        }

        // Allow block to change TE if needed before altering attributes
        TE = getTileEntityForBlockActivation(TE);
        ActionResult actionResult = new ActionResult();

        preOnBlockActivated(TE, player, side, hitX, hitY, hitZ, actionResult);

        // If no prior event occurred, try regular activation
        if (!actionResult.altered) {

            if (PlayerPermissions.hasElevatedPermission(TE, player, false)) {

                ItemStack itemStack = player.getHeldItem(hand);

                if (itemStack != null) {

                    /* Sides 0-5 are side covers, and 6 is the base block. */
                    int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[side]) ? side : 6;

                    if (itemStack.getItem() instanceof IHammer && ((IHammer)itemStack.getItem()).canUseHammer(world, player)) {

                        if (onHammerRightClick(TE, player)) {
                            actionResult.setAltered();
                        }

                    } else if (ItemInit.enableChisel && itemStack.getItem() instanceof IChisel && ((IChisel)itemStack.getItem()).canUseChisel(world, player)) {

                        if (TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                            if (onChiselClick(TE, effectiveSide, false)) {
                                actionResult.setAltered();
                            }
                        }

                    } else if (FeatureRegistry.enableCovers && BlockProperties.isCover(itemStack)) {

                        Block block = BlockProperties.toBlock(itemStack);

                        /* Will handle blocks that save directions using only y axis (pumpkin) */
                        int metadata = block instanceof BlockDirectional ? MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 2.5D) & 3 : itemStack.getItemDamage();

                        /* Will handle blocks that save directions using all axes (logs, quartz) */
                        if (BlockProperties.blockRotates(itemStack)) {
                            int rot = Direction.rotateOpposite[EntityLivingUtil.getRotationValue(player)];
                            int side_interpolated = player.rotationPitch < -45.0F ? 0 : player.rotationPitch > 45 ? 1 : rot == 0 ? 3 : rot == 1 ? 4 : rot == 2 ? 2 : 5;
                            metadata = block.onBlockPlaced(world, TE.xCoord, TE.yCoord, TE.zCoord, side_interpolated, hitX, hitY, hitZ, metadata);
                        }

                        ItemStack tempStack = itemStack.copy();
                        tempStack.setItemDamage(metadata);

                        /* Base cover should always be checked. */

                        if (effectiveSide == 6 && (!canCoverSide(TE, world, TE.xCoord, TE.yCoord, TE.zCoord, 6) || TE.hasAttribute(TE.ATTR_COVER[6]))) {
                            effectiveSide = side;
                        }

                        if (canCoverSide(TE, world, TE.xCoord, TE.yCoord, TE.zCoord, effectiveSide) && !TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                            TE.addAttribute(TE.ATTR_COVER[effectiveSide], tempStack);
                            actionResult.setAltered().decInventory().setSoundSource(itemStack);
                        }

                    } else if (player.isSneaking()) {

                        if (FeatureRegistry.enableIllumination && BlockProperties.isIlluminator(itemStack)) {
                            if (!TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
                                TE.addAttribute(TE.ATTR_ILLUMINATOR, itemStack);
                                actionResult.setAltered().decInventory().setSoundSource(itemStack);
                            }
                        } else if (FeatureRegistry.enableOverlays && BlockProperties.isOverlay(itemStack)) {
                            if (!TE.hasAttribute(TE.ATTR_OVERLAY[effectiveSide]) && (effectiveSide < 6 && TE.hasAttribute(TE.ATTR_COVER[effectiveSide]) || effectiveSide == 6)) {
                                TE.addAttribute(TE.ATTR_OVERLAY[effectiveSide], itemStack);
                                actionResult.setAltered().decInventory().setSoundSource(itemStack);
                            }
                        } else if (FeatureRegistry.enableDyeColors && BlockProperties.isDye(itemStack, false)) {
                            if (!TE.hasAttribute(TE.ATTR_DYE[effectiveSide])) {
                                TE.addAttribute(TE.ATTR_DYE[effectiveSide], itemStack);
                                actionResult.setAltered().decInventory().setSoundSource(itemStack);
                            }
                        }

                    }
                }
            }
        }

        if (!actionResult.altered) {

            // If no prior or regular event occurred, try a post event
            postOnBlockActivated(TE, player, side, hitX, hitY, hitZ, actionResult);

        } else {

            if (actionResult.itemStack == null) {
                actionResult.setSoundSource(BlockProperties.getCover(TE, 6));
            }
            damageItemWithChance(world, player);
            onNeighborBlockChange(world, TE.xCoord, TE.yCoord, TE.zCoord, this);
            world.notifyBlocksOfNeighborChange(TE.xCoord, TE.yCoord, TE.zCoord, this);

        }

        if (actionResult.playSound) {
            BlockProperties.playBlockSound(TE.getWorld(), actionResult.itemStack, TE.xCoord, TE.yCoord, TE.zCoord, false);
        }

        if (actionResult.decInv) {
            EntityLivingUtil.decrementCurrentSlot(player);
        }

        return actionResult.altered;
    }

    /**
     * Cycles through chisel patterns.
     */
    public boolean onChiselClick(TEBase TE, int side, boolean leftClick)
    {
        String design = TE.getChiselDesign(side);
        String designAdj = "";

        if (design.equals("")) {

            World world = TE.getWorld();

            /* Match pattern with adjacent pattern if possible. */

            TEBase[] TE_list = getAdjacentTileEntities(world, TE.xCoord, TE.yCoord, TE.zCoord);

            for (TEBase TE_current : TE_list) {
                if (TE_current != null) {
                    TE_current.getBlockType();
                    if (TE_current.hasChiselDesign(side)) {
                        design = TE_current.getChiselDesign(side);
                        designAdj = design;
                    }
                }
            }

        }

        if (designAdj.equals("")) {
            design = leftClick ? DesignHandler.getPrev("chisel", design) : DesignHandler.getNext("chisel", design);
        }

        if (!design.equals("")) {
            TE.setChiselDesign(side, design);
        }

        return true;
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor, World worldIn) {
    	/* This will check for and eject side covers that are obstructed. */
    	int x = pos.getX();

        if (!worldIn.isRemote) {
            TEBase TE = getTileEntity(world, x, y, z);
            if (TE != null) {
                for (int side = 0; side < 6; ++side) {
                    if (TE.hasAttribute(TE.ATTR_COVER[side])) {
                        if (!canCoverSide(TE, world, x, y, z, side)) {
                            TE.removeAttributes(side);
                            continue;
                        }
                        EnumFacing dir = EnumFacing.getOrientation(side);
                        if (world.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite()) && isSideSolid(world, x, y, z, dir)) {
                            TE.removeAttributes(side);
                        }
                    }
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
        TEBase TE = getTileEntity(blockAccess, x, y, z);
        int power = 0;

        /* Indirect power is provided by any cover. */

        if (TE != null) {
            for (int idx = 0; idx < 7; ++idx) {
                if (TE.hasAttribute(TE.ATTR_COVER[idx])) {
                    Block block = BlockProperties.toBlock(BlockProperties.getCover(TE, idx));
                    int tempPower = block.isProvidingWeakPower(blockAccess, x, y, z, side);
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
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        TEBase TE = getTileEntity(blockAccess, x, y, z);
        int power = 0;

        /* Strong power is provided by the base cover, or a side cover if one exists. */


        int effectiveSide = EnumFacing.OPPOSITES[side];
        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[effectiveSide])) {
            Block block = BlockProperties.toBlock(BlockProperties.getCover(TE, effectiveSide));
            int tempPower = block.isProvidingWeakPower(blockAccess, x, y, z, side);
            if (tempPower > power) {
                power = tempPower;
            }
        } else if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            Block block = BlockProperties.toBlock(BlockProperties.getCover(TE, 6));
            int tempPower = block.isProvidingWeakPower(blockAccess, x, y, z, side);
            if (tempPower > power) {
                power = tempPower;
            }
        }


        return power;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla digging particles form spawning.
     */
    public boolean addHitEffects(World world, RayTraceResult target, EffectRenderer effectRenderer)
    {
        TEBase TE = getTileEntity(world, target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ());

        if (TE != null) {

            int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[target.sideHit]) ? target.sideHit : 6;
            ItemStack itemStack = BlockProperties.getCover(TE, effectiveSide);

            if (BlockProperties.hasAttribute(TE, TE.ATTR_OVERLAY[effectiveSide])) {
                Overlay overlay = OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[effectiveSide]));
                if (OverlayHandler.coversFullSide(overlay, target.sideHit)) {
                    itemStack = overlay.getItemStack();
                }
            }

            Block block = BlockProperties.toBlock(itemStack);

            double xOffset = target.blockX + world.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - 0.1F * 2.0F) + 0.1F + block.getBlockBoundsMinX();
            double yOffset = target.blockY + world.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - 0.1F * 2.0F) + 0.1F + block.getBlockBoundsMinY();
            double zOffset = target.blockZ + world.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - 0.1F * 2.0F) + 0.1F + block.getBlockBoundsMinZ();

            switch (target.sideHit) {
                case 0:
                    yOffset = target.blockY + block.getBlockBoundsMinY() - 0.1D;
                    break;
                case 1:
                    yOffset = target.blockY + block.getBlockBoundsMaxY() + 0.1D;
                    break;
                case 2:
                    zOffset = target.blockZ + block.getBlockBoundsMinZ() - 0.1D;
                    break;
                case 3:
                    zOffset = target.blockZ + block.getBlockBoundsMaxZ() + 0.1D;
                    break;
                case 4:
                    xOffset = target.blockX + block.getBlockBoundsMinX() - 0.1D;
                    break;
                case 5:
                    xOffset = target.blockX + block.getBlockBoundsMaxX() + 0.1D;
                    break;
            }

            ParticleHelper.addHitEffect(TE, target, xOffset, yOffset, zOffset, itemStack, effectRenderer);

            return true;

        }

        return super.addHitEffects(world, target, effectRenderer);
    }

    private TEBase getTileEntity(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    @SideOnly(Side.CLIENT)
    /**
     * Renders block destruction effects.
     * This is controlled to prevent block destroy effects if left-clicked with a Carpenter's Hammer while player is in creative mode.
     *
     * Returns false to display effects.  True suppresses them (backwards).
     */
    public boolean addDestroyEffects(World world, int x, int y, int z, int metadata, EffectRenderer effectRenderer)
    {
        /*
         * We don't have the ability to accurately determine the entity that is
         * hitting the block. So, instead we're guessing based on who is
         * closest. This should be adequate most of the time.
         */

        TEBase TE = getTileEntity(world, x, y, z);

        if (TE != null) {

            EntityPlayer entityPlayer = world.getClosestPlayer(x, y, z, 6.5F);

            if (entityPlayer != null) {
                if (!suppressDestroyBlock(entityPlayer)) {
                    ParticleHelper.addDestroyEffect(world, x, y, z, BlockProperties.getCover(TE, 6), effectRenderer);
                } else {
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    /**
     * Returns light value based on cover or side covers.
     */
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
    	TEBase TE = getSimpleTileEntity(world, pos);
        if (TE != null) {
            return TE.getLightValue();
        }
        return 0;
    }

    @Override
    /**
     * Returns the block hardness at a location. Args: world, x, y, z
     */
    
    public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            Block b = BlockProperties.toBlock(is);
            if (b instanceof BlockCoverable) {
                return blockHardness;
            }
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getHardness(world, x, y, z, b, is.getItemDamage()) : b.getBlockHardness(world, pos);
        }

        return blockHardness;
    }

    @Override
    /**
     * Chance that fire will spread and consume this block.
     */
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing side) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (TE != null) {
            ItemStack is = BlockProperties.getFeatureSensitiveSideItemStack(TE, side);
            Block b = BlockProperties.toBlock(is);
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getFlammability(world, x, y, z, side, b, is.getItemDamage()) : Blocks.FIRE.getFlammability(b);
        }
        return super.getFlammability(world, pos, side);
    }

    @Override
    /**
     * Called when fire is updating on a neighbor block.
     */
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing side) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);
        if (TE != null) {
            ItemStack is = BlockProperties.getFeatureSensitiveSideItemStack(TE, side);
            Block b = BlockProperties.toBlock(is);
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getFireSpread(world, x, y, z, side, b, is.getItemDamage()) : Blocks.FIRE.getEncouragement(b);
        }

        return super.getFireSpreadSpeed(world, pos, side);
    }
    @Override
    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents fire from dying from rain.
     */
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, x, y, z);
        if (TE != null) {
            ItemStack is = BlockProperties.getFeatureSensitiveSideItemStack(TE, side);
            Block b = BlockProperties.toBlock(is);
            if (b instanceof BlockCoverable) {
                return false;
            }
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).sustainsFire(world, x, y, z, side, b, is.getItemDamage()) : b.isFireSource(world, pos, side);
        }        
        return false;
    }

    @Override
    /**
     * Location sensitive version of getExplosionRestance
     */
    
    public float getExplosionResistance(Entity exploder) {
    	World world;
    	BlockPos pos;
    	Entity entity;
    	double explosionX;
    	double explosionY;
    	double explosionZ;
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
		TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            Block b = BlockProperties.toBlock(is);
            if (b instanceof BlockCoverable) {
                return this.getExplosionResistance(entity);
            }
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getBlastResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ, b, is.getItemDamage()) : b.getExplosionResistance(entity);
        }

        return this.getExplosionResistance(entity);
    }
    

    @Override
    /**
     * Returns whether block is wood
     */
    public boolean isWood(IBlockAccess world, BlockPos pos) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            Block b = BlockProperties.toBlock(is);
            if (b instanceof BlockCoverable) {
                return true;
            }
            return b instanceof IWrappableBlock ? ((IWrappableBlock)b).isLog(world, x, y, z, b, is.getItemDamage()) : b.isWood(world, pos);
        }

        return super.isWood(world, pos);
    }
    

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     */
    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            Block block = BlockProperties.toBlock(BlockProperties.getCover(TE, 6));
            if (entity instanceof EntityWither) {
                return !block.equals(Blocks.BEDROCK) && !block.equals(Blocks.END_PORTAL) && !block.equals(Blocks.END_PORTAL_FRAME) && !block.equals(Blocks.COMMAND_BLOCK);
            } else if (entity instanceof EntityDragon) {
                return !block.equals(Blocks.OBSIDIAN) && !block.equals(Blocks.END_STONE) && !block.equals(Blocks.BEDROCK);
            }
        }

        return super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).onEntityCollidedWithBlock(world, pos, state, entity);
        }
    }

    @Override
    /**
     * Spawns EntityItem in the world for the given ItemStack if the world is not remote.
     */
    protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemStack)
    {
        // Clear metadata for Carpenter's blocks
        Block block = BlockProperties.toBlock(itemStack);
        if (block instanceof BlockCoverable) {
            itemStack.setItemDamage(0);
        }

        super.dropBlockAsItem(world, x, y, z, itemStack);
    }

    @Override
    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
    		ItemStack stack) {
    	// TODO Auto-generated method stub
    	super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    /**
     * Indicates whether block destruction should be suppressed when block is clicked.
     * Will return true if player is holding a Carpenter's tool in creative mode.
     */
    protected boolean suppressDestroyBlock(EntityPlayer entityPlayer)
    {
        if (entityPlayer == null) {
            return false;
        }

        ItemStack itemStack = entityPlayer.getHeldItemMainhand();

        if (itemStack != null) {
            Item item = itemStack.getItem();
            return entityPlayer.capabilities.isCreativeMode && item != null && (item instanceof IHammer || item instanceof IChisel);
        }

        return false;
    }

    /**
     * Drops block as {@link ItemStack} and notifies relevant systems of
     * block removal.  Block attributes will drop later in destruction.
     * <p>
     * This is usually called when a {@link #onNeighborBlockChange(World, int, int, int, Block) neighbor changes}.
     *
     * @param world the {@link World}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param dropBlock whether block {@link ItemStack} is dropped
     */
    protected void destroyBlock(World world, BlockPos pos, boolean dropBlock)
    {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
        // Drop attributes
        int metadata = dropBlock ? 0 : METADATA_DROP_ATTR_ONLY;
        ArrayList<ItemStack> items = getDrops(world, x, y, z, metadata, 0);
        for (ItemStack item : items) {
            dropBlockAsItem(world, x, y, z, item);
        }

        world.setBlockToAir(pos);
    }

    @Override
    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     *
     * Return true if the block is actually destroyed.
     *
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @return True if the block is actually destroyed.
     */
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
    		boolean willHarvest) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	if (world.isRemote) {
            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }

        // Grab drops while tile entity exists (before calling super)
        int metadata = player != null && player.capabilities.isCreativeMode ? METADATA_DROP_ATTR_ONLY : 0;
        ArrayList<ItemStack> items = getDrops(world, x, y, z, metadata, 0);

        // Drop attributes if block destroyed, and no Carpenter's Tool is held by entity
        if (!suppressDestroyBlock(player) && super.removedByPlayer(state, world, pos, player, willHarvest)) {
            for (ItemStack item : items) {
                dropBlockAsItem(world, x, y, z, item);
            }
            return true;
        }

        return false;
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    
    @Override
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	ArrayList<ItemStack> ret = (ArrayList<ItemStack>) super.getDrops(world,  pos, state, fortune); // Add block item drop
        TEBase TE = getSimpleTileEntity(world, pos);

        if (metadata == METADATA_DROP_ATTR_ONLY) {
            ret.clear(); // Remove block instance from drop list
        }

        if (TE != null)
        {
            for (int idx = 0; idx < 7; ++idx) {
                if (TE.hasAttribute(TE.ATTR_COVER[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_COVER[idx]));
                }
                if (TE.hasAttribute(TE.ATTR_OVERLAY[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_OVERLAY[idx]));
                }
                if (TE.hasAttribute(TE.ATTR_DYE[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_DYE[idx]));
                }
            }
            if (TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
                ret.add(TE.getAttributeForDrop(TE.ATTR_ILLUMINATOR));
            }
        }

        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    	TEBase TE = getTileEntity(world, pos);

        if (TE != null) {
            if (TE.hasAttribute(TE.ATTR_COVER[6])) {
                BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).randomDisplayTick(state, world, pos, rand);
            }
            if (TE.hasAttribute(TE.ATTR_OVERLAY[6])) {
                if (OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[6])).equals(Overlay.MYCELIUM)) {
                    Blocks.MYCELIUM.randomDisplayTick(state, world, pos, rand);
                }
            }
        }
    }

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a solid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param blockAccess The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param side The direction relative to the given position the plant wants to be, typically its UP
     * @param plantable The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side,
    		IPlantable plantable) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	TEBase TE = getTileEntity(world, pos);

        if (TE != null) {

            /* If side is not solid, it can't sustain a plant. */

            if (!isSideSolid(state, world, pos, side)) {
                return false;
            }

            /*
             * Add base block, top block, and both of their associated
             * overlays to judge whether plants can be supported on block.
             */

            List<Block> blocks = new ArrayList<Block>();

            for (int side1 = 1; side1 < 7; side1 += 5) {
                if (TE.hasAttribute(TE.ATTR_COVER[side1])) {
                    blocks.add(BlockProperties.toBlock(BlockProperties.getCover(TE, side1)));
                }
                if (TE.hasAttribute(TE.ATTR_OVERLAY[side1])) {
                    blocks.add(BlockProperties.toBlock(OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[side1])).getItemStack()));
                }
            }

            /* Add types using cover material */

            Material material = BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).getMaterial(state);
            if (material.equals(Material.GRASS)) {
                blocks.add(Blocks.GRASS);
            } else if (material.equals(Material.GROUND)) {
                blocks.add(Blocks.DIRT);
            } else if (material.equals(Material.SAND)) {
                blocks.add(Blocks.SAND);
            }

            switch (plantable.getPlantType(world, pos))
            {
                case Desert: return blocks.contains(Blocks.SAND);
                case Nether: return blocks.contains(Blocks.SOUL_SAND);
                case Plains: return blocks.contains(Blocks.GRASS) || blocks.contains(Blocks.DIRT);
                case Beach:
                    boolean isBeach = blocks.contains(Blocks.GRASS) || blocks.contains(Blocks.DIRT) || blocks.contains(Blocks.SAND);
                    boolean hasWater = world.getBlockState(pos).getMaterial() == Material.WATER;
                    return isBeach && hasWater;
                default:
                    break;
            }
        }

        return super.canSustainPlant(state, world, pos, side, plantable);
    }

    /**
     * Returns whether this block is considered solid.
     */
    protected boolean isBlockSolid(IBlockAccess blockAccess, BlockPos pos, IBlockState state)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            return !TE.hasAttribute(TE.ATTR_COVER[6]) || BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).isOpaqueCube(state);
        } else {
            return false;
        }
    }

    @Override
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving,
    		ItemStack stack) {
    	if (!world.isRemote) {

            TEBase TE = getTileEntity(world, pos);

            if (TE != null) {
                TE.setOwner(new ProtectedObject((EntityPlayer)entityLiving));
            }

        }
    }

    @Override
    /**
     * Gets the hardness of block at the given coordinates in the given world, relative to the ability of the given
     * EntityPlayer.
     */
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
    	/* Don't damage block if holding Carpenter's tool. */

        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack != null) {
            Item item = itemStack.getItem();
            if (item instanceof IHammer || item instanceof IChisel) {
                return -1;
            }
        }

        /* Return block hardness of cover. */

        TEBase TE = getTileEntity(world, pos);

        if (TE != null) {
            return ForgeHooks.blockStrength((IBlockState) BlockProperties.toBlock(BlockProperties.getCover(TE, 6)), player, world, pos);
        } else {
            return super.getPlayerRelativeBlockHardness(state, player, world, pos);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z)
    {
        TEBase TE = getTileEntity(blockAccess, x, y, z);
        if (TE != null)
        {
            ItemStack itemStack = BlockProperties.getCover(TE, 6);
            Block block = BlockProperties.toBlock(itemStack);
            if (!(block instanceof BlockCoverable)) {
                return block instanceof IWrappableBlock ? ((IWrappableBlock)block).getColorMultiplier(blockAccess, x, y, z, block, itemStack.getItemDamage()) : block.colorMultiplier(blockAccess, x, y, z);
            }
        }

        return super.colorMultiplier(blockAccess, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: world, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
    		EnumFacing side) {
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
    	// Side checks in out-of-range areas will crash
        if (y > 0 && y < blockAccess.getHeight())
        {
            TEBase TE = getTileEntity(blockAccess, x, y, z);
            if (TE != null) {
                EnumFacing side_src = EnumFacing.getOrientation(side);
                EnumFacing side_adj = side_src.getOpposite();

                TEBase TE_adj = (TEBase) blockAccess.getTileEntity(x, y, z);
                TEBase TE_src = (TEBase) blockAccess.getTileEntity(x + side_adj.offsetX, y + side_adj.offsetY, z + side_adj.offsetZ);

                if (TE_adj.getBlockType().isSideSolid(blockAccess, x, y, z, side_adj) == TE_src.getBlockType().isSideSolid(blockAccess, x + side_adj.offsetX, y + side_adj.offsetY, z + side_adj.offsetZ, EnumFacing.getOrientation(side))) {

                    if (shareFaces(TE_adj, TE_src, side_adj, side_src)) {

                        Block block_adj = BlockProperties.toBlock(BlockProperties.getCover(TE_adj, 6));
                        Block block_src = BlockProperties.toBlock(BlockProperties.getCover(TE_src, 6));

                        if (!TE_adj.hasAttribute(TE.ATTR_COVER[6])) {
                            return TE_src.hasAttribute(TE.ATTR_COVER[6]);
                        } else {
                            if (!TE_src.hasAttribute(TE.ATTR_COVER[6]) && block_adj.getRenderBlockPass() == 0) {
                                return !block_adj.isOpaqueCube();
                            } else if (TE_src.hasAttribute(TE.ATTR_COVER[6]) && block_src.isOpaqueCube() == block_adj.isOpaqueCube() && block_src.getRenderBlockPass() == block_adj.getRenderBlockPass()) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

    @Override
    /**
     * Determines if this block should render in this pass.
     */
    public boolean canRenderInPass(int pass)
    {
        ForgeHooksClient.setRenderPass(pass);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns which pass this block be rendered on. 0 for solids and 1 for alpha.
     */
    public int getRenderBlockPass()
    {
        /*
         * Alpha properties of block or cover depend on this returning a value
         * of 1, so it's the default value.  However, when rendering in player
         * hand we'll encounter sorting artifacts, and thus need to enforce
         * opaque rendering, or 0.
         */

        if (ForgeHooksClient.getWorldRenderPass() < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Returns whether two blocks share faces.
     * Primarily for slopes, stairs and slabs.
     */
    protected boolean shareFaces(TEBase TE_adj, TEBase TE_src, EnumFacing side_adj, EnumFacing side_src)
    {
        return TE_adj.getBlockType().isSideSolid(TE_adj.getWorld(), TE_adj.xCoord, TE_adj.yCoord, TE_adj.zCoord, side_adj) &&
               TE_src.getBlockType().isSideSolid(TE_src.getWorld(), TE_src.xCoord, TE_src.yCoord, TE_src.zCoord, side_src);
    }

    @Override
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube(IBlockState state) {
    	if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return false;
        }

        if (FeatureRegistry.enableRoutableFluids) {
            // Server condition may fail, so don't throw error if performing server-side
            try {
                Class<?> clazz = RoutableFluidsHelper.getCallerClass();
                if (clazz != null) {
                    for (Class clazz1 : RoutableFluidsHelper.liquidClasses) {
                        if (clazz.isAssignableFrom(clazz1)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {}
        }

        return false;
    }

    @Override
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Should block use the brightest neighbor light value as its own
     */
    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
    	return true;
    }

    @Override
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    	world.setTileEntity(pos, createNewTileEntity(world, 0));
    }

    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
    	return new TEBase();
    }
    
    @Override
    public boolean hasTileEntity() {
    	return true;
    }

    /**
     * This method is configured on as as-needed basis.
     * It's calling order is not guaranteed.
     */
    protected void preOnBlockClicked(TEBase TE, World world, int x, int y, int z, EntityPlayer entityPlayer, ActionResult actionResult) {}

    /**
     * Called before cover or decoration checks are performed.
     */
    protected void preOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ, ActionResult actionResult) {}

    /**
     * Called if cover and decoration checks have been performed but
     * returned no changes.
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ, ActionResult actionResult) {}

    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        return false;
    }

    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        return false;
    }

    protected void damageItemWithChance(World world, EntityPlayer entityPlayer)
    {
        Item item = entityPlayer.getHeldItemMainhand().getItem();

        if (item instanceof IHammer) {
            ((IHammer) item).onHammerUse(world, entityPlayer);
        } else if (item instanceof IChisel) {
            ((IChisel) item).onChiselUse(world, entityPlayer);
        }
    }

    /**
     * Returns whether side of block supports a cover.
     */
    protected boolean canCoverSide(TEBase TE, World world, int x, int y, int z, int side)
    {
        return side == 6;
    }

    /**
     * Allows a tile entity called during block activation to be changed before
     * altering attributes like cover, dye, overlay, etc.
     * <p>
     * Primarily offered for the garage door, when open, to swap the top piece
     * with the bottom piece for consistency.
     *
     * @param  TE the originating {@link TEBase}
     * @return a swapped in {@link TEBase}, or the passed in {@link TEBase}
     */
    protected TEBase getTileEntityForBlockActivation(TEBase TE)
    {
        return TE;
    }

}
