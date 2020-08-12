package powercraft.api.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PC_EntityItem extends EntityItem {

	public PC_EntityItem(World world, double x, double y, double z, ItemStack itemStack) {
		super(world, x, y, z, itemStack);
	}

	@Override
	public boolean combineItems(EntityItem entityItem) {
		if (ticksExisted > 25)
			return super.combineItems(entityItem);
		return false;
	}

}
