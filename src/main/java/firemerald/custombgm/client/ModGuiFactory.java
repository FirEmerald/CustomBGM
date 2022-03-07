package firemerald.custombgm.client;

import java.util.Set;

import firemerald.custombgm.client.gui.GuiModOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ModGuiFactory implements IModGuiFactory
{
	public Minecraft mc;

	@Override
	public void initialize(Minecraft minecraftInstance)
	{
		mc = minecraftInstance;
	}

	@Override
	public boolean hasConfigGui()
	{
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new GuiModOptions(Minecraft.getMinecraft(), parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
}