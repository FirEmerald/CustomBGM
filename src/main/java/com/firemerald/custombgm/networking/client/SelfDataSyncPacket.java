package com.firemerald.custombgm.networking.client;

import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.ClientState;
import com.firemerald.fecore.networking.client.ClientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class SelfDataSyncPacket extends ClientPacket
{
	private final ResourceLocation music;
	private final int priority;

	public SelfDataSyncPacket(IPlayer player)
	{
		music = player.getMusicOverride();
		priority = player.getCurrentPriority();
	}

	public SelfDataSyncPacket(FriendlyByteBuf buf)
	{
		String music = buf.readUtf();
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		this.priority = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(music == null ? "" : music.toString());
		buf.writeVarInt(priority);
	}

	@SuppressWarnings("resource")
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleClient(Context context)
	{
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().player != null)
			{
				IPlayer player = ClientState.clientPlayer;
				if (player != null) player.setServerMusic(music, priority);
			}
		});
	}
}
