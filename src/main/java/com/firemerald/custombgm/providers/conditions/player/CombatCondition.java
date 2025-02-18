package com.firemerald.custombgm.providers.conditions.player;

import java.util.Optional;
import java.util.stream.Stream;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.capabilities.ServerPlayerData;
import com.firemerald.custombgm.providers.conditions.holderset.OptionalHolderSetCondition;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class CombatCondition extends OptionalHolderSetCondition<EntityType<?>> {
	public static final MapCodec<CombatCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("entity").forGetter(condition -> condition.holderSet),
				Codecs.INT_BOUNDS.optionalFieldOf("count", MinMaxBounds.Ints.atLeast(1)).forGetter(condition -> condition.entityCount)
				)
		.apply(instance, CombatCondition::new)
	);

	public final MinMaxBounds.Ints entityCount;

	private CombatCondition(Optional<HolderSet<EntityType<?>>> holderSet, MinMaxBounds.Ints entityCount) {
		super(holderSet);
		this.entityCount = entityCount;
	}

    private int getEntities(ServerPlayerData player) {
    	Stream<EntityType<?>> stream = player.getTargeters().stream().map(Entity::getType);
    	if (holderSet.isPresent()) {
    		final Stream<EntityType<?>> stream2 = stream;
    		HolderSet<EntityType<?>> types = holderSet.get();
    		stream = types.unwrap().map(tag -> stream2.filter(type -> type.is(tag)), holders -> stream2.filter(type -> ForgeRegistries.ENTITY_TYPES.getHolder(type).filter(holders::contains).isPresent()));
    	}
    	return (int) stream.count();
    }

	@Override
	public boolean test(PlayerConditionData player) {
		ServerPlayerData playerData = ServerPlayerData.getServerPlayerData(player);
		if (playerData != null) {
			int numEntities = getEntities(playerData);
			return entityCount.matches(numEntities);
		}
		else return false;
	}

	@Override
	public MapCodec<CombatCondition> codec() {
		return CODEC;
	}

	public static class Builder extends OptionalHolderSetCondition.Builder<EntityType<?>, CombatCondition, Builder> {
		private MinMaxBounds.Ints entityCount;

		public Builder(Provider provider) {
			super(provider, Registries.ENTITY_TYPE);
			entityCount = MinMaxBounds.Ints.ANY;
		}

		public Builder setEntityCount(MinMaxBounds.Ints entityCount) {
			if (entityCount == null) throw new IllegalStateException("Attempted to set entityCount to a null value");
			this.entityCount = entityCount;
			return this;
		}

		@Override
		public CombatCondition build() {
			return new CombatCondition(holderSet, entityCount);
		}
	}
}