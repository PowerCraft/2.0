package powercraft.api.registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercraft.api.building.PC_ISpecialHarvesting;

public class PC_BuildingRegistry {

	private static List<PC_ISpecialHarvesting> specialHarvestings = new ArrayList<PC_ISpecialHarvesting>();

	public static void register(PC_ISpecialHarvesting specialHarvesting) {
		if (!specialHarvestings.contains(specialHarvesting)) {
			specialHarvestings.add(specialHarvesting);
		}
	}

	public static PC_ISpecialHarvesting getSpecialHarvestingFor(World world, int x, int y, int z, Block block,
			int meta) {
		for (int i = 0; i < 3; i++) {
			for (PC_ISpecialHarvesting specialHarvesting : specialHarvestings) {
				if (specialHarvesting.useFor(world, x, y, z, block, meta, i)) {
					return specialHarvesting;
				}
			}
		}
		return null;
	}

}
