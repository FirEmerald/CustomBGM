package com.firemerald.custombgm.common;

import firemerald.api.core.IProxy;
import firemerald.api.core.plugin.ITransformer;
import com.firemerald.custombgm.Main;
import com.firemerald.custombgm.api.CustomBGMCapabilities;
import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.blockentity.BlockEntityEntityTester;
import com.firemerald.custombgm.command.CommandLSStats;
import com.firemerald.custombgm.init.CustomBGMTabs;
import com.firemerald.custombgm.networking.client.SelfDataSyncPacket;
import com.firemerald.custombgm.networking.client.TileGUIPacket;
import com.firemerald.custombgm.networking.server.ShapeToolSetPacket;
import com.firemerald.custombgm.networking.server.InitializedPacket;
import com.firemerald.custombgm.networking.server.TileGUIClosedPacket;
import com.firemerald.custombgm.plugin.Plugin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IProxy
{
	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
    	loadConfig();
    	SimpleNetworkWrapper network = Main.instance().network = NetworkRegistry.INSTANCE.newSimpleChannel("custombgm");
		int p = 0;
		network.registerMessage(SelfDataSyncPacket.Handler.class, SelfDataSyncPacket.class, p++, Side.CLIENT);
		network.registerMessage(TileGUIPacket.Handler.class, TileGUIPacket.class, p++, Side.CLIENT);

		network.registerMessage(TileGUIClosedPacket.Handler.class, TileGUIClosedPacket.class, p++, Side.SERVER);
		network.registerMessage(ShapeToolSetPacket.Handler.class, ShapeToolSetPacket.class, p++, Side.SERVER);
		network.registerMessage(InitializedPacket.Handler.class, InitializedPacket.class, p++, Side.SERVER);

    	MinecraftForge.EVENT_BUS.register(new ForgeBusEventHandler());
    }

	@Override
    public void onInitialization(FMLInitializationEvent event)
    {
    	CustomBGMCapabilities.register();
    	CustomBGMTabs.init();
		GameRegistry.registerTileEntity(BlockEntityBGM.class, new ResourceLocation(Plugin.MOD_ID, "bgm"));
		GameRegistry.registerTileEntity(BlockEntityEntityTester.class, new ResourceLocation(Plugin.MOD_ID, "entity_tester"));
		GameRegistry.registerTileEntity(BlockEntityBossSpawner.class, new ResourceLocation(Plugin.MOD_ID, "boss_spawner"));
	}

	@Override
    public void onServerStarting(FMLServerStartingEvent event)
    {
    	if (ITransformer.IS_DEOBFUSCATED) event.getServer().setOnlineMode(false);
    	event.registerServerCommand(new CommandLSStats());
    }
}