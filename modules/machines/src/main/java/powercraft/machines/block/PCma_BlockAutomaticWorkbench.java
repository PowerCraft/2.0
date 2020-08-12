package powercraft.machines.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.machines.tile.PCma_TileEntityAutomaticWorkbench;

@PC_BlockInfo(name = "Automatic Workbench", tileEntity = PCma_TileEntityAutomaticWorkbench.class, canPlacedRotated = true)
public class PCma_BlockAutomaticWorkbench extends PC_Block implements PC_IItemInfo {
	private static final int TXTOP = 0, TXSIDE = 1, TXFRONT = 2;

	public PCma_BlockAutomaticWorkbench(int id) {
		super(Material.ground, "side", "workbench_top", "side", "workbench_front", "side", "side");
		setHardness(0.7F);
		setResistance(10.0F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
		PCma_TileEntityAutomaticWorkbench tew = (PCma_TileEntityAutomaticWorkbench) world.getTileEntity(i, j, k);

		if (tew != null) {
			PC_InventoryUtils.dropInventoryContents(tew, world, tew.getCoord());
		}

		super.breakBlock(world, i, j, k, par5, par6);
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block l) {
		if (l != Blocks.air && l.canProvidePower()) {
			boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k)
					|| world.isBlockIndirectlyGettingPowered(i, j + 1, k)
					|| world.isBlockIndirectlyGettingPowered(i, j - 1, k);

			if (flag) {
				world.scheduleBlockUpdate(i, j, k, l, tickRate(world));
			}
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (world.isBlockIndirectlyGettingPowered(i, j, k) || world.isBlockIndirectlyGettingPowered(i, j + 1, k)
				|| world.isBlockIndirectlyGettingPowered(i, j - 1, k)) {
			((PCma_TileEntityAutomaticWorkbench) PC_Utils.getTE(world, i, j, k)).doCrafting();
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {

		PC_GresRegistry.openGres("AutomaticWorkbench", entityplayer, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		return true;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

}
