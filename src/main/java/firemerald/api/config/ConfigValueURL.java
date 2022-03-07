package firemerald.api.config;

import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Property;

public class ConfigValueURL extends ConfigValueBase
{
	public URL val;
	public final URL defaultValue;

	public ConfigValueURL(Category category, String name, URL defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, defaultValue.toString(), comment);
	}

	@Override
	public void loadConfig()
	{
		try
		{
			val = new URL(getProperty().getString());
		}
		catch (MalformedURLException e)
		{
			this.val = defaultValue;
		}
	}

	@Override
	public void saveConfig()
	{
		getProperty().set(val.toString());
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTBase val = tag.getTag(name);
		if (val == null || !(val instanceof NBTTagString)) this.val = this.defaultValue;
		else try
		{
			this.val = new URL(((NBTTagString) val).getString());
		}
		catch (MalformedURLException e)
		{
			this.val = defaultValue;
		}
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setString(name, val.toString());
	}
}