package com.dragonblocks.proxy;

import com.dragonblocks.Main;
import com.dragonblocks.util.handler.DesignHandler;
import com.dragonblocks.util.handler.EventHandler;
import com.dragonblocks.util.handler.OverlayHandler;
import com.dragonblocks.util.handler.PacketHandler;
import com.dragonblocks.util.registry.BlockRegistry;
import com.dragonblocks.util.registry.FeatureRegistry;
import com.dragonblocks.util.registry.ItemInit;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public void registerItemRenderer(Item item, int meta, String id) {};

    public final static int ENTITY_ID_TILE = 0;

    public void preInit(FMLPreInitializationEvent event, Configuration config)
    {
        FeatureRegistry.preInit(event, config); // Do before block and item registration
        BlockRegistry.preInit(event, config); // Do before item registration
        ItemInit.preInit(event, config);
        DesignHandler.preInit(event);
    }

    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        Main.channel.register(new PacketHandler());

        /* Initialize blocks and items */

        BlockRegistry.init(event);
        ItemInit.init(event);

        if (FeatureRegistry.enableOverlays) {
            OverlayHandler.init();
        }

        /* Register tile entities */

//        GameRegistry.registerTileEntity(                      TEBase.class,            "TileEntityCarpentersSlope"); // Compatibility mapping
//        GameRegistry.registerTileEntity(                      TEBase.class,              "TileEntityCarpentersBed"); // Compatibility mapping
//        GameRegistry.registerTileEntity(                      TEBase.class,            "TileEntityCarpentersBlock");
//        GameRegistry.registerTileEntity(  TECarpentersDaylightSensor.class,              "TileEntityCarpentersExt"); // Compatibility mapping
//        GameRegistry.registerTileEntity(  TECarpentersDaylightSensor.class,   "TileEntityCarpentersDaylightSensor");
//        GameRegistry.registerTileEntity(       TECarpentersFlowerPot.class,        "TileEntityCarpentersFlowerPot");
//        GameRegistry.registerTileEntity(            TECarpentersSafe.class,             "TileEntityCarpentersSafe");
//        GameRegistry.registerTileEntity(           TECarpentersTorch.class,            "TileEntityCarpentersTorch");
//        GameRegistry.registerTileEntity(      TECarpentersGarageDoor.class,       "TileEntityCarpentersGarageDoor");
//
//        /* Register entities */
//
//        if (ItemRegistry.enableTile) {
//            EntityRegistry.registerModEntity(EntityCarpentersTile.class, "CarpentersTile", ENTITY_ID_TILE, CarpentersBlocks.instance, 64, 999, false);
//        }
    }

}
