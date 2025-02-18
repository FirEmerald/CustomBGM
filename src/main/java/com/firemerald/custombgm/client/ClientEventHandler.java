package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.gui.components.TrackControlButton;
import com.firemerald.custombgm.client.gui.screen.TracksScreen;
import com.firemerald.custombgm.config.ClientConfig;
import com.firemerald.fecore.client.gui.components.WidgetSprites;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent.UpdateCause;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {
	public static WidgetSprites makeWidgetSprites(String name) {
		return new WidgetSprites(
				CustomBGMAPI.id("icon/" + name),
				CustomBGMAPI.id("icon/" + name + "_disabled"),
				CustomBGMAPI.id("icon/" + name + "_hovered"));
	}

	public static final WidgetSprites PREVIOUS = makeWidgetSprites("previous");
	public static final WidgetSprites RANDOM = makeWidgetSprites("random");
	public static final WidgetSprites NEXT = makeWidgetSprites("next");
	public static final WidgetSprites TRACKS = makeWidgetSprites("tracks");

	@SubscribeEvent
	public static void tickPost(ClientTickEvent event) {
		if (event.phase == Phase.END) BGMEngine.clientTick();
	}

	@SubscribeEvent
	public static void afterScreenInit(ScreenEvent.Init.Post event) {
		final int iconWidth = 11, iconHeight = 11;
		final int sizeX = iconWidth + 4, sizeY = iconHeight + 4;
		final int margin = 5;
		final int width = (4 * sizeX) + (3 * margin);
		final int height = (sizeY);
		final int x1 = ClientConfig.menuButtonsHorizontal.getLeft(event.getScreen().width, width, ClientConfig.menuButtonsHorizontalOffset);
		final int y1 = ClientConfig.menuButtonsVertical.getTop(event.getScreen().height, height, ClientConfig.menuButtonsVerticalOffset);
		final int x2 = x1 + sizeX + margin, x3 = x2 + sizeX + margin, x4 = x3 + sizeX + margin;
		if (event.getScreen() instanceof TitleScreen) {
			event.addListener(new TrackControlButton(x1, y1, sizeX, sizeY, PREVIOUS, iconWidth, iconHeight, BGMEngine::previousTrack));
			event.addListener(new TrackControlButton(x2, y1, sizeX, sizeY, RANDOM  , iconWidth, iconHeight, BGMEngine::randomTrack));
			event.addListener(new TrackControlButton(x3, y1, sizeX, sizeY, NEXT    , iconWidth, iconHeight, BGMEngine::nextTrack));
			event.addListener(new TrackControlButton(x4, y1, sizeX, sizeY, TRACKS  , iconWidth, iconHeight, () -> Minecraft.getInstance().setScreen(new TracksScreen())));
		}
	}

	@SubscribeEvent
	public static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		BGMEngine.serverOverride = null;
		ClientModEventHandler.getBGMProviders().onTagsUnloaded();
	}

	public static void onInput(InputEvent event)
	{
		if (ClientModEventHandler.TRACKS_MENU.consumeClick()) {
			Minecraft.getInstance().setScreen(new TracksScreen());
		}
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.Key event) {
		onInput(event);
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.MouseButton.Post event) {
		onInput(event);
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.MouseScrollingEvent event) {
		onInput(event);
	}

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == UpdateCause.CLIENT_PACKET_RECEIVED) {
			ClientModEventHandler.getBGMProviders().onTagsLoaded(event.getRegistryAccess());
		}
	}
}
