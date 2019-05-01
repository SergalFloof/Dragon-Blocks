package com.dragonblocks.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dragonblocks.CarpentersBlocksCachedResources;
import com.dragonblocks.entity.item.EntityCarpentersTile;
import com.dragonblocks.renderer.entity.RenderCarpentersTile;
import com.dragonblocks.util.Reference;
import com.dragonblocks.util.handler.ShadersHandler;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}
	
	@Override
	public void registerVariantRenderer(Item item, int meta, String filename, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(Reference.MODID, filename), id));
	}

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
//        MinecraftForge.EVENT_BUS.register(new IconRegistry());
        CarpentersBlocksCachedResources.INSTANCE.init();

        ShadersHandler.init();

        /* Register entity renderers */

        RenderingRegistry.registerEntityRenderingHandler(EntityCarpentersTile.class, new RenderCarpentersTile());
    }

}
