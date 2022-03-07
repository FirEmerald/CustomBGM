package firemerald.api.config;

import net.minecraftforge.common.config.Configuration;

public abstract class ConfigValue implements IConfigElement
{
	public final Configuration config;
	public final String category;

	public ConfigValue(Category category)
	{
		this.config = category.config;
		this.category = category.name;
		category.values.add(this);
	}
}