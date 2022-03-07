package firemerald.api.betterscreens;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public abstract class GuiPopup extends GuiBetterScreen
{
	public GuiScreen under;
	public boolean active = false;

	public void activate()
	{
		active = true;
		under = Minecraft.getMinecraft().currentScreen;
		Minecraft.getMinecraft().displayGuiScreen(this);
	}

	@Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
		under.setWorldAndResolution(mc, width, height);
		super.setWorldAndResolution(mc, width, height);
    }

	@Override
	public void initGui()
	{
		under.initGui();
		super.initGui();
	}

	public void deactivate()
	{
		active = false;
		mc.displayGuiScreen(under);
	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1) deactivate();
        else super.keyTyped(typedChar, keyCode);
    }

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		if (under instanceof GuiBetterScreen) ((GuiBetterScreen) under).drawScreen(mx, my, partialTicks, false);
		else if (under instanceof GuiBetterContainer) ((GuiBetterContainer) under).drawScreen(mx, my, partialTicks, false);
		else under.drawScreen(mx, my, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 300);
		doRender(mc, mx, my, partialTicks, canHover);
		super.render(mc, mx, my, partialTicks, canHover);
		GlStateManager.popMatrix();
	}

	public abstract void doRender(Minecraft mc, int mx, int my, float partialTicks, boolean canHover);
}