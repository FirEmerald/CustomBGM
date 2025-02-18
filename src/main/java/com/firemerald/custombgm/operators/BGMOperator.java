package com.firemerald.custombgm.operators;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.capabilities.ServerPlayerData;
import com.firemerald.custombgm.client.gui.screen.BGMScreen;
import com.firemerald.custombgm.client.gui.screen.OperatorScreen;
import com.firemerald.custombgm.codecs.BGMDistributionCodec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BGMOperator<O extends BGMOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorBase<Player, O, S> {
	public BgmDistribution music = BgmDistribution.EMPTY;
	public int priority = 1;
	public int count = 0;

	public BGMOperator(S source) {
		super(Player.class, source);
	}

	@Override
	public void serverTick(Level level, double x, double y, double z) {
		int prevCount = count;
		super.serverTick(level, x, y, z);
		count = this.getSuccessCount();
		if (count != prevCount) source.updateOutputValue();
	}

	@Override
	public boolean operate(Player player) {
		ServerPlayerData.get(player).ifPresent(playerData -> playerData.addMusicOverride(music, priority));
		return true;
	}

	@Override
	public Stream<? extends Player> allEntities(Level level) {
		return level.players().stream();
	}

	@Override
	public void load(CompoundTag tag) {
		int prevCount = this.count;
		super.load(tag);
		count = tag.getInt("count");
		float volume = tag.contains("volume", 99) ? tag.getFloat("volume") : 1f;
		music = new BgmDistribution(BGMDistributionCodec.fromTag(tag.get("music")), volume);
		priority = tag.getInt("priority");
		if (this.count != prevCount) source.updateOutputValue();
	}

	@Override
	public void save(CompoundTag tag) {
		super.save(tag);
		tag.putInt("count", count);
		tag.putFloat("volume", music.volume());
		tag.put("music", BGMDistributionCodec.toTag(music.distribution()));
		tag.putInt("priority", priority);
	}

	@Override
	public void read(FriendlyByteBuf buf) {
		super.read(buf);
		music = BgmDistribution.STREAM_CODEC.decode(buf);
		priority = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		super.write(buf);
		BgmDistribution.STREAM_CODEC.encode(buf, music);
		buf.writeInt(priority);
	}

	@Override
	public int getOutputLevel() {
		return count;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OperatorScreen<O, S> getScreen()
	{
		return new BGMScreen<>(source);
	}

	public static void addTooltip(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag, Function<ItemStack, CompoundTag> getData) {
		tooltipComponents.add(Component.translatable("custombgm.tooltip.bgm"));
	}
}