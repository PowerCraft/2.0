package powercraft.storage;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;

public class PCs_BigChestInventory implements IInventory {

	private ItemStack inv[] = new ItemStack[256];
	private PCs_EntityItemInBigChest entity[] = new PCs_EntityItemInBigChest[256];
	private World world;
	private PC_VecI mid;
	private PCs_TileEntityBigChest te;
	public boolean destroy;

	public PCs_BigChestInventory(PCs_TileEntityBigChest te) {
		this.te = te;
	}

	public PCs_BigChestInventory(World world, PC_VecI mid, PCs_TileEntityBigChest te) {
		this.world = world;
		this.mid = mid;
		this.te = te;
	}

	public void setPos(World world, PC_VecI mid) {
		this.world = world;
		this.mid = mid;
		for (int i = 0; i < 100; i++) {
			onSlotChange(i);
		}
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inv[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if (inv[var1] != null) {
			if (inv[var1].stackSize <= var2) {
				ItemStack itemstack = inv[var1];
				inv[var1] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = inv[var1].splitStack(var2);
			if (inv[var1].stackSize == 0) {
				inv[var1] = null;
			}
			onSlotChange(var1);
			markDirty();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return inv[var1];
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inv[var1] = var2;
		onSlotChange(var1);
	}

	@Override
	public String getInventoryName() {
		return "BigChestInventory";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	private void onSlotChange(int slot) {
		if (world != null) {
			if (world.isRemote) {
				PCs_EntityItemInBigChest e = entity[slot];
				if (inv[slot] == null) {
					if (e != null) {
						e.setDead();
						entity[slot] = null;
					}
				} else {
					if (e == null) {
						Random rand = new Random();
						makeEntity(world,
								new PC_VecF(mid).offset(rand.nextFloat() * 2 - 1, rand.nextFloat() * 2 - 1,
										rand.nextFloat() * 2 - 1),
								mid, new PC_VecF(rand.nextFloat() * 2 - 1, rand.nextFloat() * 2 - 1,
										rand.nextFloat() * 2 - 1).mul(10.0f),
								slot);
					}
				}
			} else if (!destroy) {
				if (inv[slot] == null) {
					// PC_PacketHandler.setTileEntity(te, new PC_Entry("slotChange", new
					// PC_Struct2<Integer, byte[]>(slot, null)));
				} else {
					ItemStack is = inv[slot];
					NBTTagCompound nbtTag = new NBTTagCompound();
					is.writeToNBT(nbtTag);
					// try {
					// PC_PacketHandler.setTileEntity(te, new PC_Entry("slotChange", new
					// PC_Struct2<Integer, byte[]>(slot, CompressedStreamTools.compress(nbtTag))));
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				}
			}
		}
	}

	private PCs_EntityItemInBigChest makeEntity(World world, PC_VecF pos, PC_VecI mid, PC_VecF move, int slot) {
		PCs_EntityItemInBigChest e = new PCs_EntityItemInBigChest(world, pos, mid, move, slot);
		world.spawnEntityInWorld(e);
		entity[slot] = e;
		return e;
	}

	public void collectItem(EntityItem entity) {
		if (entity.isDead)
			return;
		ItemStack is = entity.getEntityItem();
		if (PC_InventoryUtils.storeItemStackToInventoryFrom(this, is)) {
			entity.setDead();
		} else {
			entity.setEntityItemStack(is);
		}
	}

	public void interact(EntityPlayer entityPlayer, int slot) {
		if (inv[slot] == null)
			return;
		PC_InventoryUtils.storeItemStackToInventoryFrom(entityPlayer.inventory, inv[slot]);
		if (inv[slot].stackSize == 0) {
			inv[slot] = null;
		}
		onSlotChange(slot);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

}
