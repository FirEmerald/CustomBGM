package com.firemerald.custombgm.providers.conditions.player.location;

import java.util.Optional;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record InFluidCondition(Optional<HolderSet<Fluid>> fluids, Optional<StatePropertiesPredicate> properties) implements BGMProviderCondition {
	public static final MapCodec<InFluidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(
					RegistryCodecs.homogeneousList(Registries.FLUID).optionalFieldOf("fluids").forGetter(InFluidCondition::fluids),
					StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(InFluidCondition::properties)
					)
			.apply(instance, InFluidCondition::new)
			);

	@Override
	public MapCodec<InFluidCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData) {
        FluidState fluidstate = playerData.insideFluid();
        if (fluidstate == null) return this.fluids.isPresent() && this.fluids.get().size() == 0;
        else if (this.fluids.isPresent() && !fluidstate.is(this.fluids.get())) return false;
        else if (this.properties.isPresent() && !this.properties.get().matches(fluidstate)) return false;
        return true;
	}

    public static class Builder {
        private Optional<HolderSet<Fluid>> fluids = Optional.empty();
        private Optional<StatePropertiesPredicate> properties = Optional.empty();

        @SuppressWarnings("deprecation")
		public Builder of(Fluid fluid) {
            this.fluids = Optional.of(HolderSet.direct(fluid.builtInRegistryHolder()));
            return this;
        }

        public Builder of(HolderSet<Fluid> fluids) {
            this.fluids = Optional.of(fluids);
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate properties) {
            this.properties = Optional.of(properties);
            return this;
        }

        public InFluidCondition build() {
            return new InFluidCondition(this.fluids, this.properties);
        }
    }
}
