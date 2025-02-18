package com.firemerald.custombgm.codecs;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.fecore.codec.Codecs;
import com.firemerald.fecore.codec.CodedCodec;
import com.firemerald.fecore.codec.stream.DistributionStreamCodec;
import com.firemerald.fecore.codec.stream.StreamCodec;
import com.firemerald.fecore.distribution.IDistribution;
import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomBGMCodecs {
	public static final StreamCodec<IDistribution<BGM>> BGM_DISTRIBUTION_CODEC = new DistributionStreamCodec<>(BGM.STREAM_CODEC);
    public static final Codec<ItemPredicate> ITEM_PREDICATE = CodedCodec.ofJson(ItemPredicate::serializeToJson, ItemPredicate::fromJson);
    public static final Codec<EntityPredicate> ENTITY_PREDICATE = CodedCodec.ofJson(EntityPredicate::serializeToJson, EntityPredicate::fromJson);
    public static final Codec<StatePropertiesPredicate> STATE_PROPERTIES_PREDICATE = CodedCodec.ofJson(StatePropertiesPredicate::serializeToJson, StatePropertiesPredicate::fromJson);
    public static final Codec<MobEffect> MOB_EFFECT = Codecs.byNameCodec(() -> ForgeRegistries.MOB_EFFECTS);

}
