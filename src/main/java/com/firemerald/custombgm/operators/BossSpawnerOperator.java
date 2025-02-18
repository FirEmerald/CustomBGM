package com.firemerald.custombgm.operators;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.capabilities.BossTracker;
import com.firemerald.custombgm.capabilities.ServerPlayerData;
import com.firemerald.custombgm.client.gui.screen.BossSpawnerScreen;
import com.firemerald.custombgm.client.gui.screen.OperatorScreen;
import com.firemerald.custombgm.codecs.BGMDistributionCodec;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.registries.ForgeRegistries;

public class BossSpawnerOperator<O extends BossSpawnerOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorBase<Player, O, S> {
	public BgmDistribution music = BgmDistribution.EMPTY;
	public int priority = 1;
	public boolean isRelative = true, disableMusic = false;
	public double spawnX = 0.5, spawnY = 1, spawnZ = 0.5;
	public ResourceLocation toSpawn;
	public CompoundTag spawnTag = new CompoundTag();
	private Entity boss;
	public boolean killed = false;
	public int levelOnActive = 7, levelOnKilled = 15;

	public BossSpawnerOperator(S source) {
		super(Player.class, source);
	}

	public void setKilled(boolean killed) {
		if (this.killed != killed) {
			this.killed = killed;
			source.updateOutputValue();
			source.setIsChanged();
		}
	}

	@Override
	public void serverTick(Level level, double x, double y, double z) {
		//if (!level.isClientSide && !isActive()) setKilled(false);
		super.serverTick(level, x, y, z);
		if (!level.isClientSide) {
			int count = this.getSuccessCount();
			if (!isActive()) {
				despawn();
				setKilled(false);
			}
			else if (!killed) {
				if (boss == null) { //boss does not exist
					if (count > 0) { //spawn boss
						Optional<Reference<EntityType<?>>> typeOpt = ForgeRegistries.ENTITY_TYPES.getDelegate(toSpawn);
						if (typeOpt.isPresent()) {
							Entity entity = typeOpt.get().value().create(level); //TODO
							if (isRelative) {
								x += spawnX;
								y += spawnY;
								z += spawnZ;
							}
							if (spawnTag != null) {
				                CompoundTag nbttagcompound = entity.saveWithoutId(new CompoundTag());
				                UUID uuid = entity.getUUID();
				                nbttagcompound.merge(spawnTag);
				                entity.load(nbttagcompound);
				                entity.setUUID(uuid);
							}
							entity.setPos(x, y, z);
							BossTracker.get(entity).ifPresent(tracker -> {
								if (source.isEntity()) tracker.setBossSpawnerEntity((Entity) source);
								else tracker.setBossSpawnerBlock((BlockEntity) source);
							});
							BossSpawnerOperator.this.setBoss(entity);
							((ServerLevel) level).addFreshEntityWithPassengers(entity);
						}
					}
				}
				else if (count <= 0) { //no players in area, despawn boss
					despawn();
				}
			}
		}
	}

	public void setBoss(Entity boss) {
		if (boss != this.boss) {
			this.boss = boss;
			source.updateOutputValue();
			source.setIsChanged();
		}
	}

	public void despawn() {
		if (boss != null) {
			boss.discard();
			setBoss(null);
		}
	}

	@Override
	public boolean operate(Player player) {
		if (boss != null && !disableMusic) ServerPlayerData.get(player).ifPresent(playerData -> playerData.addMusicOverride(music, priority));
		return true;
	}

	@Override
	public Stream<? extends Player> allEntities(Level level) {
		return level.players().stream();
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		float volume = tag.contains("volume", 99) ? tag.getFloat("volume") : 1f;
		music = new BgmDistribution(BGMDistributionCodec.fromTag(tag.get("music")), volume);
		priority = tag.getInt("priority");
		if (tag.contains("isRelative", 99)) isRelative = tag.getBoolean("isRelative");
		else isRelative = true;
		disableMusic = tag.getBoolean("disableMusic");
		killed = tag.getBoolean("killed");
		spawnX = tag.getDouble("spawnX");
		spawnY = tag.getDouble("spawnY");
		spawnZ = tag.getDouble("spawnZ");
		String toSpawn = tag.getString("toSpawn");
		this.toSpawn = toSpawn.isEmpty() ? null : ResourceLocation.tryParse(toSpawn);
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
		tag.putFloat("volume", music.volume());
		tag.put("music", BGMDistributionCodec.toTag(music.distribution()));
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
		music = BgmDistribution.STREAM_CODEC.decode(buf);
		priority = buf.readInt();
		isRelative = buf.readBoolean();
		disableMusic = buf.readBoolean();
		spawnX = buf.readDouble();
		spawnY = buf.readDouble();
		spawnZ = buf.readDouble();
		String toSpawn = buf.readUtf();
		this.toSpawn = toSpawn.isEmpty() ? null : ResourceLocation.tryParse(toSpawn);
		spawnTag = buf.readNbt();
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
		BgmDistribution.STREAM_CODEC.encode(buf, music);
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

	public static final Decoder<byte[]> TOOLTIP_INFO_DECODER = RecordCodecBuilder.create(instance ->
	instance.group(
			Codec.BYTE.optionalFieldOf("levelOnActive", (byte) 7).forGetter(val -> val[0]),
			Codec.BYTE.optionalFieldOf("levelOnKilled", (byte) 15).forGetter(val -> val[1])
			).apply(instance, (v1, v2) -> new byte[] {v1, v2})
	);

	public static void addTooltip(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag, Function<ItemStack, CompoundTag> getData) {
		int powerOnSpawned = 7, powerOnKilled = 15;
		CompoundTag blockData = getData.apply(stack);
		if (blockData != null) {
			Optional<Pair<byte[], Tag>> result = TOOLTIP_INFO_DECODER.decode(NbtOps.INSTANCE, blockData).result();
			if (result.isPresent()) {
				byte[] tooltipData = result.get().getFirst();
				powerOnSpawned = tooltipData[0];
				powerOnKilled = tooltipData[1];
			}
		}
		tooltipComponents.add(Component.translatable("custombgm.tooltip.boss_spawner", powerOnSpawned, powerOnKilled));
	}
}