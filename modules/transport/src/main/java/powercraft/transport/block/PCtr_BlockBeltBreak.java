package powercraft.transport.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_BeltHelper;

@PC_BlockInfo(name = "break belt", canPlacedRotated = true)
public class PCtr_BlockBeltBreak extends PCtr_BlockBeltBase {

	public PCtr_BlockBeltBreak(int id) {
		super("belt_break");
		setBlockName("PCtr_BlockBeltBreak");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.packItems(world, pos);
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.doSpecialItemAction(world, pos, (EntityItem) entity);

			if (PCtr_BeltHelper.storeNearby(world, pos, (EntityItem) entity, isPowered(world, pos))) {
				return;
			}
		}

		boolean halted = isPowered(world, pos);

		if (halted) {
			if (entity instanceof EntityMinecart && halted) {
				entity.motionX *= 0.2D;
				entity.motionZ *= 0.2D;
			} else {
				entity.motionX *= 0.6D;
				entity.motionZ *= 0.6D;
			}
		}

		PC_Direction direction = getRotation(world.getBlockMetadata(i, j, k));

		PC_VecI pos_leading_to = pos.offset(direction.getOffset());

		boolean leadsToNowhere = PCtr_BeltHelper.isBlocked(world, pos_leading_to);
		leadsToNowhere = leadsToNowhere && PCtr_BeltHelper.isBeyondStorageBorder(world, direction, pos, entity,
				PCtr_BeltHelper.STORAGE_BORDER_LONG);

		if (!leadsToNowhere) {
			PCtr_BeltHelper.entityPreventDespawning(world, pos, !halted, entity);
		}

		double speed_max = PCtr_BeltHelper.MAX_HORIZONTAL_SPEED * 0.6D;
		double boost = PCtr_BeltHelper.HORIZONTAL_BOOST * 0.6D;
		PCtr_BeltHelper.moveEntityOnBelt(world, pos, entity, true, !halted && !leadsToNowhere, direction, speed_max,
				boost);
	}

	private boolean isPowered(World world, PC_VecI pos) {
		return getRedstonePowereValue(world, pos.x, pos.y, pos.z) > 0;
	}

}
