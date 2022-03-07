package firemerald.api.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Property;

public class ConfigValueString extends ConfigValueBase
{
	public String val;
	public final String defaultValue;

	public ConfigValueString(Category category, String name, String defaultValue, String comment)
	{
		super( category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.defaultValue = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, defaultValue, comment);
	}

	@Override
	public void loadConfig()
	{
		val = getProperty().getString();
	}

	@Override
	public void saveConfig()
	{
		getProperty().set(val);
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		NBTBase val = tag.getTag(name);
		if (val == null || !(val instanceof NBTPrimitive || val instanceof NBTTagString)) this.val = this.defaultValue;
		else this.val = tag.getString(name);
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setString(name, val);
	}
}