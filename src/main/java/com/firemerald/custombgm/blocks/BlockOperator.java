package com.firemerald.custombgm.blocks;

import java.util.List;

import com.firemerald.custombgm.blockentity.BlockEntityEntityOperator;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.fecore.block.BlockEntityGUIBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public abstract class BlockOperator<O extends OperatorBase<?, O, S>, S extends BlockEntity & IOperatorSource<O, S>> extends BlockEntityGUIBlock
{
	protected final ITooltipProvider tooltip;

    public BlockOperator(ITooltipProvider tooltip, ResourceKey<Block> id)
    {
    	this(tooltip, BlockBehaviour.Properties.of().setId(id).mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
    }

    public BlockOperator(ITooltipProvider tooltip, BlockBehaviour.Properties properties)
    {
    	super(properties);
    	this.tooltip = tooltip;
    }

	@Override
	public RenderShape getRenderShape(BlockState blockState)
	{
		return RenderShape.MODEL;
	}

    @Override
	public boolean hasAnalogOutputSignal(BlockState state)
    {
       return true;
    }

	@SuppressWarnings("unchecked")
	@Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos)
    {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity instanceof IOperatorSource ? ((S) tileentity).getOperator().getOutputLevel() : 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack)
    {
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		if (blockentity instanceof BlockEntityEntityOperator) ((BlockEntityEntityOperator<?, ?>)blockentity).setName(stack.getHoverName());
    }

	@Override
	public abstract S newBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		this.tooltip.addTooltip(stack, context, tooltipComponents, tooltipFlag, DataComponents.BLOCK_ENTITY_DATA);
		tooltipComponents.add(Component.translatable("custombgm.tooltip.redstone_activated"));
	}

	@Override
	public boolean canOpenGUI(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
		return player.isCreative();
	}
}