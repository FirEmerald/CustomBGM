package firemerald.custombgm.client;

import firemerald.custombgm.api.CustomBGMAPI;
import firemerald.custombgm.api.ISoundLoop;
import firemerald.custombgm.client.audio.LoopingSounds;
import firemerald.custombgm.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	@Override
    public void onConstruction(FMLConstructionEvent event)
    {
		super.onConstruction(event);
		CustomBGMAPI.instance = new CustomBGMAPI() {
			@Override
			public ISoundLoop grabSound(ResourceLocation name, SoundCategory category, boolean disablePan)
			{
				return LoopingSounds.grabSound(name, category, disablePan);
			}

			@Override
			public ISoundLoop playSound(ResourceLocation name, SoundCategory category, boolean disablePan)
			{
				return LoopingSounds.playSound(name, category, disablePan);
			}
		};
    }

	@Override
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
		super.onPreInitialization(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
    }

    @Override
	public void loadConfig()
    {
    	super.loadConfig();
    	ConfigClientOptions.INSTANCE.loadConfig();
    }

	@Override
	public EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public boolean isThePlayer(Entity entity)
	{
		return entity == Minecraft.getMinecraft().player;
	}

	@Override
	public GameType getGameType(EntityPlayer player)
	{
		return player.world.isRemote && isThePlayer(player) ? Minecraft.getMinecraft().playerController.getCurrentGameType() : super.getGameType(player);
	}
}