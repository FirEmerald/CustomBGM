package com.firemerald.custombgm.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

import com.firemerald.custombgm.client.BGMEngine;
import com.firemerald.custombgm.util.VolumedBGM;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.IComponent;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.client.gui.screen.BetterScreen;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;

public class TracksScreen extends BetterScreen
{
	public class TrackButton extends Button {
		public final VolumedBGM bgm;

	    public TrackButton(int x, int y, VolumedBGM bgm) {
	    	super(x, y, bgm.getName(), null);
	    	this.bgm = bgm;
	    	setAction();
	    }

		public TrackButton(int x, int y, int widthIn, int heightIn, VolumedBGM bgm) {
	    	super(x, y, widthIn, heightIn, bgm.getName(), null);
	    	this.bgm = bgm;
	    	setAction();
		}

		private void setAction() {
			this.onClick = () -> BGMEngine.setTarget(bgm);
		}

		public boolean isPlaying() {
			return BGMEngine.isPlaying(bgm);
		}

	    @Override
	    public boolean renderAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && isPlaying();
	    }

	    @Override
	    public boolean renderTextAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && (isPlaying() || this.renderTextAsFocused(hovered));
	    }

	    @Override
	    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int margin, int color) {
	    	Component leftText = this.getMessage();
	    	Component rightText = Component.translatable(bgm.loop().guiKey);
	        int leftWidth = font.width(leftText);
	        int rightWidth = font.width(rightText);
	        int totalSize = leftWidth + rightWidth;
	        int effectiveWidth = this.getWidth() - (margin * 4);
	        if (totalSize <= effectiveWidth) {
		        renderScrollingString(guiGraphics, font, leftText, this.getX1() + margin, this.getY1(), this.getX1() + margin + leftWidth, this.getY2(), color);
		        renderScrollingString(guiGraphics, font, rightText, this.getX2() - margin - rightWidth, this.getY1(), this.getX2() - margin, this.getY2(), color);
	        } else {
		        double leftFactor = ((double) leftWidth) / ((double) totalSize);
		        int separator = this.getX1() + margin + (int) (effectiveWidth * leftFactor) + margin;
		        renderScrollingString(guiGraphics, font, leftText, this.getX1() + margin, this.getY1(), separator - margin, this.getY2(), color);
		        renderScrollingString(guiGraphics, font, rightText, separator + margin, this.getY1(), this.getX2() - margin, this.getY2(), color);
	        }
	    }
	}

	public class SoundButton extends Button {
		public final VolumedBGM bgm;
		public final Sound sound;

	    public SoundButton(int x, int y, VolumedBGM bgm, Sound sound) {
	    	super(x, y, Component.literal(sound.getLocation().toString()), null);
	    	this.bgm = bgm;
	    	this.sound = sound;
	    	setAction();
	    }

		public SoundButton(int x, int y, int widthIn, int heightIn, VolumedBGM bgm, Sound sound) {
	    	super(x, y, widthIn, heightIn, Component.literal(sound.getLocation().toString()), null);
	    	this.bgm = bgm;
	    	this.sound = sound;
	    	setAction();
		}

		private void setAction() {
			this.onClick = () -> BGMEngine.setTarget(bgm, sound);
		}

		public boolean isPlaying() {
			return BGMEngine.isPlaying(sound, bgm.loop());
		}

	    @Override
	    public boolean renderAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && isPlaying();
	    }

	    @Override
	    public boolean renderTextAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && (isPlaying() || this.renderTextAsFocused(hovered));
	    }
	}

    public final VerticalScrollableComponentPane trackButtons;
    public final VerticalScrollBar trackButtonsScroll;
    public final Button exit;
    private final List<IComponent> trackComponents = new ArrayList<>();
	private IDistribution<VolumedBGM> music = EmptyDistribution.get();

	@SuppressWarnings("resource")
	public TracksScreen()
	{
		super(Component.translatable("custombgm.gui.tracks"));
		this.font = Minecraft.getInstance().font;
		trackButtons = new VerticalScrollableComponentPane(0, 0, 200, 200);
		trackButtonsScroll = new VerticalScrollBar(200, 0, 210, 200, trackButtons);
		trackButtons.setScrollBar(trackButtonsScroll);

		exit = new Button(0, 200, 200, 20, Component.translatable("fecore.gui.exit"), this::onClose);
		rebuildTracks(BGMEngine.clientOverride);
	}

	@Override
	public void tick() {
		if (!BGMEngine.clientOverride.equals(music)) rebuildTracks(BGMEngine.clientOverride);
	}

	@Override
	public void init()
	{
		int scrollWidth = Math.min(width, 300);
		int scrollX1 = (width - scrollWidth) / 2;
		int scrollX3 = scrollX1 + scrollWidth;
		int scrollX2 = scrollX3 - 10;
		int buttonWidth = Math.min(200, width);
		int buttonX1 = (width - buttonWidth) / 2;
		int buttonX2 = buttonX1 + buttonWidth;
		int y1 = 0;
		int y2 = height - 20;
		int y3 = height;
		trackButtons.setSize(scrollX1, y1, scrollX2, y2);
		trackButtonsScroll.setSize(scrollX2, y1, scrollX3, y2);
		exit.setSize(buttonX1, y2, buttonX2, y3);

		addRenderableWidget(trackButtons);
		addRenderableWidget(trackButtonsScroll);
		addRenderableWidget(exit);
		rebuildTracks(BGMEngine.clientOverride);
	}

	public void rebuildTracks(IDistribution<VolumedBGM> tracks) {
		this.music = tracks;
		trackComponents.forEach(trackButtons::removeComponent);
		int y = 0;
		int width = trackButtons.getWidth();
		for (VolumedBGM track : tracks.getValues()) {
			TrackButton button = new TrackButton(0, y, width, 20, track);
			trackButtons.addComponent(button);
			trackComponents.add(button);
			y += 20;
			List<Sound> sounds = new ArrayList<>();
			if (track.sound().equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
				sounds.add(SoundManager.INTENTIONALLY_EMPTY_SOUND);
			} else {
				WeighedSoundEvents soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(track.sound());
				if (soundEvent != null) soundEvent.getSounds(sounds);
			}
			for (Sound sound : sounds) {
				Button button2 = new SoundButton(20, y, width - 20, 20, track, sound);
				trackButtons.addComponent(button2);
				trackComponents.add(button2);
				y += 20;
			}
		}
		trackButtons.updateScrollSize();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float partialTicks, boolean canHover)
	{
		this.renderBackground(guiGraphics, mx, my, partialTicks);
		super.render(guiGraphics, mx, my, partialTicks, canHover);
	}
}