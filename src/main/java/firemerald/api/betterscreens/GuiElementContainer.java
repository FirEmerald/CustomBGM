package firemerald.api.betterscreens;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import firemerald.api.betterscreens.components.IComponent;
import net.minecraft.client.Minecraft;

public abstract class GuiElementContainer implements IGuiInteractable, IGuiHolder
{
	private final List<IGuiElement> guiElements = new ArrayList<>();
	public IComponent focused;
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
	public boolean onKeyPressed(char chr, int scancode)
	{
		if (focused != null) return focused.onKeyPressed(chr, scancode);
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
}