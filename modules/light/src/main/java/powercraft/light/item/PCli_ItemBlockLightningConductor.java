package powercraft.light.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;

public class PCli_ItemBlockLightningConductor extends PC_ItemBlock {
	Block block;

	public PCli_ItemBlockLightningConductor(Block block) {
		super(block);
		this.block = block;
		setMaxDamage(0);
		setHasSubtypes(false);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		if (PC_Utils.getBID(world, x, y + 1, z) != Blocks.air) {
			return false;
		}

		if (!PC_Utils.setBID(world, x, y, z, block, 0)) {
			return false;
		}

		if (!PC_Utils.setBID(world, x, y + 1, z, block, 1)) {
			return false;
		}

		if (world.getBlock(x, y, z) == block) {
			Block block = this.block;
			block.onBlockPlacedBy(world, x, y, z, player, stack);
			world.removeTileEntity(x, y, z);
		}

		if (world.getBlock(x, y + 1, z) == block) {
			Block block = this.block;
			block.onBlockPlacedBy(world, x, y + 1, z, player, stack);
			PC_TileEntity te = PC_Utils.getTE(world, x, y + 1, z);

			if (te != null) {
				te.create(stack, player, world, x, y + 1, z, side, hitX, hitY, hitZ);
			}
		}

		return true;
	}
}
