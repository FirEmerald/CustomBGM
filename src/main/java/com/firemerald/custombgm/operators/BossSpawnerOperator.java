package com.firemerald.custombgm.operators;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.firemerald.custombgm.api.capabilities.IBossTracker;
import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.gui.screen.BossSpawnerScreen;
import com.firemerald.custombgm.client.gui.screen.OperatorScreen;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BossSpawnerOperator<O extends BossSpawnerOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorBase<Player, O, S>
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

	public BossSpawnerOperator(S source)
	{
		super(Player.class, source);
	}

	public void setKilled(boolean killed)
	{
		if (this.killed != killed)
		{
			this.killed = killed;
			source.updateOutputValue();
			source.setIsChanged();
		}
	}

	@Override
	public void serverTick(Level level, double x, double y, double z)
	{
		//if (!level.isClientSide && !isActive()) setKilled(false);
		super.serverTick(level, x, y, z);
		if (!level.isClientSide)
		{
			int count = this.getSuccessCount();
			if (!isActive())
			{
				despawn();
				setKilled(false);
			}
			else if (!killed)
			{
				if (boss == null) //boss does not exist
				{
					if (count > 0) //spawn boss
					{
						@SuppressWarnings("deprecation")
						EntityType<?> type = Registry.ENTITY_TYPE.get(toSpawn);
						Entity entity = type.create(level);
						if (isRelative)
						{
							x += spawnX;
							y += spawnY;
							z += spawnZ;
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
						IBossTracker.get(entity).ifPresent(tracker -> {
							if (source.isEntity()) tracker.setBossSpawnerEntity((Entity) source);
							else tracker.setBossSpawnerBlock((BlockEntity) source);
							BossSpawnerOperator.this.setBoss(entity);
							((ServerLevel) level).addFreshEntityWithPassengers(entity);
						});
					}
				}
				else if (count <= 0) //no players in area, despawn boss
				{
					despawn();
				}
			}
		}
	}

	public void setBoss(Entity boss)
	{
		if (boss != this.boss)
		{
			this.boss = boss;
			source.updateOutputValue();
			source.setIsChanged();
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
	public boolean operate(Player player)
	{
		if (boss != null && !disableMusic) IPlayer.get(player).ifPresent(iPlayer -> iPlayer.addMusicOverride(music, priority));
		return true;
	}

	@Override
	public Stream<? extends Player> allEntities(Level level)
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
		despawn();
	}

	@Override
	public void save(CompoundTag tag)
	{
		super.save(tag);
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
		despawn();
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
	public void onRemoved()
	{
		super.onRemoved();
		despawn();
	}

	@Override
	public int getOutputLevel()
	{
		if (killed) return levelOnKilled;
		else if (boss != null) return levelOnActive;
		else return 0;
	}

	@Override
	public boolean isActive()
	{
		return toSpawn != null && super.isActive();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OperatorScreen<O, S> getScreen()
	{
		return new BossSpawnerScreen<>(source);
	}

	public static void addTooltip(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag, Supplier<CompoundTag> operatorTag)
	{
		int powerOnSpawned = 7;
		int powerOnKilled = 15;
		CompoundTag blockTag = operatorTag.get();
    	if (blockTag != null)
    	{
    		if (blockTag.contains("levelOnActive", 99)) powerOnSpawned = blockTag.getByte("levelOnActive");
    		if (blockTag.contains("levelOnKilled", 99)) powerOnKilled = blockTag.getByte("levelOnKilled");
    	}
		tooltip.add(new TranslatableComponent("custombgm.tooltip.boss_spawner", powerOnSpawned, powerOnKilled));
	}
}