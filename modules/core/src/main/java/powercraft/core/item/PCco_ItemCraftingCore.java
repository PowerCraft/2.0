package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.item.PC_Item;

public class PCco_ItemCraftingCore extends PC_Item {

	public PCco_ItemCraftingCore(int ids) {
		super("craftingcore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

}
