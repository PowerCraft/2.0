package powercraft.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import powercraft.api.registry.PC_ItemRegistry;

public class PC_CreativeTab extends CreativeTabs {

	public PC_CreativeTab() {
		super("Power Craft");
	}

	@Override
	public String getTranslatedTabLabel() {
		return "Power Craft";
	}

	@Override
	public Item getTabIconItem() {
		return PC_ItemRegistry.getPCItemByName("PCco_ItemActivator");
	}

}
