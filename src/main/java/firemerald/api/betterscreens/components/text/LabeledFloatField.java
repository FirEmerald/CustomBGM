package firemerald.api.betterscreens.components.text;

import firemerald.api.core.function.FloatConsumer;
import firemerald.api.core.function.FloatPredicate;
import net.minecraft.client.gui.FontRenderer;

public class LabeledFloatField extends LabeledBetterTextField
{
	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float min, float max, String label, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, FloatPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, String label, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, float min, float max, String label, FloatConsumer onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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

	public LabeledFloatField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, String label, FloatPredicate onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, label, v -> {
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
