package com.firemerald.custombgm.datagen.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.function.TriConsumer;

import com.firemerald.custombgm.api.providers.BGMProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.AlwaysCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NeverCondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

public abstract class MusicProviderProvider implements DataProvider {
	protected static final List<ICondition> ALWAYS = List.of(AlwaysCondition.INSTANCE);
	protected static final List<ICondition> NEVER = List.of(NeverCondition.INSTANCE);
	protected static final List<ICondition> TEST_CONDITIONS = NEVER;
	protected static final List<ICondition> VANILLA_CONDITIONS = NEVER;

	protected final Target target;
	protected final PackOutput.PathProvider path;
    protected final String modId;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    public MusicProviderProvider(PackOutput output, Target target, String modId, CompletableFuture<HolderLookup.Provider> registries) {
		this.target = target;
		this.path = output.createPathProvider(target, "custom_bgm");
        this.modId = modId;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(registries -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
			generate(registries, (name, loadConditions, provider) -> {
				ResourceLocation key = ResourceLocation.fromNamespaceAndPath(modId, name);
				if (set.contains(key)) throw new IllegalStateException("Duplicate provider " + key.toString() + " for " + target);
				list.add(DataProvider.saveStable(output, registries, BGMProvider.CONDITIONAL_CODEC, Optional.of(new WithConditions<BGMProvider>(loadConditions == null ? Collections.emptyList() : loadConditions, provider)), path.json(key)));
			});
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

	public abstract void generate(HolderLookup.Provider registries, TriConsumer<String, List<ICondition>, BGMProvider> register);

	@Override
	public String getName() {
		return target.name() + " BGM Providers - " + modId;
	}
}