package com.firemerald.custombgm.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.util.TriConsumer;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.datagen.impl.MusicProviderProvider;
import com.firemerald.custombgm.providers.BaseMusicProvider;
import com.firemerald.custombgm.providers.conditions.player.CombatCondition;
import com.firemerald.custombgm.providers.conditions.player.location.BiomeCondition;
import com.firemerald.custombgm.providers.conditions.player.location.StructureCondition;
import com.firemerald.custombgm.util.BGMDistributionBuilder;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CustomBGMServerMusicProviderProvider extends MusicProviderProvider {
	public CustomBGMServerMusicProviderProvider(PackOutput output, CompletableFuture<Provider> completableFuture) {
		super(output, Target.DATA_PACK, CustomBGMAPI.MOD_ID, completableFuture);
	}

	@Override
	public void generate(Provider provider, TriConsumer<String, List<ICondition>, BGMProvider> register) {
		register.accept("combat_test", TEST_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new CombatCondition.Builder(provider).build())
				.setMusic(new BGMDistributionBuilder("mc4")
						.add("mp1.boss.incinerator_drone", 0.7f)
						.add("mp1.screen.bootup", 0.3f)
						.build())
				.build());
		register.accept("nether_test", TEST_CONDITIONS, new BaseMusicProvider.Builder()
				.setCondition(new BiomeCondition.Builder(provider).setTag(BiomeTags.IS_NETHER).build())
				.setMusic(new BGM("mc4", "mp1.screen.bootup"))
				.build());
		register.accept("stronghold_test", null, new BaseMusicProvider.Builder()
				.setCondition(new StructureCondition.Builder(provider)
						.setKey(BuiltinStructures.STRONGHOLD)
						.build())
				.setMusic(new BGM("mc4", "mp1.phazon_mines.main"))
				.build());
	}

}
