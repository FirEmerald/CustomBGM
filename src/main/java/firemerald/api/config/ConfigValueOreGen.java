package firemerald.api.config;

import net.minecraft.nbt.NBTTagCompound;

public class ConfigValueOreGen extends ConfigValueBoolean
{
	public final ConfigValueInt size, minHeight, maxHeight;
	public final ConfigValueDouble count;

	public ConfigValueOreGen(Category category, String name, boolean defGen, int defSize, double defCount, int defMinHeight, int defMaxHeight)
	{
		this(category, name, defGen, defSize, defCount, defMinHeight, defMaxHeight, "generate " + name + " in world.", name + " generation size.", name + " generation count. Integer portion is constant, decimal portion is random chance, I.E. 1.5 means 1 cluster always with a 50% chance of a second cluster.", name + " minimum generation height.", name + " maximum generation height.");
	}

	public ConfigValueOreGen(Category category, String name, String commentName, boolean defGen, int defSize, double defCount, int defMinHeight, int defMaxHeight)
	{
		this(category, name, defGen, defSize, defCount, defMinHeight, defMaxHeight, "generate " + commentName + " in world.", commentName + " generation size.", commentName + " generation count. Integer portion is constant, decimal portion is random chance, I.E. 1.5 means 1 cluster always with a 50% chance of a second cluster.", commentName + " minimum generation height.", commentName + " maximum generation height.");
	}

	public ConfigValueOreGen(Category category, String name, boolean defGen, int defSize, double defCount, int defMinHeight, int defMaxHeight, String genComment, String sizeComment, String countComment, String minHeightComment, String maxHeightComment)
	{
		super(category, name + "_gen", defGen, genComment);
		this.size = new ConfigValueInt(category, name + "_size", defSize, 1, 65536, sizeComment);
		this.count = new ConfigValueDouble(category, name + "_count", defCount, 0, 65536, countComment);
		this.minHeight = new ConfigValueInt(category, name + "_min_height", defMinHeight, 0, 255, minHeightComment);
		this.maxHeight = new ConfigValueInt(category, name + "_max_height", defMaxHeight, 0, 255, maxHeightComment);
	}

	@Override
	public void loadConfig()
	{
		super.loadConfig();
		size.loadConfig();
		count.loadConfig();
		minHeight.loadConfig();
		maxHeight.loadConfig();
	}

	@Override
	public void saveConfig()
	{
		super.saveConfig();
		size.saveConfig();
		count.saveConfig();
		minHeight.saveConfig();
		maxHeight.saveConfig();
	}

	@Override
	public void loadNBT(NBTTagCompound tag)
	{
		super.loadNBT(tag);
		size.loadNBT(tag);
		count.loadNBT(tag);
		minHeight.loadNBT(tag);
		maxHeight.loadNBT(tag);
	}

	@Override
	public void saveNBT(NBTTagCompound tag)
	{
		super.saveNBT(tag);
		size.saveNBT(tag);
		count.saveNBT(tag);
		minHeight.saveNBT(tag);
		maxHeight.saveNBT(tag);
	}

	public boolean getDoesGen()
	{
		return val;
	}

	public void setDoesGen(boolean doesGen)
	{
		val = doesGen;
	}

	public int getSize()
	{
		return size.val;
	}

	public void setSize(int size)
	{
		this.size.val = size;
	}

	public double getCount()
	{
		return count.val;
	}

	public void setCount(double count)
	{
		this.count.val = count;
	}

	public int getMinHeight()
	{
		return minHeight.val;
	}

	public void setMinHeight(int minHeight)
	{
		this.minHeight.val = minHeight;
	}

	public int getMaxHeight()
	{
		return maxHeight.val;
	}

	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight.val = maxHeight;
	}
}