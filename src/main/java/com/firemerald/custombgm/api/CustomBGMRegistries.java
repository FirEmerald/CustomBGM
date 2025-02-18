package com.firemerald.custombgm.api;

import java.util.function.Supplier;

import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;

public class CustomBGMRegistries {
    public static Supplier<IForgeRegistry<MapCodec<? extends BGMProvider>>> providerCodecs;
    public static Supplier<IForgeRegistry<MapCodec<? extends BGMProviderCondition>>> conditionCodecs;
    public static Supplier<IForgeRegistry<MapCodec<? extends BGMProviderVolume>>> volumeCodecs;

    public static class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends BGMProvider>>> PROVIDER_CODECS = key("provider_codecs");
        public static final ResourceKey<Registry<MapCodec<? extends BGMProviderCondition>>> CONDITION_CODECS = key("condition_codecs");
        public static final ResourceKey<Registry<MapCodec<? extends BGMProviderVolume>>> VOLUME_CODECS = key("volume_codecs");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(CustomBGMAPI.id(name));
        }
    }
}
