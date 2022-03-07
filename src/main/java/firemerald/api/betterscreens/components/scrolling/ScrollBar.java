package firemerald.api.betterscreens.components.scrolling;

import org.lwjgl.util.vector.Vector3f;

import firemerald.api.betterscreens.ButtonState;
import firemerald.api.betterscreens.components.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ScrollBar extends Component
{
	private float scrollSize;
	protected float scrollHeight;
	protected float scrollBarSize;
	public boolean enabled = false;
	public final IScrollable scrollable;
	private float pY = 0;
	protected boolean pressedScroll = false;
	private float pressedScrollVal;

	public ScrollBar(int x1, int y1, int x2, int y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		setSize(x1, y1, x2, y2);
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		scrollSize = y2 - y1;
		setMaxScroll();
	}

	public void setMaxScroll()
	{
		float size = scrollable.getMaxScroll();
		if (size <= 0)
		{
			enabled = false;
		}
		else
		{
			enabled = true;
			scrollBarSize = scrollSize * scrollSize / (scrollSize + size);
			if (scrollBarSize < 10) scrollBarSize = 10;
			scrollHeight = scrollSize - scrollBarSize;
		}
	}

	@Override
	public boolean canScrollH(int mx, int my)
	{
		return enabled;
	}

	@Override
	public boolean canScrollV(int mx, int my)
	{
		return enabled;
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partial, boolean canHover)
	{
		Gui.drawRect(x1, y1, x2, y2, 0xFF000000);
		if (enabled)
		{
			Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xFF404040);
			float scroll = scrollable.getScroll();
			float max = scrollable.getMaxScroll();
			ButtonState state;
			if (pressedScroll) state = ButtonState.PUSH;
			else
			{
				if (canHover && mx >= x1 + 1 && mx < x2 - 1)
				{
					float scrollPos = y1 + 1 + scrollHeight * scroll / max;
					if (my >= scrollPos && my < scrollPos + scrollBarSize) state = ButtonState.HOVER;
					else state = ButtonState.NONE;
				}
				else state = ButtonState.NONE;
			}
			//state.applyButtonEffects(false);
			Vector3f outerCol = state.getColor(.5f, .5f, .5f);
			Vector3f innerCol = state.getColor(1, 1, 1);
			float scrollY = y1 + scrollHeight * scroll / max;
	        GlStateManager.disableTexture2D();
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder bufferbuilder = tessellator.getBuffer();
	        GlStateManager.color(outerCol.x, outerCol.y, outerCol.z);
	        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
	        bufferbuilder.pos(x1, scrollY + scrollBarSize, 0).endVertex();
	        bufferbuilder.pos(x2, scrollY + scrollBarSize, 0).endVertex();
	        bufferbuilder.pos(x2, scrollY                , 0).endVertex();
	        bufferbuilder.pos(x1, scrollY                , 0).endVertex();
	        tessellator.draw();
	        GlStateManager.color(innerCol.x, innerCol.y, innerCol.z);
	        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
	        bufferbuilder.pos(x1 + 1, scrollY + scrollBarSize - 1, 0).endVertex();
	        bufferbuilder.pos(x2 - 1, scrollY + scrollBarSize - 1, 0).endVertex();
	        bufferbuilder.pos(x2 - 1, scrollY                 + 1, 0).endVertex();
	        bufferbuilder.pos(x1 + 1, scrollY                 + 1, 0).endVertex();
	        tessellator.draw();
	        GlStateManager.enableTexture2D();
			//state.removeButtonEffects();
		}
		else Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xFF202020);
        GlStateManager.color(1, 1, 1);
	}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY)
	{
		int size = scrollable.getMaxScroll();
		int scroll = scrollable.getScroll();
		scroll -= scrollY / 6;
		if (scroll < 0) scroll = 0;
		else if (scroll > size) scroll = size;
		scrollable.setScroll(scroll);
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		if (button == 0)
		{
			pY = my;
			if (mx >= x1 + 1 && mx < x2 - 1)
			{
				float scroll = scrollable.getScroll();
				float scrollPos = y1 + 1 + scrollHeight * scroll / scrollable.getMaxScroll();
				if (my >= scrollPos && my < scrollPos + scrollBarSize)
				{
					pressedScroll = true;
					pressedScrollVal = scroll;
				}
			}
		}
	}

	@Override
	public void onDrag(int mx, int my, int button)
	{
		if (button == 0 && pressedScroll)
		{
			float dY = my - pY;
			int max = scrollable.getMaxScroll();
			int scroll = (int) (pressedScrollVal + dY * max / scrollHeight);
			if (scroll < 0) scroll = 0;
			else if (scroll > max) scroll = max;
			scrollable.setScroll(scroll);
		}
	}

	@Override
	public void onMouseReleased(int mx, int my, int button)
	{
		if (button == 0)
		{
			pressedScroll = false;
		}
	}
}