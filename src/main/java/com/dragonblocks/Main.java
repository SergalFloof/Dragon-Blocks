package com.dragonblocks;

import com.dragonblocks.proxy.CommonProxy;
import com.dragonblocks.tabs.MainTab;
import com.dragonblocks.util.Reference;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Main 
{
	
	public static final CreativeTabs dragonblocks = new MainTab("dragonblocks");
	public static FMLEventChannel channel;
	
	
	@SidedProxy (clientSide = Reference.CLIENTPROXY, serverSide = Reference.COMMONPROXY )
	public static CommonProxy proxy;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		
//		FeatureRegistry.preInit(event, config); // Do before block and item registration
//        BlockRegistry.preInit(event, config); // Do before item registration
//        DesignHandler.preInit(event);
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.MODID);
//		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
//        config.load();
//
//        proxy.preInit(event, config);
//
//        if (config.hasChanged()) {
//            config.save();
//        }
		
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event) {
		
//		proxy.init(event);
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		
	
		
	}

}
