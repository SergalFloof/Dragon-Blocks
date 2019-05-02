package com.dragonblocks.entity.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dragonblocks.api.IHammer;
import com.dragonblocks.util.BlockProperties;
import com.dragonblocks.util.handler.DesignHandler;
import com.dragonblocks.util.handler.DyeHandler;
import com.dragonblocks.util.protection.PlayerPermissions;
import com.dragonblocks.util.registry.ItemInit;

public class EntityCarpentersTile extends EntityBase {

    private int ticks;
    private boolean boundsSet;

    private final static byte ID_DIRECTION = 13;
    private final static byte ID_DYE       = 14;
    private final static byte ID_DESIGN    = 15;
    private final static byte ID_ROTATION  = 16;

    private final static String TAG_DESIGN    = "tile"; // Description holding for compatibility with pre-3.2.0
    private final static String TAG_DIRECTION = "dir";
    private final static String TAG_DYE       = "dye";
    private final static String TAG_ROTATION  = "rot";

    /** Depth of tile. */
    private final static double depth = 0.0625D;

    private final static double[][] bounds =
    {
            {         0.0D, 1.0D - depth,         0.0D,  1.0D,  1.0D,  1.0D },
            {         0.0D,         0.0D,         0.0D,  1.0D, depth,  1.0D },
            {         0.0D,         0.0D, 1.0D - depth,  1.0D,  1.0D,  1.0D },
            {         0.0D,         0.0D,         0.0D,  1.0D,  1.0D, depth },
            { 1.0D - depth,         0.0D,         0.0D,  1.0D,  1.0D,  1.0D },
            {         0.0D,         0.0D,         0.0D, depth,  1.0D,  1.0D }
    };

    public EntityCarpentersTile(World world)
    {
        super(world);
    }

