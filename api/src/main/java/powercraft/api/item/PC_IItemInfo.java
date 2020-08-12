package powercraft.api.item;

import java.util.List;

import net.minecraft.item.ItemStack;
import powercraft.launcher.loader.PC_ModuleObject;

public interface PC_IItemInfo {

	public PC_ModuleObject getModule();

	public List<ItemStack> getItemStacks(List<ItemStack> arrayList);

	public boolean showInCraftingTool();
}
