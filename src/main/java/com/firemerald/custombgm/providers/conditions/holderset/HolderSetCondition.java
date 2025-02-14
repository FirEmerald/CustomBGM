package com.firemerald.custombgm.providers.conditions.holderset;

import java.util.function.Function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceKey;

public abstract class HolderSetCondition<T> extends AbstractHolderSetCondition<T> {
	public static <T, U extends HolderSetCondition<T>> MapCodec<U> getCodec(ResourceKey<Registry<T>> registryKey, String fieldName, Function<HolderSet<T>, U> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance
				.group(
						RegistryCodecs.homogeneousList(registryKey).fieldOf(fieldName).forGetter(condition -> condition.holderSet)
						)
				.apply(instance, constructor)
				);
	}

	public final HolderSet<T> holderSet;

	public HolderSetCondition(HolderSet<T> holderSet) {
		this.holderSet = holderSet;
	}

    public abstract static class Builder<T, U extends HolderSetCondition<T>, V extends Builder<T, U, V>> extends AbstractHolderSetCondition.Builder<T, U, V> {
        protected HolderSet<T> holderSet = HolderSet.empty();

        public Builder(RegistryLookup<T> lookup) {
        	super(lookup);
        }

        public Builder(HolderLookup.Provider provider, ResourceKey<Registry<T>> registryKey) {
        	super(provider, registryKey);
        }

        @Override
        public V setHolderSet(HolderSet<T> holderSet) {
            this.holderSet = holderSet;
            return me();
        }
    }
}