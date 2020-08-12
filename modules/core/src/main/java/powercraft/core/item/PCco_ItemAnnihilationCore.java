package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.item.PC_Item;

public class PCco_ItemAnnihilationCore extends PC_Item {

	public PCco_ItemAnnihilationCore(int ids) {
		super("annihilationcore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

}
