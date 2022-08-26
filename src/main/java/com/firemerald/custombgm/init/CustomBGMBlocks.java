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
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BGM)
	public static final BlockBGM BGM = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER)
	public static final BlockEntityTester ENTITY_TESTER = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER)
	public static final BlockBossSpawner BOSS_SPAWNER = null;

	public static void registerBlocks(IEventBus eventBus)
	{
		DeferredRegister<Block> blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, CustomBGMAPI.MOD_ID);
		blocks.register(RegistryNames.BLOCK_ENTITY_BGM, BlockBGM::new);
		blocks.register(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER, BlockEntityTester::new);
		blocks.register(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER, BlockBossSpawner::new);
		blocks.register(eventBus);
	}
}