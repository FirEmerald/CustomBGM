package com.firemerald.custombgm.network.clientbound;

import com.firemerald.custombgm.client.BGMEngine;
import com.firemerald.custombgm.providers.OverrideResults;
import com.firemerald.fecore.network.client.ClientPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class MusicSyncPacket extends ClientPacket {
	private final OverrideResults override;

	public MusicSyncPacket(OverrideResults override) {
		this.override = override;
	}

	public MusicSyncPacket(FriendlyByteBuf buf) {
		override = OverrideResults.STREAM_CODEC.decode(buf);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		OverrideResults.STREAM_CODEC.encode(buf, override);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleClient(Context ctx) {
		BGMEngine.serverOverride = override; //this is now thread-safe
	}
}
