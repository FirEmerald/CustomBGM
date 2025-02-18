package com.firemerald.custombgm.providers.conditions.player.location;

import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.custombgm.providers.conditions.holderset.HolderSetCondition;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureCondition extends HolderSetCondition<Structure> {
	public static final MapCodec<StructureCondition> CODEC = getCodec(Registries.STRUCTURE, "structure", StructureCondition::new);

	private StructureCondition(HolderSet<Structure> holderSet) {
		super(holderSet);
	}

	@Override
	public boolean test(PlayerConditionData player) {
		if (player.player != null && player.player.level() instanceof ServerLevel level) {
			BlockPos pos = player.player.blockPosition();
			return getStructureWithPieceAt(level.structureManager(), pos, holderSet).isValid();
		} else return false;
	}

	public static StructureStart getStructureWithPieceAt(StructureManager structureManager, BlockPos blockPos, HolderSet<Structure> structures) {
		Registry<Structure> registry = structureManager.registryAccess().registryOrThrow(Registries.STRUCTURE);
		for(StructureStart structureStart : structureManager.startsForStructure(
				new ChunkPos(blockPos),
				structure -> registry.getHolder(registry.getId(structure)).filter(structureRef -> structures.contains(structureRef)).isPresent())) {
			if (structureManager.structureHasPieceAt(blockPos, structureStart)) {
				return structureStart;
			}
		}
		return StructureStart.INVALID_START;
	}

	@Override
	public MapCodec<StructureCondition> codec() {
		return CODEC;
	}

	public static class Builder extends HolderSetCondition.Builder<Structure, StructureCondition, Builder> {
		public Builder(Provider provider) {
			super(provider, Registries.STRUCTURE);
		}

		@Override
		public StructureCondition build() {
			return new StructureCondition(holderSet);
		}
	}
}