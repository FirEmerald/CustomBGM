package com.firemerald.custombgm.network.clientbound;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.client.BGMEngine;
import com.firemerald.custombgm.providers.OverrideResults;
import com.firemerald.fecore.network.clientbound.ClientboundPacket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MusicSyncPacket extends ClientboundPacket<RegistryFriendlyByteBuf> {
	public static final Type<MusicSyncPacket> TYPE = new Type<>(CustomBGMAPI.id("music_sync"));

	private final OverrideResults override;

	public MusicSyncPacket(OverrideResults override) {
		this.override = override;
	}

	public MusicSyncPacket(RegistryFriendlyByteBuf buf) {
		override = OverrideResults.STREAM_CODEC.decode(buf);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buf) {
		OverrideResults.STREAM_CODEC.encode(buf, override);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleClient(IPayloadContext context) {
		BGMEngine.serverOverride = override; //this is now thread-safe
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
