package powercraft.teleport;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.item.PC_Item;

public class PCtp_ItemTeleporterCore extends PC_Item {

	public PCtp_ItemTeleporterCore(int id) {
		super("teleportercore");
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}

}
