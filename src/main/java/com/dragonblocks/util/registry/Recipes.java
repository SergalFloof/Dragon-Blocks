package com.dragonblocks.util.registry;

import net.minecraft.item.Item;

public class Recipes {

    public static Item itemCarpentersHammer;
    public static Item itemCarpentersChisel;
    public static Item itemCarpentersDoor;
    public static Item itemCarpentersBed;
    public static Item itemCarpentersTile;

    public static boolean enableHammer                  = true;
    public static boolean enableChisel                  = true;
    public static boolean enableTile                    = true;
    public static int     itemCarpentersToolsUses       = 400;
    public static boolean itemCarpentersToolsDamageable = true;

    public static double itemHammerDamageChanceFromSlopes      = 0.75D;
    public static double itemHammerDamageChanceFromStairs      = 1.0D;
    public static double itemHammerDamageChanceFromCollapsible = 0.2D;

    /**
     * Registers item IDs.
     */
//    public static void preInit(FMLPreInitializationEvent event, Configuration config)
//    {
//        enableHammer                  = config.get("items",            "Enable Hammer",                  enableHammer).getBoolean(enableHammer);
//        enableChisel                  = config.get("items",            "Enable Chisel",                  enableChisel).getBoolean(enableChisel);
//        enableTile                    = config.get("items",              "Enable Tile",                    enableTile).getBoolean(enableTile);
//        itemCarpentersToolsUses       = config.get("items",        "Vanilla Tool Uses",       itemCarpentersToolsUses).getInt(itemCarpentersToolsUses);
//        itemCarpentersToolsDamageable = config.get("items", "Vanilla Tools Damageable", itemCarpentersToolsDamageable).getBoolean(itemCarpentersToolsDamageable);
//
//        recipeQuantityTile = config.get("recipe quantities", "Tile", recipeQuantityTile).getInt(recipeQuantityTile);
//
//        itemHammerDamageChanceFromSlopes      = config.get("items",      "itemHammerDamageChanceFromSlopes",      itemHammerDamageChanceFromSlopes).getDouble(     itemHammerDamageChanceFromSlopes);
//        itemHammerDamageChanceFromStairs      = config.get("items",      "itemHammerDamageChanceFromStairs",      itemHammerDamageChanceFromStairs).getDouble(     itemHammerDamageChanceFromStairs);
//        itemHammerDamageChanceFromCollapsible = config.get("items", "itemHammerDamageChanceFromCollapsible", itemHammerDamageChanceFromCollapsible).getDouble(itemHammerDamageChanceFromCollapsible);
//
//        registerItems();
//    }

  

    
//    private static void registerRecipes()
//    {
//        if (enableHammer) {
//            GameRegistry.addRecipe(new ItemStack(itemCarpentersHammer, 1), new Object[] { "XX ", " YX", " Y ", 'X', Items.iron_ingot, 'Y', BlockRegistry.blockCarpentersBlock });
//        }
//        if (enableChisel) {
//            GameRegistry.addRecipe(new ItemStack(itemCarpentersChisel, 1), new Object[] { "X", "Y", 'X', Items.iron_ingot, 'Y', BlockRegistry.blockCarpentersBlock });
//        }
//        if (enableTile) {
//            GameRegistry.addRecipe(new ItemStack(itemCarpentersTile, recipeQuantityTile), new Object[] { "XXX", "YYY", 'X', Blocks.hardened_clay, 'Y', BlockRegistry.blockCarpentersBlock });
//        }
//        if (BlockRegistry.enableDoor) {
//            GameRegistry.addRecipe(new ItemStack(itemCarpentersDoor, BlockRegistry.recipeQuantityDoor), new Object[] { "XX", "XX", "XX", 'X', BlockRegistry.blockCarpentersBlock });
//        }
//        if (BlockRegistry.enableBed) {
//            GameRegistry.addRecipe(new ItemStack(itemCarpentersBed, BlockRegistry.recipeQuantityBed), new Object[] { "XXX", "YYY", 'X', Blocks.wool, 'Y', BlockRegistry.blockCarpentersBlock });
//        }
//    }

}
