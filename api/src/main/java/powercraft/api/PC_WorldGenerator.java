package powercraft.api;

import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import powercraft.api.annotation.PC_OreInfo;
import powercraft.api.block.PC_Block;

public class PC_WorldGenerator implements IWorldGenerator {

	private static HashMap<Integer, PC_OreInfo> info = new HashMap<Integer, PC_OreInfo>();
	private static HashMap<Integer, PC_Block> stacks = new HashMap<Integer, PC_Block>();

	static int counter = 0;

	public static void register(PC_OreInfo ore, PC_Block block) {
		info.put(counter, ore);
		stacks.put(counter++, block);
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		generateOverworld(rand, chunkX, chunkZ, world);
	}

	private void generateOverworld(Random rand, int chunkX, int chunkZ, World world) {
		generateOverworld(world, rand, chunkX * 16, chunkZ * 16);
	}

	public void generateOverworld(World world, Random rand, int blockXPos, int blockZPos) {
		for (int i = 0; i < info.size(); i++) {
			PC_OreInfo ore = info.get(i);
			PC_Block block = stacks.get(i);
			addOreSpawn(block, world, rand, blockXPos, blockZPos, 16, 16, ore.genOresDepositMaxCount(),
					block.getGenOresSpawnMetadata(rand, world, blockXPos, blockZPos),
					ore.genOresInChunk() * ore.genOresDepositMaxCount(), ore.genOresMinY(), ore.genOresMaxY());
		}
	}

	/**
	 * @param block          The block you want to generate
	 * @param world          The world (not the dimension) in which this block
	 *                       should be generated
	 * @param random         Random number to get block generation coordinates
	 * @param blockXPos      The number to have an empty space along the X
	 *                       coordinates for the generation method (uses quartz ore)
	 * @param blockZPos      The number to have an empty space along the Z
	 *                       coordinates for the generation method (uses quartz ore)
	 * @param maxX           The number that sets the maximum X coordinate to
	 *                       generate ore on the X axis per chunk
	 * @param maxZ           The number that sets the maximum Z coordinate to
	 *                       generate ore on the Z axis per chunk
	 * @param maxVeinSize    Maximum number of ore blocks in one vein
	 * @param chancesToSpawn Chance to generate blocks per chunk
	 * @param minY           The minimum Y coordinate at which ore can be generated
	 * @param maxY           The maximum Y coordinate at which ore can be generated
	 */
	public void addOreSpawn(Block block, World world, Random random, int blockXPos, int blockZPos, int maxX, int maxZ,
			int maxVeinSize, int meta, int chancesToSpawn, int minY, int maxY) {
		int maxPossY = minY + (maxY - 1);

		int diffBtwnMinMaxY = maxY - minY;
		for (int x = 0; x < chancesToSpawn; x++) {
			int posX = blockXPos + random.nextInt(maxX);
			int posY = minY + random.nextInt(diffBtwnMinMaxY);
			int posZ = blockZPos + random.nextInt(maxZ);
			(new WorldGenMinable(block, meta, maxVeinSize, Blocks.stone)).generate(world, random, posX, posY, posZ);
		}
	}
}
