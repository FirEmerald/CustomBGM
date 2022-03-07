package firemerald.custombgm.plugin;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.custombgm.api.CustomBGMAPI;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@SortingIndex(value = Integer.MAX_VALUE)
@TransformerExclusions(value = {"firemerald.custombgm.plugin.", "firemerald.api.core.plugin."})
@Name(value = "CustomBGM")
@MCVersion("1.12.2")
public class Plugin implements IFMLLoadingPlugin, IFMLCallHook
{
	public static final String MOD_ID = CustomBGMAPI.MOD_ID;
    public static final String MC_VERSION = "[1.12.2]";
    public static final String MOD_VERSION = "[1.0.0]";

    private File location;

    public final Logger logger = LogManager.getLogger("CustomBGM");

    public static Logger logger()
    {
    	return instance.logger;
    }

    private static Plugin instance;

    public static Plugin instance()
    {
    	return instance;
    }

    public Plugin()
    {
    	instance = this;
    }

    public File getLocation()
    {
    	return location;
    }

	@Override
	public Void call() throws Exception
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {"firemerald.custombgm.plugin.Transformer"};
	}

	@Override
	public String getModContainerClass()
	{
		return "firemerald.custombgm.plugin.Core";
	}

	@Override
	public String getSetupClass()
	{
		return getClass().getName();
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		location = (File) data.get("coremodLocation");
        if (location == null) location = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replace("%20", " "));
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}