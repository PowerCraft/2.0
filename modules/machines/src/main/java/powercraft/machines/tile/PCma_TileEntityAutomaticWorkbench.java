package powercraft.machines.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncInv;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCma_TileEntityAutomaticWorkbench extends PC_TileEntity implements PC_IInventory, PC_IPacketHandler {

	private ItemStack actContents[] = new ItemStack[18];

	private static class ContainerFake extends Container {
		public ContainerFake() {
		}

		@Override
		public boolean canInteractWith(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void onCraftMatrixChanged(IInventory iinventory) {
		}
	}

	private static Container fakeContainer = new ContainerFake();

	private boolean isOrderingEnabled = true;

	@PC_ClientServerSync
	public boolean redstoneActivated;

	public boolean isRedstoneActivated() {
		return redstoneActivated;
	}

	public void setRedstoneActivated(boolean state) {
		if (redstoneActivated != state) {
			redstoneActivated = state;
			notifyChanges("redstoneActivated");
		}
	}

	private InventoryCrafting getStorageAsCraftingGrid(Container container) {
		if (container == null) {
			container = fakeContainer;
		}

		InventoryCrafting craftGrid = new InventoryCrafting(container, 3, 3);

		for (int n = 0; n < 9; n++) {
			craftGrid.setInventorySlotContents(n, getStackInSlot(n));
		}

		return craftGrid;
	}

	private InventoryCrafting getRecipeAsCraftingGrid(Container container) {
		if (container == null) {
			container = fakeContainer;
		}

		InventoryCrafting craftGrid = new InventoryCrafting(container, 3, 3);

		for (int n = 9; n < 18; n++) {
			craftGrid.setInventorySlotContents(n - 9, getStackInSlot(n));
		}

		return craftGrid;
	}

	private boolean areProductsMatching() {
		ItemStack recipe = getRecipeProduct();
		ItemStack storage = getStorageProduct();

		if (recipe == null || storage == null) {
			return false;
		}

		return ItemStack.areItemStacksEqual(storage, recipe);
	}

	public ItemStack getStorageProduct() {
		ItemStack product = CraftingManager.getInstance().findMatchingRecipe(getStorageAsCraftingGrid(null), worldObj);

		if (product != null) {
			return product.copy();
		}

		return null;
	}

	public ItemStack getRecipeProduct() {
		ItemStack product = CraftingManager.getInstance().findMatchingRecipe(getRecipeAsCraftingGrid(null), worldObj);

		if (product != null) {
			return product.copy();
		}

		return null;
	}

	@Override
	public void markDirty() {
	}

	public void reorderACT() {
		if (worldObj.isRemote)
			return;
		List<ItemStack> stacks = new ArrayList<ItemStack>();

		for (int i = 0; i < 9; i++) {
			ItemStack stack = getStackInSlot(i);
			setInventorySlotContents(i, null);

			if (stack == null) {
				continue;
			}

			for (int j = i + 1; j < 9; j++) {
				ItemStack stack2 = getStackInSlot(j);

				if (stack2 == null) {
					continue;
				}

				if (stack == stack2 && (!stack.getHasSubtypes() || stack.getItemDamage() == stack2.getItemDamage())) {
					stack.stackSize += stack2.stackSize;
					setInventorySlotContents(j, null);
				}
			}

			stacks.add(stack);
		}

		for (ItemStack stack : stacks) {
			insertStackIntoInventory_do(stack);

			if (stack.stackSize > 0) {
				int itemX = xCoord;
				int itemY = yCoord;
				int itemZ = zCoord;
				int orientation = getBlockMetadata();

				switch (orientation) {
				case 0:
					itemZ++;
					break;

				case 1:
					itemX--;
					break;

				case 2:
					itemZ--;
					break;

				case 3:
					itemX++;
					break;
				}

				while (stack.stackSize > 0) {
					int batchSize = Math.min(stack.stackSize, stack.getMaxStackSize());
					ItemStack batch = stack.splitStack(batchSize);
					EntityItem drop = new EntityItem(worldObj, itemX + 0.5D, itemY + 0.5D, itemZ + 0.5D, batch);
					drop.motionX = 0.0D;
					drop.motionY = 0.0D;
					drop.motionZ = 0.0D;
					worldObj.spawnEntityInWorld(drop);
				}
			}
		}

		markDirty();
	}

	private boolean insertStackIntoInventory_do(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		boolean[] matching = new boolean[9];

		for (int i = 0; i < 9; i++) {
			if (getStackInSlot(i + 9) == null) {
				continue;
			}

			ItemStack storageStack = getStackInSlot(i);
			matching[i] = (stack.isItemEqual(getStackInSlot(i + 9))
					&& (storageStack == null || storageStack.stackSize <= storageStack.getMaxStackSize()));
		}

		boolean end = false;
		boolean storedSomething = false;

		while (!end) {
			storedSomething = false;

			for (int i = 0; i < 9; i++) {
				if (!matching[i]) {
					continue;
				}

				ItemStack storageStack = getStackInSlot(i);
				matching[i] = (storageStack == null || storageStack.stackSize < storageStack.getMaxStackSize());

				if (matching[i]) {
					if (storageStack == null) {
						setInventorySlotContents(i, stack.splitStack(1));
					} else {
						storageStack.stackSize++;
						stack.stackSize--;
					}

					storedSomething = true;
				}

				if (stack.stackSize <= 0) {
					end = true;
				}

				if (end) {
					break;
				}
			}

			if (end || !storedSomething) {
				break;
			}
		}

		return storedSomething;
	}

	public void orderAndCraft() {
		if (worldObj == null || worldObj.isRemote)
			return;
		if (isOrderingEnabled) {
			isOrderingEnabled = false;
			reorderACT();

			if (!isRedstoneActivated()) {
				doCrafting();
			}

			reorderACT();
			isOrderingEnabled = true;
		}
	}

	public void doCrafting() {
		ItemStack currentStack = null;
		boolean needsSound = false;
		boolean forceEject = false;

		while (areProductsMatching()) {
			if (currentStack == null) {
				currentStack = getStorageProduct();
				decrementStorage();
			} else {
				if (currentStack.stackSize + getStorageProduct().stackSize >= currentStack.getMaxStackSize()) {
					forceEject = true;
				} else {
					currentStack.stackSize += getStorageProduct().stackSize;
					decrementStorage();
				}
			}

			if (currentStack != null) {
				if ((forceEject && currentStack.stackSize > 0)
						|| currentStack.stackSize >= currentStack.getMaxStackSize() || isRedstoneActivated()) {
					dispenseItem(currentStack);
					currentStack = null;
					needsSound = true;

					if (isRedstoneActivated()) {
						makeSound();
						return;
					}
				}
			}
		}

		if (currentStack != null) {
			dispenseItem(currentStack);
			needsSound = true;
		}

		if (needsSound) {
			makeSound();
		}
	}

	public void decrementStorage() {
		for (int i = 0; i < 9; i++) {
			if (actContents[i] != null) {
				actContents[i].stackSize--;

				if (actContents[i].getItem().hasContainerItem()) {
					ItemStack con = PC_Utils.getContainerItemStack(actContents[i]);
					if (con.isItemStackDamageable() && con.getItemDamage() > con.getMaxDamage()) {
						con = null;
					}
					if (con != null) {
						if (actContents[i].getItem().doesContainerItemLeaveCraftingGrid(actContents[i])) {
							dispenseItem(con);
						} else {
							if (actContents[i].stackSize <= 0) {
								actContents[i] = con;
							} else {
								dispenseItem(con);
							}
						}
					}
				}

				if (actContents[i].stackSize <= 0) {
					actContents[i] = null;
				}
			}
		}
	}

	public void decrementRecipe() {
		for (int i = 9; i < 18; i++) {
			if (actContents[i] != null) {
				actContents[i].stackSize--;

				if (actContents[i].getItem().hasContainerItem()) {
					ItemStack con = PC_Utils.getContainerItemStack(actContents[i]);
					if (con.isItemStackDamageable() && con.getItemDamage() > con.getMaxDamage()) {
						con = null;
					}
					if (con != null) {
						if (actContents[i].getItem().doesContainerItemLeaveCraftingGrid(actContents[i])) {
							dispenseItem(con);
						} else {
							if (actContents[i].stackSize <= 0) {
								actContents[i] = con;
							} else {
								dispenseItem(con);
							}
						}
					}
				}

				if (actContents[i].stackSize <= 0) {
					actContents[i] = null;
				}
			}
		}
	}

	private boolean dispenseItem(ItemStack stack2drop) {
		if (stack2drop == null || stack2drop.stackSize <= 0) {
			return false;
		}
		if (worldObj.isRemote)
			return true;
		PC_VecI offset = ((PC_Block) getBlockType()).getRotation(PC_Utils.getMD(worldObj, xCoord, yCoord, zCoord))
				.getOffset();

		double d = xCoord + offset.x * 1.0D + 0.5D;
		double d1 = yCoord + 0.5D;
		double d2 = zCoord + offset.z * 1.0D + 0.5D;
		double d3 = worldObj.rand.nextDouble() * 0.02000000000000001D + 0.05000000000000001D;
		EntityItem entityitem = new EntityItem(worldObj, d, d1 - 0.29999999999999999D, d2, stack2drop.copy());
		entityitem.motionX = offset.x * d3;
		entityitem.motionY = 0.05000000298023221D;
		entityitem.motionZ = offset.z * d3;

		if (!worldObj.isRemote) {
			worldObj.spawnEntityInWorld(entityitem);
		}

		return true;
	}

	private void makeSound() {
		if (PC_SoundRegistry.isSoundEnabled()) {
			worldObj.playAuxSFX(1000, xCoord, yCoord, zCoord, 0);
		}
	}

	@Override
	public int getSizeInventory() {
		return 18;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return actContents[i];
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
		reorderACT();
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (actContents[i] != null) {
			if (actContents[i].stackSize <= j) {
				ItemStack itemstack = actContents[i];
				actContents[i] = null;
				markDirty();
				return itemstack;
			}

			ItemStack itemstack1 = actContents[i].splitStack(j);

			if (actContents[i].stackSize == 0) {
				actContents[i] = null;
			}

			markDirty();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		actContents[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		orderAndCraft();
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return "Automatic Workbench";
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		// this.redstoneActivated = nbttagcompound.getBoolean("redstoneActivated");
		PC_InventoryUtils.loadInventoryFromNBT(nbttagcompound, "Items", this);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		// nbttagcompound.setBoolean("redstoneActivated", this.redstoneActivated);
		PC_InventoryUtils.saveInventoryToNBT(nbttagcompound, "Items", this);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}

		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (actContents[par1] != null) {
			ItemStack itemstack = actContents[par1];
			actContents[par1] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public boolean canPlayerInsertStackTo(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canDispenseStackFrom(int slot) {
		return slot < 9;
	}

	@Override
	protected void onCall(String key, Object[] value) {
		if (key.equals("orderAndCraft")) {
			orderAndCraft();
		}
	}

	@Override
	public boolean canDropStackFrom(int slot) {
		return true;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canPlayerTakeStack(int slotIndex, EntityPlayer entityPlayer) {
		return true;
	}

	/*
	 * @Override public boolean isInvNameLocalized() { return false; }
	 * 
	 * @Override public boolean isStackValidForSlot(int i, ItemStack itemstack) {
	 * return true; }
	 */

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return PC_InventoryUtils.makeIndexList(0, 9);
	}

	/*
	 * @Override public boolean canInsertItem(int i, ItemStack itemstack, int j) {
	 * return isStackValidForSlot(i, itemstack); }
	 */

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return canDispenseStackFrom(i);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return true;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		setRedstoneActivated((Boolean) o[2]);
		return true;
	}

	@Override
	public void syncInventory(int side, EntityPlayer player, int slot) {
		if (side == 0) {
			if (worldObj.isRemote) {
				PC_PacketHandler
						.sendToServer(new PC_PacketSyncInv(this, new Object[] { new PC_VecI(xCoord, yCoord, zCoord) }));
				PC_PacketHandler.sendToServer(new PC_PacketSyncPlayerInv(player.inventory));
			}
		}
	}

}
