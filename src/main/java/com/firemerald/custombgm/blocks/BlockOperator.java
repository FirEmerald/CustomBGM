package com.firemerald.custombgm.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.firemerald.custombgm.blockentity.BlockEntityEntityOperator;
import com.firemerald.custombgm.item.ITooltipProvider;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.fecore.block.BlockEntityGUIBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public abstract class BlockOperator<O extends OperatorBase<?, O, S>, S extends BlockEntity & IOperatorSource<O, S>> extends BlockEntityGUIBlock
{
	protected final ITooltipProvider tooltip;

    public BlockOperator(ITooltipProvider tooltip)
    {
    	this(tooltip, BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).noDrops());
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
    	if (stack.hasCustomHoverName())
    	{
    		BlockEntity blockentity = level.getBlockEntity(blockPos);
    		if (blockentity instanceof BlockEntityEntityOperator) ((BlockEntityEntityOperator<?, ?>)blockentity).setName(stack.getHoverName());
    	}
    }

	@Override
	public abstract S newBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		this.tooltip.addTooltip(stack, level, tooltip, flag, () -> {
	    	CompoundTag root = stack.getTag();
	    	if (root != null && root.contains("BlockEntityTag", 10)) return root.getCompound("BlockEntityTag");
	    	else return null;
		});
		tooltip.add(new TranslatableComponent("custombgm.tooltip.redstone_activated"));
	}
}