package firemerald.api.betterscreens.components.text;

import java.util.function.Predicate;

import net.minecraft.client.gui.FontRenderer;

public class FloatHolder extends BetterTextField
{
	private float val;

	public FloatHolder(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h)
	{
		super(componentId, fontrendererObj, x, y, w, h, (Predicate<String>) null);
		this.onChanged = (str) -> {
			try
			{
				this.val = Float.parseFloat(str);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		};
	}

	public FloatHolder(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float min, float max)
	{
		super(componentId, fontrendererObj, x, y, w, h, (Predicate<String>) null);
		this.onChanged = (str) -> {
			try
			{
				float v2 = Float.parseFloat(str);
				if (v2 >= min && v2 <= max)
				{
					this.val = v2;
					return true;
				}
				else return false;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		};
	}

	public FloatHolder(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val)
	{
		super(componentId, fontrendererObj, x, y, w, h, (Predicate<String>) null);
		this.onChanged = (str) -> {
			try
			{
				this.val = Float.parseFloat(str);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		};
		this.setFloat(val);
	}

	public FloatHolder(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, float val, float min, float max)
	{
		super(componentId, fontrendererObj, x, y, w, h, (Predicate<String>) null);
		this.onChanged = (str) -> {
			try
			{
				float v2 = Float.parseFloat(str);
				if (v2 >= min && v2 <= max)
				{
					this.val = v2;
					return true;
				}
				else return false;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		};
		this.setFloat(val);
	}

    public void setFloat(float v)
    {
    	setString(Float.toString(this.val = v)); //bypass setting val again
    }

    public float getFloat()
    {
    	return val;
    }
}
