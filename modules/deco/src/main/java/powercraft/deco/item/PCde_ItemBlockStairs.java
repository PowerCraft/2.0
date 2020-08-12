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
import powercraft.deco.tile.PCde_TileEntityStairs;

public class PCde_ItemBlockStairs extends PC_ItemBlock {

	public PCde_ItemBlockStairs(Block block) {
		super(block);
		setMaxDamage(0);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l,
			float par8, float par9, float par10) {
		Block id = world.getBlock(i, j, k);

		if (id == Blocks.snow) {
			l = 1;
		} else if (id != Blocks.vine && id != Blocks.glass_pane && id != Blocks.deadbush) {
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
						j += 1;
					}

				} else if (PC_KeyRegistry.isPlacingReversed(entityplayer)) {
					j--;
				}

			} else if (bID == PCde_App.platform) {
				if (PC_KeyRegistry.isPlacingReversed(entityplayer)) {
					j--;
				}
			}

			j--;
		}

		if (j == 255 && bID.getMaterial().isSolid()) {
			return false;
		}

		if (world.canPlaceEntityOnSide(PCde_App.stairs, i, j, k, false, l, entityplayer, itemstack)) {
			Block block = PCde_App.stairs;
			if (PC_Utils.setBID(world, i, j, k, block, 0)) {
				// set tile entity
				PCde_TileEntityStairs ted = (PCde_TileEntityStairs) world.getTileEntity(i, j, k);
				if (ted == null) {
					ted = (PCde_TileEntityStairs) ((BlockContainer) block).createNewTileEntity(world, 0);
				}
				world.setTileEntity(i, j, k, ted);
				// block.onBlockPlaced(world, i, j, k, l);
				block.onBlockPlacedBy(world, i, j, k, entityplayer, itemstack);

				world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
				world.markBlockForUpdate(i, j, k);

				// world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F,
				// block.setStepSound(p_149672_1_), (block.stepSound.getVolume() + 1.0F) / 2.0F,
				// block.stepSound.getPitch() * 0.8F);

				itemstack.stackSize--;
			}
		}
		return true;
	}

}
