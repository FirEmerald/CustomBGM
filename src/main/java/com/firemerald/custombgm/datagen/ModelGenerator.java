package com.firemerald.custombgm.datagen;

import com.firemerald.custombgm.blocks.ActivatorDetectorRailBlock;
import com.firemerald.custombgm.init.CustomBGMBlockEntities;
import com.firemerald.custombgm.init.CustomBGMBlocks;
import com.firemerald.custombgm.init.CustomBGMItems;
import com.firemerald.fecore.init.registry.BlockObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelGenerator extends BlockStateProvider
{
	public final String modId;

	public ModelGenerator(DataGenerator gen, String modId, ExistingFileHelper exFileHelper)
	{
		super(gen, modId, exFileHelper);
		this.modId = modId;
	}

	@Override
	protected void registerStatesAndModels()
	{
		simpleItem(CustomBGMItems.BGM_MINECART_ITEM, "item/bgm_minecart");
		simpleItem(CustomBGMItems.ENTITY_TESTER_MINECART_ITEM, "item/entity_tester_minecart");
		simpleItem(CustomBGMItems.BOSS_SPAWNER_MINECART_ITEM, "item/boss_spawner_minecart");
		simpleBlock(CustomBGMBlockEntities.BGM, "block/bgm");
		simpleBlock(CustomBGMBlockEntities.ENTITY_TESTER, "block/entity_tester");
		simpleBlock(CustomBGMBlockEntities.BOSS_SPAWNER, "block/boss_spawner");
		ModelFile activatorDetectorRail = models().withExistingParent("activator_detector_rail", mcLoc("block/rail_flat")).texture("rail", "block/activator_detector_rail");
		ModelFile activatorDetectorRailRaisedNE = models().withExistingParent("activator_detector_rail_raised_ne", mcLoc("block/template_rail_raised_ne")).texture("rail", "block/activator_detector_rail");
		ModelFile activatorDetectorRailRaisedSW = models().withExistingParent("activator_detector_rail_raised_sw", mcLoc("block/template_rail_raised_sw")).texture("rail", "block/activator_detector_rail");
		ModelFile activatorDetectorRailOn = models().withExistingParent("activator_detector_rail_on", mcLoc("block/rail_flat")).texture("rail", "block/activator_detector_rail_on");
		ModelFile activatorDetectorRailOnRaisedNE = models().withExistingParent("activator_detector_rail_on_raised_ne", mcLoc("block/template_rail_raised_ne")).texture("rail", "block/activator_detector_rail_on");
		ModelFile activatorDetectorRailOnRaisedSW = models().withExistingParent("activator_detector_rail_on_raised_sw", mcLoc("block/template_rail_raised_sw")).texture("rail", "block/activator_detector_rail_on");
		ModelFile activatorDetectorRailDetected = models().withExistingParent("activator_detector_rail_detected", mcLoc("block/rail_flat")).texture("rail", "block/activator_detector_rail_detected");
		ModelFile activatorDetectorRailDetectedRaisedNE = models().withExistingParent("activator_detector_rail_detected_raised_ne", mcLoc("block/template_rail_raised_ne")).texture("rail", "block/activator_detector_rail_detected");
		ModelFile activatorDetectorRailDetectedRaisedSW = models().withExistingParent("activator_detector_rail_detected_raised_sw", mcLoc("block/template_rail_raised_sw")).texture("rail", "block/activator_detector_rail_detected");
		ModelFile activatorDetectorRailOnDetected = models().withExistingParent("activator_detector_rail_on_detected", mcLoc("block/rail_flat")).texture("rail", "block/activator_detector_rail_on_detected");
		ModelFile activatorDetectorRailOnDetectedRaisedNE = models().withExistingParent("activator_detector_rail_on_detected_raised_ne", mcLoc("block/template_rail_raised_ne")).texture("rail", "block/activator_detector_rail_on_detected");
		ModelFile activatorDetectorRailOnDetectedRaisedSW = models().withExistingParent("activator_detector_rail_on_detected_raised_sw", mcLoc("block/template_rail_raised_sw")).texture("rail", "block/activator_detector_rail_on_detected");
		getVariantBuilder(CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL.getBlock()).forAllStatesExcept(state -> {
			return new ConfiguredModel[] {
				switch (state.getValue(PoweredRailBlock.SHAPE))
				{
				case NORTH_SOUTH -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetected : activatorDetectorRailOn :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetected : activatorDetectorRail);
				case EAST_WEST -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetected : activatorDetectorRailOn :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetected : activatorDetectorRail, 0, 90, false);
				case ASCENDING_NORTH -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetectedRaisedNE : activatorDetectorRailOnRaisedNE :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetectedRaisedNE : activatorDetectorRailRaisedNE);
				case ASCENDING_EAST -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetectedRaisedNE : activatorDetectorRailOnRaisedNE :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetectedRaisedNE : activatorDetectorRailRaisedNE, 0, 90, false);
				case ASCENDING_SOUTH -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetectedRaisedSW : activatorDetectorRailOnRaisedSW :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetectedRaisedSW : activatorDetectorRailRaisedSW);
				case ASCENDING_WEST -> new ConfiguredModel(state.getValue(PoweredRailBlock.POWERED) ?
						state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailOnDetectedRaisedSW : activatorDetectorRailOnRaisedSW :
							state.getValue(ActivatorDetectorRailBlock.DETECTED) ? activatorDetectorRailDetectedRaisedSW : activatorDetectorRailRaisedSW, 0, 90, false);
				default -> throw new IllegalArgumentException("Unexpected value: " + state.getValue(PoweredRailBlock.SHAPE));
				}
			};
		}, BaseRailBlock.WATERLOGGED);
		simpleItem(CustomBGMBlocks.ACTIVATOR_DETECTOR_RAIL, "block/activator_detector_rail");
	}

	public void simpleItem(ItemLike item, String texture)
	{
		simpleItem(item, new ResourceLocation(modId, texture));
	}

	public void simpleItem(ItemLike item, ResourceLocation texture)
	{
		itemModels().getBuilder(item.asItem().getRegistryName().toString())
		.parent(new ModelFile.UncheckedModelFile("item/generated"))
		.texture("layer0", texture);
	}

	public void layeredItem(ItemLike item, String... textures)
	{
		ResourceLocation[] textureLocs = new ResourceLocation[textures.length];
		for (int i = 0; i < textures.length; ++i) textureLocs[i] = new ResourceLocation(modId, textures[i]);
		layeredItem(item, textureLocs);
	}

	public void layeredItem(ItemLike item, ResourceLocation... textures)
	{
		ItemModelBuilder builder = itemModels().getBuilder(item.asItem().getRegistryName().toString())
		.parent(new ModelFile.UncheckedModelFile("item/generated"));
		for (int i = 0; i < textures.length; ++i) builder.texture("layer" + i, textures[i]);
	}

	public ModelFile simpleBlock(BlockObject<?, ?> block, String texture)
	{
		return simpleBlock(block, new ResourceLocation(modId, texture));
	}

	public ModelFile simpleBlock(BlockObject<?, ?> block, ResourceLocation texture)
	{
		ModelFile model = models().cubeAll(block.getBlock().getRegistryName().getPath(), texture);
		simpleBlock(block.getBlock(), model);
		simpleBlockItem(block.getBlock(), model);
		return model;
	}
}
