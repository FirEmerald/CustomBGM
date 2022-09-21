package com.firemerald.custombgm.operators;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.gui.screen.BGMScreen;
import com.firemerald.custombgm.client.gui.screen.OperatorScreen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public class BGMOperator<O extends BGMOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorBase<Player, O, S>
{
	public ResourceLocation music;
	public int priority;
	public int count = 0;

	public BGMOperator(S source)
	{
		super(Player.class, source);
	}

	@Override
	public void serverTick(Level level, double x, double y, double z)
	{
		int prevCount = count;
		super.serverTick(level, x, y, z);
		count = this.getSuccessCount();
		if (count != prevCount) source.updateOutputValue();
	}

	@Override
	public boolean operate(Player player)
	{
		LazyOptional<IPlayer> iPlayer = IPlayer.get(player);
		if (iPlayer.isPresent())
		{
			iPlayer.resolve().get().addMusicOverride(music, priority);
			return true;
		}
		else return false;
	}

	@Override
	public Stream<? extends Player> allEntities(Level level)
	{
		return level.players().stream();
	}

	@Override
	public void load(CompoundTag tag)
	{
		int prevCount = this.count;
		super.load(tag);
		count = tag.getInt("count");
		String music = tag.getString("music");
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = tag.getInt("priority");
		if (this.count != prevCount) source.updateOutputValue();
	}

	@Override
	public void save(CompoundTag tag)
	{
		super.save(tag);
		tag.putInt("count", count);
		tag.putString("music", music == null ? "" : music.toString());
		tag.putInt("priority", priority);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		super.read(buf);
		String music = buf.readUtf();
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeUtf(music == null ? "" : music.toString());
		buf.writeInt(priority);
	}

	@Override
	public int getOutputLevel()
	{
		return count;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public OperatorScreen<O, S> getScreen()
	{
		return new BGMScreen<>(source);
	}

	public static void addTooltip(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag, Supplier<CompoundTag> operatorTag)
	{
		tooltip.add(new TranslatableComponent("custombgm.tooltip.bgm"));
	}
}