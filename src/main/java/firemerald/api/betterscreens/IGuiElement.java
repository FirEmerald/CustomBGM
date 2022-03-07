package firemerald.api.betterscreens;

import net.minecraft.client.Minecraft;

public interface IGuiElement
{
	public abstract void tick(Minecraft mc, int mx, int my);

	public abstract void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover);

	public abstract int getX1();

	public abstract int getY1();

	public abstract int getX2();

	public abstract int getY2();

	public abstract void setHolder(IGuiHolder holder);

	public abstract IGuiHolder getHolder();

	public default int getHolderOffsetX()
	{
		return getHolder() == null ? 0 : getHolder().getComponentOffsetX();
	}

	public default int getHolderOffsetY()
	{
		return getHolder() == null ? 0 : getHolder().getComponentOffsetY();
	}

	public default int getTrueX1()
	{
		return getHolderOffsetX() + getX1();
	}

	public default int getTrueY1()
	{
		return getHolderOffsetY() + getY1();
	}

	public default int getTrueX2()
	{
		return getHolderOffsetX() + getX2();
	}

	public default int getTrueY2()
	{
		return getHolderOffsetY() + getY2();
	}

	public default int getSelectorX1(GuiPopup selector)
	{
		return getTrueX1();
	}

	public default int getSelectorY1(GuiPopup selector)
	{
		return getTrueY1();
	}

	public default int getSelectorX2(GuiPopup selector)
	{
		return getTrueX2();
	}

	public default int getSelectorY2(GuiPopup selector)
	{
		return getTrueY2();
	}

	default public void setScissor(int offX1, int offY1, int w, int h)
	{
		int x1 = this.getTrueX1() + offX1;
		int y1 = this.getTrueY1() + offY1;
		ScissorUtil.pushScissor(x1, y1, x1 + w, y1 + h);
	}
}