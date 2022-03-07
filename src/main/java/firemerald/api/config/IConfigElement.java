package firemerald.api.config;

import net.minecraft.nbt.NBTTagCompound;

public interface IConfigElement
{
	//load from configuration
	public void loadConfig();

	//save to configuration
	public void saveConfig();

	//load from NBT
	public void loadNBT(NBTTagCompound tag);

	//save to NBT
	public void saveNBT(NBTTagCompound tag);
}