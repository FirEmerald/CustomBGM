package firemerald.api.betterscreens.components;

import firemerald.api.betterscreens.GuiElementContainer;
import firemerald.api.betterscreens.ScissorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class ComponentPane extends GuiElementContainer implements IComponent
{
	public int x1, y1, x2, y2;
	public boolean isFocused = false;
	protected int margin;
	protected int ex1, ey1;

	public ComponentPane(int x1, int y1, int x2, int y2)
	{
		this(x1, y1, x2, y2, 0);
	}

	public ComponentPane(int x1, int y1, int x2, int y2, int margin)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.setMargin(margin);
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		ex1 = x1 + margin;
		ey1 = y1 + margin;
	}

	public void setMargin(int margin)
	{
		this.margin = margin;
		ex1 = x1 + margin;
		ey1 = y1 + margin;
	}

	@Override
	public void onFocus()
	{
		this.isFocused = true;
	}

	@Override
	public void onUnfocus()
	{
		if (this.focused != null)
		{
			this.focused.onUnfocus();
			this.focused = null;
		}
		this.isFocused = false;
	}

	@Override
	public boolean contains(float x, float y)
	{
		return (x >= x1 && y >= y1 && x < x2 && y < y2);
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		this.setScissor(margin, margin, x2 - x1 - (margin << 1), y2 - y1 - (margin << 1));
		GlStateManager.pushMatrix();
		GlStateManager.translate(ex1, ey1, 0);
		super.render(mc, mx - ex1, my - ey1, partialTicks, canHover);
		GlStateManager.popMatrix();
		ScissorUtil.popScissor();
	}

	@Override
	public void tick(Minecraft mc, int mx, int my)
	{
		super.tick(mc, mx - ex1, my - ey1);
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		super.onMousePressed(mx - ex1, my - ey1, button);
	}

	@Override
	public void onMouseReleased(int mx, int my, int button)
	{
		super.onMouseReleased(mx - ex1, my - ey1, button);
	}

	@Override
	public boolean canScrollH(int mx, int my)
	{
		return super.canScrollH(mx - ex1, my - ey1);
	}

	@Override
	public boolean canScrollV(int mx, int my)
	{
		return super.canScrollV(mx - ex1, my - ey1);
	}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY)
	{
		super.onMouseScroll(mx - ex1, my - ey1, scrollX, scrollY);
	}

	@Override
	public void onDrag(int mx, int my, int button)
	{
		super.onDrag(mx - ex1, my - ey1, button);
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

	@Override
	public int getComponentOffsetX()
	{
		return this.getHolderOffsetX() + this.getX1();
	}

	@Override
	public int getComponentOffsetY()
	{
		return this.getHolderOffsetY() + this.getY1();
	}
}