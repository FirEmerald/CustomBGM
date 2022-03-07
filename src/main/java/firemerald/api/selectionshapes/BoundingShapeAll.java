package firemerald.api.selectionshapes;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import firemerald.api.betterscreens.IGuiElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class BoundingShapeAll extends BoundingShape
{
	@Override
	public String getUnlocalizedName()
	{
		return "shape.all";
	}

	@Override
	public boolean isWithin(@Nullable Entity entity, double posX, double posY, double posZ, double testerX, double testerY, double testerZ)
	{
		return true;
	}

	@Override
	public void addGuiElements(Vec3d pos, IShapeGui gui, FontRenderer font, Consumer<IGuiElement> addElement, int width) {}
}