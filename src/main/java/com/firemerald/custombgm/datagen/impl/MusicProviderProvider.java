package com.firemerald.custombgm.datagen.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.util.TriConsumer;

import com.firemerald.custombgm.api.providers.BGMProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;

public abstract class MusicProviderProvider implements DataProvider {
	protected static final List<ICondition> ALWAYS = List.of(TrueCondition.INSTANCE);
	protected static final List<ICondition> NEVER = List.of(FalseCondition.INSTANCE);
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
				DataResult<JsonElement> encoded = BGMProvider.CODEC.encode(provider, RegistryOps.create(JsonOps.INSTANCE, registries), JsonNull.INSTANCE);
				encoded.get().ifLeft(json -> {
					if (loadConditions != null && !loadConditions.isEmpty()) ((JsonObject) json).add("conditions", CraftingHelper.serialize(loadConditions.toArray(ICondition[]::new)));
					list.add(DataProvider.saveStable(output, json, path.json(key)));
				});
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