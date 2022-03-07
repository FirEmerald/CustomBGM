package firemerald.custombgm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.api.core.CoreModMainClass;
import firemerald.api.core.IChunkLoader;
import firemerald.api.core.IFMLEventHandler;
import firemerald.custombgm.client.ClientProxy;
import firemerald.custombgm.common.CommonProxy;
import firemerald.custombgm.plugin.Core;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Main extends CoreModMainClass<CommonProxy> implements LoadingCallback
{
    public static final Logger LOGGER = LogManager.getLogger("CustomBGM"); //has to be static to prevent a crash

	private static Main instance;

	public static Main instance()
	{
		return instance;
	}

	public static Logger logger()
	{
		return LOGGER;
	}

	public static CommonProxy proxy()
	{
		return instance.proxy;
	}

	public static SimpleNetworkWrapper network()
	{
		return instance.network;
	}

	public static void registerFMLEventHandler(IFMLEventHandler handler)
	{
		instance.addFMLEventHandler(handler);
	}

	public Main()
	{
		super();
		instance = this;
	}

	@Override
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		super.onPreInitialization(event);
		ForgeChunkManager.setForcedChunkLoadingCallback(this, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected CommonProxy makeClientProxy()
	{
		return new ClientProxy();
	}

	@Override
	@SideOnly(Side.SERVER)
	protected CommonProxy makeServerProxy()
	{
		return new CommonProxy();
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		List<Ticket> invalid = new ArrayList<>();
		tickets.forEach(ticket -> {
			switch (ticket.getType())
			{
			case ENTITY:
				invalid.add(ticket);
				break;
			case NORMAL:
				NBTTagCompound tag = ticket.getModData();
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				BlockPos pos = new BlockPos(x, y, z);
				TileEntity tile = world.getTileEntity(pos);
				if (!(tile instanceof IChunkLoader && ((IChunkLoader) tile).setTicket(ticket))) invalid.add(ticket);
				break;
			default:
				invalid.add(ticket);
			}
		});
		invalid.forEach(ForgeChunkManager::releaseTicket);
	}

	@Override
	public ModContainer getModContainer()
	{
		return Core.getInstance();
	}

	@Override
	public LoadController getLoadController()
	{
		return Core.loadController;
	}
}
