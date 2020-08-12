package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.item.PC_Item;

public class PCco_ItemFormationCore extends PC_Item {

	public PCco_ItemFormationCore(int ids) {
		super("formationcore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

}
