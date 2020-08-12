package powercraft.logic.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;

public class PClo_ItemPulsarCore extends PC_Item {

	public PClo_ItemPulsarCore(int id) {
		super("pulsarcore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return true;
	}

}
