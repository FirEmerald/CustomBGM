package firemerald.api.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;

public class ConfigValueFloat extends ConfigValueBase
{
	public float val;
	public final float defaultValue, min, max;

	public ConfigValueFloat(Category category, String name, float defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.defaultValue = defaultValue;
		this.min = Float.NEGATIVE_INFINITY;
		this.max = Float.POSITIVE_INFINITY;
	}

	public ConfigValueFloat(Category category, String name, float defaultValue, float min, float max, String comment)
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
		val = (float) getProperty().getDouble();
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
		else this.val = ((NBTPrimitive) val).getFloat();
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setFloat(name, val);
	}
}