package com.firemerald.custombgm.entity;

import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.custombgm.operators.OperatorBase;
import com.firemerald.fecore.FECoreMod;
import com.firemerald.fecore.client.gui.screen.NetworkedGUIEntityScreen;
import com.firemerald.fecore.networking.client.EntityGUIPacket;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class OperatorMinecart<O extends OperatorBase<?, O, S>, S extends OperatorMinecart<O, S> & IOperatorSource<O, S>> extends AbstractMinecart implements IOperatorSource<O, S>
{
    public final O operator;
    protected boolean isActive = false;

	public OperatorMinecart(EntityType<?> type, Level level)
	{
		super(type, level);
    	this.operator = makeOperator();
	}

	public OperatorMinecart(EntityType<?> type, Level level, double x, double y, double z)
	{
		super(type, level, x, y, z);
    	this.operator = makeOperator();
	}

	protected abstract O makeOperator();

	@Override
	public Type getMinecartType()
	{
		return Type.COMMAND_BLOCK;
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

	@Override
	public O getOperator()
	{
		return this.operator;
	}

	@Override
	public void setIsChanged() {}

	@Override
	public void updateOutputValue() {}

	@Override
	public boolean isActive()
	{
		return isActive;
	}

	@Override
	public Vec3 getPosition()
	{
		return this.position();
	}

	@Override
	public Component getTheName()
	{
		return this.getName();
	}

	@Override
	public void setTheName(Component name)
	{
		this.setCustomName(name);
	}

	@Override
	public boolean isEntity()
	{
		return true;
	}

	@Override
	public CommandSourceStack createACommandSourceStack()
	{
		return this.createCommandSourceStack();
	}

	@Override
	public void activateMinecart(int x, int y, int z, boolean active)
	{
		this.isActive = active;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public InteractionResult interact(Player player, InteractionHand hand)
	{
    	if (!player.isCreative()) return InteractionResult.PASS;
    	else
    	{
			if (level.isClientSide) return InteractionResult.SUCCESS;
	    	else
	    	{
	    		if (player instanceof ServerPlayer) FECoreMod.NETWORK.sendTo(new EntityGUIPacket(this), (ServerPlayer) player);
	    		return InteractionResult.CONSUME;
	    	}
    	}
	}

	@Override
    public void tick()
    {
		super.tick();
		if (!level.isClientSide) operator.serverTick(level, position().x, position().y, position().z);
    }

	@Override
	public abstract ItemStack getPickResult();

	@Override
	public int getComparatorLevel()
	{
		return operator.getOutputLevel();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag)
	{
		super.readAdditionalSaveData(tag);
		operator.load(tag);
		isActive = tag.getBoolean("isActive");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag)
	{
		super.addAdditionalSaveData(tag);
		operator.save(tag);
		tag.putBoolean("isActive", isActive);
	}
	
	@Override
	public void onRemovedFromWorld()
	{
		super.onRemovedFromWorld();
		operator.onRemoved();
	}
}
