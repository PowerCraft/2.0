package powercraft.api.recipes;

import net.minecraft.item.ItemStack;

public class PC_SmeltRecipe implements PC_IRecipe {

	private ItemStack result;
	private ItemStack input;
	private float experience;

	public PC_SmeltRecipe(ItemStack result, ItemStack input) {
		this(result, input, 0.0f);
	}

	public PC_SmeltRecipe(ItemStack result, ItemStack input, float experience) {
		this.result = result;
		this.input = input;
		this.experience = experience;
	}

	public ItemStack getResult() {
		return result.copy();
	}

	public ItemStack getInput() {
		return input.copy();
	}

	public float getExperience() {
		return experience;
	}

	@Override
	public boolean canBeCrafted() {
		return true;
	}

}
