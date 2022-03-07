package firemerald.api.betterscreens;

import java.awt.Rectangle;
import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.core.function.TriFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiPopupSelector extends GuiPopup
{
	public final Runnable onCancel;
	public IGuiElement from;
	public final ScrollableComponentPane pane;
	public final int numVals;

	public GuiPopupSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action)
	{
		this(from, values, action, (Runnable) null);
	}

	public <T> GuiPopupSelector(IGuiElement from, T[] values, BiConsumer<Integer, T> action, TriFunction<T, Rectangle, Runnable, IGuiElement> newButton)
	{
		this(from, values, action, null, newButton);
	}

	public GuiPopupSelector(IGuiElement from, String[] values, BiConsumer<Integer, String> action, Runnable onCancel)
	{
		this(from, values, action, onCancel, (str, size, onRelease) -> new Button(size.x, size.y, size.width, size.height, str, onRelease));
	}

	public <T> GuiPopupSelector(IGuiElement from, T[] values, BiConsumer<Integer, T> action, Runnable onCancel, TriFunction<T, Rectangle, Runnable, IGuiElement> newButton)
	{
		this.zLevel += 1000;
		this.onCancel = onCancel;
		this.from = from;
		numVals = values.length;
		int x1 = from.getSelectorX1(this);
		int y1 = from.getSelectorY1(this);
		int x2 = from.getSelectorX2(this);
		int y2 = from.getSelectorY2(this);
		this.addElement(pane = new ScrollableComponentPane(x1, y1, x2, y2));
		int w = x2 - x1;
		int h = y2 - y1;
		int y = 0;
		for (int i = 0; i < values.length; i++)
		{
			final int j = i;
			final T val = values[i];
			pane.addElement(newButton.apply(val, new Rectangle(0, y, w, h), () ->
			{
				deactivate();
				action.accept(j, val);
			}));
			y += h;
		}
		pane.updateComponentSize();
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int x1 = from.getSelectorX1(this);
		int y1 = from.getSelectorY1(this);
		int x2 = from.getSelectorX2(this);
		int y2 = from.getSelectorY2(this);
		int h = y2 - y1;
		int sY, eY;
		if ((y2 + y1) > height) //on bottom
		{
			sY = Math.max(0, y2 - h * numVals);
			eY = y2;
		}
		else
		{
			sY = y1;
			eY = Math.min(height, y1 + h * numVals);
		}
		pane.setSize(x1, sY, x2, eY);
	}

	public void cancel()
	{
		if (onCancel == null) deactivate();
		else onCancel.run();
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		super.onMousePressed(mx, my, button);
		if (button == 0 && this.focused == null) deactivate();
	}

	@Override
	public void doRender(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		GlStateManager.disableTexture2D();
		GlStateManager.color(0, 0, 0, .5f);
		GlStateManager.enableBlend();
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		b.pos(0    , height, 0).endVertex();
		b.pos(width, height, 0).endVertex();
		b.pos(width, 0     , 0).endVertex();
		b.pos(0    , 0     , 0).endVertex();
		t.draw();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
	}
}