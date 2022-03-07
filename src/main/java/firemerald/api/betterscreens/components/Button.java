package firemerald.api.betterscreens.components;

import java.util.function.Function;

import firemerald.api.betterscreens.IGuiHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class Button extends Gui implements IComponent
{
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    /** Button width in pixels */
    public int width;
    /** Button height in pixels */
    public int height;
    /** The x position of this control. */
    public int x;
    /** The y position of this control. */
    public int y;
    /** The string displayed on this control. */
    public String displayString;
    /** True if this control is enabled, false to disable. */
    public boolean enabled;
    public int packedFGColour; //FML
    public Runnable onClick;
	public boolean focused = false;
	public IGuiHolder holder = null;

    public Button(int x, int y, String buttonText, Runnable onClick)
    {
        this(x, y, 200, 20, buttonText, onClick);
    }

    public Button(int x, int y, int widthIn, int heightIn, String buttonText, Runnable onClick)
    {
        this.enabled = true;
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.onClick = onClick;
    }

    public Button setAction(Runnable onClick)
    {
    	this.onClick = onClick;
    	return this;
    }

    public Button setAction(Function<? super Button, Runnable> onClickFunction)
    {
    	return setAction(onClickFunction.apply(this));
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
    	return this.enabled ? mouseOver ? 2 : 1 : 0;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
    {
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        boolean hovered = canHover && this.contains(mx, my);
        int i = this.getHoverState(hovered);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (this.height == 20)
        {
        	if (this.width == 200)
        	{
                this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width, this.height);
        	}
        	else
        	{
                this.drawTexturedModalRect(this.x                 , this.y, 0                   , 46 + i * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        	}
        }
        else
        {
        	if (this.width == 200)
        	{
                this.drawTexturedModalRect(this.x, this.y                  , 0, 46 + i * 20                  , this.width, this.height / 2);
                this.drawTexturedModalRect(this.x, this.y + this.height / 2, 0, 66 + i * 20 - this.height / 2, this.width, this.height / 2);
        	}
        	else
        	{
                this.drawTexturedModalRect(this.x                 , this.y                  , 0                   , 46 + i * 20                  , this.width / 2, this.height / 2);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y                  , 200 - this.width / 2, 46 + i * 20                  , this.width / 2, this.height / 2);
                this.drawTexturedModalRect(this.x                 , this.y + this.height / 2, 0                   , 66 + i * 20 - this.height / 2, this.width / 2, this.height / 2);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2, 66 + i * 20 - this.height / 2, this.width / 2, this.height / 2);
        	}
        }
        int j = 14737632;
        if (packedFGColour != 0) j = packedFGColour;
        else if (!this.enabled) j = 10526880;
        else if (hovered) j = 16777120;
        this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
	public void onMousePressed(int mx, int my, int button)
    {
        if (this.enabled && button == 0) onClick.run();
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {}

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getButtonWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

	@Override
	public int getX1()
	{
		return x;
	}

	@Override
	public int getY1()
	{
		return y;
	}

	@Override
	public int getX2()
	{
		return x + width;
	}

	@Override
	public int getY2()
	{
		return y + height;
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

	@Override
	public void onFocus()
	{
		this.focused = true;
	}

	@Override
	public void onUnfocus()
	{
		this.focused = false;
	}

	@Override
	public void onMouseReleased(int mx, int my, int button) {}

	@Override
	public void onDrag(int mx, int my, int button) {}

	@Override
	public void onMouseScroll(int mx, int my, int scrollX, int scrollY) {}

	@Override
	public void tick(Minecraft mc, int mx, int my) {}

	@Override
	public boolean onKeyPressed(char chr, int scancode)
	{
		return false;
	}

	@Override
	public boolean onKeyReleased(int scancode)
	{
		return false;
	}

	@Override
	public boolean contains(float x, float y)
	{
		return (x >= getX1() && y >= getY1() && x < getX2() && y < getY2());
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		this.x = x1;
		this.y = y1;
		this.width = x2 - x1 + 1;
		this.height = y2 - y1;
	}
}
