package firemerald.api.betterscreens.components.text;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

import net.minecraft.client.gui.FontRenderer;

public class LabeledIntegerField extends LabeledBetterTextField
{
	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, IntConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				onChanged.accept(Integer.parseInt(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, double min, double max, String label, IntConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				int v2 = Integer.parseInt(v);
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

	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, IntPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				return onChanged.test(Integer.parseInt(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
	}

	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, int val, String label, IntConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				onChanged.accept(Integer.parseInt(v));
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setInteger(val);
	}

	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, int val, int min, int max, String label, IntConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				int v2 = Integer.parseInt(v);
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
		this.setInteger(val);
	}

	public LabeledIntegerField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, int val, String label, IntPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
			try
			{
				return onChanged.test(Integer.parseInt(v));
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		});
		this.setInteger(val);
	}

    public void setInteger(int v)
    {
    	setString(Integer.toString(v)); //bypass setting val again
    }
}
