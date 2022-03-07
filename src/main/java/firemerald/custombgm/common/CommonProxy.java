package firemerald.custombgm.common;

import firemerald.api.core.IProxy;
import firemerald.api.core.plugin.ITransformer;
import firemerald.custombgm.Main;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.command.CommandLSStats;
import firemerald.custombgm.init.LSTabs;
import firemerald.custombgm.networking.client.SelfDataSyncPacket;
import firemerald.custombgm.networking.client.TileGUIPacket;
import firemerald.custombgm.networking.server.ShapeToolSetPacket;
import firemerald.custombgm.networking.server.TileGUIClosedPacket;
import firemerald.custombgm.plugin.Plugin;
import firemerald.custombgm.tileentity.TileEntityBGM;
import firemerald.custombgm.tileentity.TileEntityBossSpawner;
import firemerald.custombgm.tileentity.TileEntityEntityTester;
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

    	MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

	@Override
    public void onInitialization(FMLInitializationEvent event)
    {
    	Capabilities.register();
    	LSTabs.init();
		GameRegistry.registerTileEntity(TileEntityBGM.class, new ResourceLocation(Plugin.MOD_ID, "bgm"));
		GameRegistry.registerTileEntity(TileEntityEntityTester.class, new ResourceLocation(Plugin.MOD_ID, "entity_tester"));
		GameRegistry.registerTileEntity(TileEntityBossSpawner.class, new ResourceLocation(Plugin.MOD_ID, "boss_spawner"));
	}

	@Override
    public void onServerStarting(FMLServerStartingEvent event)
    {
    	if (ITransformer.IS_DEOBFUSCATED) event.getServer().setOnlineMode(false);
    	event.registerServerCommand(new CommandLSStats());
    }

	public EntityPlayer getPlayer()
	{
		return null;
	}

	public boolean isThePlayer(Entity entity)
	{
		return false;
	}

	public GameType getGameType(EntityPlayer player)
	{
		return player instanceof EntityPlayerMP ? ((EntityPlayerMP) player).interactionManager.getGameType() : null;
	}

	public void loadConfig() {}
}