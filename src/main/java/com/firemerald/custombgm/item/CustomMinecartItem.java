package com.firemerald.custombgm.item;

import java.util.List;

import javax.annotation.Nullable;

import com.firemerald.fecore.util.StackUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class CustomMinecartItem extends Item
{
	public static interface MinecartConstructor<T extends AbstractMinecart> {
		public T construct(Level level, double x, double y, double z);
	}

	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		@Override
		public ItemStack execute(BlockSource position, ItemStack stack) {
			Direction direction = position.getBlockState().getValue(DispenserBlock.FACING);
			Level level = position.getLevel();
			double x = position.x() + direction.getStepX() * 1.125D;
			double y = Math.floor(position.y()) + direction.getStepY();
			double z = position.z() + direction.getStepZ() * 1.125D;
			BlockPos blockPos = position.getPos().relative(direction);
			BlockState blockState = level.getBlockState(blockPos);
			RailShape railShape = blockState.getBlock() instanceof BaseRailBlock railBlock ? railBlock.getRailDirection(blockState, level, blockPos, null) : RailShape.NORTH_SOUTH;
			double offY;
			if (blockState.is(BlockTags.RAILS)) {
				if (railShape.isAscending()) offY = 0.6D;
				else offY = 0.1D;
			} else {
				BlockPos blockPosBelow = blockPos.below();
				BlockState blockStateBelow = level.getBlockState(blockPosBelow);
				if (!blockState.isAir() || !blockStateBelow.is(BlockTags.RAILS)) return this.defaultDispenseItemBehavior.dispense(position, stack);
				RailShape railShapeBelow = blockStateBelow.getBlock() instanceof BaseRailBlock railBlock ? railBlock.getRailDirection(blockStateBelow, level, blockPosBelow, null) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && railShapeBelow.isAscending()) offY = -0.4D;
				else offY = -0.9D;
			}
			AbstractMinecart abstractminecart = ((CustomMinecartItem)stack.getItem()).constructor.construct(level, x, y + offY, z);
			if (stack.hasCustomHoverName()) abstractminecart.setCustomName(stack.getHoverName());
			level.addFreshEntity(abstractminecart);
			stack.shrink(1);
			return stack;
		}

		@Override
		protected void playSound(BlockSource position) {
			position.getLevel().levelEvent(1000, position.getPos(), 0);
		}
	};

	protected final ITooltipProvider tooltip;
	protected final MinecartConstructor<?> constructor;

	public CustomMinecartItem(MinecartConstructor<?> constructor, ITooltipProvider tooltip, Item.Properties properties) {
		super(properties);
		this.constructor = constructor;
		this.tooltip = tooltip;
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
		this.tooltip.addTooltip(stack, level, tooltipComponents, tooltipFlag, StackUtils::decodeEntityData);
		tooltipComponents.add(Component.translatable("custombgm.tooltip.activator_rail_activated"));
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
	      Level level = pContext.getLevel();
	      BlockPos blockpos = pContext.getClickedPos();
	      BlockState blockstate = level.getBlockState(blockpos);
	      if (!blockstate.is(BlockTags.RAILS)) return InteractionResult.FAIL;
	      else {
	         ItemStack itemstack = pContext.getItemInHand();
	         if (!level.isClientSide) {
	            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
	            double offY = 0.0D;
	            if (railshape.isAscending()) offY = 0.5D;
	            AbstractMinecart abstractminecart = constructor.construct(level, blockpos.getX() + 0.5D, blockpos.getY() + 0.0625D + offY, blockpos.getZ() + 0.5D);
	            if (itemstack.hasCustomHoverName()) abstractminecart.setCustomName(itemstack.getHoverName());
	            level.addFreshEntity(abstractminecart);
	            level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(pContext.getPlayer(), level.getBlockState(blockpos.below())));
	         }
	         itemstack.shrink(1);
	         return InteractionResult.sidedSuccess(level.isClientSide);
	      }
	}
}