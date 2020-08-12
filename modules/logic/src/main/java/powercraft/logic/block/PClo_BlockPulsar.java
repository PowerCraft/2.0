package powercraft.logic.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.annotation.PC_Shining;
import powercraft.api.annotation.PC_Shining.OFF;
import powercraft.api.annotation.PC_Shining.ON;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Property;
import powercraft.logic.tile.PClo_TileEntityPulsar;

@PC_Shining
@PC_BlockInfo(name = "Redstone Pulsar", tileEntity = PClo_TileEntityPulsar.class)
public class PClo_BlockPulsar extends PC_Block implements PC_IItemInfo {
	@ON
	public static PClo_BlockPulsar on;
	@OFF
	public static PClo_BlockPulsar off;

	public PClo_BlockPulsar(boolean on) {
		super(Material.wood, "pulsar");
		setHardness(0.8F);
		setResistance(30.0F);
		setStepSound(Block.soundTypeWood);

		if (on) {
			setCreativeTab(CreativeTabs.tabRedstone);
		}
	}

	@Override
	public void initConfig(PC_Property config) {
		super.initConfig(config);
		on.setLightLevel(config.getInt("brightness", 7) * 0.0625F);
	}

	@Override
	public boolean showInCraftingTool() {
		if (this == on)
			return true;
		return false;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		ItemStack ihold = player.getCurrentEquippedItem();

		if (ihold != null) {

			if (ihold.getItem() == Items.stick) {
				changeDelay(world, player, i, j, k, player.isSneaking() ? -1 : 1);
				return true;
			}
		}

		if (world.isRemote) {
			PC_GresRegistry.openGres("Pulsar", player, PC_Utils.<PC_TileEntity>getTE(world, i, j, k));
		}

		return true;
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		printDelay(world, i, j, k);
	}

	public static void changeDelay(World world, EntityPlayer player, int x, int y, int z, int delay) {
		PClo_TileEntityPulsar ent = (PClo_TileEntityPulsar) world.getTileEntity(x, y, z);
		ent.setTimes(delay, ent.getHold());
		ent.printDelay();
	}

	public static void printDelay(World world, int x, int y, int z) {
		PClo_TileEntityPulsar ent = (PClo_TileEntityPulsar) world.getTileEntity(x, y, z);

		if (!world.isRemote) {
			ent.printDelayTime();
		}
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (isActive(world, i, j, k) && world.isRemote) {
			world.spawnParticle("reddust", i + 0.5D, j + 1.0D, k + 0.5D, 0D, 0D, 0D);
		}
	}

	public boolean isActive(IBlockAccess iblockaccess, int x, int y, int z) {
		return PC_Utils.getBID(iblockaccess, x, y, z) == PClo_BlockPulsar.on;
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		if (isActive(iblockaccess, i, j, k)) {
			return 0xffffff;
		} else {
			return 0x777777;
		}
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this));
		return arrayList;
	}

	@Override
	public int getProvidingWeakRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return isActive(world, x, y, z) ? 15 : 0;
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		PClo_TileEntityPulsar te = PC_Utils.getTE(world, x, y, z);
		return te.isActive() ? 15 : 0;
	}

}
