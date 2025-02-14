package com.firemerald.custombgm.providers.conditions;

import java.util.Arrays;
import java.util.Collection;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.holderset.HolderCondition;
import com.google.common.base.Optional;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public class VanillaBGMCondition extends HolderCondition<SoundEvent> {
	public static final MapCodec<VanillaBGMCondition> CODEC = getCodec(Registries.SOUND_EVENT, "music", VanillaBGMCondition::new);

	private VanillaBGMCondition(HolderSet<SoundEvent> holderSet) {
		super(holderSet);
	}

	@Override
	public Holder<SoundEvent> getHolder(PlayerConditionData player) {
		Optional<MusicInfo> bgmOpt = player.getVanillaBGM();
		MusicInfo info;
		Music music;
		return bgmOpt.isPresent() && (info = bgmOpt.get()) != null && (music = info.music()) != null ? music.getEvent() : null;
	}

	@Override
	public MapCodec<VanillaBGMCondition> codec() {
		return CODEC;
	}

	public static class Builder extends HolderCondition.Builder<SoundEvent, VanillaBGMCondition, Builder> {
		public Builder(Provider provider) {
			super(provider, Registries.SOUND_EVENT);
		}

		public Builder setMusic(Music music) {
			if (music == null) throw new IllegalStateException("Attempted to add a null Music");
			return this.setHolder(music.getEvent());
		}

		public Builder setMusics(Music... musics) {
            return setHolders(Arrays.stream(musics).map(Music::getEvent).toList());
		}

		public Builder setMusics(Collection<Music> musics) {
            return setHolders(musics.stream().map(Music::getEvent).toList());
		}

		@Override
		public VanillaBGMCondition build() {
			return new VanillaBGMCondition(holderSet);
		}
	}
}