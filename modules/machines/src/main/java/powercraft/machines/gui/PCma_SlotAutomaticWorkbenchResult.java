package powercraft.machines.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import powercraft.api.inventory.PC_Slot;
import powercraft.machines.tile.PCma_TileEntityAutomaticWorkbench;

public class PCma_SlotAutomaticWorkbenchResult extends PC_Slot {
	private final PCma_TileEntityAutomaticWorkbench storageInv;
	private Container parent;

	public PCma_SlotAutomaticWorkbenchResult(PCma_TileEntityAutomaticWorkbench storage, IInventory result,
			Container parent, int i) {
		super(result, i);
		storageInv = storage;
		this.parent = parent;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer thePlayer, ItemStack itemstack) {
		itemstack.onCrafting(thePlayer.worldObj, thePlayer, itemstack.stackSize);

		if (itemstack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
			thePlayer.addStat(AchievementList.buildWorkBench, 1);
		} else if (itemstack.getItem() == Items.wooden_pickaxe) {
			thePlayer.addStat(AchievementList.buildPickaxe, 1);
		} else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
			thePlayer.addStat(AchievementList.buildFurnace, 1);
		} else if (itemstack.getItem() == Items.wooden_hoe) {
			thePlayer.addStat(AchievementList.buildHoe, 1);
		} else if (itemstack.getItem() == Items.bread) {
			thePlayer.addStat(AchievementList.makeBread, 1);
		} else if (itemstack.getItem() == Items.cake) {
			thePlayer.addStat(AchievementList.bakeCake, 1);
		} else if (itemstack.getItem() == Items.stone_pickaxe) {
			thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
		} else if (itemstack.getItem() == Items.wooden_sword) {
			thePlayer.addStat(AchievementList.buildSword, 1);
		} else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
			thePlayer.addStat(AchievementList.enchantments, 1);
		} else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
			thePlayer.addStat(AchievementList.bookcase, 1);
		}

		storageInv.decrementRecipe();
		parent.onCraftMatrixChanged(storageInv);
	}
}
