package firemerald.api.betterscreens.components.text;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.gui.FontRenderer;

public class BetterTextField extends TextField
{
	private boolean valid = true;
	public Predicate<String> onChanged;

	public BetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, Consumer<String> onChanged)
	{
		this(componentId, fontrendererObj, x, y, w, h, v -> {
			onChanged.accept(v);
			return true;
		});
	}

	public BetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, Predicate<String> onChanged)
	{
		super(componentId, fontrendererObj, x, y, w, h);
		this.onChanged = onChanged;
	}

	public BetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String val, Consumer<String> onChanged)
	{
		this(componentId, fontrendererObj, x, y, w, h, onChanged);
		setString(val);
	}

	public BetterTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int w, int h, String val, Predicate<String> onChanged)
	{
		this(componentId, fontrendererObj, x, y, w, h, onChanged);
		setString(val);
	}

	@Override
    public void setText(String textIn)
    {
    	super.setText(textIn);
    	onChanged();
    }

	@Override
    public void setResponderEntryValue(int idIn, String textIn)
    {
		super.setResponderEntryValue(idIn, textIn);
		onChanged();
    }

    public void onChanged()
    {
    	boolean prevValid = valid;
    	try
    	{
    		valid = onChanged.test(getText());
    	}
    	catch (NumberFormatException e)
    	{
    		valid = false;
    	}
    	if (prevValid ^ valid) this.setTextColor(valid ? 0xE0E0E0 : 0xE00000);
    }

    public void setString(String v)
    {
		valid = true;
    	super.setText(v); //bypass setting val again
    }

    public boolean isValid()
    {
    	return valid;
    }
}
