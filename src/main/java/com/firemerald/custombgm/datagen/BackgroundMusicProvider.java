package com.firemerald.custombgm.datagen;

import java.util.function.BiConsumer;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.datagen.impl.providers.BaseMusicProviderBuilder;
import com.firemerald.custombgm.datagen.impl.providers.MusicProviderBuilder;
import com.firemerald.custombgm.datagen.impl.providers.MusicProviderProviders;
import com.firemerald.custombgm.datagen.impl.providers.conditions.BiomeConditionBuilder;
import com.firemerald.custombgm.datagen.impl.providers.conditions.CombatConditionBuilder;
import com.firemerald.fecore.util.distribution.UnweightedDistribution;
import com.firemerald.fecore.util.distribution.WeightedDistribution;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;

public class BackgroundMusicProvider extends MusicProviderProviders
{
	public BackgroundMusicProvider(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	public void buildProviders(BiConsumer<ResourceLocation, MusicProviderBuilder<?>> register)
	{
		register.accept(new ResourceLocation(CustomBGMAPI.MOD_ID, "combat_test_datagen"), 
				new BaseMusicProviderBuilder<>()
				.addLoadingCondition(GSON.fromJson("{\"type\":\"forge:false\"}", JsonObject.class))
				.setCondition(new CombatConditionBuilder())
				.setMusic(
						WeightedDistribution.<ResourceLocation>builder()
						.add(new ResourceLocation("mc4", "mp1.boss.incinerator_drone"), 0.7f)
						.add(new ResourceLocation("mc4", "mp1.screen.bootup"), 0.3f)
						.build()
						)
				);
		register.accept(new ResourceLocation(CustomBGMAPI.MOD_ID, "nether_test_datagen"), 
				new BaseMusicProviderBuilder<>()
				.addLoadingCondition(GSON.fromJson("{\"type\":\"forge:false\"}", JsonObject.class))
				.setCondition(
						new BiomeConditionBuilder()
						.addTag(BiomeTags.IS_NETHER)
						)
				.setMusic(
						UnweightedDistribution.<ResourceLocation>builder()
						.add(new ResourceLocation("mc4", "mp1.boss.incinerator_drone"))
						.add(new ResourceLocation("mc4", "mp1.screen.bootup"))
						.build()
						)
				);
	}
}