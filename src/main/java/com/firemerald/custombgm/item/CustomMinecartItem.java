package com.firemerald.custombgm.item;

import java.util.List;

import javax.annotation.Nullable;

import com.firemerald.custombgm.entity.OperatorMinecart;
import com.firemerald.custombgm.operators.OperatorBase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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

public class CustomMinecartItem<O extends OperatorBase<?, O, S>, S extends OperatorMinecart<O, S>> extends Item
{
	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior()
	{
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack)
		{
			Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
			Level level = blockSource.getLevel();
			double x = blockSource.x() + direction.getStepX() * 1.125D;
			double y = Math.floor(blockSource.y()) + direction.getStepY();
			double z = blockSource.z() + direction.getStepZ() * 1.125D;
			BlockPos blockpos = blockSource.getPos().relative(direction);
			BlockState blockstate = level.getBlockState(blockpos);
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
			double yOffset;
			if (blockstate.is(BlockTags.RAILS))
			{
				if (railshape.isAscending()) yOffset = 0.6D;
				else yOffset = 0.1D;
			}
			else
			{
				if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
				BlockState blockstate1 = level.getBlockState(blockpos.below());
				RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, level, blockpos.below(), null) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && railshape1.isAscending()) yOffset = -0.4D;
				else yOffset = -0.9D;
			}
			OperatorMinecart<?, ?> minecart = ((CustomMinecartItem<?, ?>) itemStack.getItem()).makeMinecart(itemStack, null, level, x, y + yOffset, z);
			if (itemStack.hasCustomHoverName()) minecart.setCustomName(itemStack.getHoverName());
			level.addFreshEntity(minecart);
			itemStack.shrink(1);
			return itemStack;
		}

		@Override
		protected void playSound(BlockSource blockSource)
		{
			blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
		}
	};


	protected final MinecartConstructor<O, S> constructor;
	protected final ITooltipProvider tooltip;

	public CustomMinecartItem(MinecartConstructor<O, S> constructor, ITooltipProvider tooltip, Item.Properties properties)
	{
		super(properties);
		this.constructor = constructor;
		this.tooltip = tooltip;
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext useContext)
	{
		Level level = useContext.getLevel();
		BlockPos blockpos = useContext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		if (!blockstate.is(BlockTags.RAILS)) return InteractionResult.FAIL;
		else
		{
			ItemStack itemstack = useContext.getItemInHand();
			if (!level.isClientSide)
			{
				RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
				double yOffset = 0.0D;
				if (railshape.isAscending()) yOffset = 0.5D;
				OperatorMinecart<O, S> minecart = makeMinecart(itemstack, useContext.getPlayer(), level, blockpos.getX() + 0.5, blockpos.getY() + 0.0625 + yOffset, blockpos.getZ() + 0.5);
				if (itemstack.hasCustomHoverName()) minecart.setCustomName(itemstack.getHoverName());
				level.addFreshEntity(minecart);
				level.gameEvent(useContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
			}
			itemstack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	public OperatorMinecart<O, S> makeMinecart(ItemStack stack, @Nullable Player player, Level level, double x, double y, double z)
	{
		S entity = constructor.construct(level, x, y, z);
		if (stack.hasCustomHoverName()) entity.setCustomName(stack.getHoverName());
		EntityType.updateCustomEntityTag(level, player, entity, stack.getTag());
        return entity;
	}


	@FunctionalInterface
	public static interface MinecartConstructor<O extends OperatorBase<?, O, S>, S extends OperatorMinecart<O, S>>
	{
		public abstract S construct(Level level, double x, double y, double z);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		this.tooltip.addTooltip(stack, level, tooltip, flag, () -> {
	    	CompoundTag root = stack.getTag();
	    	if (root != null && root.contains("EntityTag", 10)) return root.getCompound("EntityTag");
	    	else return null;
		});
		tooltip.add(new TranslatableComponent("custombgm.tooltip.activator_rail_activated"));
	}
}