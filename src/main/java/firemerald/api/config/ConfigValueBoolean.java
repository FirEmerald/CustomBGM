package firemerald.api.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;

public class ConfigValueBoolean extends ConfigValueBase
{
	public boolean val;
	public final boolean def;

	public ConfigValueBoolean(Category category, String name, boolean defaultValue, String comment)
	{
		super(category, name, comment + " [default: " + defaultValue + "]");
		this.val = this.def = defaultValue;
	}

	public Property getProperty()
	{
		return config.get(category, name, def, comment);
	}

	@Override
	public void loadConfig()
	{
		val = getProperty().getBoolean();
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
		if (val == null || !(val instanceof NBTPrimitive)) this.val = this.def;
		else this.val = ((NBTPrimitive) val).getDouble() > 0.5;
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		tag.setBoolean(name, val);
	}
}