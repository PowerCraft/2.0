package powercraft.machines.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.registry.PC_ChunkForcerRegistry;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.tile.PCma_TileEntityChunkLoader;

@PC_BlockInfo(name = "Chunk Loader", tileEntity = PCma_TileEntityChunkLoader.class)
public class PCma_BlockChunkLoader extends PC_Block implements PC_I3DRecipeHandler {

	public PCma_BlockChunkLoader(int id) {
		super(Material.glass);
	}

	@Override
	public boolean showInCraftingTool() {
		return false;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean foundStructAt(EntityPlayer entityplayer, World world, PC_Struct2<PC_VecI, Integer> structStart) {
		PC_VecI mid = structStart.a.offset(1, 0, 1);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int h = 0;
				if (i != j && -i != j) {
					h = 1;
				}
				if (!world.canBlockSeeTheSky(mid.x + i, mid.y + h, mid.z + j))
					return false;
			}
		}
		// if(!PC_Utils.isPlayerOPOrOwner(entityplayer)){
		// return false;
		// }
		PC_Utils.setBID(world, mid.offset(0, 1, 1), Blocks.air, 0);
		PC_Utils.setBID(world, mid.offset(0, 1, -1), Blocks.air, 0);
		PC_Utils.setBID(world, mid.offset(1, 1, 0), Blocks.air, 0);
		PC_Utils.setBID(world, mid.offset(-1, 1, 0), Blocks.air, 0);
		PC_Utils.setBID(world, mid, this, 0);
		PC_ChunkForcerRegistry.forceChunkUpdate(world, mid, 1);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		PC_ChunkForcerRegistry.stopForceChunkUpdate(world, new PC_VecI(x, y, z));
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public IIcon getIcon(PC_Direction par1, int par2) {
		return Blocks.glass.getIcon(par1.getMCDir(), par2);
	}

	@Override
	public boolean canBeCrafted() {
		return true;
	}

}
