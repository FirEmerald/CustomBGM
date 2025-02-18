package com.firemerald.custombgm.providers.conditions.holderset;

import java.util.Arrays;
import java.util.List;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.holdersets.OrHolderSet;

public abstract class AbstractHolderSetCondition<T> implements BGMProviderCondition {
    public abstract static class Builder<T, U extends AbstractHolderSetCondition<T>, V extends Builder<T, U, V>> {
        public final RegistryLookup<T> lookup;

        public Builder(RegistryLookup<T> lookup) {
        	this.lookup = lookup;
        }

        public Builder(HolderLookup.Provider provider, ResourceKey<Registry<T>> registryKey) {
        	this(provider.lookupOrThrow(registryKey));
        }

        @SuppressWarnings("unchecked")
		protected V me() {
        	return (V) this;
        }

        public V setHolder(Holder<T> holder) {
            return setHolderSet(HolderSet.direct(holder));
        }

        public V setHolders(@SuppressWarnings("unchecked") Holder<T>... holders) {
            return setHolderSet(HolderSet.direct(holders));
        }

        public V setHolders(List<? extends Holder<T>> holders) {
            return setHolderSet(HolderSet.direct(holders));
        }

        @SuppressWarnings("deprecation")
		public V setTag(TagKey<T> tag) {
            return setHolderSet(HolderSet.emptyNamed(lookup, tag));
        }

        @SuppressWarnings("deprecation")
		public V setTags(@SuppressWarnings("unchecked") TagKey<T>... tags) {
            return setHolderSet(Arrays.stream(tags).map(tag -> HolderSet.emptyNamed(lookup, tag)).toList());
        }

        public V setKey(ResourceKey<T> key) {
            return setHolder(lookup.getOrThrow(key));
        }

        public V setKeys(@SuppressWarnings("unchecked") ResourceKey<T>... keys) {
            return setHolders(Arrays.stream(keys).map(lookup::getOrThrow).toList());
        }

        public V setHolderSet(@SuppressWarnings("unchecked") HolderSet<T>... holderSets) {
        	return setHolderSet(Arrays.asList(holderSets));
        }

        @SuppressWarnings("unchecked")
		public V setHolderSet(List<? extends HolderSet<T>> holderSets) {
        	return setHolderSet(new OrHolderSet<>((List<HolderSet<T>>) holderSets));
        }

        public abstract V setHolderSet(HolderSet<T> holderSet);

        public abstract U build();
    }
}