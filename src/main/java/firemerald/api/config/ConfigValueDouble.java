package firemerald.api.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;

public class ConfigValueDouble extends ConfigValueBase
{
	public double val;
	public final double defaultValue, min, max;

	public ConfigValueDouble(Category category, String name, double defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.defaultValue = defaultValue;
		this.min = Double.NEGATIVE_INFINITY;
		this.max = Double.POSITIVE_INFINITY;
	}

	public ConfigValueDouble(Category category, String name, double defaultValue, double min, double max, String comment)
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
		val = getProperty().getDouble();
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
		else this.val = ((NBTPrimitive) val).getDouble();
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setDouble(name, val);
	}
}