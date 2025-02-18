package com.firemerald.custombgm.capabilities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.UnknownNullability;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class BossTracker implements ICapabilitySerializable<ByteTag> {
	public static final ResourceLocation NAME = CustomBGMAPI.id("boss_tracker");
	public static final Capability<BossTracker> CAP = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<BossTracker> holder = LazyOptional.of(() -> this);

	public static LazyOptional<BossTracker> get(ICapabilityProvider obj)
	{
		return obj.getCapability(CAP);
	}

	public static LazyOptional<BossTracker> get(ICapabilityProvider obj, @Nullable Direction side)
	{
		return obj.getCapability(CAP, side);
	}

    private Level level = null;
    private BlockPos pos = null;
    private int entityId = -1;
    private boolean isBoss = false;

	public void setBossSpawnerBlock(BlockEntity blockEntity) {
		this.level = blockEntity.getLevel();
		this.pos = blockEntity.getBlockPos();
		this.entityId = -1;
		this.isBoss = true;
	}

	public void setBossSpawnerEntity(Entity entity) {
		this.level = entity.level();
		this.pos = null;
		this.entityId = entity.getId();
		this.isBoss = true;
	}

	public void setNoBossSpawner() {
		this.level = null;
		this.pos = null;
		this.entityId = -1;
		this.isBoss = false;
	}

    public Object getBossSpawnerObject() {
    	if (!isBoss || level == null) return null;
    	else if (entityId >= 0) return level.getEntity(entityId);
    	else if (pos != null) return level.getBlockEntity(pos);
    	else return null;
    }

	public boolean isBoss() {
		return isBoss;
	}

	@Override
	public @UnknownNullability ByteTag serializeNBT() {
		return isBoss ? ByteTag.ONE : ByteTag.ZERO;
	}

	@Override
	public void deserializeNBT(ByteTag nbt) {
		isBoss = nbt.getAsByte() != 0;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
        return CAP.orEmpty(cap, holder);
	}
}
