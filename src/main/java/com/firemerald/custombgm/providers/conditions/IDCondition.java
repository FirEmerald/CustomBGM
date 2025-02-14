package com.firemerald.custombgm.providers.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public abstract class IDCondition implements BGMProviderCondition {
	public static <T extends IDCondition> MapCodec<T> getCodec(String keysKey, Function<ResourceLocation[], T> constructor) {
		return Codecs.RL_ARRAY_CODEC.fieldOf(keysKey).xmap(constructor, condition -> condition.ids);
	}

	public final ResourceLocation[] ids;

	public IDCondition(ResourceLocation[] ids) {
		this.ids = ids;
	}

	public abstract static class Builder<T extends IDCondition, U extends Builder<T, U>> {
		protected final List<ResourceLocation> ids;
		@SuppressWarnings("unchecked")
		protected final U me = (U) this;

		public Builder() {
			ids = new ArrayList<>();
		}

		public Builder(T derive) {
			ids = new ArrayList<>(derive.ids.length);
			for (ResourceLocation id : derive.ids) ids.add(id);
		}

		public U addID(ResourceLocation id) {
			if (id == null) throw new IllegalStateException("Attempted to add a null ResourceLocation");
			ids.add(id);
			return me;
		}

		public U addKey(ResourceKey<?> key) {
			if (key == null) throw new IllegalStateException("Attempted to add a null ResourceLocation");
			return addID(key.location());
		}

		public abstract T build();
	}
}