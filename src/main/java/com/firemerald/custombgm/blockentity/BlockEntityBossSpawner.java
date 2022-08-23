package com.firemerald.custombgm.blockentity;

import java.util.UUID;
import java.util.stream.Stream;

import com.firemerald.custombgm.api.CustomBGMCapabilities;
import com.firemerald.custombgm.api.IPlayer;
import com.firemerald.custombgm.client.gui.GuiBossSpawner;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.fecore.betterscreens.BlockEntityGUIScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public class BlockEntityBossSpawner extends BlockEntityEntityOperator<Player>
{
	public ResourceLocation music;
	public int priority;
	public boolean isRelative = true, disableMusic = false;
	public double spawnX = 0.5, spawnY = 1, spawnZ = 0.5;
	public ResourceLocation toSpawn;
	public CompoundTag spawnTag = new CompoundTag();
	private Entity boss;
	public boolean killed = false;
	public int levelOnActive = 7, levelOnKilled = 15;

    public BlockEntityBossSpawner(BlockPos pos, BlockState state)
    {
    	this(CustomBGMBlockEntities.BOSS_SPAWNER, pos, state);
    }

	public BlockEntityBossSpawner(BlockEntityType<? extends BlockEntityBossSpawner> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, Player.class);
	}

	public int getComparatorLevel()
	{
		if (killed) return levelOnKilled;
		else if (boss != null) return levelOnActive;
		else return 0;
	}

	public void setKilled(boolean killed)
	{
		if (this.killed != killed)
		{
			this.killed = killed;
			level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
			this.setChanged();
		}
	}

	@Override
	public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
	{
		if (!level.isClientSide && level.hasNeighborSignal(worldPosition)) setKilled(false);
		super.serverTick(level, blockPos, blockState);
		if (!level.isClientSide)
		{
			int count = this.getSuccessCount();
			if (boss == null) //boss does not exist
			{
				if (isActive() && count > 0 && toSpawn != null) //spawn boss
				{
					@SuppressWarnings("deprecation")
					EntityType<?> type = Registry.ENTITY_TYPE.get(toSpawn);
					Entity entity = type.create(level);
					double x, y, z;
					if (isRelative)
					{
						x = spawnX + worldPosition.getX();
						y = spawnY + worldPosition.getY();
						z = spawnZ + worldPosition.getZ();
					}
					else
					{
						x = spawnX;
						y = spawnY;
						z = spawnZ;
					}
					if (spawnTag != null)
					{
		                CompoundTag nbttagcompound = entity.saveWithoutId(new CompoundTag());
		                UUID uuid = entity.getUUID();
		                nbttagcompound.merge(spawnTag);
		                entity.load(nbttagcompound);
		                entity.setUUID(uuid);
					}
					entity.setPos(x, y, z);
					setBoss(entity);
					((ServerLevel) level).addFreshEntityWithPassengers(entity);
				}
			}
			else if ((!isActive() || count <= 0) && boss != null) //no players in area, despawn boss
			{
				despawn();
			}
		}
	}

	public void setBoss(Entity boss)
	{
		if (boss != this.boss)
		{
			this.boss = boss;
			level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
			this.setChanged();
		}
	}

	public void despawn()
	{
		if (boss != null)
		{
			boss.discard();
			setBoss(null);
		}
	}

	@Override
	public boolean isActive()
	{
		return !level.isClientSide && !killed && toSpawn != null && level.hasNeighborSignal(worldPosition);
	}

	@Override
	public boolean operate(Player player)
	{
		if (boss != null && !disableMusic)
		{
			LazyOptional<IPlayer> iPlayer = player.getCapability(CustomBGMCapabilities.MUSIC_PLAYER, null);
			if (iPlayer.isPresent()) iPlayer.resolve().get().addMusicOverride(music, priority);
		}
		return true;
	}

	@Override
	public Stream<? extends Player> allEntities()
	{
		return level.players().stream();
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		String music = tag.getString("music");
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = tag.getInt("priority");
		if (tag.contains("isRelative", 99)) isRelative = tag.getBoolean("isRelative");
		else isRelative = true;
		disableMusic = tag.getBoolean("disableMusic");
		killed = tag.getBoolean("killed");
		spawnX = tag.getDouble("spawnX");
		spawnY = tag.getDouble("spawnY");
		spawnZ = tag.getDouble("spawnZ");
		String toSpawn = tag.getString("toSpawn");
		this.toSpawn = toSpawn.isEmpty() ? null : new ResourceLocation(toSpawn);
		if (tag.contains("spawnTag", 10))
		{
			spawnTag = tag.getCompound("spawnTag");
			if (spawnTag.isEmpty()) spawnTag = null;
		}
		else spawnTag = null;
		levelOnActive = tag.contains("levelOnActive", 99) ? tag.getByte("levelOnActive") : 7;
		levelOnKilled = tag.contains("levelOnKilled", 99) ? tag.getByte("levelOnKilled") : 15;
	}

	@Override
	public void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.putString("music", music == null ? "" : music.toString());
		tag.putInt("priority", priority);
		tag.putBoolean("isRelative", isRelative);
		tag.putBoolean("disableMusic", disableMusic);
		tag.putBoolean("killed", killed);
		tag.putDouble("spawnX", spawnX);
		tag.putDouble("spawnY", spawnY);
		tag.putDouble("spawnZ", spawnZ);
		tag.putString("toSpawn", toSpawn == null ? "" : toSpawn.toString());
		if (spawnTag != null) tag.put("spawnTag", spawnTag);
		tag.putByte("levelOnActive", (byte) levelOnActive);
		tag.putByte("levelOnKilled", (byte) levelOnKilled);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		super.read(buf);
		String music = buf.readUtf();
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = buf.readInt();
		isRelative = buf.readBoolean();
		disableMusic = buf.readBoolean();
		spawnX = buf.readDouble();
		spawnY = buf.readDouble();
		spawnZ = buf.readDouble();
		String toSpawn = buf.readUtf();
		this.toSpawn = toSpawn.isEmpty() ? null : new ResourceLocation(toSpawn);
		spawnTag = buf.readAnySizeNbt();
		if (spawnTag.isEmpty()) spawnTag = null;
		byte levels = buf.readByte();
		levelOnActive = levels & 0xF;
		levelOnKilled = (levels >> 4) & 0xF;
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeUtf(music == null ? "" : music.toString());
		buf.writeInt(priority);
		buf.writeBoolean(isRelative);
		buf.writeBoolean(disableMusic);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnY);
		buf.writeDouble(spawnZ);
		buf.writeUtf(toSpawn == null ? "" : toSpawn.toString());
		buf.writeNbt(spawnTag == null ? new CompoundTag() : spawnTag);
		buf.writeByte(levelOnActive | (levelOnKilled << 4));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockEntityGUIScreen getScreen()
	{
		return new GuiBossSpawner(this.worldPosition);
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		despawn();
	}
	
	@Override
	public void onChunkUnloaded()
	{
		super.onChunkUnloaded();
		despawn();
	}
}