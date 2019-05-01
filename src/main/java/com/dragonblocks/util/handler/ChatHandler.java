package com.dragonblocks.util.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatHandler {

    /**
     * Sends a chat message to a player.
     */
    public static void sendMessageToPlayer(String string, EntityPlayer entityPlayer)
    {
        sendMessageToPlayer(string, entityPlayer, true);
    }

    /**
     * Sends a chat message to a player with style override.
     */
    public static void sendMessageToPlayer(String string, EntityPlayer entityPlayer, boolean styled)
    {
    	TextComponentTranslation chat = new TextComponentTranslation(string, new Object[0]);

        if (styled) {
            chat.getStyle().setColor(TextFormatting.GOLD);
        }

        entityPlayer.sendMessage(chat);
    }

    /**
     * Gets full unlocalized name for ItemStack (e.g. tile.dirt.name)
     *
     * @param  itemStack the ItemStack
     * @return the full unlocalized string
     */
    public static String getUnlocalizedNameEfficiently(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItem() != null) {
            return itemStack.getItem().getUnlocalizedNameInefficiently(itemStack) + ".name";
        }

        return "";
    }

}
