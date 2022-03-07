package firemerald.custombgm.init;

import static firemerald.api.core.InitFunctions.addBlock;

import firemerald.custombgm.blocks.BlockBGM;
import firemerald.custombgm.blocks.BlockBossSpawner;
import firemerald.custombgm.blocks.BlockEntityTester;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

public class LSBlocks
{
	public static final BlockBGM BGM = new BlockBGM();
	public static final BlockEntityTester ENTITY_TESTER = new BlockEntityTester();
	public static final BlockBossSpawner BOSS_SPAWNER = new BlockBossSpawner();

	public static void init(IForgeRegistry<Block> registry)
	{
		addBlock(BGM, "bgm", registry);
		addBlock(ENTITY_TESTER, "entity_tester", registry);
		addBlock(BOSS_SPAWNER, "boss_spawner", registry);
	}
}