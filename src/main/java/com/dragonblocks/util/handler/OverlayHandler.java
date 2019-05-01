package com.dragonblocks.util.handler;

import java.util.HashMap;
import java.util.Map;

import com.dragonblocks.util.registry.FeatureRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OverlayHandler {

    public enum Overlay {
        NONE(new ItemStack(Block.air)),
        GRASS(new ItemStack(Blocks.grass)),
        SNOW(new ItemStack(Blocks.snow)),
        WEB(new ItemStack(Blocks.web)),
        VINE(new ItemStack(Blocks.vine)),
        HAY(new ItemStack(Blocks.hay_block)),
        MYCELIUM(new ItemStack(Blocks.mycelium));

        private ItemStack itemStack;

        private Overlay(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }

    public static Map overlayMap = new HashMap();

    /**
     * Initializes overlay definitions from configuration file.
     */
    public static void init()
    {
        for (String name : FeatureRegistry.overlayItems) {

            String itemName = name.substring(0, name.indexOf(":"));

            if (!overlayMap.containsKey(itemName)) {

                String overlayType = name.substring(name.indexOf(":") + 1).toLowerCase();

                if (overlayType.equals("grass")) {
                    overlayMap.put(itemName, Overlay.GRASS);
                } else if (overlayType.equals("snow")) {
                    overlayMap.put(itemName, Overlay.SNOW);
                } else if (overlayType.equals("web")) {
                    overlayMap.put(itemName, Overlay.WEB);
                } else if (overlayType.equals("vine")) {
                    overlayMap.put(itemName, Overlay.VINE);
                } else if (overlayType.equals("hay")) {
                    overlayMap.put(itemName, Overlay.HAY);
                } else if (overlayType.equals("mycelium")) {
                    overlayMap.put(itemName, Overlay.MYCELIUM);
                }

            }

        }
    }

    /**
     * Returns true if overlay covers a majority or all of side.
     */
    public static boolean coversFullSide(Overlay overlay, int side)
    {
        switch (overlay) {
            case GRASS:
            case SNOW:
            case HAY:
            case MYCELIUM:
                return side == 1;
            case WEB:
            case VINE:
                return true;
            default: {}
        }
        return true;
    }

    /**
     * Returns overlay from qualified ItemStack.
     */
    public static Overlay getOverlayType(ItemStack itemStack)
    {
        Object object = overlayMap.get(itemStack.getDisplayName());

        if (object == null) {
            object = overlayMap.get(ChatHandler.getDefaultTranslation(itemStack));
        }

        return object == null ? Overlay.NONE : (Overlay) object;
    }

}
