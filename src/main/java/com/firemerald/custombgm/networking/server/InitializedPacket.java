package com.firemerald.custombgm.networking.server;

import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.fecore.networking.server.ServerPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class InitializedPacket extends ServerPacket
{
	public InitializedPacket() {}

	public InitializedPacket(FriendlyByteBuf buf) {}

	@Override
	public void write(FriendlyByteBuf buf) {}

	@Override
	public void handleServer(Context context)
	{
		final Player player = context.getSender();
		context.enqueueWork(() -> {
			IPlayer.get(player).ifPresent(iPlayer -> iPlayer.setInit(true));
		});
	}
}
