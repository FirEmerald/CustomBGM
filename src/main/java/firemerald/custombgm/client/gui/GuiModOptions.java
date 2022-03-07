package firemerald.custombgm.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableBoolean;

import firemerald.api.betterscreens.EnumTextAlignment;
import firemerald.api.betterscreens.GuiBetterScreen;
import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.scrolling.ScrollBar;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.betterscreens.components.text.BetterTextField;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.api.config.ConfigValueFloat;
import firemerald.api.config.ConfigValueInt;
import firemerald.api.core.client.Translator;
import firemerald.custombgm.client.ConfigClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("unused")
public class GuiModOptions extends GuiBetterScreen
{
	public static interface IOption
	{
		public boolean isChanged();

		public void reset();
	}

	public static class BooleanOption extends Button implements IOption
	{
		private final boolean original;
		public final ConfigValueBoolean option;

		public BooleanOption(int x, int y, int w, int h, ConfigValueBoolean option, String translationKey)
		{
			super(x, y, w, h, Translator.format(translationKey, option.val), null);
			this.onClick = () -> this.displayString = Translator.format(translationKey, option.val = !option.val);
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	public static class IntegerOption extends BetterTextField implements IOption
	{
		private final int original;
		public final ConfigValueInt option;

		public IntegerOption(int x, int y, int w, int h, ConfigValueInt option)
		{
			super(-1, Minecraft.getMinecraft().fontRenderer, x, y, w, h, Integer.toString(option.val), (Predicate<String>) null);
			this.onChanged = str -> {
				try
				{
					int val = Integer.parseInt(str);
					if (val >= option.min && val <= option.max)
					{
						option.val = val;
						return true;
					}
					else return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			};
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	public static class FloatOption extends BetterTextField implements IOption
	{
		private final float original;
		public final ConfigValueFloat option;

		public FloatOption(int x, int y, int w, int h, ConfigValueFloat option)
		{
			super(-1, Minecraft.getMinecraft().fontRenderer, x, y, w, h, Float.toString(option.val), (Predicate<String>) null);
			this.onChanged = str -> {
				try
				{
					float val = Float.parseFloat(str);
					if (val >= option.min && val <= option.max)
					{
						option.val = val;
						return true;
					}
					else return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			};
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	private final Minecraft mc;
	private final GuiScreen parentGuiScreen;

	private final ScrollableComponentPane pane;
	private final ScrollBar scrollBar;
	private final FloatingText optionsLabel;

	private final FloatingText preloadedBuffersLabel;
	private final IntegerOption preloadedBuffers;

	private final Button okay, cancel;
	private final List<IOption> clientOptions = new ArrayList<>();

	public GuiModOptions(Minecraft minecraft, GuiScreen screen)
	{
		this.mc = minecraft;
		this.parentGuiScreen = screen;
		ConfigClientOptions clientConfig = ConfigClientOptions.INSTANCE;
		this.addElement(optionsLabel = new FloatingText(0, 0, 336, 20, mc.fontRenderer, Translator.translate("options.custombgm.options"), EnumTextAlignment.CENTER));
		this.addElement(pane = new ScrollableComponentPane(10, 24, 320, 336));
		this.addElement(scrollBar = new ScrollBar(320, 24, 336, 336, pane));
		int y = 0;
		pane.addElement(preloadedBuffersLabel = new FloatingText(0, y, 150, 20, mc.fontRenderer, Translator.translate("options.custombgm.preloadedbuffers"), EnumTextAlignment.RIGHT));
		this.addClient(preloadedBuffers = new IntegerOption(150, y, 150, 20, clientConfig.preloadedBuffers));
		y += 24;
		this.addElement(okay = new Button(0, 340, 163, 20, Translator.format("okay"), () -> {
			MutableBoolean save = new MutableBoolean(false);
			clientOptions.forEach(o -> {
				if (o.isChanged()) save.setTrue();
			});
			if (save.booleanValue()) clientConfig.saveConfig();
			save.setFalse();
			this.mc.displayGuiScreen(parentGuiScreen);
		}));
		this.addElement(cancel = new Button(173, 340, 336, 20, Translator.format("cancel"), () -> {
			clientOptions.forEach(IOption::reset);
			this.mc.displayGuiScreen(parentGuiScreen);
		}));
		pane.updateComponentSize();
		pane.updateScrollSize();
	}

	private <T extends IGuiElement & IOption> void addClient(T element)
	{
		pane.addElement(element);
		clientOptions.add(element);
	}

	@Override
    public void initGui()
    {
		int offX, offY;
		int maxH = 48 + pane.height;
		int paneY2;
		if (this.height > maxH)
		{
			offY = (this.height - maxH) / 2;
			paneY2 = offY + 24 + pane.height;
		}
		else
		{
			offY = 0;
			paneY2 = this.height - 24;
		}
		offX = (this.width - 336) / 2;
		optionsLabel.setSize(offX, offY, offX + 336, offY + 20);
		pane.setSize(offX + 10, offY + 24, offX + 320, paneY2);
		scrollBar.setSize(offX + 320, offY + 24, offX + 336, paneY2);
		okay.setSize(offX, paneY2 + 4, offX + 163, paneY2 + 24);
		cancel.setSize(offX + 173, paneY2 + 4, offX + 336, paneY2 + 24);
		pane.updateScrollSize();
    }

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		this.drawDefaultBackground();
		super.render(mc, mx, my, partialTicks, canHover);
	}
}