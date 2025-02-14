package com.firemerald.custombgm.datagen;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.custombgm.blocks.ActivatorDetectorRailBlock;
import com.firemerald.custombgm.init.CustomBGMObjects;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;

public class CustomBGMModelProvider extends ModelProvider {
	public CustomBGMModelProvider(PackOutput output) {
		super(output, CustomBGMAPI.MOD_ID);
	}

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
    	itemModels.generateFlatItem(CustomBGMObjects.BGM_MINECART.getItem(), ModelTemplates.FLAT_ITEM);
    	itemModels.generateFlatItem(CustomBGMObjects.ENTITY_TESTER_MINECART.getItem(), ModelTemplates.FLAT_ITEM);
    	itemModels.generateFlatItem(CustomBGMObjects.BOSS_SPAWNER_MINECART.getItem(), ModelTemplates.FLAT_ITEM);
    	blockModels.createTrivialCube(CustomBGMObjects.BGM.getBlock());
    	blockModels.createTrivialCube(CustomBGMObjects.ENTITY_TESTER.getBlock());
    	blockModels.createTrivialCube(CustomBGMObjects.BOSS_SPAWNER.getBlock());
    	createActivatorDetectorRail(blockModels, CustomBGMObjects.ACTIVATOR_DETECTOR_RAIL.getBlock());
    }

    public static final ResourceLocation RENDER_TYPE_CUTOUT = ResourceLocation.withDefaultNamespace("cutout");
    public static final ExtendedModelTemplate RAIL_FLAT = ModelTemplates.RAIL_FLAT.extend().renderType(RENDER_TYPE_CUTOUT).build();
    public static final ExtendedModelTemplate RAIL_RAISED_NE = ModelTemplates.RAIL_RAISED_NE.extend().renderType(RENDER_TYPE_CUTOUT).build();
    public static final ExtendedModelTemplate RAIL_RAISED_SW = ModelTemplates.RAIL_RAISED_SW.extend().renderType(RENDER_TYPE_CUTOUT).build();

    public void createActivatorDetectorRail(BlockModelGenerators blockModels, Block railBlock) {
        ResourceLocation flat_idle = blockModels.createSuffixedVariant(railBlock, "", RAIL_FLAT, TextureMapping::rail);
        ResourceLocation raised_ne_idle = blockModels.createSuffixedVariant(railBlock, "", RAIL_RAISED_NE, TextureMapping::rail);
        ResourceLocation raised_sw_idle = blockModels.createSuffixedVariant(railBlock, "", RAIL_RAISED_SW, TextureMapping::rail);
        ResourceLocation flat_powered = blockModels.createSuffixedVariant(railBlock, "_on", RAIL_FLAT, TextureMapping::rail);
        ResourceLocation raised_ne_powered = blockModels.createSuffixedVariant(railBlock, "_on", RAIL_RAISED_NE, TextureMapping::rail);
        ResourceLocation raised_sw_powered = blockModels.createSuffixedVariant(railBlock, "_on", RAIL_RAISED_SW, TextureMapping::rail);
        ResourceLocation flat_detected = blockModels.createSuffixedVariant(railBlock, "_detected", RAIL_FLAT, TextureMapping::rail);
        ResourceLocation raised_ne_detected = blockModels.createSuffixedVariant(railBlock, "_detected", RAIL_RAISED_NE, TextureMapping::rail);
        ResourceLocation raised_sw_detected = blockModels.createSuffixedVariant(railBlock, "_detected", RAIL_RAISED_SW, TextureMapping::rail);
        ResourceLocation flat_powered_detected = blockModels.createSuffixedVariant(railBlock, "_on_detected", RAIL_FLAT, TextureMapping::rail);
        ResourceLocation raised_ne_powered_detected = blockModels.createSuffixedVariant(railBlock, "_on_detected", RAIL_RAISED_NE, TextureMapping::rail);
        ResourceLocation raised_sw_powered_detected = blockModels.createSuffixedVariant(railBlock, "_on_detected", RAIL_RAISED_SW, TextureMapping::rail);
        PropertyDispatch propertydispatch = PropertyDispatch.properties(BlockStateProperties.POWERED, ActivatorDetectorRailBlock.DETECTED, BlockStateProperties.RAIL_SHAPE_STRAIGHT)
            .generate(
                (powered, detected, shape) -> {
                    switch (shape) {
                        case NORTH_SOUTH:
                            return Variant.variant().with(VariantProperties.MODEL,
                            		powered ?
                            				detected ? flat_powered_detected : flat_powered :
                            				detected ? flat_detected : flat_idle);
                        case EAST_WEST:
                            return Variant.variant()
                                .with(VariantProperties.MODEL,
                                		powered ?
                                				detected ? flat_powered_detected : flat_powered :
                                				detected ? flat_detected : flat_idle)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                        case ASCENDING_EAST:
                            return Variant.variant()
                                .with(VariantProperties.MODEL,
                                		powered ?
                                				detected ? raised_ne_powered_detected : raised_ne_powered :
                                				detected ? raised_ne_detected : raised_ne_idle)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                        case ASCENDING_WEST:
                            return Variant.variant()
                                .with(VariantProperties.MODEL,
                                		powered ?
                                				detected ? raised_sw_powered_detected : raised_sw_powered :
                                				detected ? raised_sw_detected : raised_sw_idle)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                        case ASCENDING_NORTH:
                            return Variant.variant().with(VariantProperties.MODEL,
                            		powered ?
                            				detected ? raised_ne_powered_detected : raised_ne_powered :
                            				detected ? raised_ne_detected : raised_ne_idle);
                        case ASCENDING_SOUTH:
                            return Variant.variant().with(VariantProperties.MODEL,
                            		powered ?
                            				detected ? raised_sw_powered_detected : raised_sw_powered :
                            				detected ? raised_sw_detected : raised_sw_idle);
                        default:
                            throw new UnsupportedOperationException("Fix your generator!");
                    }
                }
            );
        blockModels.registerSimpleFlatItemModel(railBlock);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(railBlock).with(propertydispatch));
    }

}
