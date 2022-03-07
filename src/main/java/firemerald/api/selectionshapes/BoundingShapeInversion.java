package firemerald.api.selectionshapes;

import java.util.function.Consumer;

import firemerald.api.betterscreens.IGuiElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class BoundingShapeInversion extends BoundingShape
{
	public BoundingShape shape = new BoundingShapeCylinder();

	@Override
	public String getUnlocalizedName()
	{
		return "shape.inversion";
	}

	@Override
	public boolean isWithin(Entity entity, double posX, double posY, double posZ, double testerX, double testerY, double testerZ)
	{
		return !shape.isWithin(entity, posX, posY, posZ, testerX, testerY, testerZ);
	}

	@Override
	public void saveToNBT(NBTTagCompound tag)
	{
		super.saveToNBT(tag);
		NBTTagCompound tag2 = new NBTTagCompound();
		shape.saveToNBT(tag2);
		tag.setTag("shape", tag2);
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag)
	{
		super.loadFromNBT(tag);
		shape = tag.hasKey("shape", 10) ? BoundingShape.constructFromNBT(tag.getCompoundTag("shape")) : new BoundingShapeSphere();
	}

	@Override
	public void saveToBuffer(ByteBuf buf)
	{
		super.saveToBuffer(buf);
		shape.saveToBuffer(buf);
	}

	@Override
	public void loadFromBuffer(ByteBuf buf)
	{
		super.loadFromBuffer(buf);
		shape = BoundingShape.constructFromBuffer(buf);
	}

	@Override
	public void addGuiElements(Vec3d pos, IShapeGui gui, FontRenderer font, Consumer<IGuiElement> addElement, int width)
	{
		int offX = (width - 200) >> 1;
		addElement.accept(new ButtonConfigureShape(offX, 0, 200, 20, gui::openShape, () -> shape, shape -> this.shape = shape));
	}
}