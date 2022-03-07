package firemerald.api.betterscreens.components.text;

import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

import net.minecraft.client.gui.FontRenderer;

public class LabeledDoubleField extends LabeledBetterTextField
{
	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double min, double max, String label, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, DoublePredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, String label, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, double min, double max, String label, DoubleConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledDoubleField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double val, String label, DoublePredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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
