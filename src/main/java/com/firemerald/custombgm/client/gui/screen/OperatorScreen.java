package com.firemerald.custombgm.client.gui.screen;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.firemerald.custombgm.blockentity.BlockEntityEntityOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.fecore.boundingshapes.BoundingShape;
import com.firemerald.fecore.boundingshapes.BoundingShapeSphere;
import com.firemerald.fecore.client.gui.components.ButtonConfigureShape;
import com.firemerald.fecore.client.gui.components.text.BetterTextField;
import com.firemerald.fecore.client.gui.screen.NetworkedGUIEntityScreen;
import com.firemerald.fecore.client.gui.screen.ShapesScreen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public abstract class OperatorScreen<O extends OperatorBase<?, O, S>, S extends IOperatorSource<O, S>> extends NetworkedGUIEntityScreen<S>
{
	public String selector = "";
	public BoundingShape shape = new BoundingShapeSphere();
	public Component customName = BlockEntityEntityOperator.DEFAULT_NAME;
	public BetterTextField selectorTxt;
    public ButtonConfigureShape configureShape;

	public OperatorScreen(Component title, S source)
	{
		super(title, source);
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
		return configureShape = new ButtonConfigureShape(x, y, w, h, (shape, onAccept) -> new ShapesScreen(entity.getPosition(), shape, onAccept).activate(), () -> this.shape, shape -> this.shape = shape);
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
		this.setName(Component.Serializer.fromJson(buf.readUtf()));
		selectorTxt.setString(selector);
		configureShape.onShapeChanged(shape);
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		shape.saveToBuffer(buf);
		buf.writeUtf(selector == null ? "" : selector);
		buf.writeUtf(Component.Serializer.toJson(this.customName));
	}
}