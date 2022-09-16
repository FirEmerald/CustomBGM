package com.firemerald.custombgm.blockentity;

import java.util.stream.Stream;

import com.firemerald.custombgm.api.capabilities.IPlayer;
import com.firemerald.custombgm.client.gui.screen.GuiBGM;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.fecore.client.gui.screen.BlockEntityGUIScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public class BlockEntityBGM extends BlockEntityEntityOperator<Player>
{
	public ResourceLocation music;
	public int priority;
	public int count = 0;

	public BlockEntityBGM(BlockPos pos, BlockState state)
	{
		this(CustomBGMBlockEntities.BGM.getBlockEntityType(), pos, state);
	}

    public BlockEntityBGM(BlockEntityType<? extends BlockEntityBGM> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state, Player.class);
    }

	@Override
	public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
	{
		int prevCount = this.getSuccessCount();
		super.serverTick(level, blockPos, blockState);
		count = this.getSuccessCount();
		if (count != prevCount) level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
	}

	@Override
	public boolean isActive()
	{
		return !level.isClientSide && level.hasNeighborSignal(worldPosition);
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
	public Stream<? extends Player> allEntities()
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
		if (this.count != prevCount && level != null) level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
	}

	@Override
	public void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
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
	@OnlyIn(Dist.CLIENT)
	public BlockEntityGUIScreen getScreen()
	{
		return new GuiBGM(this.worldPosition);
	}
}