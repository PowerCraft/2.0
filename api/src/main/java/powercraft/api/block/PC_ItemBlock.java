package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercraft.api.item.PC_IItemInfo;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.loader.PC_ModuleObject;

public class PC_ItemBlock extends ItemBlock implements PC_IItemInfo {

	private static final PC_Direction[] sides = { PC_Direction.BOTTOM, PC_Direction.TOP, PC_Direction.FRONT,
			PC_Direction.BACK, PC_Direction.LEFT, PC_Direction.RIGHT };
	private PC_ModuleObject module;

	public PC_ItemBlock(Block block) {
		super(block);
	}

	public void setModule(PC_ModuleObject module) {
		this.module = module;
	}

	@Override
	public PC_ModuleObject getModule() {
		return module;
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> itemStacks) {
		itemStacks.add(new ItemStack(this));
		return itemStacks;
	}

	@Override
	public boolean showInCraftingTool() {
		return true;
	}

	@Override
	public void getSubItems(Item index, CreativeTabs creativeTab, List list) {
		list.addAll(getItemStacks(new ArrayList<ItemStack>()));
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int dir,
			float xHit, float yHit, float zHit) {
		Block block = PC_Utils.getBID(world, x, y, z);
		int metadata = PC_Utils.getMD(world, x, y, z);
		PC_Direction pcDir = PC_Direction.getFromMCDir(dir);

		if (block == Blocks.snow && (metadata & 7) < 1) {
			dir = 1;
		} else if (!PC_Utils.isBlockReplaceable(world, x, y, z)) {

			PC_VecI offset = null;

			if (block instanceof PC_Block) {
				offset = ((PC_Block) block).moveBlockTryToPlaceAt(world, x, y, z, pcDir, xHit, yHit, zHit, itemStack,
						entityPlayer);
			}

			if (offset == null) {
				offset = pcDir.getOffset();
			}

			x += offset.x;
			y += offset.y;
			z += offset.z;

		}

		if (itemStack.stackSize == 0) {
			return false;
		}

		PC_VecI move;
		do {

			move = null;
			block = PC_Utils.getBID(world, x, y, z);
			metadata = PC_Utils.getMD(world, x, y, z);

			if (block != Blocks.air) {
				if (!((block == Blocks.snow && (metadata & 7) < 1) || PC_Utils.isBlockReplaceable(world, x, y, z))) {
					if (block instanceof PC_Block) {
						move = ((PC_Block) block).moveBlockTryToPlaceAt(world, x, y, z, pcDir, xHit, yHit, zHit,
								itemStack, entityPlayer);
					}
					if (move == null) {
						return false;
					}
					x += move.x;
					y += move.y;
					z += move.z;
					continue;
				}

			}

			for (int i = 0; i < 6; i++) {
				PC_VecI offset = sides[i].getOffset();
				Block b = PC_Utils.getBlock(world, x + offset.x, y + offset.y, z + offset.z);
				if (b instanceof PC_Block) {
					PC_Block pcBlock = (PC_Block) b;
					move = pcBlock.moveBlockTryToPlaceOnSide(world, x, y, z, sides[i].mirror(), xHit, yHit, zHit,
							pcBlock, itemStack, entityPlayer);
					if (move != null)
						break;
				}
			}
			if (move != null) {
				x += move.x;
				y += move.y;
				z += move.z;
			}
		} while (move != null);

		if (!entityPlayer.canPlayerEdit(x, y, z, dir, itemStack)) {
			return false;
		} else if (y == 255 && block.getMaterial().isSolid()) {
			return false;
		} else if (world.canPlaceEntityOnSide(block, x, y, z, false, dir, entityPlayer, itemStack)) {
			metadata = itemStack.getItemDamage();
			block = Block.getBlockFromItem(itemStack.getItem());
			metadata = block.onBlockPlaced(world, x, y, z, dir, xHit, yHit, zHit, metadata);
			if (block instanceof PC_Block) {
				metadata = ((PC_Block) block).makeBlockMetadata(itemStack, entityPlayer, world, x, y, z, dir, xHit,
						yHit, zHit, metadata);
			}
			if (y < 255 && y > 0) {

				if (placeBlockAt(itemStack, entityPlayer, world, x, y, z, dir, xHit, yHit, zHit, metadata)) {
					--itemStack.stackSize;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir,
			float hitX, float hitY, float hitZ, int metadata) {
		Block block = Block.getBlockFromItem(stack.getItem());

		if (!PC_Utils.setBID(world, x, y, z, block, metadata)) {
			return false;
		}

		if (PC_Utils.getBID(world, x, y, z) == block) {
			block.onBlockPlacedBy(world, x, y, z, player, stack);
			TileEntity te = PC_Utils.getTE(world, x, y, z);
			if (te instanceof PC_TileEntity) {
				((PC_TileEntity) te).create(stack, player, world, x, y, z, dir, hitX, hitY, hitZ);
			}
			PC_Utils.setMD(world, x, y, z, metadata);
		}

		return true;
	}

	public void doCrafting(ItemStack itemStack, InventoryCrafting inventoryCrafting) {

	}

	public int getBurnTime(ItemStack fuel) {
		return 0;
	}

}
