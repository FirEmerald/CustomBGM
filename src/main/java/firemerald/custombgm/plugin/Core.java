package firemerald.custombgm.plugin;

import java.io.File;
import java.util.ArrayList;

import com.google.common.eventbus.EventBus;

import firemerald.custombgm.Main;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class Core extends DummyModContainer
{
	public static LoadController loadController;
	public static final ModMetadata METADATA = new ModMetadata();
	static
	{
		METADATA.authorList = new ArrayList<>();
    	METADATA.authorList.add("FirEmerald");
    	METADATA.credits = "FirEmerald";
    	METADATA.description = "Framework for mods to add in sounds with custom loop points";
    	METADATA.modId = Plugin.MOD_ID;
    	METADATA.name = "Looped Sounds";
    	METADATA.version = Plugin.MOD_VERSION;
    	METADATA.logoFile = "assets/custombgm/textures/logo.png";
	}
	private static Core instance;

	public static Core getInstance()
	{
		return instance;
	}

    public Core()
    {
        super(METADATA);
        instance = this;
    }

    @Override
    public File getSource()
    {
        return Plugin.instance().getLocation();
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public String getGuiClassName()
    {
    	return "firemerald.custombgm.client.ModGuiFactory";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	loadController = controller;
    	bus.register(new Main());
        return true;
    }

    @Override
    public Object getMod()
    {
        return Main.instance();
    }
}