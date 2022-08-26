package com.firemerald.custombgm.client.gui.screen;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.firemerald.custombgm.blockentity.BlockEntityEntityOperator;
import com.firemerald.fecore.boundingshapes.BoundingShape;
import com.firemerald.fecore.boundingshapes.BoundingShapeSphere;
import com.firemerald.fecore.client.gui.components.ButtonConfigureShape;
import com.firemerald.fecore.client.gui.components.text.BetterTextField;
import com.firemerald.fecore.client.gui.screen.BlockEntityGUIScreen;
import com.firemerald.fecore.client.gui.screen.ShapesScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public abstract class GuiTileEntityOperator extends BlockEntityGUIScreen
{
	public String selector = "";
	public BoundingShape shape = new BoundingShapeSphere();
	public Component customName = BlockEntityEntityOperator.DEFAULT_NAME;
	public final BlockPos pos;
	public BetterTextField selectorTxt;
    public ButtonConfigureShape configureShape;

	public GuiTileEntityOperator(Component title, BlockPos pos)
	{
		super(title);
		this.pos = pos;
	}

	public BetterTextField setupSelectorTextField(int id, int x, int y, int w, int h)
	{
		selectorTxt = new BetterTextField(font, x, y, w, h, title, (Consumer<String>) (str -> selector = str)); //TODO
		selectorTxt.setMaxLength(Short.MAX_VALUE);
		selectorTxt.setString(selector);
		return selectorTxt;
	}

	public ButtonConfigureShape setupShapeField(int x, int y, int w, int h)
	{
		return configureShape = new ButtonConfigureShape(x, y, w, h, (shape, onAccept) -> new ShapesScreen(new Vec3(pos.getX(), pos.getY(), pos.getZ()), shape, onAccept).activate(), () -> this.shape, shape -> this.shape = shape);
	}

	@Override
	public BlockPos getTilePos()
	{
		return this.pos;
	}

	public void setName(@Nullable Component customName)
	{
		if (customName != null) this.customName = customName;
		else this.customName = BlockEntityEntityOperator.DEFAULT_NAME;
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		shape = BoundingShape.constructFromBuffer(buf);
		selector = buf.readUtf();
		//selectorNBT = buf.readNbt();
		this.setName(Component.Serializer.fromJson(buf.readUtf()));
		selectorTxt.setString(selector);
		configureShape.onShapeChanged(shape);
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		shape.saveToBuffer(buf);
		buf.writeUtf(selector == null ? "" : selector);
		//buf.writeNbt(selectorNBT);
		buf.writeUtf(Component.Serializer.toJson(this.customName));
	}
}