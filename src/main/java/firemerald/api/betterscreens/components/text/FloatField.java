package firemerald.api.betterscreens.components.text;

import firemerald.api.core.function.FloatConsumer;
import firemerald.api.core.function.FloatPredicate;
import net.minecraft.client.gui.FontRenderer;

public class FloatField extends BetterTextField
{
	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(Float.parseFloat(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float min, float max, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				float v2 = Float.parseFloat(v);
				if (v2 >= min && v2 <= max)
				{
					onChanged.accept(v2);
					return true;
				}
				else return false;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, FloatPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(Float.parseFloat(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(Float.parseFloat(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setFloat(val);
	}

	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, float min, float max, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				float v2 = Float.parseFloat(v);
				if (v2 >= min && v2 <= max)
				{
					onChanged.accept(v2);
					return true;
				}
				else return false;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setFloat(val);
	}

	public FloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, FloatPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(Float.parseFloat(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setFloat(val);
	}

    public void setFloat(float v)
    {
    	setString(Float.toString(v)); //bypass setting val again
    }
}
