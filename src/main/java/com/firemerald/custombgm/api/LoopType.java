package com.firemerald.custombgm.api;

import com.firemerald.fecore.codec.Codecs;
import com.firemerald.fecore.codec.EnumCodec;
import com.firemerald.fecore.codec.stream.EnumStreamCodec;
import com.firemerald.fecore.codec.stream.StreamCodec;
import com.mojang.serialization.Codec;

public enum LoopType {
	FALSE("custombgm.gui.music.unlooped"),
	TRUE("custombgm.gui.music.looped"),
	SHUFFLE("custombgm.gui.music.shuffled");

	public static final Codec<LoopType> CODEC = Codecs.withAlternative(new EnumCodec<>(values()), Codec.BOOL.xmap(v -> v ? LoopType.TRUE : LoopType.FALSE, v -> v != LoopType.FALSE));
	public static final StreamCodec<LoopType> STREAM_CODEC = new EnumStreamCodec<>(LoopType.class);
	public final String guiKey;

	LoopType(String guiKey) {
		this.guiKey = guiKey;
	}
}
