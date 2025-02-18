package com.firemerald.custombgm.codecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.LoopType;
import com.firemerald.custombgm.util.SoundProperties;
import com.firemerald.custombgm.util.WeightedBGM;
import com.firemerald.fecore.codec.Codecs;
import com.firemerald.fecore.distribution.DistributionUtil;
import com.firemerald.fecore.distribution.EmptyDistribution;
import com.firemerald.fecore.distribution.IDistribution;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class BGMDistributionCodec implements Codec<IDistribution<BGM>> {
	public static BGMDistributionCodec INSTANCE = new BGMDistributionCodec();

	private BGMDistributionCodec() {}

	@Override
	public <T> DataResult<T> encode(IDistribution<BGM> input, DynamicOps<T> ops, T prefix) {
		//first step: determine target data type
		if (input.isEmpty()) return WeightedBGM.LIST_CODEC.encode(Collections.emptyList(), ops, prefix);
		else if (input.collectionSize() == 1) {
			BGM bgm = input.getFirstValue();
			float weight = input.getFirstWeight();
			SoundProperties props = new SoundProperties(bgm, weight, LoopType.TRUE, 1f);
			if (props.isEmpty()) { //singleton default
				return ResourceLocation.CODEC.encode(bgm.sound(), ops, prefix);
			} else {
				RecordBuilder<T> map = ops.mapBuilder();
				props.loop().ifPresent(loop -> map.add("loop", LoopType.CODEC.encode(loop, ops, prefix)));
				props.weight().ifPresent(weight2 -> map.add("weight", Codec.FLOAT.encode(weight2, ops, prefix)));
				map.add("music", ResourceLocation.CODEC.encode(bgm.sound(), ops, prefix));
				return map.build(prefix);
			}
		} else {
			LoopType constantLoop = null;
			Float constantWeight = null;
			Map<BGM, Float> entries;
			List<BGM> entries2;
			if (input.hasWeights()) {
				entries = input.getWeightedValues();
				entries2 = null;
				boolean first = true;
				for (Map.Entry<BGM, Float> entry : entries.entrySet()) {
					if (first) {
						first = false;
						constantLoop = entry.getKey().loop();
						constantWeight = entry.getValue();
					} else {
						if (constantLoop != null && constantLoop != entry.getKey().loop()) constantLoop = null;
						if (constantWeight != null && constantWeight != entry.getValue()) constantWeight = null;
						if (constantLoop == null && constantWeight == null) break;
					}
				}
			} else {
				entries = null;
				entries2 = input.getUnweightedValues();
				constantWeight = 1f;
				boolean first = true;
				for (BGM bgm : entries2) {
					if (first) {
						first = false;
						constantLoop = bgm.loop();
					} else {
						if (constantLoop != null && constantLoop != bgm.loop()) constantLoop = null;
						if (constantLoop == null) break;
					}
				}
			}
			if (constantLoop == LoopType.TRUE && constantWeight != null && constantWeight == 1f) { //all default, save as list
				return Codecs.RL_ARRAY_CODEC.encode(entries.keySet().stream().map(BGM::sound).toArray(ResourceLocation[]::new), ops, prefix);
			} else { //has at least one default
				RecordBuilder<T> map = ops.mapBuilder();
				if (constantLoop != null && constantLoop != LoopType.TRUE) map.add("loop", LoopType.CODEC.encode(constantLoop, ops, prefix));
				if (constantWeight != null && constantWeight != 1f) map.add("weight", Codec.FLOAT.encode(constantWeight, ops, prefix));
				List<T> defaults = new ArrayList<>();
				final LoopType constantLoopFinal = constantLoop;
				final Float constantWeightFinal = constantWeight;
				if (entries != null) entries.forEach((bgm, weight) -> {
					T key = ResourceLocation.CODEC.encode(bgm.sound(), ops, prefix).getOrThrow(false, str -> {});
					SoundProperties props = new SoundProperties(bgm, weight, constantLoopFinal, constantWeightFinal);
					if (props.isEmpty()) defaults.add(key);
					else map.add(key, SoundProperties.ADAPTABLE_CODEC.encode(props, ops, prefix));
				});
				if (entries2 != null) entries2.forEach(bgm -> {
					T key = ResourceLocation.CODEC.encode(bgm.sound(), ops, prefix).getOrThrow(false, str -> {});
					SoundProperties props = new SoundProperties(bgm, constantLoopFinal);
					if (props.isEmpty()) defaults.add(key);
					else map.add(key, SoundProperties.ADAPTABLE_CODEC.encode(props, ops, prefix));
				});
				if (!defaults.isEmpty()) {
					T val;
					if (defaults.size() == 1) val = defaults.get(0);
					else val = ops.createList(defaults.stream());
					map.add("music", val);
				}
				return map.build(prefix);
			}
		}
	}

	@Override
	public <T> DataResult<Pair<IDistribution<BGM>, T>> decode(DynamicOps<T> ops, T input) {
		Map<BGM, Float> entries = new HashMap<>();
		DataResult<T> result = decodeInto(ops, input, LoopType.TRUE, 1, entry -> {
			entries.compute(new BGM(entry), (bgm, v) -> (v == null ? 0 : v) + entry.weight());
		});
		return result.map(t -> Pair.of(DistributionUtil.get(entries), t));
	}

	public <T> DataResult<T> decodeInto(DynamicOps<T> ops, T input, LoopType loop, float weight, Consumer<WeightedBGM> add) {
		DataResult<Pair<ResourceLocation,T>> singleSound = ResourceLocation.CODEC.decode(ops, input);
		if (Codecs.isSuccess(singleSound)) return singleSound.map(pair -> {
			add.accept(new WeightedBGM(pair.getFirst(), loop, weight));
			return pair.getSecond();
		});
		DataResult<MapLike<T>> mapResult = ops.getMap(input);
		if (Codecs.isSuccess(mapResult)) return mapResult.flatMap(map -> {
			List<String> errors = new ArrayList<>();
			LoopType newLoop = Codecs.getOrDefault(ops, map, "loop", loop, LoopType.CODEC, errors::add);
			float newWeight = Codecs.getOrDefault(ops, map, "weight", 1f, Codec.FLOAT, errors::add) * weight;

			boolean noPropErrors = errors.isEmpty();

			T music = map.get("music");
			if (music != null) {
				DataResult<T> additional = decodeInto(ops, music, newLoop, newWeight, noPropErrors ? add : a -> {}).mapError(err -> "Invalid \"music\": " + err);
				if (Codecs.isError(additional)) errors.add("Invalid \"music\": " + additional.error().get().message());
			}

			map.entries().forEach(pair -> {
				DataResult<Pair<String,T>> keyStrRes = Codec.STRING.decode(ops, pair.getFirst());
				if (Codecs.isError(keyStrRes)) errors.add("Invalid key: " + keyStrRes.error().get().message());
				else {
					String keyStr = keyStrRes.getOrThrow(false, errors::add).getFirst();
					switch (keyStr) {
					case "loop", "weight", "music": break; //reserved
					default:
						DataResult<ResourceLocation> soundRes = ResourceLocation.read(keyStr);
						if (Codecs.isError(soundRes)) errors.add("Invalid key: " + keyStrRes.error().get().message());
						else {
							ResourceLocation sound = soundRes.getOrThrow(false, errors::add);
							DataResult<Pair<SoundProperties, T>> propsRes = SoundProperties.ADAPTABLE_CODEC.decode(ops, pair.getSecond());
							if (Codecs.isError(propsRes)) errors.add("Invalid sound properties: " + propsRes.error().get().message());
							else if (noPropErrors) {
								SoundProperties props = propsRes.getOrThrow(false, errors::add).getFirst();
								add.accept(new WeightedBGM(sound, props, newLoop, newWeight));
							}
						}
					}
				}
			});

			if (errors.isEmpty()) return DataResult.success(ops.empty());
			else return DataResult.error(() -> {
				StringJoiner joiner = new StringJoiner(", ", "Map contained errors: ", "");
				errors.forEach(joiner::add);
				return joiner.toString();
			});
		});
		DataResult<Consumer<Consumer<T>>> listResult = ops.getList(input);
		if (Codecs.isSuccess(listResult)) return listResult.flatMap(decoder -> {
			List<String> errors = new ArrayList<>();
			decoder.accept(obj -> {
				DataResult<T> result = decodeInto(ops, obj, loop, weight, add);
				if (Codecs.isError(result)) errors.add(result.error().get().message());
			});
			if (errors.isEmpty()) return DataResult.success(ops.empty());
			else return DataResult.error(() -> {
				StringJoiner joiner = new StringJoiner(", ", "List contained errors: ", "");
				errors.forEach(joiner::add);
				return joiner.toString();
			});
		});
		return DataResult.error(() ->
		"Invalid type: Expected a String, Map, or List. Errors thrown: " +
		singleSound.error().get().message() + ", " +
		mapResult.error().get().message() + ", " +
		listResult.error().get().message() + ", ");
	}

	public static Tag toTag(IDistribution<BGM> distribution) {
		return INSTANCE.encode(distribution, NbtOps.INSTANCE, EndTag.INSTANCE).getOrThrow(false, str -> {});
	}

	public static IDistribution<BGM> fromTag(Tag tag) {
		if (tag == null) return EmptyDistribution.get();
		else {
			DataResult<Pair<IDistribution<BGM>, Tag>> result = INSTANCE.decode(NbtOps.INSTANCE, tag);
			if (Codecs.isSuccess(result)) return result.getOrThrow(false, str -> {}).getFirst();
			else return EmptyDistribution.get();
		}
	}
}
