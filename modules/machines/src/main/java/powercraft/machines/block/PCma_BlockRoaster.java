package powercraft.machines.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.machines.tile.PCma_TileEntityRoaster;

@PC_BlockInfo(name = "Roaster", tileEntity = PCma_TileEntityRoaster.class)
public class PCma_BlockRoaster extends PC_Block implements PC_IItemInfo {
	private static final int TXDOWN = 2, TXTOP = 1, TXSIDE = 0;

	public PCma_BlockRoaster(int id) {
		super(Material.ground, "roaster_down", "roaster_top", "roaster_side");
		setStepSound(Block.soundTypeMetal);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int tickRate(World world) {
		return 4;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		if (world.isRemote) {
			return true;
		}
		PC_GresRegistry.openGres("Roaster", entityplayer, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		return true;
	}

	public static boolean isIndirectlyPowered(IBlockAccess iBlockAccess, int x, int y, int z) {
		PCma_TileEntityRoaster te = PC_Utils.getTE(iBlockAccess, x, y, z);
		if (te == null)
			return false;
		World world = te.getWorldObj();
		if (world.isRemote) {
			return te.isActive();
		}

		boolean on = false;

		/**
		 * TODO if (PC_Utils.isPoweredDirectly(world, x, y, z)) { on= true; }
		 */

		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			on = true;
		}

		/**
		 * TODO if (PC_Utils.isPoweredDirectly(world, x, y-1, z)) { on= true; }
		 */

		if (world.isBlockIndirectlyGettingPowered(x, y - 1, z)) {
			on = true;
		}

		if (on != te.isActive()) {
			te.setActive(on);
		}
		return on;
	}

	private static boolean hasFuel(IBlockAccess world, int x, int y, int z) {
		try {
			return PC_Utils.<PCma_TileEntityRoaster>getTE(world, x, y, z).getBurnTime() > 0;
		} catch (RuntimeException re) {
			return false;
		}
	}

	private boolean isNethering(World world, int x, int y, int z) {
		try {
			return ((PCma_TileEntityRoaster) world.getTileEntity(x, y, z)).getNetherTime() > 0
					&& isIndirectlyPowered(world, x, y, z);
		} catch (RuntimeException re) {
			return false;
		}
	}

	// removed static
	public boolean isBurning(IBlockAccess world, int x, int y, int z) {
		return isIndirectlyPowered(world, x, y, z) && hasFuel(world, x, y, z);
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (isBurning(world, i, j, k)) {
			if (random.nextInt(24) == 0) {
				world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, "fire.fire", 1.0F + random.nextFloat(),
						random.nextFloat() * 0.7F + 0.3F);
			}

			for (int c = 0; c < 5; c++) {
				float y = j + 0.74F + (random.nextFloat() * 0.3F);
				float x = i + 0.2F + (random.nextFloat() * 0.6F);
				float z = k + 0.2F + (random.nextFloat() * 0.6F);
				world.spawnParticle("smoke", x, y, z, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", x, y, z, 0.0D, 0.0D, 0.0D);
			}

			for (int c = 0; c < 5; c++) {
				float y = j + 1.3F;
				float x = i + 0.2F + (random.nextFloat() * 0.6F);
				float z = k + 0.2F + (random.nextFloat() * 0.6F);
				world.spawnParticle("smoke", x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}

		if (isNethering(world, i, j, k)) {
			for (int c = 0; c < 8; c++) {
				float y = j + 0.74F + (random.nextFloat() * 0.3F);
				float x = i + 0.2F + (random.nextFloat() * 0.6F);
				float z = k + 0.2F + (random.nextFloat() * 0.6F);
				world.spawnParticle("reddust", x, y, z, 0.0D, 0.0D, 0.0D);
			}

			for (int c = 0; c < 20; c++) {
				float y = (float) j + -2 + (random.nextFloat() * 4F);
				float x = (float) i + -6 + (random.nextFloat() * 12F);
				float z = (float) k + -6 + (random.nextFloat() * 12F);
				world.spawnParticle("reddust", x, y, z, 0.6D, 0.001D, 0.001D);
			}
		}
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

}
