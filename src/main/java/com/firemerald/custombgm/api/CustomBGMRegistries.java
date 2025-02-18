package com.firemerald.custombgm.api;

import com.firemerald.custombgm.api.providers.BGMProvider;
import com.firemerald.custombgm.api.providers.conditions.BGMProviderCondition;
import com.firemerald.custombgm.api.providers.volume.BGMProviderVolume;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CustomBGMRegistries {
    public static final Registry<MapCodec<? extends BGMProvider>> PROVIDER_CODECS = new RegistryBuilder<>(Keys.PROVIDER_CODECS).create();
    public static final Registry<MapCodec<? extends BGMProviderCondition>> CONDITION_CODECS = new RegistryBuilder<>(Keys.CONDITION_CODECS).create();
    public static final Registry<MapCodec<? extends BGMProviderVolume>> VOLUME_CODECS = new RegistryBuilder<>(Keys.VOLUME_CODECS).create();

    public static class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends BGMProvider>>> PROVIDER_CODECS = key("provider_codecs");
        public static final ResourceKey<Registry<MapCodec<? extends BGMProviderCondition>>> CONDITION_CODECS = key("condition_codecs");
        public static final ResourceKey<Registry<MapCodec<? extends BGMProviderVolume>>> VOLUME_CODECS = key("volume_codecs");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(CustomBGMAPI.id(name));
        }
    }
}
