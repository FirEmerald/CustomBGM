package com.firemerald.custombgm.mixin;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.firemerald.custombgm.entity.OperatorMinecart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DetectorRailBlock.class)
public class MixinDetectorRailBlock {
	@Shadow
	private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> cartType, Predicate<Entity> filter) {
		return null;
	}

	@Inject(method = "getAnalogOutputSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", at = @At("TAIL"))
	private void getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (blockState.getValue(DetectorRailBlock.POWERED)) {
			@SuppressWarnings("rawtypes")
			List<OperatorMinecart> list = this.getInteractingMinecartOfType(level, pos, OperatorMinecart.class, p_153123_ -> true);
            if (!list.isEmpty()) cir.setReturnValue(list.get(0).getComparatorLevel());
		}
	}
}
