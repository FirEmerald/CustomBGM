package firemerald.api.betterscreens.components.text;

import net.minecraft.client.gui.FontRenderer;

public class LabeledTextField extends TextField
{
	public String label;
	public int disCol = 7368816;
	public FontRenderer font;

	public LabeledTextField(int componentId, FontRenderer font, int x, int y, int w, int h, String label)
	{
		super(componentId, font, x, y, w, h);
		this.font = font;
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
