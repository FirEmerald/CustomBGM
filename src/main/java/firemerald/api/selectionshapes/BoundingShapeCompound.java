package firemerald.api.selectionshapes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableInt;

import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.core.client.Translator;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class BoundingShapeCompound extends BoundingShape
{
	public List<BoundingShape> shapes = new ArrayList<>();

	@Override
	public void saveToNBT(NBTTagCompound tag)
	{
		super.saveToNBT(tag);
		NBTTagList list = new NBTTagList();
		shapes.stream().map(shape -> {
			NBTTagCompound tag2 = new NBTTagCompound();
			shape.saveToNBT(tag2);
			return tag2;
		}).forEach(list::appendTag);
		tag.setTag("shapes", list);
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag)
	{
		super.loadFromNBT(tag);
		shapes.clear();
		NBTTagList list = tag.getTagList("shapes", 10);
		if (list != null) for (int i = 0; i < list.tagCount(); i++) shapes.add(BoundingShape.constructFromNBT(list.getCompoundTagAt(i)));
	}

	@Override
	public void saveToBuffer(ByteBuf buf)
	{
		super.saveToBuffer(buf);
		ByteBufUtils.writeVarInt(buf, shapes.size(), 4);
		shapes.forEach(shape -> shape.saveToBuffer(buf));
	}

	@Override
	public void loadFromBuffer(ByteBuf buf)
	{
		super.loadFromBuffer(buf);
		shapes.clear();
		int numShapes = ByteBufUtils.readVarInt(buf, 4);
		for (int i = 0; i < numShapes; i++) shapes.add(BoundingShape.constructFromBuffer(buf));
	}

	@Override
	public void addGuiElements(Vec3d pos, IShapeGui gui, FontRenderer font, Consumer<IGuiElement> addElement, int width)
	{
		int offX = (width - 300) >> 1;
		MutableInt y = new MutableInt(0);
		for (int i = 0; i < shapes.size(); i++)
		{
			final int j = i;
			addElement.accept(new ButtonConfigureShape(offX, y.getValue(), 200, 20, gui::openShape, () -> shapes.get(j), shape -> shapes.set(j, shape)));
			addElement.accept(new Button(offX + 200, y.getValue(), 100, 20, Translator.translate("gui.shape.remove"), () -> {
				shapes.remove(j);
				gui.updateGuiButtonsList();
				}));
			y.add(20);
		}
		addElement.accept(new Button(offX + 50, y.getValue(), 200, 20, Translator.translate("gui.shape.add"), () -> {
			shapes.add(new BoundingShapeSphere());
			gui.updateGuiButtonsList();
		}));
	}
}