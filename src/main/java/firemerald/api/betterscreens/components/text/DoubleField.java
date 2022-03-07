package firemerald.api.betterscreens.components.text;

import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

import net.minecraft.client.gui.FontRenderer;

public class DoubleField extends BetterTextField
{
	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(Double.parseDouble(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double min, double max, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				double v2 = Double.parseDouble(v);
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

	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, DoublePredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(Double.parseDouble(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(Double.parseDouble(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setDouble(val);
	}

	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, double min, double max, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				double v2 = Double.parseDouble(v);
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
		this.setDouble(val);
	}

	public DoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, DoublePredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(Double.parseDouble(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setDouble(val);
	}

    public void setDouble(double v)
    {
    	setString(Double.toString(v)); //bypass setting val again
    }
}
