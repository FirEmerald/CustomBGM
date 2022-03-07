package firemerald.custombgm.api;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import firemerald.api.core.capabilities.NullStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Registers and holds the capability instance for the IPlayer capability
 *
 * @author FirEmerald
 *
 */
public class Capabilities
{
	/**
	 * Capability instance for IPlayer
	 */
	public static Capability<IPlayer> player = null;

	/**
	 * true only after the register() command has been run. DO NOT MODIFY!
	 */
	public static boolean registered = false;

	/**
	 * Registers the capability instance
	 */
	public static void register() //run during init unless you only deal with these when CustomBGM is installed
	{
		if (!registered)
		{
			try //Because Forge's capability manager is literally broken. Honestly, why doesn't it return the instance and instead rely on an apparently unstable annotation system?
			{
				CapabilityManager manager = CapabilityManager.INSTANCE;
				Field f = CapabilityManager.class.getDeclaredField("providers");
				f.setAccessible(true);
				@SuppressWarnings("unchecked")
				IdentityHashMap<String, Capability<?>> map = (IdentityHashMap<String, Capability<?>>) f.get(manager);
				player = register(IPlayer.class, new NullStorage<IPlayer>(), () -> null, manager, map);
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
				FMLCommonHandler.instance().exitJava(0, false);
			}
			registered = true;
			boolean crash = false;
			if (player == null)
			{
				new Exception("player capability not set").printStackTrace();
				crash = true;
			}
			if (crash) FMLCommonHandler.instance().exitJava(0, false);
		}
	}

	/**
	 * Helper method to register the capability
	 *
	 * @param <T> capability type
	 * @param clazz capability class
	 * @param storage capaiblity storage
	 * @param factory capability factory
	 * @param manager the capability manager
	 * @param map the capability manager providers map
	 * @return the capability instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> Capability<T> register(Class<T> clazz, IStorage<T> storage, Callable<T> factory, CapabilityManager manager, Map<String, Capability<?>> map)
	{
		CapabilityManager.INSTANCE.register(clazz, storage, factory);
		return (Capability<T>) map.get(clazz.getName().intern());
	}
}