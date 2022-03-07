package firemerald.api.betterscreens.components;

import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonToggle extends Button
{
	public boolean state = true;

    public ButtonToggle(int x, int y, String buttonText, Consumer<Boolean> onClick)
    {
    	super(x, y, buttonText, null);
    	setToggleAction(onClick);
    }

    public ButtonToggle(int x, int y, int widthIn, int heightIn, String buttonText, Consumer<Boolean> onClick)
    {
    	super(x, y, widthIn, heightIn, buttonText, null);
    	setToggleAction(onClick);
    }

    public ButtonToggle(int x, int y, String buttonText, boolean state, Consumer<Boolean> onClick)
    {
    	super(x, y, buttonText, null);
    	this.state = state;
    	setToggleAction(onClick);
    }

    public ButtonToggle(int x, int y, int widthIn, int heightIn, String buttonText, boolean state, Consumer<Boolean> onClick)
    {
    	super(x, y, widthIn, heightIn, buttonText, null);
    	this.state = state;
    	setToggleAction(onClick);
    }

    public ButtonToggle setToggleAction(Consumer<Boolean> onClick)
    {
    	this.onClick = () -> onClick.accept(this.state = !this.state);
    	return this;
    }

    public ButtonToggle setToggleAction(Function<? super ButtonToggle, Consumer<Boolean>> onClickFunction)
    {
    	return setToggleAction(onClickFunction.apply(this));
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    @Override
	protected int getHoverState(boolean mouseOver)
    {
    	return this.state ? super.getHoverState(mouseOver) : 0;
    }
}
