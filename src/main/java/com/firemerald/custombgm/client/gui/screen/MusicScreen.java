package com.firemerald.custombgm.client.gui.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.client.gui.MusicSuggestions;
import com.firemerald.custombgm.util.WeightedBGM;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.decoration.FloatingText;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.client.gui.components.text.BetterTextField;
import com.firemerald.fecore.client.gui.components.text.FloatField;
import com.firemerald.fecore.client.gui.components.text.TextField;
import com.firemerald.fecore.client.gui.screen.PopupScreen;
import com.firemerald.fecore.distribution.DistributionUtil;
import com.firemerald.fecore.distribution.IDistribution;
import com.firemerald.fecore.util.SimpleCollector;
import com.mojang.blaze3d.platform.InputConstants;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MusicScreen extends PopupScreen {
    public Consumer<BgmDistribution> onAccept;
	public final FloatingText volumeLabel;
	public final FloatField volumeTxt;
	private float volume;
    public final Button okay, cancel;
    public final VerticalScrollableComponentPane musicControls;
    public final VerticalScrollBar musicControlsScroll;
    private final List<BGMComponents> musicEntries = new ArrayList<>();
    private final Button addEntry;
	public MusicSuggestions suggestions;

    /*
     * [    MUSIC    ]
     * weight [WEIGHT]
     * [loop ][remove]
     */

    public class BGMComponents {
    	//TODO null sounds?
    	public static final int SIZE_Y = 60;

    	public final BetterTextField musicTxt;
    	public final FloatingText weightLabel;
    	public final FloatField weightTxt;
    	public final Button loopButton;
    	public final Button removeButton;
    	public ResourceLocation music;
    	public LoopType loop;
    	public float weight;

    	public BGMComponents(int y, ResourceLocation music, LoopType loop, float weight) {
    		this.musicTxt = new BetterTextField(font, 0, y, 390, 20, music.toString(), Component.translatable("custombgm.gui.bgm.music.name.narrate"), str -> {
    			ResourceLocation newMusic = ResourceLocation.tryParse(str);
    			if (newMusic == null) return false;
    			else {
    				this.music = newMusic;
    				return true;
    			}
    		});
    		this.musicTxt.setMaxLength(Integer.MAX_VALUE);
    		this.musicTxt.setValue(music.toString());
    		this.musicTxt.setResponder(val -> {
        		if (suggestions != null) {
        			suggestions.setInput(musicTxt);
        			suggestions.updateCommandInfo();
        		}
    		});
    		this.weightLabel = new FloatingText(0, y + 20, 195, y + 40, font, I18n.get("custombgm.gui.music.weight"));
    		this.weightTxt = new FloatField(font, 195, y + 20, 195, 20, weight, Component.translatable("custombgm.gui.music.weight.narrate"), (FloatConsumer) (val -> this.weight = val));
    		this.loopButton = new Button(0, y + 40, 195, 20, Component.translatable(loop.guiKey), null);
    		loopButton.onClick = () -> {
    			switch (this.loop) {
    			case TRUE:
    				this.loop = LoopType.SHUFFLE;
    				break;
    			case SHUFFLE:
    				this.loop = LoopType.FALSE;
    				break;
    			case FALSE:
    				this.loop = LoopType.TRUE;
    				break;
    			}
    			loopButton.displayString = Component.translatable(this.loop.guiKey);
    		}; //TODO could be a selector dropdown? is that better?
    		this.removeButton = new Button(195, y + 40, 195, 20, Component.translatable("custombgm.gui.music.remove"), () -> removeMusicEntry(this));
    		this.music = music;
    		this.loop = loop;
    		this.weight = weight;
    	}

    	public BGMComponents(int y) {
    		this(y, new ResourceLocation("minecraft", "none"), LoopType.TRUE, 1f);
    	}

    	public void addComponent() {
    		musicControls.addComponent(musicTxt);
    		musicControls.addComponent(weightLabel);
    		musicControls.addComponent(weightTxt);
    		musicControls.addComponent(loopButton);
    		musicControls.addComponent(removeButton);
    	}

    	public void removeComponent() {
    		musicControls.removeComponent(musicTxt);
    		musicControls.removeComponent(weightLabel);
    		musicControls.removeComponent(weightTxt);
    		musicControls.removeComponent(loopButton);
    		musicControls.removeComponent(removeButton);
    	}

    	public void moveUp() {
			move(musicTxt, -SIZE_Y);
			move(weightLabel, -SIZE_Y);
			move(weightTxt, -SIZE_Y);
			move(loopButton, -SIZE_Y);
			move(removeButton, -SIZE_Y);
    	}
    }

	@SuppressWarnings("resource")
	public MusicScreen(BgmDistribution distribution, Consumer<BgmDistribution> onAccept) {
		super(Component.translatable("custombgm.gui.music"));
		this.font = Minecraft.getInstance().font;
		this.onAccept = onAccept;
		IDistribution<BGM> music = distribution.distribution();
		volume = distribution.volume();
		int y = 0;
		addRenderableWidget(volumeLabel = new FloatingText(0, y, 200, y + 20, font, I18n.get("custombgm.gui.music.volume")));
		addRenderableWidget(volumeTxt = new FloatField(font, 200, y, 200, 20, volume, Component.translatable("custombgm.gui.music.volume.narrate"), (FloatConsumer) (val -> this.volume = val)));
		y += 20;
		addRenderableWidget(musicControls = new VerticalScrollableComponentPane(0, y, 390, y + 100));
		addRenderableWidget(musicControlsScroll = new VerticalScrollBar(390, y, 400, y + 100, musicControls));
		musicControls.setScrollBar(musicControlsScroll);
		{
			int y2 = 0;
			if (!music.isEmpty()) {
				Iterator<WeightedBGM> values = (music.hasWeights() ?
						music.getWeightedValues().entrySet().stream().map(WeightedBGM::new) :
						music.getUnweightedValues()         .stream().map(WeightedBGM::new)
						).iterator();
				while (values.hasNext()) {
					WeightedBGM entry = values.next();
					BGMComponents components = new BGMComponents(y2, entry.sound(), entry.loop(), entry.weight());
					components.addComponent();
					musicEntries.add(components);
					y2 += BGMComponents.SIZE_Y;
				}
			}
			addEntry = new Button(95, y2, 200, 20, Component.translatable("custombgm.gui.music.add"), this::addMusicEntry);
			musicControls.addComponent(addEntry);
		}
		y += 100;
		addRenderableWidget(okay = new Button(0, y, 200, 20, Component.translatable("fecore.gui.done"), () -> {
			this.onAccept.accept(new BgmDistribution(DistributionUtil.get(musicEntries.stream().collect(SimpleCollector.toFloatMap(
					components -> new BGM(components.music, components.loop),
					components -> components.weight))), volume));
			this.deactivate();
		}));
		addRenderableWidget(cancel = new Button(200, y, 400, 20, Component.translatable("fecore.gui.cancel"), this::deactivate));
		updateScrollablePane();
	}

	@Override
	public void init() {
		int offX = (width - 400) >> 1;
		int y = 0;
		volumeLabel.setSize(offX, y, offX + 200, y + 20);
		volumeTxt.setSize(offX + 200, y, offX + 400, y + 20);
		y += 20;
		musicControls.setSize(offX, y, offX + 390, height - 20);
		musicControlsScroll.setSize(offX + 390, y, offX + 400, height - 20);
		okay.setSize(offX, height - 20, offX + 200, height);
		cancel.setSize(offX + 200, height - 20, offX + 400, height);
		addRenderableWidget(musicControls);
		addRenderableWidget(musicControlsScroll);
		addRenderableWidget(okay);
		addRenderableWidget(cancel);
		updateScrollablePane();
		suggestions = new MusicSuggestions(this.minecraft, this, null, this.font, 0, 7, Integer.MIN_VALUE);
		suggestions.setAllowSuggestions(true);
		suggestions.updateCommandInfo();
		suggestions.deactivate();
	}

	@Override
	public boolean keyPressed(int key, int scancode, int mods) {
		if (this.suggestions.keyPressed(key, scancode, mods)) return true;
		else  if (key == InputConstants.KEY_ESCAPE)
		{
			cancel.onClick.run();
			return true;
		}
		else return super.keyPressed(key, scancode, mods);
	}

	@Override
	public boolean mouseScrolled(double mX, double mY, double scrollY) {
		return this.suggestions.mouseScrolled(scrollY) ? true : super.mouseScrolled(mX, mY, scrollY);
	}

	@Override
	public boolean mouseClicked(double mX, double mY, int button) {
		return this.suggestions.mouseClicked(mX, mY, button) ? true : super.mouseClicked(mX, mY, button);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public void addMusicEntry() {
		musicControls.removeComponent(addEntry);
		BGMComponents entry = new BGMComponents(musicEntries.size() * BGMComponents.SIZE_Y);
		entry.addComponent();
		musicEntries.add(entry);
		move(addEntry, BGMComponents.SIZE_Y);
		musicControls.addComponent(addEntry);
		updateScrollablePane();
	}

	public void removeMusicEntry(BGMComponents entry) {
		entry.removeComponent();
		int i = musicEntries.indexOf(entry);
		musicEntries.remove(i);
		for (; i < musicEntries.size(); ++i) musicEntries.get(i).moveUp();
		move(addEntry, -BGMComponents.SIZE_Y);
		updateScrollablePane();
	}

	private static void move(com.firemerald.fecore.client.gui.components.Component component, int yChange) {
		component.setSize(component.getX1(), component.getY1() + yChange, component.getX2(), component.getY2() + yChange);
	}

	private static void move(TextField component, int yChange) {
		component.setSize(component.getX1(), component.getY1() + yChange, component.getX2(), component.getY2() + yChange);
	}

	public void updateScrollablePane() {
		musicControls.updateComponentSize();
		musicControls.updateScrollSize();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float partialTicks, boolean canHover) {
		super.render(guiGraphics, mx, my, partialTicks, canHover);
		this.suggestions.render(guiGraphics, mx, my);
	}
}