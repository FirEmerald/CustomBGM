package com.firemerald.custombgm.operators;

import com.firemerald.fecore.client.gui.screen.NetworkedGUIEntityScreen;
import com.firemerald.fecore.util.INetworkedGUIEntity;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IOperatorSource<O extends OperatorBase<?, O, S>, S extends IOperatorSource<O, S>> extends CommandSource, INetworkedGUIEntity<S>
{
	public abstract O getOperator();

	public abstract void setIsChanged();

	public abstract void updateOutputValue();

	public abstract CommandSourceStack createACommandSourceStack();

	public abstract boolean isActive();

	public abstract Vec3 getPosition();

	public abstract Component getTheName();

	public abstract void setTheName(Component name);

	public abstract boolean isEntity();

	@Override
	@OnlyIn(Dist.CLIENT)
	public abstract NetworkedGUIEntityScreen<S> getScreen();
}