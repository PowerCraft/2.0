package powercraft.transport.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_BeltHelper;

@PC_BlockInfo(name = "belt detector", canPlacedRotated = true)
public class PCtr_BlockBeltDetector extends PCtr_BlockBeltBase {

	public PCtr_BlockBeltDetector(int id) {
		super("belt_detector");
		setBlockName("PCtr_BlockBeltDetector");
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction dir) {
		return isActive(world, x, y, z) ? 15 : 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (isActive(world, i, j, k)) {
			setStateIfEntityInteractsWithDetector(world, i, j, k);
		}
	}

	private boolean isActive(IBlockAccess world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		return PCtr_BeltHelper.isActive(meta);
	}

	private void setStateIfEntityInteractsWithDetector(World world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		boolean isAlreadyActive = PCtr_BeltHelper.isActive(meta);
		boolean isPressed = false;
		List list = world.getEntitiesWithinAABBExcludingEntity(null,
				AxisAlignedBB.getBoundingBox(i, j, k, (i + 1), j + 1D, (k + 1)));
		isPressed = list.size() > 0;

		if (isPressed != isAlreadyActive && !world.isRemote) {
			PC_Utils.setMD(world, i, j, k, meta ^ 8);
			PC_Utils.hugeUpdate(world, i, j, k);
			PC_SoundRegistry.playSound(i + 0.5D, j + 0.125D, k + 0.5D, "random.click", 0.15F, 0.5F);
		}

		Block block = PC_Utils.getBID(world, i, j, k);
		if (isPressed) {
			world.scheduleBlockUpdate(i, j, k, block, tickRate(world));
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		PC_VecI pos = new PC_VecI(i, j, k);

		if (PCtr_BeltHelper.isEntityIgnored(entity)) {
			return;
		}

		if (!isActive(world, i, j, k)) {
			setStateIfEntityInteractsWithDetector(world, i, j, k);
		}

		if (entity instanceof EntityItem) {
			PCtr_BeltHelper.packItems(world, pos);
		}

		PC_Direction direction = getRotation(world.getBlockMetadata(i, j, k));

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
