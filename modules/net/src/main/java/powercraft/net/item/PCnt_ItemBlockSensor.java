package powercraft.net.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;

public class PCnt_ItemBlockSensor extends PC_ItemBlock {

	public PCnt_ItemBlockSensor(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append(super.getUnlocalizedName()).append(".")
				.append(itemstack.getItemDamage() == 0 ? "item" : itemstack.getItemDamage() == 1 ? "living" : "player")
				.toString();
	}

	@Override
	public List<ItemStack> getItemStacks(List<ItemStack> arrayList) {
		arrayList.add(new ItemStack(this, 1, 0));
		arrayList.add(new ItemStack(this, 1, 1));
		arrayList.add(new ItemStack(this, 1, 2));
		return arrayList;
	}

}
