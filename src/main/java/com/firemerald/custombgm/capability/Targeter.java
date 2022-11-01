package com.firemerald.custombgm.capability;

import javax.annotation.Nullable;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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

	public static Targeter getOrNull(ICapabilityProvider obj)
	{
		return get(obj).resolve().orElse(null);
	}

	public static Targeter getOrNull(ICapabilityProvider obj, @Nullable Direction side)
	{
		return get(obj, side).resolve().orElse(null);
	}
	
    private final LazyOptional<Targeter> holder = LazyOptional.of(() -> this);
    public LivingEntity target = null;

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
        return CAP.orEmpty(cap, holder);
	}
}