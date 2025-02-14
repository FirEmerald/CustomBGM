package com.firemerald.custombgm.providers.conditions.modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class CompoundCondition implements BGMProviderCondition {
	public static <T extends CompoundCondition> MapCodec<T> makeCodec(Function<BGMProviderCondition[], T> transform) {
		return RecordCodecBuilder.mapCodec(
				builder -> builder
				.group(LIST_CODEC.fieldOf("conditions").forGetter(condition -> Arrays.asList(condition.conditions)))
				.apply(builder, values -> transform.apply(values.toArray(BGMProviderCondition[]::new))));
	}

	public final BGMProviderCondition[] conditions;

	protected CompoundCondition(BGMProviderCondition... values) {
		this.conditions = values;
	}

	public static abstract class Builder<T extends CompoundCondition> {
		protected final List<BGMProviderCondition> conditions;

		public Builder() {
			conditions = new ArrayList<>();
		}

		public Builder(T derive) {
			conditions = new ArrayList<>(derive.conditions.length);
			for (BGMProviderCondition condition : derive.conditions) conditions.add(condition);
		}

		public Builder<T> addCondition(BGMProviderCondition condition) {
			if (condition == null) throw new IllegalStateException("Attempted to add a null condition");
			conditions.add(condition);
			return this;
		}

		public Builder<T> addConditions(BGMProviderCondition... conditions) {
			for (BGMProviderCondition condition : conditions) this.conditions.add(condition);
			return this;
		}

		public Builder<T> addConditions(Collection<? extends BGMProviderCondition> conditions) {
			this.conditions.addAll(conditions);
			return this;
		}

		public T build() {
			return build(conditions.toArray(BGMProviderCondition[]::new));
		}

		public abstract T build(BGMProviderCondition[] conditions);
	}
}