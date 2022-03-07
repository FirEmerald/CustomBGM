package firemerald.api.selectionshapes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IShapeTool
{
	public default BoundingShape getShape(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null)
		{
			NBTTagCompound shapeTag = tag.getCompoundTag("shape");
			if (shapeTag != null)
			{
				return BoundingShape.constructFromNBT(shapeTag);
			}
		}
		return null;
	}

	public boolean canAcceptShape(ItemStack stack, BoundingShape shape);

	public default void setShape(ItemStack stack, BoundingShape shape)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) stack.setTagCompound(tag = new NBTTagCompound());
		NBTTagCompound shapeTag = new NBTTagCompound();
		shape.saveToNBT(shapeTag);
		tag.setTag("shape", shapeTag);
	}
}