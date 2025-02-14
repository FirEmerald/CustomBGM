package com.firemerald.custombgm.api;

import com.firemerald.fecore.codec.EnumCodec;
import com.firemerald.fecore.codec.EnumStreamCodec;
import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum LoopType {
	FALSE("custombgm.gui.music.unlooped"),
	TRUE("custombgm.gui.music.looped"),
	SHUFFLE("custombgm.gui.music.shuffled");

	public static final Codec<LoopType> CODEC = Codec.withAlternative(new EnumCodec<>(values()), Codec.BOOL.xmap(v -> v ? LoopType.TRUE : LoopType.FALSE, v -> v != LoopType.FALSE));
	public static final StreamCodec<ByteBuf, LoopType> STREAM_CODEC = new EnumStreamCodec<>(LoopType::values);
	public final String guiKey;

	LoopType(String guiKey) {
		this.guiKey = guiKey;
	}
}
