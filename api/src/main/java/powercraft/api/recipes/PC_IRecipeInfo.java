package powercraft.api.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import powercraft.api.utils.PC_VecI;

public interface PC_IRecipeInfo extends PC_IRecipe {

	public PC_VecI getSize();

	public List<ItemStack> getExpectedInputFor(int index);

	public int getPCRecipeSize();

}
