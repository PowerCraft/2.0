package powercraft.machines.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.tileentity.PC_TileEntityWithInventory;
import powercraft.machines.PCma_ItemRanking;

public class PCma_TileEntityTransmutabox extends PC_TileEntityWithInventory {

	private int burnTime = 0;
	@PC_ClientServerSync(clientChangeAble = false)
	private int loadTime = 0;
	private boolean finished = true;
	@PC_ClientServerSync(clientChangeAble = false)
	private int needLoadTime = 0;
	@PC_ClientServerSync
	private boolean timeCritical = false;

	public PCma_TileEntityTransmutabox() {
		super("Transmutabox Inventory", 35);
	}

	public void setTimeCritical(boolean state) {
		if (timeCritical != state) {
			timeCritical = state;
			notifyChanges("timeCritical");
		}
	}

	public float getProgress() {
		if (loadTime <= 0)
			return 0.0f;
		return loadTime / (float) needLoadTime;
	}

	@Override
	public boolean canPlayerInsertStackTo(int slot, ItemStack stack) {
		if (slot >= 1 && slot < 9)
			return PC_RecipeRegistry.isFuel(stack);
		if (slot == 9 || slot == 10)
			return false;
		if (slot >= 23)
			return false;
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == 0)
			return false;
		if (slot >= 1 && slot < 9)
			return PC_RecipeRegistry.isFuel(stack);
		if (slot == 9 || slot == 10)
			return false;
		if (slot >= 23)
			return false;
		return true;
	}

	@Override
	public boolean canDispenseStackFrom(int slot) {
		return slot >= 23;
	}

	@Override
	public boolean canDropStackFrom(int slot) {
		return slot != 9 && slot != 10;
	}

	@Override
	public int getSizeInventory() {
		return 35;
	}

	@Override
	public int getSlotStackLimit(int slot) {
		if (slot == 0)
			return 1;
		return 64;
	}

	private ItemStack getItemStackForConvertation() {
		for (int i = 11; i < 23; i++) {
			ItemStack is = decrStackSize(i, 1);
			if (is != null)
				return is;
		}
		return null;
	}

	private boolean sendToOutput(ItemStack is) {
		return PC_InventoryUtils.storeItemStackToInventoryFrom(this, is, PC_InventoryUtils.makeIndexList(23, 35));
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (finished) {
				if (inventoryContents[0] != null) {

					int free = 0;
					for (int i = 23; i < 35; i++) {
						if (inventoryContents[i] == null) {
							free += Math.min(inventoryContents[0].getMaxStackSize(), getInventoryStackLimit());
						} else if (inventoryContents[i].isItemEqual(inventoryContents[0])) {
							free += Math.min(inventoryContents[i].getMaxStackSize(), getInventoryStackLimit())
									- inventoryContents[i].stackSize;
						}
					}

					if (free > 0) {

						float outRank = PCma_ItemRanking.getRank(inventoryContents[0]);
						if (outRank > 0) {
							for (int i = 11; i < 23; i++) {
								if (inventoryContents[i] != null) {
									if (inventoryContents[i].isItemEqual(inventoryContents[0])) {
										sendToOutput(inventoryContents[i]);
										inventoryContents[i] = null;
									} else {
										if (PCma_ItemRanking.getRank(inventoryContents[i]) > 0) {
											if (timeCritical) {
												inventoryContents[9] = inventoryContents[i].copy();
												inventoryContents[i] = null;
											} else {
												inventoryContents[9] = decrStackSize(i, 1);
											}
										}
										if (inventoryContents[9] != null)
											break;
									}
								}
							}

							if (inventoryContents[9] != null) {

								float inRank = PCma_ItemRanking.getRank(inventoryContents[9]);

								if (outRank <= 0 || inRank <= 0)
									return;

								inventoryContents[10] = inventoryContents[0].copy();
								inRank *= inventoryContents[9].stackSize;

								if (!timeCritical) {

									if (inRank > outRank) {
										int num = (int) (inRank / outRank);
										int maxStack = Math.min(inventoryContents[10].getMaxStackSize(),
												getInventoryStackLimit());
										maxStack = Math.min(free, maxStack);
										if (num > maxStack)
											num = maxStack;
										inventoryContents[10].stackSize = num;
										outRank *= num;
									}

								}

								needLoadTime = (int) (outRank / inRank * 20);
								notifyChanges("needLoadTime");

								finished = false;
							}
						}
					}
				}
			} else {

				burnTime += PC_InventoryUtils.useFuel(this, PC_InventoryUtils.makeIndexList(1, 9), worldObj,
						getCoord());

				if (burnTime > 0) {
					addToLoadTime(1);
					burnTime--;
				}

				if (loadTime >= needLoadTime) {
					addToLoadTime(-needLoadTime);
					finished = true;
					sendToOutput(inventoryContents[10]);
					inventoryContents[9] = null;
					inventoryContents[10] = null;
				}
			}
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public void addToLoadTime(int num) {
		loadTime += num;
		notifyChanges("loadTime");
	}

	public void addEnergy(int energy) {
		addToLoadTime(energy * 2);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		burnTime = nbttagcompound.getInteger("burnTime");
		finished = nbttagcompound.getBoolean("finished");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("burnTime", burnTime);
		nbttagcompound.setBoolean("finished", finished);
	}

}
