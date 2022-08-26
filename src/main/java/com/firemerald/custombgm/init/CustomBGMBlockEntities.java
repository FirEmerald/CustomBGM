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
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BGM)
	public static final BlockEntityType<BlockEntityBGM> BGM = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER)
	public static final BlockEntityType<BlockEntityEntityTester> ENTITY_TESTER = null;
	@ObjectHolder(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER)
	public static final BlockEntityType<BlockEntityBossSpawner> BOSS_SPAWNER = null;

	public static void registerBlockEntities(IEventBus eventBus)
	{
		DeferredRegister<BlockEntityType<?>> blockEntities = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CustomBGMAPI.MOD_ID);
		blockEntities.register(RegistryNames.BLOCK_ENTITY_BGM, () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityBGM(BGM, pos, state), CustomBGMBlocks.BGM).build(null));
		blockEntities.register(RegistryNames.BLOCK_ENTITY_ENTITY_TESTER, () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityEntityTester(ENTITY_TESTER, pos, state), CustomBGMBlocks.ENTITY_TESTER).build(null));
		blockEntities.register(RegistryNames.BLOCK_ENTITY_BOSS_SPAWNER, () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityBossSpawner(BOSS_SPAWNER, pos, state), CustomBGMBlocks.BOSS_SPAWNER).build(null));
		blockEntities.register(eventBus);
	}
}