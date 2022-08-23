package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.blocks.BlockBGM;
import com.firemerald.custombgm.blocks.BlockBossSpawner;
import com.firemerald.custombgm.blocks.BlockEntityTester;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CustomBGMAPI.MOD_ID)
public class CustomBGMBlocks
{
	@ObjectHolder("bgm")
	public static final BlockBGM BGM = null;
	@ObjectHolder("entity_tester")
	public static final BlockEntityTester ENTITY_TESTER = null;
	@ObjectHolder("boss_spawner")
	public static final BlockBossSpawner BOSS_SPAWNER = null;
	
	public static void registerBlocks(IEventBus eventBus)
	{
		DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, CustomBGMAPI.MOD_ID);
		blocks.register("bgm", BlockBGM::new);
		blocks.register("entity_tester", BlockEntityTester::new);
		blocks.register("boss_spawner", BlockBossSpawner::new);
		blocks.register(eventBus);
	}
}