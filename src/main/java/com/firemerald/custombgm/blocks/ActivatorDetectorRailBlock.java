package com.firemerald.custombgm.blocks;

import java.util.List;
import java.util.function.Predicate;

import com.firemerald.custombgm.entity.OperatorMinecart;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ActivatorDetectorRailBlock extends PoweredRailBlock
{
    public static final MapCodec<PoweredRailBlock> CODEC = simpleCodec(ActivatorDetectorRailBlock::new);
	public static final BooleanProperty DETECTED = BooleanProperty.create("detected");

    @Override
    public MapCodec<PoweredRailBlock> codec() {
        return CODEC;
    }

	public ActivatorDetectorRailBlock(Properties properties)
	{
		super(properties, false);
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWERED, false).setValue(DETECTED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(SHAPE, POWERED, DETECTED, WATERLOGGED);
	}

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && !state.getValue(DETECTED)) this.checkPressed(level, pos, state);
    }

    @Override
    protected void tick(BlockState p_221060_, ServerLevel p_221061_, BlockPos p_221062_, RandomSource p_221063_) {
        if (p_221060_.getValue(DETECTED)) this.checkPressed(p_221061_, p_221062_, p_221060_);
    }

    private void checkPressed(Level level, BlockPos pos, BlockState state) {
        if (this.canSurvive(state, level, pos)) {
            boolean isDetected = !this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, p_153125_ -> true).isEmpty();
            if (isDetected != state.getValue(DETECTED)) {
                BlockState blockstate = state.setValue(DETECTED, isDetected);
                level.setBlock(pos, blockstate, 3);
                this.updatePowerToConnected(level, pos, blockstate, true);
                level.updateNeighborsAt(pos, this);
                level.updateNeighborsAt(pos.below(), this);
                level.setBlocksDirty(pos, state, blockstate);
            }
            if (isDetected) level.scheduleTick(pos, this, 20);
            level.updateNeighbourForOutputSignal(pos, this);
        }
    }

    protected void updatePowerToConnected(Level level, BlockPos pos, BlockState state, boolean powered) {
        RailState railstate = new RailState(level, pos, state);

        for (BlockPos blockpos : railstate.getConnections()) {
            BlockState blockstate = level.getBlockState(blockpos);
            level.neighborChanged(blockstate, blockpos, blockstate.getBlock(), null, false);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            BlockState blockstate = this.updateState(state, level, pos, isMoving);
            this.checkPressed(level, pos, blockstate);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if (blockState.getValue(DETECTED)) {
        	try { //we attempt to directly use the detector rail analog output signal code so that any other mod-added comparator outputs are supported
            	BlockState testState = Blocks.DETECTOR_RAIL.defaultBlockState().setValue(DetectorRailBlock.POWERED, true);
            	//copy all other properties
            	for (Property<?> prop : blockState.getProperties()) if (prop != DetectorRailBlock.POWERED) testState = copyProperty(prop, blockState, testState);
            	return testState.getAnalogOutputSignal(level, pos);
        	} catch (Exception e) { //fallback in case the hack above fails
        		//TODO log exception once
                List<MinecartCommandBlock> list = this.getInteractingMinecartOfType(level, pos, MinecartCommandBlock.class, entity -> true);
                if (!list.isEmpty()) return list.get(0).getCommandBlock().getSuccessCount();
                List<AbstractMinecart> list1 = this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
                if (!list1.isEmpty()) return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)list1.get(0));
    			@SuppressWarnings("rawtypes")
    			List<OperatorMinecart> list2 = this.getInteractingMinecartOfType(level, pos, OperatorMinecart.class, entity -> true);
                if (!list2.isEmpty()) return list2.get(0).getComparatorLevel();
        	}
        }
        return 0;
    }

    private <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState from, BlockState to) {
    	return to.setValue(property, from.getValue(property));
    }

    private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos pos, Class<T> cartType, Predicate<Entity> filter) {
        return level.getEntitiesOfClass(cartType, this.getSearchBB(pos), filter);
    }

    private AABB getSearchBB(BlockPos pos) {
    	final double margin = 0.2;
        return new AABB(
            pos.getX() + margin,
            pos.getY(),
            pos.getZ() + margin,
            (pos.getX() + 1) - margin,
            (pos.getY() + 1) - margin,
            (pos.getZ() + 1) - margin
        );
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(Component.translatable("custombgm.tooltip.activator_detector_rail"));
	}
}