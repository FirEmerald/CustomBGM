package firemerald.api.betterscreens;

public interface IGuiInteractable extends IGuiElement
{
	public abstract void onMousePressed(int mx, int my, int button);

	public abstract void onMouseReleased(int mx, int my, int button);

	public abstract void onDrag(int mx, int my, int button);

	default public boolean canScrollV(int mx, int my)
	{
		return false;
	}

	default public boolean canScrollH(int mx, int my)
	{
		return false;
	}

	public abstract void onMouseScroll(int mx, int my, int scrollX, int scrollY);

	public abstract boolean onKeyPressed(char chr, int scancode);

	public abstract boolean onKeyReleased(int scancode);
}