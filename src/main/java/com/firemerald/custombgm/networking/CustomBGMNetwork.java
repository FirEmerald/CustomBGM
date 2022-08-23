package com.firemerald.custombgm.networking;

import com.firemerald.custombgm.api.CustomBGMAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CustomBGMNetwork
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(CustomBGMAPI.MOD_ID, "main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
    );
    
    public static void init()
    {
    	//int id  = 0;
    	//INSTANCE.registerMessage(id++, ShapeToolSetPacket.class, ShapeToolSetPacket::write, ShapeToolSetPacket::new, ShapeToolSetPacket::handle);
    }

}
