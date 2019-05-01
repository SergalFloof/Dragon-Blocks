package com.dragonblocks.util.protection;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import com.dragonblocks.util.registry.FeatureRegistry;

public class PlayerPermissions {

    /**
     * Returns true if player is operator.
     * Can only return true if called server-side.
     */
    public static boolean isOp(EntityPlayer entityPlayer)
    {
        if (!entityPlayer.world.isRemote) {
            return ((EntityPlayerMP)entityPlayer).mcServer.getPlayerList().canSendCommands(entityPlayer.getGameProfile());
        } else {
            return false;
        }
    }

    /**
     * Whether player is an operator, owner or is in a singleplayer server.
     *
     * @param  object the {@link IProtected} block or entity
     * @param  entityPlayer the {@link EntityPlayer}
     * @param  enforceOwnership whether ownership is required, bypassing configuration settings
     * @return <code>true</code> if player has elevated permission
     */
    public static boolean hasElevatedPermission(IProtected object, EntityPlayer entityPlayer, boolean enforceOwnership)
    {
        if (entityPlayer.world.isRemote && Minecraft.getMinecraft().isSingleplayer()) { // Check if client is playing singleplayer
            return true;
        } else if (!entityPlayer.world.isRemote && Minecraft.getMinecraft().isSingleplayer()) { // Check if server is integrated (singleplayer)
            return true;
        } else if (isOp(entityPlayer)) {
            return true;
        } else if (!enforceOwnership && !FeatureRegistry.enableOwnership) {
            return true;
        } else {
            return isOwner(object, entityPlayer);
        }
    }

    /**
     * Whether the player is the owner of the object.
     *
     * @param object
     * @param entityPlayer
     * @return <code>true</code> if player is owner
     */
    private static boolean isOwner(IProtected object, EntityPlayer entityPlayer)
    {
        try {
            UUID.fromString(object.getOwner());
            return object.getOwner().equals(entityPlayer.getUniqueID().toString());
        } catch (IllegalArgumentException e) {
            return object.getOwner().equals(entityPlayer.getDisplayName());
        }
    }

}
