package powercraft.transport.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import powercraft.api.entity.PC_EntityItem;
import powercraft.api.utils.PC_Direction;
import powercraft.transport.PCtr_BeltHelper;

public class PCtr_TileEntitySplitter extends PCtr_TileEntitySeparationBeltBase {

	public PCtr_TileEntitySplitter() {
		separatorContents = new ItemStack[30];
	}

	private PC_Direction getDir(int dir) {
		return PC_Direction.getFromMCDir(dir);
	}

	public PC_Direction calculateItemDirection(Entity entity) {
		boolean notItem = !(entity instanceof EntityItem);

		ItemStack itemstack = PCtr_BeltHelper.getItemStackForEntity(entity);

		if (itemstack == null) {
			return PC_Direction.TOP;
		}

		int count[] = { 0, 0, 0, 0, 0, 0 };

		for (int s = 0; s < 6; s++) {
			for (int i = 0; i < 5; i++) {
				ItemStack stack = getStackInSlot(s * 5 + i);
				if (stack != null && stack.isItemEqual(itemstack)) {
					count[s] += stack.stackSize;
				}
			}
		}

		int dir = -1;
		for (int s = 0; s < 6; s++) {
			if (count[s] > 0) {
				if (dir == -1)
					dir = s;
				else
					break;
			}
			if (s == 5) {
				if (dir == -1)
					dir = 0;
				return getDir(dir);
			}
		}

		int all = 0;
		for (int s = 0; s < 6; s++) {
			all += count[s];
		}

		if (all == 0) {
			return PC_Direction.TOP;
		}

		float clip = 0;

		for (int s = 0; s < 6; s++) {
			float f = (float) count[s] / (float) all * (float) itemstack.stackSize + clip;
			clip = f - (int) f;
			count[s] = (int) f;
		}

		if (notItem) {
			int bestDir = -1;
			int max = count[0];
			for (int s = 1; s < 6; s++) {
				if (max < count[s]) {
					max = count[s];
					bestDir = s;
				}
			}
			PC_Direction pcdir = getDir(bestDir);
			return pcdir;
		}

		if (!worldObj.isRemote) {
			for (int s = 0; s < 6; s++) {
				if (count[s] > 0) {
					ItemStack stack = itemstack.copy();
					stack.stackSize = count[s];
					EntityItem entityitem = new PC_EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, stack);
					entityitem.motionX = entity.motionX;
					entityitem.motionY = entity.motionY;
					entityitem.motionZ = entity.motionZ;
					worldObj.spawnEntityInWorld(entityitem);
				}
			}
		}
		entity.setDead();

		return PC_Direction.TOP;
	}

}
