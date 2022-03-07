package firemerald.api.selectionshapes;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import firemerald.api.betterscreens.components.Button;
import firemerald.api.core.client.Translator;

public class ButtonConfigureShape extends Button
{
	public ButtonConfigureShape(int x, int y, int width, int height, BiConsumer<BoundingShape, Consumer<BoundingShape>> configure, Supplier<BoundingShape> getShape, Consumer<BoundingShape> setShape)
	{
		super(x, y, width, height, Translator.format("gui.shape.configure", getShape.get().getLocalizedName()), null);
		this.onClick = () -> configure.accept(BoundingShape.copy(getShape.get()), setShape.andThen(this::onShapeChanged));
	}

	public void onShapeChanged(BoundingShape shape)
	{
		this.displayString = Translator.format("gui.shape.configure", shape.getLocalizedName());
	}
}