package com.firemerald.custombgm.client.gui.components;

import com.firemerald.custombgm.client.BGMEngine;
import com.firemerald.fecore.client.gui.components.SpriteButton;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class TrackControlButton extends SpriteButton {
    public TrackControlButton(int x, int y, int width, int height, net.minecraft.network.chat.Component buttonText, WidgetSprites sprites, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, width, height, buttonText, sprites, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y, int width, int height,  WidgetSprites sprites, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, width, height, sprites, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y, int width, int height, net.minecraft.network.chat.Component buttonText, ResourceLocation sprite, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, width, height, buttonText, sprite, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y, int width, int height,  ResourceLocation sprite, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, width, height, sprite, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y, net.minecraft.network.chat.Component buttonText, WidgetSprites sprites, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, buttonText, sprites, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y,  WidgetSprites sprites, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, sprites, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y, net.minecraft.network.chat.Component buttonText, ResourceLocation sprite, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, buttonText, sprite, spriteWidth, spriteHeight, onClick);
    }

    public TrackControlButton(int x, int y,  ResourceLocation sprite, int spriteWidth, int spriteHeight, Runnable onClick) {
    	super(x, y, sprite, spriteWidth, spriteHeight, onClick);
    }

	@Override
	public boolean isActive() {
		return BGMEngine.isPlaying();
	}
}
