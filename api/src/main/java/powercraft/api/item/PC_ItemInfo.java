package powercraft.api.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.utils.PC_Struct3;

public class PC_ItemInfo {

	public Item item = null;
	public Block block = null;
	public boolean opaqueCubeLookup = false;
	public int lightOpacity = 0;
	public boolean canBlockGrass = false;
	public int lightValue = 0;
	public boolean useNeighborBrightness = false;
	public int blockFireSpreadSpeed = 0;
	public int blockFlammability = 0;
	public List<PC_Struct3<Integer, ItemStack, Float>> furnaceRecipes;
}
