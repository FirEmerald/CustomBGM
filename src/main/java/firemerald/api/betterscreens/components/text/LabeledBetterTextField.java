package firemerald.api.betterscreens.components.text;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.gui.FontRenderer;

public class LabeledBetterTextField extends BetterTextField
{
	public String label;
	public int disCol = 7368816;
	public FontRenderer font;

	public LabeledBetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, Consumer<String> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, onChanged);
		this.font = fontrendererObj;
		this.label = label;
	}

	public LabeledBetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String label, Predicate<String> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, onChanged);
		this.font = fontrendererObj;
		this.label = label;
	}

	public LabeledBetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String val, String label, Consumer<String> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, val, onChanged);
		this.font = fontrendererObj;
		this.label = label;
	}

	public LabeledBetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String val, String label, Predicate<String> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h, val, onChanged);
		this.font = fontrendererObj;
		this.label = label;
	}

	@Override
	public void setDisabledTextColour(int col)
	{
		super.setDisabledTextColour(this.disCol = col);
	}

	@Override
	public void drawTextBox()
	{
		super.drawTextBox();
		if (this.getVisible() && !this.isFocused() && this.getText().length() == 0)
		{
			int l = this.getEnableBackgroundDrawing() ? this.x + 4 : this.x;
			int i1 = this.getEnableBackgroundDrawing() ? this.y + (this.height - 8) / 2 : this.y;
			this.font.drawStringWithShadow(label, l, i1, disCol);
		}
	}
}
