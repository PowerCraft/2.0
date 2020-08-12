package powercraft.transport.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.tile.PCtr_TileEntityRedirectionBelt;
import powercraft.transport.tile.PCtr_TileEntityRedirectionBeltBase;

@PC_BlockInfo(name = "redirector belt", tileEntity = PCtr_TileEntityRedirectionBelt.class, canPlacedRotated = true)
public class PCtr_BlockBeltRedirector extends PCtr_BlockBeltBase {
	public PCtr_BlockBeltRedirector(int id) {
		super("belt_redirector");
		setBlockName("PCtr_BlockBeltRedirector");
	}

	public boolean isPowered(World world, PC_VecI pos) {
		return getRedstonePowereValue(world, pos.x, pos.y, pos.z) > 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.doSpecialItemAction(world, pos, (EntityItem) entity);

			if (PCtr_BeltHelper.storeNearby(world, pos, (EntityItem) entity, false)) {
				return;
			}
		}

		PCtr_TileEntityRedirectionBeltBase teRedir = (PCtr_TileEntityRedirectionBeltBase) world.getTileEntity(i, j, k);
		PC_Direction redir = teRedir.getDirection(entity);
		PC_Direction direction = getRotation(PC_Utils.getMD(world, pos));
		direction = direction.rotate(redir);

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
