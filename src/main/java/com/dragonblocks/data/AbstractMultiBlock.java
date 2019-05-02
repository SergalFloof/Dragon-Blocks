package com.dragonblocks.data;

import java.util.HashSet;
import java.util.Set;

import com.dragonblocks.tileentity.TEBase;
import com.dragonblocks.util.BlockProperties;
import com.dragonblocks.util.registry.FeatureRegistry;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

public abstract class AbstractMultiBlock {

    /**
     * Gathers adjacent blocks based on specific criteria, enabling
     * a group of blocks to act as a single entity when interacting
     * with only a single block.
     *
     * @param TE the {@link TEBase}
     * @param block the {@link Block} to match against
     * @return a {@link Set} of parts
     * @see {@link #getMatchingDataPattern}
     */
    public final Set<TEBase> getBlocks(TEBase TE, Block block)
    {
        Set<TEBase> set = new HashSet<TEBase>();
        int matchData = getMatchingDataPattern(TE);
        EnumFacing[] dirs = getLocateDirs(TE);
        addAndLocateBlocks(TE, block, matchData, dirs, set);
        return set;
    }

    /**
     * Adds {@link TEBase} object to set, and continues locating additional
     * blocks that are part of structure.
     *
     * @param TE the {@link TEBase}
     * @param block the {@link Block} type to locate
     * @param matchData the {@link #getMatchingDataPattern data mask} to match against
     * @param dirs the {@link #getLocateDirs directions} to search
     * @param set the {@link Set} of blocks
     */
    private void addAndLocateBlocks(TEBase TE, Block block, int matchData, EnumFacing[] dirs, Set<TEBase> set)
    {
        if (set.size() > FeatureRegistry.multiBlockSizeLimit) {
            return;
        }

        if (set.contains(TE)) {
            return;
        }

        // Add piece to final set
        set.add(TE);

        // Locate additional blocks
        for (EnumFacing dir : dirs) {
            TEBase TE_adj = BlockProperties.getTileEntity(block, TE.getWorld(), TE.getPos().getX() - dir.getFrontOffsetX(), TE.getPos().getY() - dir.getFrontOffsetY(), TE.getPos().getZ() - dir.getFrontOffsetZ());
            if (TE_adj != null && (TE_adj.getData() & matchData) == matchData) {
                addAndLocateBlocks(TE_adj, block, matchData, dirs, set);
            }
        }
    }

    /**
     * Used to match against adjacent block data when determining
     * whether a connection can be made.
     * <p>
     * Supplies an integer mask to logical AND with candidate
     * block data.
     *
     * @return an integer
     */
    abstract public int getMatchingDataPattern(TEBase TE);

    /**
     * Grabs an array of valid {@link ForgeDirection ForgeDirections}
     * used when locating additional pieces for block in a
     * two-dimensional space.
     *
     * @param TE the {@link TEBase}
     * @return an array of supported {@link ForgeDirection ForgeDirections}
     */
    abstract public EnumFacing[] getLocateDirs(TEBase TE);

}
