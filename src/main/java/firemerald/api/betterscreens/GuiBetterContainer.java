package firemerald.api.betterscreens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import firemerald.api.betterscreens.components.IComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;

public abstract class GuiBetterContainer extends GuiContainer implements IGuiInteractable, IGuiHolder
{
	private final List<IGuiElement> guiElements = new ArrayList<>();
	public IComponent focused;
	public IGuiHolder holder = null;

	public GuiBetterContainer(Container inventorySlotsIn)
	{
		super(inventorySlotsIn);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if (Mouse.getEventDWheel() != 0) this.onMouseScroll(i, j, 0, Mouse.getEventDWheel()); //TODO horizontal scolling
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawScreen(mouseX, mouseY, partialTicks, true);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean canHover)
	{
		super.drawScreen(canHover ? mouseX : Integer.MIN_VALUE, canHover ? mouseY : Integer.MIN_VALUE, partialTicks);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
		this.render(mc, mouseX, mouseY, partialTicks, canHover);
	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		super.keyTyped(typedChar, keyCode);
		this.onKeyPressed(typedChar, keyCode);
    }

	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.onMousePressed(mouseX, mouseY, mouseButton);
    }

	@Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		super.mouseReleased(mouseX, mouseY, state);
		this.onMouseReleased(mouseX, mouseY, state);
    }

	@Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		this.onDrag(mouseX, mouseY, clickedMouseButton);
    }

	@Override
    public void updateScreen()
    {
		super.updateScreen();
		this.tick(mc, Mouse.getX(), Mouse.getY());
    }

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

	public synchronized IGuiElement[] getElementsCopy()
	{
		return guiElements.toArray(new IGuiElement[guiElements.size()]);
	}

	@Override
	public void tick(Minecraft mc, int mx, int my)
	{
		for (IGuiElement element : getElementsCopy()) element.tick(mc, mx, my);
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		for (IGuiElement element : getElementsCopy()) element.render(mc, mx, my, partialTicks, canHover);
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		if (button == 0 || !Mouse.isButtonDown(0))
		{
			IComponent preFocused = focused;
			focused = null;
			IGuiElement[] elements = getElementsCopy();
			for (int i = elements.length - 1; i >= 0; i--)
			{
				IGuiElement element = elements[i];
				if (element instanceof IComponent)
				{
					IComponent c = (IComponent) element;
					if (c.contains(mx, my))
					{
						focused = c;
						break;
					}
				}
			}
			if (preFocused != focused)
			{
				if (preFocused != null) preFocused.onUnfocus();
				if (focused != null) focused.onFocus();
			}
		}
		if (focused != null) focused.onMousePressed(mx, my, button);
	}

	public IComponent getHovered(int mx, int my)
	{
		IGuiElement[] elements = getElementsCopy();
		for (int i = elements.length - 1; i >= 0; i--)
		{
			IGuiElement element = elements[i];
			if (element instanceof IComponent)
			{
				IComponent c = (IComponent) element;
				if (c.contains(mx, my)) return c;
			}
		}
		return null;
	}

	@Override
	public void onMouseReleased(int mx, int my, int button)
	{
		if (focused != null) focused.onMouseReleased(mx, my, button);
	}

	@Override
	public void onDrag(int mx, int my, int button)
	{
		if (focused != null) focused.onDrag(mx, my, button);
	}

	@Override
	public boolean canScrollH(int mx, int my)
	{
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollH(mx, my));
	}

	@Override
	public boolean canScrollV(int mx, int my)
	{
		IComponent hovered;
		return ((hovered = getHovered(mx, my)) != null && hovered.canScrollV(mx, my));
	}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY)
	{
		IComponent hovered = getHovered(mx, my);
		if (hovered != null && (hovered.canScrollH(mx, my) || hovered.canScrollV(mx, my))) hovered.onMouseScroll(mx, my, scrollX, scrollY);
	}

	@Override
	public boolean onKeyPressed(char key, int scancode)
	{
		if (focused != null) return focused.onKeyPressed(key, scancode);
		else return false;
	}

	@Override
	public boolean onKeyReleased(int scancode)
	{
		if (focused != null) return focused.onKeyReleased(scancode);
		else return false;
	}

	public void addElement(IGuiElement element)
	{
		if (element != null)
		{
			guiElements.add(element);
			element.setHolder(this);
		}
	}

	public void removeElement(IGuiElement element)
	{
		if (element != null)
		{
			guiElements.remove(element);
			if (focused == element)
			{
				focused.onUnfocus();
				focused = null;
			}
			element.setHolder(null);
		}
	}

	@Override
	public int getComponentOffsetX()
	{
		return 0;
	}

	@Override
	public int getComponentOffsetY()
	{
		return 0;
	}

	@Override
	public int getX1()
	{
		return 0;
	}

	@Override
	public int getY1()
	{
		return 0;
	}

	@Override
	public int getX2()
	{
		return width;
	}

	@Override
	public int getY2()
	{
		return height;
	}
}