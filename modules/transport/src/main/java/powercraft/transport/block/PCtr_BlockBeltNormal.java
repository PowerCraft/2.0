package powercraft.transport.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_BeltHelper;

@PC_BlockInfo(name = "normal belt", canPlacedRotated = true)
public class PCtr_BlockBeltNormal extends PCtr_BlockBeltBase {
	public PCtr_BlockBeltNormal(int id) {
		super("belt_normal");
		setBlockName("PCtr_BlockBeltNormal");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.packItems(world, pos);

			PCtr_BeltHelper.doSpecialItemAction(world, pos, (EntityItem) entity);

			if (PCtr_BeltHelper.storeNearby(world, pos, (EntityItem) entity, false)) {
				return;
			}
		}

		PC_Direction direction = getRotation(PC_Utils.getMD(world, pos));
		PC_VecI pos_leading_to = pos.offset(direction.getOffset());

		boolean leadsToNowhere = PCtr_BeltHelper.isBlocked(world, pos_leading_to);
		leadsToNowhere = leadsToNowhere && PCtr_BeltHelper.isBeyondStorageBorder(world, direction, pos, entity,
				PCtr_BeltHelper.STORAGE_BORDER_LONG);

		if (!leadsToNowhere) {
			PCtr_BeltHelper.entityPreventDespawning(world, pos, true, entity);
		}

		double speed_max = PCtr_BeltHelper.MAX_HORIZONTAL_SPEED;
		double boost = PCtr_BeltHelper.HORIZONTAL_BOOST;
		PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, !leadsToNowhere, direction, speed_max, boost);
	}
}
