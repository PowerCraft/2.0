package powercraft.deco.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;

public class PCde_ItemBlockChimney extends PC_ItemBlock {

	/**
	 * @param block - net.minecraft.block.Block
	 */
	public PCde_ItemBlockChimney(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + ".type" + itemstack.getItemDamage();
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this, 1, 0));
		arrayList.add(new ItemStack(this, 1, 1));
		arrayList.add(new ItemStack(this, 1, 2));
		arrayList.add(new ItemStack(this, 1, 3));
		arrayList.add(new ItemStack(this, 1, 4));
		arrayList.add(new ItemStack(this, 1, 5));
		arrayList.add(new ItemStack(this, 1, 6));
		arrayList.add(new ItemStack(this, 1, 7));
		arrayList.add(new ItemStack(this, 1, 8));
		arrayList.add(new ItemStack(this, 1, 9));
		arrayList.add(new ItemStack(this, 1, 10));
		arrayList.add(new ItemStack(this, 1, 11));
		arrayList.add(new ItemStack(this, 1, 12));
		arrayList.add(new ItemStack(this, 1, 13));
		arrayList.add(new ItemStack(this, 1, 14));
		arrayList.add(new ItemStack(this, 1, 15));

		return arrayList;
	}

}
