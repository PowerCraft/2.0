package powercraft.api;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import cpw.mods.fml.common.IFuelHandler;

public class PC_FuelHandler implements IFuelHandler {

	@Override
	public int getBurnTime(ItemStack fuel) {
		Item item = fuel.getItem();

		if (item instanceof PC_Item) {
			return ((PC_Item) item).getBurnTime(fuel);
		} else if (item instanceof PC_ItemArmor) {
			return ((PC_ItemArmor) item).getBurnTime(fuel);
		} else if (item instanceof PC_ItemBlock) {
			return ((PC_ItemBlock) item).getBurnTime(fuel);
		}

		return 0;
	}

}
