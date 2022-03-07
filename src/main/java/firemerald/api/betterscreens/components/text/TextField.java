package firemerald.api.betterscreens.components.text;

import firemerald.api.betterscreens.IGuiHolder;
import firemerald.api.betterscreens.components.IComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class TextField extends GuiTextField implements IComponent
{
	public boolean focused = false;
	public IGuiHolder holder = null;

    public TextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height)
    {
    	super(componentId, fontrendererObj, x - 1, y - 1, par5Width - 2, par6Height - 2);
    }

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		this.mouseClicked(mx, my, button);
	}

	@Override
	public void onMouseReleased(int mx, int my, int button) {}

	@Override
	public void onDrag(int mx, int my, int button) {} //TODO selection

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY) {}

	@Override
	public boolean onKeyPressed(char chr, int scancode)
	{
		return this.textboxKeyTyped(chr, scancode);
	}

	@Override
	public boolean onKeyReleased(int scancode)
	{
		return false;
	}

	@Override
	public void tick(Minecraft mc, int mx, int my)
	{
		this.updateCursorCounter();
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partial, boolean canHover)
	{
		this.drawTextBox();
	}

	@Override
	public int getX1()
	{
		return x - 1;
	}

	@Override
	public int getY1()
	{
		return y - 1;
	}

	@Override
	public int getX2()
	{
		return x + width + 1;
	}

	@Override
	public int getY2()
	{
		return y + height + 1;
	}

	@Override
	public void onFocus()
	{
		this.focused = true;
	}

	@Override
	public void onUnfocus()
	{
		this.setFocused(false);
		this.focused = false;
	}

	@Override
	public void setHolder(IGuiHolder holder)
	{
		this.holder = holder;
	}

	@Override
	public IGuiHolder getHolder()
	{
		return holder;
	}

	@Override
	public boolean contains(float x, float y)
	{
		return (x >= getX1() && y >= getY1() && x < getX2() && y < getY2());
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x = x1 + 1;
		this.y = y1 + 1;
		this.width = x2 - x1 - 2;
		this.height = y2 - y1 - 2;
	}
}
