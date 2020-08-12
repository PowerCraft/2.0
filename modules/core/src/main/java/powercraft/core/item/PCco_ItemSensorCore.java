package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.item.PC_Item;

public class PCco_ItemSensorCore extends PC_Item {

	public PCco_ItemSensorCore(int ids) {
		super("sensorcore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

}
