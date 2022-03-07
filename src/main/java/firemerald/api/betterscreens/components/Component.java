package firemerald.api.betterscreens.components;

import firemerald.api.betterscreens.IGuiHolder;
import net.minecraft.client.Minecraft;

public abstract class Component implements IComponent
{
	public int x1, y1, x2, y2;
	public boolean focused = false;
	public IGuiHolder holder = null;

	@Override
	public void setHolder(IGuiHolder holder)
	{
		this.holder = holder;
	}

	@Override
	public IGuiHolder getHolder()
	{
		return this.holder;
	}

	public Component(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void onFocus()
	{
		this.focused = true;
	}

	@Override
	public void onUnfocus()
	{
		this.focused = false;
	}

	@Override
	public void onMousePressed(int mx, int my, int button) {}

	@Override
	public void onMouseReleased(int mx, int my, int button) {}

	@Override
	public void onDrag(int mx, int my, int button) {}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY) {}

	@Override
	public void tick(Minecraft mc, int mx, int my) {}

	@Override
	public boolean onKeyPressed(char chr, int scancode)
	{
		return false;
	}

	@Override
	public boolean onKeyReleased(int scancode)
	{
		return false;
	}

	@Override
	public boolean contains(float x, float y)
	{
		return (x >= getX1() && y >= getY1() && x < getX2() && y < getY2());
	}

	@Override
	public int getX1()
	{
		return x1;
	}

	@Override
	public int getY1()
	{
		return y1;
	}

	@Override
	public int getX2()
	{
		return x2;
	}

	@Override
	public int getY2()
	{
		return y2;
	}
}