package com.dragonblocks.objects.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.dragonblocks.Main;
import com.dragonblocks.data.Hinge;
import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;
import com.dragonblocks.util.registry.BlockRegistry;
import com.dragonblocks.util.registry.ItemInit;

public class ItemDoor extends ItemBlock {

    public ItemDoor(String name)
    {
    	setUnlocalizedName(name);
		setRegistryName(name);
        setMaxStackSize(64);
        setCreativeTab(Main.dragonblocks);
        
        ItemInit.ITEMS.add(this);
    }
    /**
     * Callback for item usage. If the item does something special on right clicking, it will have one of these. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer entityPlayer, World world, BlockPos pos, EnumHand hand,
    		EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if (side == 1) {

            ++y;

            if (
                    y < 255                                                                                    &&
                    entityPlayer.canPlayerEdit(x, y, z, side, itemStack)                                       &&
                    entityPlayer.canPlayerEdit(x, y + 1, z, side, itemStack)                                   &&
                    world.isAirBlock(x, y, z)                                                                  &&
                    world.isAirBlock(x, y + 1, z)                                                              &&
                    World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)                                     &&
                    placeBlock(world, BlockRegistry.blockCarpentersDoor, entityPlayer, itemStack, x, y, z)     &&
                    placeBlock(world, BlockRegistry.blockCarpentersDoor, entityPlayer, itemStack, x, y + 1, z)
                    )
            {
                int facing = MathHelper.floor_double((entityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;

                /* Create bottom door piece. */

                TEBase TE = (TEBase) world.getTileEntity(x, y, z);
                Hinge.setFacing(TE, facing);
                Hinge.setHingeSide(TE, getHingePoint(TE, BlockRegistry.blockCarpentersDoor));
                Hinge.setPiece(TE, Hinge.PIECE_BOTTOM);

                /* Match door type and rigidity with adjacent type if possible. */

                TEBase TE_XN = world.getBlock(x - 1, y, z).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x - 1, y, z) : null;
                TEBase TE_XP = world.getBlock(x + 1, y, z).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x + 1, y, z) : null;
                TEBase TE_ZN = world.getBlock(x, y, z - 1).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x, y, z - 1) : null;
                TEBase TE_ZP = world.getBlock(x, y, z + 1).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x, y, z + 1) : null;

                int type = 0;
                if (TE_XN != null) {
                    Hinge.setType(TE, Hinge.getType(TE_XN));
                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_XN));
                    type = Hinge.getType(TE_XN);
                } else if (TE_XP != null) {
                    Hinge.setType(TE, Hinge.getType(TE_XP));
                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_XP));
                    type = Hinge.getType(TE_XP);
                } else if (TE_ZN != null) {
                    Hinge.setType(TE, Hinge.getType(TE_ZN));
                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_ZN));
                    type = Hinge.getType(TE_ZN);
                } else if (TE_ZP != null) {
                    Hinge.setType(TE, Hinge.getType(TE_ZP));
                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_ZP));
                    type = Hinge.getType(TE_ZP);
                }

                /* Create top door piece. */

                TEBase TE_YP = (TEBase) world.getTileEntity(x, y + 1, z);
                Hinge.setFacing(TE_YP, facing);
                Hinge.setType(TE_YP, type);
                Hinge.setHingeSide(TE_YP, Hinge.getHinge(TE));
                Hinge.setPiece(TE_YP, Hinge.PIECE_TOP);
                Hinge.setRigidity(TE_YP, Hinge.getRigidity(TE));

                BlockProperties.playBlockSound(world, new ItemStack(BlockRegistry.blockCarpentersDoor), x, y, z, false);

                if (!entityPlayer.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
                }

                return true;
            }

        }

        return false;
    }


