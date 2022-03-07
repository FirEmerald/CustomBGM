package firemerald.api.config;

public abstract class ConfigValueBase extends ConfigValue
{
	public final String name;
	public final String comment;

	public ConfigValueBase(Category category, String name, String comment)
	{
		super(category);
		this.name = name;
		this.comment = comment;
	}
}