package powercraft.transport.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;

public class PCtr_ItemBlockElevator extends PC_ItemBlock {
	public PCtr_ItemBlockElevator(Block i) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i == 0 ? 0 : 1;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + "." + (itemstack.getItemDamage() == 0 ? "up" : "down");
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this, 1, 0));
		arrayList.add(new ItemStack(this, 1, 1));
		return arrayList;
	}

}
