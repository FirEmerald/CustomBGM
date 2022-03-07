package firemerald.api.betterscreens.components.text;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagCompoundField extends BetterTextField
{
	public NBTTagCompoundField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, Consumer<NBTTagCompound> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(toTag(v));
				return true;
			}
			catch (NBTException e)
			{
				return false;
			}
		});
	}

	public NBTTagCompoundField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, Predicate<NBTTagCompound> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(toTag(v));
			}
			catch (NBTException e)
			{
				return false;
			}
		});
	}

	public NBTTagCompoundField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, NBTTagCompound val, Consumer<NBTTagCompound> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				onChanged.accept(toTag(v));
				return true;
			}
			catch (NBTException e)
			{
				return false;
			}
		});
		this.setNBT(val);
	}

	public NBTTagCompoundField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, NBTTagCompound val, Predicate<NBTTagCompound> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, v -> {
			try
			{
				return onChanged.test(toTag(v));
			}
			catch (NBTException e)
			{
				return false;
			}
		});
		this.setNBT(val);
	}

	public static NBTTagCompound toTag(String s) throws NBTException
	{
		return s.isEmpty() ? new NBTTagCompound() : JsonToNBT.getTagFromJson(s);
	}

    public void setNBT(NBTTagCompound v)
    {
    	setString(v.toString()); //bypass setting val again
    }
}
