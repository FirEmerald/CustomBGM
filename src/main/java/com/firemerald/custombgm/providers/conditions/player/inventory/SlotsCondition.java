package com.firemerald.custombgm.providers.conditions.player.inventory;

import java.util.Map;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.codecs.CustomBGMCodecs;
import com.firemerald.fecore.util.inventory.SlotRange;
import com.firemerald.fecore.util.inventory.SlotRanges;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;

public record SlotsCondition(Map<SlotRange, ItemPredicate> slots) implements BGMProviderPlayerCondition {
	public static final MapCodec<SlotsCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codec.unboundedMap(SlotRanges.CODEC, CustomBGMCodecs.ITEM_PREDICATE).fieldOf("slots").forGetter(SlotsCondition::slots)
				).apply(instance, SlotsCondition::new)
	);

	@Override
	public MapCodec<SlotsCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
		return this.slots.entrySet().stream().allMatch(entry -> matchSlots(player, entry.getValue(), entry.getKey().slots()));
    }

	//TODO replace with SlotsPredicate.matchSlots
    private static boolean matchSlots(Entity entity, ItemPredicate predicate, IntList slots) {
        for (int listIndex = 0; listIndex < slots.size(); listIndex++) {
            int slotIndex = slots.getInt(listIndex);
            SlotAccess slotaccess = entity.getSlot(slotIndex);
            if (predicate.matches(slotaccess.get())) return true;
        }
        return false;
    }

}
