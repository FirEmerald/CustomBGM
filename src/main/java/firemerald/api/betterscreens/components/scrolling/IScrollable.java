package firemerald.api.betterscreens.components.scrolling;

public interface IScrollable
{
	public int getMaxScroll();

	public int getScroll();

	public void setScroll(int scroll);
}