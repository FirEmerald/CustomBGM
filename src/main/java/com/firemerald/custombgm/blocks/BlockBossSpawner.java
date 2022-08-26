package com.firemerald.custombgm.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBossSpawner extends BlockEntityGUIBlock
{
    public BlockBossSpawner()
    {
    	super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).noDrops());
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return new BlockEntityBossSpawner(blockPos, blockState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return (type == CustomBGMBlockEntities.BOSS_SPAWNER && !level.isClientSide) ? (level2, blockPos, blockState, blockEntity) -> ((BlockEntityBossSpawner) blockEntity).serverTick(level2, blockPos, blockState) : null;
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

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos)
    {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity instanceof BlockEntityBossSpawner ? ((BlockEntityBossSpawner) tileentity).getComparatorLevel() : 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity entity, ItemStack stack)
    {
    	if (stack.hasCustomHoverName())
    	{
    		BlockEntity blockentity = level.getBlockEntity(blockPos);
    		if (blockentity instanceof BlockEntityBossSpawner) ((BlockEntityBossSpawner) blockentity).setName(stack.getHoverName());
    	}
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		int powerOnSpawned = 7;
		int powerOnKilled = 15;
    	CompoundTag root = stack.getTag();
    	if (root != null && root.contains("BlockEntityTag", 10))
    	{
    		CompoundTag blockTag = root.getCompound("BlockEntityTag");
    		if (blockTag.contains("levelOnActive", 99)) powerOnSpawned = blockTag.getByte("levelOnActive");
    		if (blockTag.contains("levelOnKilled", 99)) powerOnKilled = blockTag.getByte("levelOnKilled");
    	}
		tooltip.add(new TranslatableComponent("custombgm.tooltip.boss_spawner", powerOnSpawned, powerOnKilled));
		tooltip.add(new TranslatableComponent("custombgm.tooltip.redstone_activated"));
	}

}