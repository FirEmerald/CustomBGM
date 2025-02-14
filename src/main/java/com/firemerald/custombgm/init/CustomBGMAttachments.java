package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.attachments.BossTracker;
import com.firemerald.custombgm.attachments.ServerPlayerData;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CustomBGMAttachments {
	private static DeferredRegister<AttachmentType<?>> registry = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CustomBGMAPI.MOD_ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ServerPlayerData>> SERVER_PLAYER_DATA = registry.register("player", () -> AttachmentType.builder(holder -> new ServerPlayerData()).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<BossTracker>> BOSS_TRACKER = registry.register("boss_tracker", () -> AttachmentType.serializable(BossTracker::new).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ServerPlayer>> PLAYER_TARGET = registry.register("targeter", () -> AttachmentType.<ServerPlayer>builder(() -> null).build());

	public static void init(IEventBus bus) {
		registry.register(bus);
		registry = null;
	}
}
