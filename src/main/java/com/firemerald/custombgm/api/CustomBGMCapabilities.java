package com.firemerald.custombgm.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * Registers and holds the capability instance for the IPlayer capability
 *
 * @author FirEmerald
 *
 */
public class CustomBGMCapabilities
{
	public static final Capability<IPlayer> MUSIC_PLAYER = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IBossTracker> BOSS_TRACKER = CapabilityManager.get(new CapabilityToken<>(){});
}