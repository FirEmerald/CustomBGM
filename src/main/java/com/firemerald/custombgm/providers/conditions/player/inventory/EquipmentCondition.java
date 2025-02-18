package com.firemerald.custombgm.providers.conditions.player.inventory;

import java.util.Optional;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.codecs.CustomBGMCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public record EquipmentCondition(
	    Optional<ItemPredicate> head,
	    Optional<ItemPredicate> chest,
	    Optional<ItemPredicate> legs,
	    Optional<ItemPredicate> feet,
	    Optional<ItemPredicate> mainhand,
	    Optional<ItemPredicate> offhand) implements BGMProviderPlayerCondition {
    public static final MapCodec<EquipmentCondition> CODEC = RecordCodecBuilder.mapCodec(
    		instance -> instance.group(
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("head").forGetter(EquipmentCondition::head),
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("chest").forGetter(EquipmentCondition::chest),
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("legs").forGetter(EquipmentCondition::legs),
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("feet").forGetter(EquipmentCondition::feet),
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("mainhand").forGetter(EquipmentCondition::mainhand),
    				CustomBGMCodecs.ITEM_PREDICATE.optionalFieldOf("offhand").forGetter(EquipmentCondition::offhand)
    				)
    		.apply(instance, EquipmentCondition::new)
    		);

	@Override
	public MapCodec<EquipmentCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
        if (this.head.isPresent() && !this.head.get().matches(player.getItemBySlot(EquipmentSlot.HEAD))) return false;
        else if (this.chest.isPresent() && !this.chest.get().matches(player.getItemBySlot(EquipmentSlot.CHEST))) return false;
        else if (this.legs.isPresent() && !this.legs.get().matches(player.getItemBySlot(EquipmentSlot.LEGS))) return false;
        else if (this.feet.isPresent() && !this.feet.get().matches(player.getItemBySlot(EquipmentSlot.FEET))) return false;
        else if (this.mainhand.isPresent() && !this.mainhand.get().matches(player.getItemBySlot(EquipmentSlot.MAINHAND))) return false;
        else if (this.offhand.isPresent() && !this.offhand.get().matches(player.getItemBySlot(EquipmentSlot.OFFHAND))) return false;
        else return true;
	}

    public static class Builder {
        private Optional<ItemPredicate> head = Optional.empty();
        private Optional<ItemPredicate> chest = Optional.empty();
        private Optional<ItemPredicate> legs = Optional.empty();
        private Optional<ItemPredicate> feet = Optional.empty();
        private Optional<ItemPredicate> mainhand = Optional.empty();
        private Optional<ItemPredicate> offhand = Optional.empty();

        public Builder head(ItemPredicate.Builder head) {
            this.head = Optional.of(head.build());
            return this;
        }

        public Builder chest(ItemPredicate.Builder chest) {
            this.chest = Optional.of(chest.build());
            return this;
        }

        public Builder legs(ItemPredicate.Builder legs) {
            this.legs = Optional.of(legs.build());
            return this;
        }

        public Builder feet(ItemPredicate.Builder feet) {
            this.feet = Optional.of(feet.build());
            return this;
        }

        public Builder mainhand(ItemPredicate.Builder mainhand) {
            this.mainhand = Optional.of(mainhand.build());
            return this;
        }

        public Builder offhand(ItemPredicate.Builder offhand) {
            this.offhand = Optional.of(offhand.build());
            return this;
        }

        public EquipmentCondition build() {
            return new EquipmentCondition(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }

}
