package powercraft.deco.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.utils.PC_Utils;
import powercraft.deco.PCde_App;
import powercraft.deco.tile.PCde_TileEntityPlatform;

public class PCde_ItemBlockPlatform extends PC_ItemBlock {

	public PCde_ItemBlockPlatform(Block block) {
		super(block);
		setMaxDamage(0);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l,
			float par8, float par9, float par10) {
		Block block = world.getBlock(i, j, k);

		if (block == Blocks.snow) {
			l = 1;
		} else if (block != Blocks.vine && block != Blocks.glass_pane && block != Blocks.deadbush) {
			if (l == 0) {
				j--;
			}

			if (l == 1) {
				j++;
			}

			if (l == 2) {
				k--;
			}

			if (l == 3) {
				k++;
			}

			if (l == 4) {
				i--;
			}

			if (l == 5) {
				i++;
			}
		}

		if (itemstack.stackSize == 0) {
			return false;
		} else if (!entityplayer.canPlayerEdit(i, j, k, l, itemstack)) {
			return false;
		}

		// special placing rules for Ledge

		Block bID = PC_Utils.getBID(world, i, j - 1, k);

		if (bID == PCde_App.stairs || bID == PCde_App.platform) {

			int dir = ((MathHelper.floor_double(((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3) + 2) % 4;

			int meta = world.getBlockMetadata(i, j - 1, k);

			i -= Direction.offsetX[dir];
			k -= Direction.offsetZ[dir];

			if (bID == PCde_App.stairs) {

				if (meta == dir) {

					if (!PC_KeyRegistry.isPlacingReversed(entityplayer)) {
						j++;
					}

				}

			}

			j--;

		}

		if (j == 255 && bID.getMaterial().isSolid()) {
			return false;
		}

		if (world.canPlaceEntityOnSide(PCde_App.platform, i, j, k, false, l, entityplayer, itemstack)) {
			Block block2 = PCde_App.platform;
			if (PC_Utils.setBID(world, i, j, k, block2, 0)) {
				// set tile entity
				PCde_TileEntityPlatform ted = (PCde_TileEntityPlatform) world.getTileEntity(i, j, k);
				if (ted == null) {
					ted = (PCde_TileEntityPlatform) ((BlockContainer) block2).createNewTileEntity(world, 0);
				}
				world.setTileEntity(i, j, k, ted);
				block.onBlockPlacedBy(world, i, j, k, entityplayer, itemstack);

				world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
				world.markBlockForUpdate(i, j, k);

				world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

				itemstack.stackSize--;
			}
		}
		return true;
	}

}
