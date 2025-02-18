package com.firemerald.custombgm.capabilities;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class Targeter implements ICapabilityProvider
{
	public static final ResourceLocation NAME = new ResourceLocation(CustomBGMAPI.MOD_ID, "targeter");
	public static final Capability<Targeter> CAP = CapabilityManager.get(new CapabilityToken<>(){});

	public static LazyOptional<Targeter> get(ICapabilityProvider obj)
	{
		return obj.getCapability(CAP);
	}

	public static LazyOptional<Targeter> get(ICapabilityProvider obj, @Nullable Direction side)
	{
		return obj.getCapability(CAP, side);
	}

    private final LazyOptional<Targeter> holder = LazyOptional.of(() -> this);
    public ServerPlayer target = null;

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
        return CAP.orEmpty(cap, holder);
	}
}