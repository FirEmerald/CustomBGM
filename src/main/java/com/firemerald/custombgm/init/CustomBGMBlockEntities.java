package com.firemerald.custombgm.init;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.blockentity.BlockEntityBGM;
import com.firemerald.custombgm.blockentity.BlockEntityBossSpawner;
import com.firemerald.custombgm.blockentity.BlockEntityEntityTester;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(CustomBGMAPI.MOD_ID)
public class CustomBGMBlockEntities
{
	@ObjectHolder("bgm")
	public static final BlockEntityType<BlockEntityBGM> BGM = null;
	@ObjectHolder("entity_tester")
	public static final BlockEntityType<BlockEntityEntityTester> ENTITY_TESTER = null;
	@ObjectHolder("boss_spawner")
	public static final BlockEntityType<BlockEntityBossSpawner> BOSS_SPAWNER = null;
	
	public static void registerBlocks(IEventBus eventBus)
	{
		DeferredRegister<BlockEntityType<?>> blocks = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CustomBGMAPI.MOD_ID);
		blocks.register("bgm", () -> BlockEntityType.Builder.of(BlockEntityBGM::new, CustomBGMBlocks.BGM).build(null));
		blocks.register("entity_tester", () -> BlockEntityType.Builder.of(BlockEntityEntityTester::new, CustomBGMBlocks.ENTITY_TESTER).build(null));
		blocks.register("boss_spawner", () -> BlockEntityType.Builder.of(BlockEntityBossSpawner::new, CustomBGMBlocks.BOSS_SPAWNER).build(null));
		blocks.register(eventBus);
	}
}