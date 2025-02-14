package com.firemerald.custombgm.client;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.gui.components.TrackControlButton;
import com.firemerald.custombgm.client.gui.screen.TracksScreen;
import com.firemerald.custombgm.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ClientEventHandler {
	@SubscribeEvent
	public static void tickPost(ClientTickEvent.Post event) {
		BGMEngine.clientTick();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void selectMusic(SelectMusicEvent event) {
		if (BGMEngine.musicTick(event.getMusic(), Minecraft.getInstance())) event.setMusic(null);
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
		if (event.getScreen() instanceof TitleScreen titleScreen) {
			titleScreen.addRenderableWidget(new TrackControlButton(x1, y1, sizeX, sizeY, CustomBGMClient.PREVIOUS, iconWidth, iconHeight, BGMEngine::previousTrack));
			titleScreen.addRenderableWidget(new TrackControlButton(x2, y1, sizeX, sizeY, CustomBGMClient.RANDOM  , iconWidth, iconHeight, BGMEngine::randomTrack));
			titleScreen.addRenderableWidget(new TrackControlButton(x3, y1, sizeX, sizeY, CustomBGMClient.NEXT    , iconWidth, iconHeight, BGMEngine::nextTrack));
			titleScreen.addRenderableWidget(new TrackControlButton(x4, y1, sizeX, sizeY, CustomBGMClient.TRACKS  , iconWidth, iconHeight, () -> Minecraft.getInstance().setScreen(new TracksScreen())));
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
			ClientModEventHandler.getBGMProviders().onTagsLoaded(event.getLookupProvider());
		}
	}
}
