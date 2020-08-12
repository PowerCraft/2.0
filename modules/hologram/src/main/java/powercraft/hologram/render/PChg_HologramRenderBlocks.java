package powercraft.hologram.render;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import powercraft.api.renderer.PC_RenderBlocks;
import powercraft.api.utils.PC_Utils;

public class PChg_HologramRenderBlocks extends PC_RenderBlocks {

	public PChg_HologramRenderBlocks(IBlockAccess blockAccess) {
		super(blockAccess);
	}

	@Override
	public boolean shouldSideBeRendered(Block block, int x, int y, int z, int dir) {
		if (block != PC_Utils.getBlock(blockAccess, x, y, z)) {
			return true;
		}
		return block.shouldSideBeRendered(blockAccess, x, y, z, dir);
	}

}
