package powercraft.api.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.tileentity.PC_TileEntityWithInventory;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PC_InventoryUtils {

	public static void loadInventoryFromNBT(NBTTagCompound outerTag, String invTagName, IInventory inventory) {
		NBTTagList list = outerTag.getTagList("Items", 10);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int j = tag.getByte("Slot") & 255;
			if (j >= 0 && j < inventory.getSizeInventory()) {
				inventory.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(tag));
			}
		}
	}

	public static void saveInventoryToNBT(NBTTagCompound outerTag, String invTagName, IInventory inventory) {
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("Slot", (byte) i);
			if (inventory.getStackInSlot(i) == null)
				new ItemStack(Blocks.air).writeToNBT(tag);
			else
				inventory.getStackInSlot(i).writeToNBT(tag);
			list.appendTag(tag);
		}
		outerTag.setTag("Items", list);
	}

	public static IInventory getBlockInventoryAt(World world, PC_VecI pos) {
		return getBlockInventoryAt(world, pos.x, pos.y, pos.z);
	}

	public static IInventory getBlockInventoryAt(IBlockAccess world, int x, int y, int z) {
		TileEntity te = PC_Utils.getTE(world, x, y, z);

		if (te == null) {
			return null;
		}

		if (te instanceof PC_IInventoryWrapper) {
			return ((PC_IInventoryWrapper) te).getInventory();
		}

		if (!(te instanceof IInventory)) {
			return null;
		}

		IInventory inv = (IInventory) te;
		Block id = PC_Utils.getBID(world, x, y, z);

		if (id == Blocks.chest) {
			if (PC_Utils.getBID(world, x - 1, y, z) == Blocks.chest) {
				inv = new InventoryLargeChest("Large chest", (IInventory) PC_Utils.getTE(world, x - 1, y, z), inv);
			}

			if (PC_Utils.getBID(world, x + 1, y, z) == Blocks.chest) {
				inv = new InventoryLargeChest("Large chest", inv, (IInventory) PC_Utils.getTE(world, x + 1, y, z));
			}

			if (PC_Utils.getBID(world, x, y, z - 1) == Blocks.chest) {
				inv = new InventoryLargeChest("Large chest", (IInventory) PC_Utils.getTE(world, x, y, z - 1), inv);
			}

			if (PC_Utils.getBID(world, x, y, z + 1) == Blocks.chest) {
				inv = new InventoryLargeChest("Large chest", inv, (IInventory) PC_Utils.getTE(world, x, y, z + 1));
			}
		}

		return inv;
	}

	public static IInventory getEntityInventoryAt(World world, PC_VecI pos) {
		return getEntityInventoryAt(world, pos.x, pos.y, pos.z);
	}

	public static IInventory getEntityInventoryAt(World world, int x, int y, int z) {
		List<IInventory> list = world.getEntitiesWithinAABB(IInventory.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list.size() >= 1) {
			return list.get(0);
		}

		List<PC_IInventoryWrapper> list2 = world.getEntitiesWithinAABB(PC_IInventoryWrapper.class,
				AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(0.6D, 0.6D, 0.6D));

		if (list2.size() >= 1) {
			return list2.get(0).getInventory();
		}
		return null;
	}

	public static IInventory getInventoryAt(World world, PC_VecI pos) {
		return getInventoryAt(world, pos.x, pos.y, pos.z);
	}

	public static IInventory getInventoryAt(World world, int x, int y, int z) {
		IInventory invAt = getBlockInventoryAt(world, x, y, z);

		if (invAt != null) {
			return invAt;
		}

		return getEntityInventoryAt(world, x, y, z);
	}

	public static int[] getInvIndexesForSide(IInventory inv, PC_Direction side) {
		if (side == null)
			return null;
		int sideID = side.getMCDir();
		if (inv instanceof ISidedInventory && sideID >= 0) {
			return ((ISidedInventory) inv).getAccessibleSlotsFromSide(sideID);
		}
		return null;
	}

	public static int getSlotStackLimit(IInventory inv, int i) {
		if (inv instanceof PC_IInventory) {
			return ((PC_IInventory) inv).getSlotStackLimit(i);
		}
		return inv.getInventoryStackLimit();
	}

	public static int[] makeIndexList(int start, int end) {
		int[] indexes = new int[end - start];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i + start;
		}
		return indexes;
	}

	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack) {
		return getFirstEmptySlot(inv, itemstack, (int[]) null);
	}

	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, PC_Direction side) {
		return getFirstEmptySlot(inv, itemstack, getInvIndexesForSide(inv, side));
	}

	public static int getFirstEmptySlot(IInventory inv, ItemStack itemstack, int[] indexes) {
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				if (inv.getStackInSlot(i) == null) {
					if (inv.isItemValidForSlot(i, itemstack))
						return i;
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				if (inv.getStackInSlot(i) == null) {
					if (inv.isItemValidForSlot(i, itemstack))
						return i;
				}
			}
		}
		return -1;
	}

	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack) {
		return getSlotWithPlaceFor(inv, itemstack, (int[]) null);
	}

	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, PC_Direction side) {
		return getSlotWithPlaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
	}

	public static int getSlotWithPlaceFor(IInventory inv, ItemStack itemstack, int[] indexes) {
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if (slot.isItemEqual(itemstack) && slot.getMaxStackSize() > slot.stackSize
							&& getSlotStackLimit(inv, i) > slot.stackSize) {
						if (inv.isItemValidForSlot(i, itemstack))
							return i;
					}
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if (slot.isItemEqual(itemstack) && slot.getMaxStackSize() > slot.stackSize
							&& getSlotStackLimit(inv, i) > slot.stackSize) {
						if (inv.isItemValidForSlot(i, itemstack))
							return i;
					}
				}
			}
		}
		return getFirstEmptySlot(inv, itemstack, indexes);
	}

	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack) {
		return storeItemStackToInventoryFrom(inv, itemstack, (int[]) null);
	}

	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, PC_Direction side) {
		return storeItemStackToInventoryFrom(inv, itemstack, getInvIndexesForSide(inv, side));
	}

	public static boolean storeItemStackToInventoryFrom(IInventory inv, ItemStack itemstack, int[] indexes) {
		while (itemstack.stackSize > 0) {
			int slot = getSlotWithPlaceFor(inv, itemstack, indexes);
			if (slot < 0)
				break;
			storeItemStackToSlot(inv, itemstack, slot);
		}
		return itemstack.stackSize == 0;
	}

	public static boolean storeItemStackToSlot(IInventory inv, ItemStack itemstack, int i) {
		ItemStack slot = inv.getStackInSlot(i);
		if (slot == null) {
			int store = getSlotStackLimit(inv, i);
			if (store > itemstack.getMaxStackSize()) {
				store = itemstack.getMaxStackSize();
			}
			if (store > itemstack.stackSize) {
				store = itemstack.stackSize;
			}
			slot = itemstack.copy();
			slot.stackSize = store;
			itemstack.stackSize -= store;
		} else {
			if (slot.isItemEqual(itemstack)) {
				int store = getSlotStackLimit(inv, i);
				if (store > slot.getMaxStackSize()) {
					store = slot.getMaxStackSize();
				}
				store -= slot.stackSize;
				if (store > 0) {
					if (store > itemstack.stackSize) {
						store = itemstack.stackSize;
					}
					itemstack.stackSize -= store;
					slot.stackSize += store;
				}
			}
		}
		inv.setInventorySlotContents(i, slot);
		return itemstack.stackSize == 0;
	}

	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack) {
		return getInventorySpaceFor(inv, itemstack, (int[]) null);
	}

	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, PC_Direction side) {
		return getInventorySpaceFor(inv, itemstack, getInvIndexesForSide(inv, side));
	}

	public static int getInventorySpaceFor(IInventory inv, ItemStack itemstack, int[] indexes) {
		int space = 0;
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				int slotStackLimit = getSlotStackLimit(inv, i);
				if (itemstack == null) {
					if (slot == null) {
						space += slotStackLimit;
					}
				} else {
					if (slotStackLimit > itemstack.getMaxStackSize()) {
						slotStackLimit = itemstack.getMaxStackSize();
					}
					if (slot != null) {
						if (slot.isItemEqual(itemstack) && slotStackLimit > slot.stackSize) {
							if (inv.isItemValidForSlot(i, itemstack)) {
								space += slotStackLimit - slot.stackSize;
							}
						}
					} else {
						space += slotStackLimit;
					}
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				int slotStackLimit = getSlotStackLimit(inv, i);
				if (itemstack == null) {
					if (slot == null) {
						space += slotStackLimit;
					}
				} else {
					if (slotStackLimit > itemstack.getMaxStackSize()) {
						slotStackLimit = itemstack.getMaxStackSize();
					}
					if (slot != null) {
						if (slot.isItemEqual(itemstack) && slotStackLimit > slot.stackSize) {
							if (inv.isItemValidForSlot(i, itemstack)) {
								space += slotStackLimit - slot.stackSize;
							}
						}
					} else {
						space += slotStackLimit;
					}
				}
			}
		}
		return space;
	}

	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack) {
		return getInventoryCountOf(inv, itemstack, (int[]) null);
	}

	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, PC_Direction side) {
		return getInventoryCountOf(inv, itemstack, getInvIndexesForSide(inv, side));
	}

	public static int getInventoryCountOf(IInventory inv, ItemStack itemstack, int[] indexes) {
		int count = 0;
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if (itemstack == null) {
						count += slot.stackSize;
					} else {
						if (slot.isItemEqual(itemstack)) {
							count += slot.stackSize;
						}
					}
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					if (itemstack == null) {
						count += slot.stackSize;
					} else {
						if (slot.isItemEqual(itemstack)) {
							count += slot.stackSize;
						}
					}
				}
			}
		}
		return count;
	}

	public static int getInventoryFreeSlots(IInventory inv) {
		return getInventoryFreeSlots(inv, (int[]) null);
	}

	public static int getInventoryFreeSlots(IInventory inv, PC_Direction side) {
		return getInventoryFreeSlots(inv, getInvIndexesForSide(inv, side));
	}

	public static int getInventoryFreeSlots(IInventory inv, int[] indexes) {
		int freeSlots = 0;
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot == null) {
					freeSlots++;
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot == null) {
					freeSlots++;
				}
			}
		}
		return freeSlots;
	}

	public static int getInventoryFullSlots(IInventory inv) {
		return getInventoryFullSlots(inv, (int[]) null);
	}

	public static int getInventoryFullSlots(IInventory inv, PC_Direction side) {
		return getInventoryFullSlots(inv, getInvIndexesForSide(inv, side));
	}

	public static int getInventoryFullSlots(IInventory inv, int[] indexes) {
		int fullSlots = 0;
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					fullSlots++;
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack slot = inv.getStackInSlot(i);
				if (slot != null) {
					fullSlots++;
				}
			}
		}
		return fullSlots;
	}

	public static void moveStacks(IInventory from, IInventory to) {
		moveStacks(from, (int[]) null, to, (int[]) null);
	}

	public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, PC_Direction toSide) {
		moveStacks(from, getInvIndexesForSide(from, fromSide), to, toSide);
	}

	public static void moveStacks(IInventory from, int[] indexes, IInventory to, PC_Direction toSide) {
		moveStacks(from, indexes, to, getInvIndexesForSide(to, toSide));
	}

	public static void moveStacks(IInventory from, PC_Direction fromSide, IInventory to, int[] indexes) {
		moveStacks(from, getInvIndexesForSide(from, fromSide), to, indexes);
	}

	public static void moveStacks(IInventory from, int[] fromIndexes, IInventory to, int[] toIndexes) {
		if (fromIndexes == null) {
			int size = from.getSizeInventory();
			for (int i = 0; i < size; i++) {
				if (from.getStackInSlot(i) != null) {

					storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

					if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
						from.setInventorySlotContents(i, null);
					}
				}
			}
		} else {
			for (int j = 0; j < fromIndexes.length; j++) {
				int i = fromIndexes[j];
				if (from.getStackInSlot(i) != null) {

					storeItemStackToInventoryFrom(to, from.getStackInSlot(i), toIndexes);

					if (from.getStackInSlot(i) != null && from.getStackInSlot(i).stackSize <= 0) {
						from.setInventorySlotContents(i, null);
					}
				}
			}
		}
	}

	public static ItemStack[] groupStacks(ItemStack[] input) {
		List<ItemStack> list = stacksToList(input);
		groupStacks(list);
		return stacksToArray(list);
	}

	public static void groupStacks(List<ItemStack> input) {
		if (input == null) {
			return;
		}

		for (ItemStack st1 : input) {
			if (st1 != null) {
				for (ItemStack st2 : input) {
					if (st2 != null && st2.isItemEqual(st1)) {
						int movedToFirst = Math.min(st2.stackSize, st1.getItem().getItemStackLimit() - st1.stackSize);

						if (movedToFirst <= 0) {
							break;
						}

						st1.stackSize += movedToFirst;
						st2.stackSize -= movedToFirst;
					}
				}
			}
		}

		ArrayList<ItemStack> copy = new ArrayList<ItemStack>(input);

		for (int i = copy.size() - 1; i >= 0; i--) {
			if (copy.get(i) == null || copy.get(i).stackSize <= 0) {
				input.remove(i);
			}
		}
	}

	public static List<ItemStack> stacksToList(ItemStack[] stacks) {
		ArrayList<ItemStack> myList = new ArrayList<ItemStack>();
		Collections.addAll(myList, stacks);
		return myList;
	}

	public static ItemStack[] stacksToArray(List<ItemStack> stacks) {
		return stacks.toArray(new ItemStack[stacks.size()]);
	}

	public static void dropInventoryContents(IInventory inventory, World world, PC_VecI pos) {
		Random random = new Random();
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (inventory instanceof PC_IInventory) {
					if (!((PC_IInventory) inventory).canDropStackFrom(i))
						continue;
				}
				ItemStack itemstack = inventory.getStackInSlot(i);
				inventory.setInventorySlotContents(i, null);
				if (!world.isRemote && itemstack != null)
					PC_Utils.dropItemStack(world, pos, itemstack);
			}
		}
	}

	public static int useFuel(IInventory inv, World world, PC_VecI pos) {
		return useFuel(inv, (int[]) null, world, pos);
	}

	public static int useFuel(IInventory inv, PC_Direction side, World world, PC_VecI pos) {
		return useFuel(inv, getInvIndexesForSide(inv, side), world, pos);
	}

	public static int useFuel(IInventory inv, int[] indexes, World world, PC_VecI pos) {
		if (indexes == null) {
			int size = inv.getSizeInventory();
			for (int i = 0; i < size; i++) {
				ItemStack is = inv.getStackInSlot(i);
				int fuel = PC_RecipeRegistry.getFuelValue(is);
				if (fuel > 0) {
					inv.decrStackSize(i, 1);
					ItemStack container = PC_Utils.getContainerItemStack(is);
					if (container != null) {
						storeItemStackToInventoryFrom(inv, container, indexes);
						if (container.stackSize > 0) {
							PC_Utils.dropItemStack(world, pos, container);
						}
					}
					return fuel;
				}
			}
		} else {
			for (int j = 0; j < indexes.length; j++) {
				int i = indexes[j];
				ItemStack is = inv.getStackInSlot(i);
				int fuel = PC_RecipeRegistry.getFuelValue(is);
				if (fuel > 0) {
					inv.decrStackSize(i, 1);
					ItemStack container = PC_Utils.getContainerItemStack(is);
					if (container != null) {
						storeItemStackToInventoryFrom(inv, container, indexes);
						if (container.stackSize > 0) {
							PC_Utils.dropItemStack(world, pos, container);
						}
					}
					return fuel;
				}
			}
		}
		return 0;
	}

	/// --------------------------
	public static boolean itemStacksEqual(ItemStack one, ItemStack two) {
		if (one == null && two == null)
			return true;
		if (one == null || two == null)
			return false;
		if (!(one.getItem() == two.getItem()))
			return false;
		if (one.getItemDamage() == Short.MAX_VALUE || two.getItemDamage() == Short.MAX_VALUE)
			return true;
		if (!(one.getItemDamage() == two.getItemDamage()))
			return false;
		return ItemStack.areItemStackTagsEqual(one, two);
	}

}
