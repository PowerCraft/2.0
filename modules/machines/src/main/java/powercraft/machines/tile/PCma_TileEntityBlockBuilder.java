package powercraft.machines.tile;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.entity.PC_FakePlayer;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.tileentity.PC_TileEntityWithInventory;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCma_TileEntityBlockBuilder extends PC_TileEntityWithInventory {

	private static Random rand = new Random();
	private PC_FakePlayer fakeplayer;

	public PCma_TileEntityBlockBuilder() {
		super("Block Builder", 9);
	}

	/**
	 * @return forge - can update; false
	 */
	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		fakeplayer = new PC_FakePlayer(world);
	}

	/**
	 * Use some random item from the dispenser inventory.
	 */

	public boolean useItem(PC_VecI coord) {
		if (!worldObj.isRemote) {
			int i = -1;
			int j = 1;
			for (int k = 0; k < inventoryContents.length; k++) {
				if (inventoryContents[k] != null && rand.nextInt(j++) == 0) {
					i = k;
				}
			}
			if (i >= 0) {
				int state = try2useItem(getStackInSlot(i).copy(), coord);
				if (state == -1) {
					return true;
				}
				if (state == 0) {
					return false;
				}
				if (state != 0) {
					if (PC_SoundRegistry.isSoundEnabled()) {
						worldObj.playAuxSFX(1000, xCoord, yCoord, zCoord, 0);
					}
				}
				if (!worldObj.isRemote) {
					if (state == 1) {
						decrStackSize(i, 1);
					}
					if (state == 2) {
						getStackInSlot(i).damageItem(1, fakeplayer);
						if (getStackInSlot(i) != null && getStackInSlot(i).stackSize <= 0) {
							setInventorySlotContents(i, null);
						}
					}

					syncInventory(1, null, 0);
				}
				return true;
			} else {
				if (PC_SoundRegistry.isSoundEnabled()) {
					worldObj.playAuxSFX(1001, xCoord, yCoord, zCoord, 0);
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Use item, return state flag
	 * 
	 * @param itemstack stack to use
	 * @param dist      distance form device
	 * @return 0 = nothing happened to the stack. 1 = decrement stack size, 2 =
	 *         damage item
	 */
	private int try2useItem(ItemStack itemstack, PC_VecI front) {
		if (!worldObj.isRemote) {
			int l = PC_Utils.getMD(worldObj, front) & 7;

			PC_VecI below = new PC_VecI(front.offset(0, -1, 0));
			PC_VecI above = new PC_VecI(front.offset(0, 1, 0));

			Block blockFront = PC_Utils.getBID(worldObj, front);
			int metaFront = PC_Utils.getMD(worldObj, front);

			Block blockBelow = PC_Utils.getBID(worldObj, below);
			int metaBelow = PC_Utils.getMD(worldObj, below);

			Block blockAbove = PC_Utils.getBID(worldObj, above);
			int metaAbove = PC_Utils.getMD(worldObj, above);

			Block block = blockFront;

			// try to put minecart
			if (itemstack.getItem() instanceof ItemMinecart) {

				if (PC_BlockRegistry.isBlock(worldObj, front, "PCtr_BlockBelt") || block instanceof BlockRail) {
					worldObj.spawnEntityInWorld(EntityMinecart.createMinecart(worldObj, front.x + 0.5F, front.y + 0.5F,
							front.z + 0.5F, ((ItemMinecart) itemstack.getItem()).minecartType));
					return 1;
				}
			}

			// ending block
			if (block == Blocks.obsidian || block == Blocks.bedrock || block == Blocks.stonebrick
			/** TODO || (PC_MSGRegistry.hasFlag(worldObj, front, PC_Utils.HARVEST_STOP)) */
			) {
				return -1;
			}

			// try to place front
			if (itemstack.getItem() instanceof ItemBlock) {

				/**
				 * TODO if(PC_MSGRegistry.hasFlag(itemstack, PC_Utils.NO_BUILD)){ return 0; }
				 */

				ItemBlock item = ((ItemBlock) itemstack.getItem());

				if (Block.getBlockFromItem(item).canPlaceBlockAt(worldObj, front.x, front.y, front.z)) {
					PC_Utils.setBID(worldObj, front.x, front.y, front.z, Block.getBlockFromItem(item),
							item.getMetadata(itemstack.getItemDamage()));
					return 1;
				}

				/*
				 * if (isEmptyBlock(idFront) &&
				 * Block.blocksList[item.itemID].canPlaceBlockAt(worldObj, x + incX * 2, y, z +
				 * incZ * 2)) { PC_Utils.setBID(worldObj, x + incX * 2, y, z + incZ * 2,
				 * item.itemID, item.getMetadata(itemstack.getItemDamage())); return 1; }
				 */

				return 0;
			}

			// use on front block (usually bonemeal on crops)
			if (!PC_Utils.isBlockReplaceable(worldObj, front) && !(itemstack.getItem() instanceof ItemReed)) {
				int dmgOrig = itemstack.getItemDamage();
				int sizeOrig = itemstack.stackSize;

				if (itemstack.getItem().onItemUse(itemstack, fakeplayer, worldObj, front.x, front.y, front.z, 1, 0.0f,
						0.0f, 0.0f)) {

					if (itemstack.getItem() instanceof ItemMonsterPlacer) {
						return 1;
					}

					Block blockFrontNew = PC_Utils.getBID(worldObj, front);
					int metaFrontNew = PC_Utils.getMD(worldObj, front);
					Block blockAboveNew = PC_Utils.getBID(worldObj, above);
					int metaAboveNew = PC_Utils.getMD(worldObj, above);

					int dmgNew = itemstack.getItemDamage();
					int sizeNew = itemstack.stackSize;

					// if not bonemeal, or if target block was changed
					if (!(itemstack.getItem() == Items.dye && itemstack.getItemDamage() == 15)
							|| (blockFront != blockFrontNew || metaFront != metaFrontNew || blockAbove != blockAboveNew
									|| metaAbove != metaAboveNew)) {
						if (dmgOrig != dmgNew) {
							return 2;
						}
						if (sizeOrig != sizeNew) {
							return 1;
						}
					}
				}
			}

			// use below
			if (PC_Utils.isBlockReplaceable(worldObj, front) && !PC_Utils.isBlockReplaceable(worldObj, below)) {
				int dmg1 = itemstack.getItemDamage();
				int size1 = itemstack.stackSize;

				if (itemstack.getItem().onItemUse(itemstack, fakeplayer, worldObj, below.x, below.y, below.z, 1, 0.0f,
						0.0f, 0.0f)) {

					if (itemstack.getItem() instanceof ItemMonsterPlacer) {
						return 1;
					}

					Block blockBelowNew = PC_Utils.getBID(worldObj, below);
					int metaBelowNew = PC_Utils.getMD(worldObj, below);
					Block blockFrontNew = PC_Utils.getBID(worldObj, front);
					int metaFrontNew = PC_Utils.getMD(worldObj, front);

					int dmg2 = itemstack.getItemDamage();
					int size2 = itemstack.stackSize;
					// if not bonemeal, or if target block was changed
					if (!(itemstack.getItem() == Items.dye && itemstack.getItemDamage() == 15)
							|| (blockBelow != blockBelowNew || metaBelow != metaBelowNew || blockFront != blockFrontNew
									|| metaFront != metaFrontNew)) {
						if (dmg1 != dmg2) {
							return 2;
						}
						if (size1 != size2) {
							return 1;
						}
					}
				}
			}
		}
		return 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		PC_InventoryUtils.loadInventoryFromNBT(nbtTagCompound, "Items", this);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		PC_InventoryUtils.saveInventoryToNBT(nbtTagCompound, "Items", this);
	}

}
