package com.firemerald.custombgm.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.function.TriConsumer;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.datagen.impl.MusicProviderProvider;
import com.firemerald.custombgm.providers.BaseMusicProvider;
import com.firemerald.custombgm.providers.BiomeMusicProvider;
import com.firemerald.custombgm.providers.ScreenMusicProvider;
import com.firemerald.custombgm.providers.conditions.PlayBossMusicCondition;
import com.firemerald.custombgm.providers.conditions.VanillaBGMCondition;
import com.firemerald.custombgm.providers.conditions.modifier.AndCondition;
import com.firemerald.custombgm.providers.conditions.modifier.NotCondition;
import com.firemerald.custombgm.providers.conditions.player.InGameCondition;
import com.firemerald.custombgm.providers.conditions.player.attributes.GameModeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.BiomeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.DimensionCondition;
import com.firemerald.custombgm.providers.conditions.player.location.UnderwaterCondition;
import com.firemerald.custombgm.providers.volume.BiomeVolume;
import com.firemerald.custombgm.util.BGMDistributionBuilder;
import com.firemerald.fecore.distribution.EmptyDistribution;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;

public class CustomBGMClientMusicProviderProvider extends MusicProviderProvider {
	public CustomBGMClientMusicProviderProvider(PackOutput output, CompletableFuture<Provider> completableFuture) {
		super(output, Target.RESOURCE_PACK, CustomBGMAPI.MOD_ID, completableFuture);
	}

	@Override
	public void generate(Provider provider, TriConsumer<String, List<ICondition>, BGMProvider> register) {
		register.accept("menu_test", TEST_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new VanillaBGMCondition.Builder(provider).setMusic(Musics.MENU).build())
				.setMusic(new BGMDistributionBuilder("mc4")
						.add("mpt.screen.title")
						.add("mp1.screen.bootup", LoopType.FALSE)
						.add("mp1.screen.menu")
						.add("mp2.screen.bootup", LoopType.FALSE)
						.add("mp2.screen.menu")
						.add("mp3.screen.title")
						.add(SoundEvents.VILLAGER_AMBIENT, LoopType.SHUFFLE)
						.add(Musics.MENU.getEvent(), LoopType.SHUFFLE)
						.build())
				.build());
		register.accept("vanilla", NEVER, new BaseMusicProvider.Builder()
				.setMusic(EmptyDistribution.get())
				.setPriority(Integer.MAX_VALUE)
				.build());


		register.accept("screen", VANILLA_CONDITIONS, new ScreenMusicProvider.Builder()
				.setPriority(8)
				.build()
				);
		register.accept("end_boss", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new AndCondition.Builder()
						.addCondition(PlayBossMusicCondition.TRUE)
						.addCondition(new DimensionCondition.Builder()
								.addKey(Level.END)
								.build())
						.build())
				.setMusic(new BGM(Musics.END_BOSS))
				.setPriority(7)
				.build());
		register.accept("end", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new AndCondition.Builder()
						.addCondition(new DimensionCondition.Builder()
								.addKey(Level.END)
								.build())
						.build())
				.setMusic(new BGM(Musics.END))
				.setPriority(6)
				.build());
		register.accept("underwater", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new AndCondition.Builder()
						.addCondition(UnderwaterCondition.TRUE)
						.addCondition(new BiomeCondition.Builder(provider)
								.setTag(BiomeTags.PLAYS_UNDERWATER_MUSIC)
								.build())
						.build())
				.setVolume(BiomeVolume.INSTANCE)
				.setMusic(new BGM(Musics.UNDER_WATER))
				.setPriority(5)
				.build());
		register.accept("creative", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new AndCondition.Builder()
						.addCondition(GameModeCondition.of(GameType.CREATIVE))
						.addCondition(new NotCondition(new DimensionCondition.Builder()
								.addKey(Level.NETHER)
								.build()))
						.build())
				.setVolume(BiomeVolume.INSTANCE)
				.setMusic(new BGM(Musics.CREATIVE))
				.setPriority(4)
				.build());
		register.accept("biome", VANILLA_CONDITIONS, new BiomeMusicProvider.Builder()
				.setPriority(3)
				.build());
		register.accept("in_game", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(InGameCondition.TRUE)
				.setVolume(BiomeVolume.INSTANCE)
				.setMusic(new BGM(Musics.GAME))
				.setPriority(2)
				.build());
		register.accept("menu", VANILLA_CONDITIONS, new BaseMusicProvider.Builder()
				.setMusic(new BGM(Musics.MENU))
				.setPriority(1)
				.build());
	}

}
