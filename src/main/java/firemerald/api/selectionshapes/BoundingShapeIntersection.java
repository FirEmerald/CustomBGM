package firemerald.api.selectionshapes;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public class BoundingShapeIntersection extends BoundingShapeCompound
{
	@Override
	public String getUnlocalizedName()
	{
		return "shape.intersection";
	}

	@Override
	public boolean isWithin(@Nullable Entity entity, double posX, double posY, double posZ, double testerX, double testerY, double testerZ)
	{
		return !shapes.isEmpty() && shapes.stream().allMatch(shape -> shape.isWithin(entity, posX, posY, posZ, testerX, testerY, testerZ));
	}
}