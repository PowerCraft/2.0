package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;

public class PCco_ItemPowerDust extends PC_Item {

	public PCco_ItemPowerDust(int id) {
		super("powerdust");
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return 3600;
	}

}
