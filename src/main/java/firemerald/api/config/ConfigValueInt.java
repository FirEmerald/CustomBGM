package firemerald.api.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;

public class ConfigValueInt extends ConfigValueBase
{
	public int val;
	public final int defaultValue, min, max;

	public ConfigValueInt(Category category, String name, int defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.defaultValue = defaultValue;
		this.min = Integer.MIN_VALUE;
		this.max = Integer.MAX_VALUE;
	}

	public ConfigValueInt(Category category, String name, int defaultValue, int min, int max, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + ", min: " + min + ", max: " + max + "]");
		this.val = this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	public Property getProperty()
	{
		return config.get(category, name, defaultValue, comment, min, max);
	}

	@Override
	public void loadConfig()
	{
		val = getProperty().getInt();
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
		if (val == null || !(val instanceof NBTPrimitive)) this.val = this.defaultValue;
		else this.val = ((NBTPrimitive) val).getInt();
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setInteger(name, val);
	}
}