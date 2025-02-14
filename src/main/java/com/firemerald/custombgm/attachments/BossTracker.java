package com.firemerald.custombgm.attachments;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.ByteTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BossTracker implements INBTSerializable<ByteTag> {
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
	public @UnknownNullability ByteTag serializeNBT(Provider provider) {
		return isBoss ? ByteTag.ONE : ByteTag.ZERO;
	}

	@Override
	public void deserializeNBT(Provider provider, ByteTag nbt) {
		isBoss = nbt.getAsByte() != 0;
	}
}