    public EntityCarpentersTile(EntityPlayer entityPlayer, World world, int x, int y, int z, EnumFacing dir, EnumFaceDirection offset_side, boolean ignoreNeighbors)
    {
        super(world, entityPlayer);
        posX = x;
        posY = y;
        posZ = z;
        setDirection(dir);
        setBoundingBox();

        if (!ignoreNeighbors) {

            List<EntityCarpentersTile> list = new ArrayList<EntityCarpentersTile>();
            double factor = 0.2D;

            boundingBox.contract(0.1D, 0.1D, 0.1D);

            switch (offset_side) {
                case DOWN:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(0.0D, -factor, 0.0D));
                    break;
                case UP:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(0.0D, factor, 0.0D));
                    break;
                case NORTH:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(0.0D, 0.0D, -factor));
                    break;
                case SOUTH:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(0.0D, 0.0D, factor));
                    break;
                case WEST:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(-factor, 0.0D, 0.0D));
                    break;
                case EAST:
                    list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.offset(factor, 0.0D, 0.0D));
                    break;
                default:

                    switch (dir) {
                        case DOWN:
                        case UP:
                            list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.expand(factor, 0.0D, factor));
                            break;
                        case NORTH:
                        case SOUTH:
                            list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.expand(factor, factor, 0.0D));
                            break;
                        case WEST:
                        case EAST:
                            list = world.getEntitiesWithinAABB(EntityCarpentersTile.class, boundingBox.expand(0.0D, factor, factor));
                            break;
                        default: {}
                    }

            }

            for (EntityCarpentersTile tile : list)
            {
                /* Skip checking diagonal tiles when tile is placed in center. */

                if (offset_side.equals(ForgeDirection.UNKNOWN))
                {
                    switch (dir) {
                        case DOWN:
                        case UP:
                            if (!(tile.posX == posX || tile.posZ == posZ)) {
                                continue;
                            }
                            break;
                        case NORTH:
                        case SOUTH:
                            if (!(tile.posX == posX || tile.posY == posY)) {
                                continue;
                            }
                            break;
                        case WEST:
                        case EAST:
                            if (!(tile.posZ == posZ || tile.posY == posY)) {
                                continue;
                            }
                            break;
                        default: {}
                    }
                }

                /* Match up tile properties with neighbor. */

                if (!tile.getDye().equals(getDefaultDye())) {
                    setDye(tile.getDye());
                }
                if (tile.getRotation() != 0) {
                    setRotation(tile.getRotation());
                }
                if (tile.hasDesign()) {
                    setDesign(tile.getDesign());
                }
            }

        }
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    @Override
    @SideOnly(Side.CLIENT)
    protected void preparePlayerToSpawn() { }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    @Override
    protected void setSize(float width, float height) { }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    @Override
    public void setPosition(double x, double y, double z) { }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double posX, double posY, double posZ, float yaw, float pitch, int par9) { }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    @Override
    public void mountEntity(Entity entity) { }

    @Override
    protected boolean func_145771_j(double x, double y, double z)
    {
        return false;
    }

    public String getDefaultDye()
    {
        return "dyeWhite";
    }

    public void playTileSound()
    {
        BlockProperties.playBlockSound(worldObj, new ItemStack(Blocks.hardened_clay), (int) Math.floor(posX), (int) Math.floor(posY), (int) Math.floor(posZ), true);
    }

    public void playDyeSound()
    {
        BlockProperties.playBlockSound(world, new ItemStack(Blocks.SAND), (int) Math.floor(posX), (int) Math.floor(posY), (int) Math.floor(posZ), true);
    }

    public double[] getBounds()
    {
        return bounds[getDataWatcher().getWatchableObjectInt(ID_DIRECTION)];
    }

    public void setBoundingBox()
    {
        double bounds[] = getBounds();
        boundingBox.setBounds(posX + bounds[0], posY + bounds[1], posZ + bounds[2], posX + bounds[3], posY + bounds[4], posZ + bounds[5]);
    }

    public EnumFacing getDirection()
    {
        return EnumFacing.getOrientation(getDataWatcher().getWatchableObjectInt(ID_DIRECTION));
    }

    public void setDirection(EnumFacing dir)
    {
        getDataWatcher().updateObject(ID_DIRECTION, new Integer(dir.ordinal()));
    }

    public void setRotation(int rotation)
    {
        getDataWatcher().updateObject(ID_ROTATION, new Integer(rotation));
    }

    public void rotate()
    {
        int rotation = getRotation();
        setRotation(++rotation & 3);
    }

    public int getRotation()
    {
        return getDataWatcher().getWatchableObjectInt(ID_ROTATION);
    }

    public void setDye(String dye)
    {
        getDataWatcher().updateObject(ID_DYE, new String(dye));
    }

    public String getDye()
    {
        return getDataWatcher().getWatchableObjectString(ID_DYE);
    }

    public boolean hasDesign()
    {
        return DesignHandler.listTile.contains(getDesign());
    }

    public void setDesign(String tile)
    {
        getDataWatcher().updateObject(ID_DESIGN, new String(tile));
    }

    public String getDesign()
    {
        return getDataWatcher().getWatchableObjectString(ID_DESIGN);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        getDataWatcher().updateObject(ID_DESIGN, String.valueOf(nbtTagCompound.getString(TAG_DESIGN)));
        getDataWatcher().updateObject(ID_DYE, String.valueOf(nbtTagCompound.getString(TAG_DYE)));
        getDataWatcher().updateObject(ID_DIRECTION, Integer.valueOf(nbtTagCompound.getInteger(TAG_DIRECTION)));
        getDataWatcher().updateObject(ID_ROTATION, Integer.valueOf(nbtTagCompound.getInteger(TAG_ROTATION)));
        super.readEntityFromNBT(nbtTagCompound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setString(TAG_DESIGN, getDataWatcher().getWatchableObjectString(ID_DESIGN));
        nbtTagCompound.setString(TAG_DYE, getDataWatcher().getWatchableObjectString(ID_DYE));
        nbtTagCompound.setInteger(TAG_DIRECTION, getDataWatcher().getWatchableObjectInt(ID_DIRECTION));
        nbtTagCompound.setInteger(TAG_ROTATION, getDataWatcher().getWatchableObjectInt(ID_ROTATION));
        super.writeEntityToNBT(nbtTagCompound);
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity entity)
    {
        if (entity instanceof EntityPlayer) {

            EntityPlayer entityPlayer = (EntityPlayer) entity;
            ItemStack itemStack = entityPlayer.getHeldItemMainhand();

            boolean hasHammer = false;

            if (itemStack != null) {
                Item item = itemStack.getItem();

                if (item instanceof IHammer) {
                    hasHammer = true;
                }
            }

            if (entityPlayer.capabilities.isCreativeMode && !hasHammer) {
                return;
            }

        }

        entityDropItem(getItemDrop(), 0.0F);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        if (!boundsSet) {
            setBoundingBox();
            boundsSet = true;
        }

        if (!world.isRemote) {

            if (ticks++ >= 20) {

                ticks = 0;

                if (!isDead && !onValidSurface()) {
                    setDead();
                    onBroken((Entity)null);
                }

            }

        }
    }

    /**
     * Returns representative ItemStack for entity.
     */
    private ItemStack getItemDrop()
    {
        return new ItemStack(ItemInit.itemTile);
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return getItemDrop();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        // Has absolutely no effect
        return pass == 0;
    }

    /**
     * Returns true if tile is on a valid surface.
     */
    public boolean onValidSurface()
    {
        EnumFacing dir = getDirection();

        int x_offset = MathHelper.floor(posX) - dir.getFrontOffsetX();
        int y_offset = MathHelper.floor(posY) - dir.getFrontOffsetY();
        int z_offset = MathHelper.floor(posZ) - dir.getFrontOffsetZ();

        return world.getBlock(x_offset, y_offset, z_offset).isSideSolid(world, x_offset, y_offset, z_offset, dir);
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2)
    {
        if (!world.isRemote) {

            Entity entity = damageSource.getTrueSource();

            boolean dropItem = false;

            if (entity instanceof EntityPlayer && PlayerPermissions.hasElevatedPermission(this, (EntityPlayer)entity, false)) {

                EntityPlayer entityPlayer = (EntityPlayer) entity;
                ItemStack itemStack = entityPlayer.getHeldItemMainhand();

                if (itemStack != null) {

                    if (itemStack.getItem() instanceof IHammer) {
                        if (entity.isSneaking()) {
                            if (!isDead) {
                                dropItem = true;
                            }
                        } else {
                            setDesign(DesignHandler.getNext("tile", getDesign()));
                        }
                    } else if (entityPlayer.capabilities.isCreativeMode) {
                        if (!isDead) {
                            dropItem = true;
                        }
                    }

                }

            }

            playTileSound();

            if (dropItem)
            {
                setDead();
                setBeenAttacked();
                onBroken(damageSource.getTrueSource());
                return true;
            }

        }

        return false;
    }

    @Override
    /**
     * First layer of player interaction.
     */
    public boolean interactFirst(EntityPlayer entityPlayer)
    {
        if (world.isRemote) {

            return true;

        } else if (PlayerPermissions.hasElevatedPermission(this, entityPlayer, false)) {

            ItemStack itemStack = entityPlayer.getHeldItemMainhand();

            if (itemStack != null) {

                if (itemStack.getItem() instanceof IHammer) {

                    if (entityPlayer.isSneaking()) {
                        rotate();
                    } else {
                        setDesign(DesignHandler.getPrev("tile", getDesign()));
                    }

                    playTileSound();

                } else if (BlockProperties.isDye(itemStack, true)) {

                    if (entityPlayer.isSneaking()) {

                        String dye = DyeHandler.getOreDictName(itemStack);

                        if (!getDye().equals(dye)) {
                            setDye(DyeHandler.getOreDictName(itemStack));
                            playDyeSound();
                        }

                    }

                }

                return true;
            }

        }

        return false;
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    @Override
    public void moveEntity(double x, double y, double z)
    {
        if (!world.isRemote && !isDead && x * x + y * y + z * z > 0.0D)
        {
            setDead();
            onBroken((Entity)null);
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    @Override
    public void addVelocity(double x, double y, double z)
    {
        if (!world.isRemote && !isDead && x * x + y * y + z * z > 0.0D)
        {
            setDead();
            onBroken((Entity)null);
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataWatcher().addObject(ID_DESIGN, new String(""));
        getDataWatcher().addObject(ID_DYE, new String("dyeWhite"));
        getDataWatcher().addObject(ID_DIRECTION, new Integer(0));
        getDataWatcher().addObject(ID_ROTATION, new Integer(0));
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * returns the bounding box for this entity
     */
    
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox() {
//    	return boundingBox;
//    }
//    @Override
//    public AxisAlignedBB getBoundingBox()
//    {
//        return boundingBox;
//    }

    @Override
    public float getCollisionBorderSize()
    {
        return 0.0F;
    }

    @Override
    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }

}