//    @Override
//   
//    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
//    {
//        if (side == 1) {
//
//            ++y;
//
//            if (
//                    y < 255                                                                                    &&
//                    entityPlayer.canPlayerEdit(x, y, z, side, itemStack)                                       &&
//                    entityPlayer.canPlayerEdit(x, y + 1, z, side, itemStack)                                   &&
//                    world.isAirBlock(x, y, z)                                                                  &&
//                    world.isAirBlock(x, y + 1, z)                                                              &&
//                    World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)                                     &&
//                    placeBlock(world, BlockRegistry.blockCarpentersDoor, entityPlayer, itemStack, x, y, z)     &&
//                    placeBlock(world, BlockRegistry.blockCarpentersDoor, entityPlayer, itemStack, x, y + 1, z)
//                    )
//            {
//                int facing = MathHelper.floor_double((entityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;
//
//                /* Create bottom door piece. */
//
//                TEBase TE = (TEBase) world.getTileEntity(x, y, z);
//                Hinge.setFacing(TE, facing);
//                Hinge.setHingeSide(TE, getHingePoint(TE, BlockRegistry.blockCarpentersDoor));
//                Hinge.setPiece(TE, Hinge.PIECE_BOTTOM);
//
//                /* Match door type and rigidity with adjacent type if possible. */
//
//                TEBase TE_XN = world.getBlock(x - 1, y, z).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x - 1, y, z) : null;
//                TEBase TE_XP = world.getBlock(x + 1, y, z).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x + 1, y, z) : null;
//                TEBase TE_ZN = world.getBlock(x, y, z - 1).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x, y, z - 1) : null;
//                TEBase TE_ZP = world.getBlock(x, y, z + 1).equals(BlockRegistry.blockCarpentersDoor) ? (TEBase) world.getTileEntity(x, y, z + 1) : null;
//
//                int type = 0;
//                if (TE_XN != null) {
//                    Hinge.setType(TE, Hinge.getType(TE_XN));
//                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_XN));
//                    type = Hinge.getType(TE_XN);
//                } else if (TE_XP != null) {
//                    Hinge.setType(TE, Hinge.getType(TE_XP));
//                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_XP));
//                    type = Hinge.getType(TE_XP);
//                } else if (TE_ZN != null) {
//                    Hinge.setType(TE, Hinge.getType(TE_ZN));
//                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_ZN));
//                    type = Hinge.getType(TE_ZN);
//                } else if (TE_ZP != null) {
//                    Hinge.setType(TE, Hinge.getType(TE_ZP));
//                    Hinge.setRigidity(TE, Hinge.getRigidity(TE_ZP));
//                    type = Hinge.getType(TE_ZP);
//                }
//
//                /* Create top door piece. */
//
//                TEBase TE_YP = (TEBase) world.getTileEntity(x, y + 1, z);
//                Hinge.setFacing(TE_YP, facing);
//                Hinge.setType(TE_YP, type);
//                Hinge.setHingeSide(TE_YP, Hinge.getHinge(TE));
//                Hinge.setPiece(TE_YP, Hinge.PIECE_TOP);
//                Hinge.setRigidity(TE_YP, Hinge.getRigidity(TE));
//
//                BlockProperties.playBlockSound(world, new ItemStack(BlockRegistry.blockCarpentersDoor), x, y, z, false);
//
//                if (!entityPlayer.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
//                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
//                }
//
//                return true;
//            }
//
//        }
//
//        return false;
//    }

    /**
     * Returns a hinge point allowing double-doors if a matching neighboring door is found.
     * It returns the default hinge point if no neighboring doors are found.
     */
    private int getHingePoint(TEBase TE, Block block)
    {
        int facing = Hinge.getFacing(TE);
        Hinge.getHinge(TE);
        Hinge.getState(TE);
        int piece = Hinge.getPiece(TE);

        World world = TE.getWorld();

        TEBase TE_ZN = world.getBlock(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ() - 1).equals(block) ? (TEBase) world.getTileEntity(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ() - 1) : null;
        TEBase TE_ZP = world.getBlock(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ() + 1).equals(block) ? (TEBase) world.getTileEntity(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ() + 1) : null;
        TEBase TE_XN = world.getBlock(TE.getPos().getX() - 1, TE.getPos().getY(), TE.getPos().getZ()).equals(block) ? (TEBase) world.getTileEntity(TE.getPos().getX() - 1, TE.getPos().getY(), TE.getPos().getZ()) : null;
        TEBase TE_XP = world.getBlock(TE.getPos().getX() + 1, TE.getPos().getY(), TE.getPos().getZ()).equals(block) ? (TEBase) world.getTileEntity(TE.getPos().getX() + 1, TE.getPos().getY(), TE.getPos().getZ()) : null;

        switch (facing)
        {
            case Hinge.FACING_XN:

                if (TE_ZP != null) {
                    if (piece == Hinge.getPiece(TE_ZP) && facing == Hinge.getFacing(TE_ZP) && Hinge.getHinge(TE_ZP) == Hinge.HINGE_LEFT) {
                        return Hinge.HINGE_RIGHT;
                    }
                }

                break;
            case Hinge.FACING_XP:

                if (TE_ZN != null) {
                    if (piece == Hinge.getPiece(TE_ZN) && facing == Hinge.getFacing(TE_ZN) && Hinge.getHinge(TE_ZN) == Hinge.HINGE_LEFT) {
                        return Hinge.HINGE_RIGHT;
                    }
                }

                break;
            case Hinge.FACING_ZN:

                if (TE_XN != null) {
                    if (piece == Hinge.getPiece(TE_XN) && facing == Hinge.getFacing(TE_XN) && Hinge.getHinge(TE_XN) == Hinge.HINGE_LEFT) {
                        return Hinge.HINGE_RIGHT;
                    }
                }

                break;
            case Hinge.FACING_ZP:

                if (TE_XP != null) {
                    if (piece == Hinge.getPiece(TE_XP) && facing == Hinge.getFacing(TE_XP) && Hinge.getHinge(TE_XP) == Hinge.HINGE_LEFT) {
                        return Hinge.HINGE_RIGHT;
                    }
                }

                break;
        }

        return Hinge.HINGE_LEFT;
    }

}
