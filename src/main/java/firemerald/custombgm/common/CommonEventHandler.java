package firemerald.custombgm.common;

import firemerald.custombgm.Main;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.capability.PlayerBase;
import firemerald.custombgm.capability.PlayerServer;
import firemerald.custombgm.init.LSBlocks;
import firemerald.custombgm.init.LSItems;
import firemerald.custombgm.init.LSSounds;
import firemerald.custombgm.networking.client.SelfDataSyncPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonEventHandler
{
	@SubscribeEvent
	public void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		LSItems.init(event.getRegistry());
		if (FMLCommonHandler.instance().getSide().isClient()) LSItems.registerModels();
	}

	@SubscribeEvent
	public void onBlockRegistry(RegistryEvent.Register<Block> event)
	{
		LSBlocks.init(event.getRegistry());
	}

	@SubscribeEvent
	public void onSoundRegistry(RegistryEvent.Register<SoundEvent> event)
	{
		LSSounds.register(event.getRegistry());
	}

	@SubscribeEvent
	public void AttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityPlayer)
		{
			event.addCapability(IPlayer.CAPABILITY_NAME, event.getObject().world.isRemote ? new PlayerBase() : new PlayerServer());
		}
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.END) while (!CommonState.QUEUED_ACTIONS.isEmpty()) CommonState.QUEUED_ACTIONS.poll().run();
	}

	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public void onLivingUpdatePostAlways(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.world.isRemote && entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			IPlayer lsPlayer = player.getCapability(Capabilities.player, null);
			if (lsPlayer != null)
			{
				if (entity instanceof EntityPlayerMP) Main.network().sendTo(new SelfDataSyncPacket(lsPlayer), (EntityPlayerMP) entity);
				lsPlayer.clearMusicOverride();
			}
		}
	}
}