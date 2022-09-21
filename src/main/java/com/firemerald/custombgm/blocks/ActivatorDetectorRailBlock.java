package com.firemerald.custombgm.blocks;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ActivatorDetectorRailBlock extends PoweredRailBlock
{
	public static final BooleanProperty DETECTED = BooleanProperty.create("detected");

	public ActivatorDetectorRailBlock(Properties properties)
	{
		super(properties, false);
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWERED, false).setValue(DETECTED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (!level.isClientSide && !state.getValue(DETECTED)) this.checkPressed(level, pos, state);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
	{
		if (state.getValue(DETECTED)) this.checkPressed(level, pos, state);
	}

	private void checkPressed(Level level, BlockPos pos, BlockState state)
	{
		if (this.canSurvive(state, level, pos))
		{
			boolean wasDetected = state.getValue(DETECTED);
			boolean detected = !this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, entity -> true).isEmpty();
			if (detected ^ wasDetected)
			{
				BlockState blockstate = state.setValue(DETECTED, Boolean.valueOf(detected));
				level.setBlock(pos, blockstate, 3);
				this.updatePowerToConnected(level, pos, blockstate, detected);
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.below(), this);
				level.setBlocksDirty(pos, state, blockstate);
			}
			if (detected) level.scheduleTick(pos, this, 20);
			level.updateNeighbourForOutputSignal(pos, this);
		}
	}

	protected void updatePowerToConnected(Level level, BlockPos pos, BlockState state, boolean detected)
	{
		RailState railstate = new RailState(level, pos, state);
		for(BlockPos blockpos : railstate.getConnections())
		{
			BlockState blockstate = level.getBlockState(blockpos);
			blockstate.neighborChanged(level, blockpos, blockstate.getBlock(), pos, false);
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean p_52487_)
	{
		if (!oldState.is(state.getBlock()))
		{
			BlockState blockstate = this.updateState(state, level, pos, p_52487_);
			this.checkPressed(level, pos, blockstate);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		if (state.getValue(DETECTED))
		{
			List<MinecartCommandBlock> commandMinecarts = this.getInteractingMinecartOfType(level, pos, MinecartCommandBlock.class, entity -> true);
			if (!commandMinecarts.isEmpty()) return commandMinecarts.get(0).getCommandBlock().getSuccessCount();
			List<AbstractMinecart> abstractMinecarts = this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, e -> e.isAlive());
			if (!abstractMinecarts.isEmpty() && abstractMinecarts.get(0).getComparatorLevel() > -1) return abstractMinecarts.get(0).getComparatorLevel();
			List<AbstractMinecart> containerMinecarts = abstractMinecarts.stream().filter(EntitySelector.CONTAINER_ENTITY_SELECTOR).collect(java.util.stream.Collectors.toList());
			if (!containerMinecarts.isEmpty()) return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)containerMinecarts.get(0));
		}
		return 0;
	}

	private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> minecartClass, Predicate<Entity> validator)
	{
		return level.getEntitiesOfClass(minecartClass, this.getSearchBB(pos), validator);
	}

	private AABB getSearchBB(BlockPos blockPos)
	{
		double d0 = 0.2;
		return new AABB(blockPos.getX() + d0, blockPos.getY(), blockPos.getZ() + d0, blockPos.getX() + 1 - d0, blockPos.getY() + 1 - d0, blockPos.getZ() + 1 - d0);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(SHAPE, POWERED, DETECTED, WATERLOGGED);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(new TranslatableComponent("custombgm.tooltip.activator_detector_rail"));
	}
}