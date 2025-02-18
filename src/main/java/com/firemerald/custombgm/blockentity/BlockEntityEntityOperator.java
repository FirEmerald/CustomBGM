package com.firemerald.custombgm.blockentity;

import javax.annotation.Nullable;

import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.fecore.client.gui.screen.NetworkedGUIEntityScreen;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockEntityEntityOperator<O extends OperatorBase<?, O, S>, S extends BlockEntityEntityOperator<O, S> & IOperatorSource<O, S>> extends BlockEntity implements IOperatorSource<O, S>
{
	public static final Component DEFAULT_NAME = Component.literal("@");
    private Component customName = DEFAULT_NAME;
    public final O operator;

	public BlockEntityEntityOperator(BlockEntityType<? extends S> type, BlockPos pos, BlockState state)
    {
    	super(type, pos, state);
    	this.operator = makeOperator();
    }

	protected abstract O makeOperator();

    @Override
    public CommandSourceStack createACommandSourceStack()
    {
       return new CommandSourceStack(this, Vec3.atCenterOf(worldPosition), Vec2.ZERO, (ServerLevel) getLevel(), 2, getName().getString(), getName(), getLevel().getServer(), null);
    }

	public void serverTick(Level level, BlockPos blockPos, BlockState blockState)
	{
		operator.serverTick(level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	@Override
    public void load(CompoundTag tag)
	{
		super.load(tag);
		operator.load(tag);
        if (tag.contains("CustomName", 8)) this.setName(Component.Serializer.fromJson(tag.getString("CustomName")));
        else this.setName(null);
	}

	@Override
	public void saveAdditional(CompoundTag tag)
	{
		super.saveAdditional(tag);
		operator.save(tag);
        if (this.customName != null) tag.putString("CustomName", Component.Serializer.toJson(this.customName));
	}

	public Component getName()
	{
		return this.customName;
	}

	public void setName(@Nullable Component customName)
	{
		if (customName != null) this.customName = customName;
		else this.customName = DEFAULT_NAME;
	}

	@Override
	public Component getTheName()
	{
		return getName();
	}

	@Override
	public void setTheName(Component name)
	{
		setName(name);
	}

	@Override
	public boolean acceptsSuccess()
	{
		return false;
	}

	@Override
	public boolean acceptsFailure()
	{
		return false;
	}

	@Override
	public boolean shouldInformAdmins()
	{
		return false;
	}

	@Override
	public void sendSystemMessage(Component component) {}

	@Override
	public void updateOutputValue()
	{
		if (level != null) level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
	}

	@Override
	public Vec3 getPosition()
	{
		return new Vec3(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
	}

	@Override
	public boolean isEntity()
	{
		return false;
	}

	@Override
	public void setIsChanged()
	{
		this.setChanged();
	}

	@Override
	public boolean isActive()
	{
		return level.hasNeighborSignal(worldPosition);
	}

	@Override
	public void setRemoved()
	{
		super.setRemoved();
		this.operator.onRemoved();
	}

	@Override
	public void onChunkUnloaded()
	{
		super.onChunkUnloaded();
		this.operator.onRemoved();
	}

	@Override
	public O getOperator()
	{
		return operator;
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.operator.readInternal(buf);
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		this.operator.write(buf);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public NetworkedGUIEntityScreen<S> getScreen()
	{
		return this.operator.getScreen();
	}
}