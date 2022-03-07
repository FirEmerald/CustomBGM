package firemerald.api.betterscreens.components.scrolling;

import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.ComponentPane;
import firemerald.api.betterscreens.components.IComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class ScrollableComponentPane extends ComponentPane implements IScrollable
{
	public int height = 0;
	protected int scroll = 0;
	protected int scrollSize = 0;
	public int h = 0;
	public ScrollBar scrollBar = null;

	public ScrollableComponentPane(int x1, int y1, int x2, int y2)
	{
		this(x1, y1, x2, y2, 0);
	}

	public ScrollableComponentPane(int x1, int y1, int x2, int y2, int border)
	{
		super(x1, y1, x2, y2, border);
		setSize(x1, y1, x2, y2);
	}

	public void setScrollBar(ScrollBar scrollBar)
	{
		this.scrollBar = scrollBar;
		scrollBar.setMaxScroll();
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		h = y2 - y1;
		updateScrollSize();
	}

	public void updateComponentSize()
	{
		int h = 0;
		for (IGuiElement el : this.getElementsCopy())
		{
			int y = el.getY2();
			if (y > h) h = y;
		}
		height = h + margin * 2;
	}

	public void updateScrollSize()
	{
		scrollSize = height - h;
		if (scrollSize < 0) scroll = scrollSize = 0;
		else if (scroll > scrollSize) scroll = scrollSize;
		if (scrollBar != null) scrollBar.setMaxScroll();
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		super.onMousePressed(mx, my + scroll, button);
	}

	@Override
	public void onMouseReleased(int mx, int my, int button)
	{
		super.onMouseReleased(mx, my + scroll, button);
	}

	@Override
	public void onDrag(int mx, int my, int button)
	{
		super.onDrag(mx, my + scroll, button);
	}

	@Override
	public IComponent getHovered(int mx, int my)
	{
		return super.getHovered(mx, my + scroll);
	}

	@Override
	public boolean canScrollV(int mx, int my)
	{
		if (scrollSize > 0) return true;
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollV(mx, my + scroll));
	}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY)
	{
		IComponent hovered = getHovered(mx, my);
		boolean hScrollV;
		if (hovered != null)
		{
			hScrollV = hovered.canScrollV(mx, my + scroll);
		}
		else hScrollV = false;
		if (hScrollV) hovered.onMouseScroll(mx, my + scroll, scrollX, scrollY);
		if (!hScrollV)
		{
			scroll -= scrollY / 6;
			if (scroll < 0) scroll = 0;
			else if (scroll > scrollSize) scroll = scrollSize;
		}
		//TODO if (mouseDown) onDrag(mx, my); //Because it's kinda a drag XD
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -scroll, 0);
		super.render(mc, mx, my + scroll, partialTicks, canHover);
		GlStateManager.popMatrix();
	}

	@Override
	public void tick(Minecraft mc, int mx, int my)
	{
		super.tick(mc, mx, my + scroll);
	}

	@Override
	public int getMaxScroll()
	{
		return scrollSize;
	}

	@Override
	public int getScroll()
	{
		return scroll;
	}

	@Override
	public void setScroll(int scroll)
	{
		this.scroll = scroll;
	}

	@Override
	public int getComponentOffsetY()
	{
		return super.getComponentOffsetY() - (int) Math.floor(scroll);
	}
}