package powercraft.transport.tile;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.PCtr_App;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.block.PCtr_BlockBeltRedirector;

public class PCtr_TileEntityRedirectionBelt extends PCtr_TileEntityRedirectionBeltBase {
	public PCtr_TileEntityRedirectionBelt() {
	}

	@Override
	protected PC_Direction calculateItemDirection(Entity entity) {
		PCtr_BlockBeltRedirector block = ((PCtr_BlockBeltRedirector) PCtr_App.redirectionBelt);
		PC_VecI pos = getCoord();
		int meta = PCtr_BeltHelper.getRotation(PC_Utils.getMD(worldObj, pos));
		PC_Direction redir = null;

		if (block.isPowered(worldObj, pos)) {
			switch (meta) {
			case 0:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))
						|| PC_Utils.getBID(worldObj, pos.offset(-1, 0, 0)) == Blocks.air) {
					redir = PC_Direction.LEFT;
				} else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))
						|| PC_Utils.getBID(worldObj, pos.offset(1, 0, 0)) == Blocks.air) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 1:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))
						|| PC_Utils.getBID(worldObj, pos.offset(0, 0, -1)) == Blocks.air) {
					redir = PC_Direction.LEFT;
				} else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))
						|| PC_Utils.getBID(worldObj, pos.offset(0, 0, 1)) == Blocks.air) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 2:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))
						|| PC_Utils.getBID(worldObj, pos.offset(1, 0, 0)) == Blocks.air) {
					redir = PC_Direction.LEFT;
				} else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))
						|| PC_Utils.getBID(worldObj, pos.offset(-1, 0, 0)) == Blocks.air) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 3:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))
						|| PC_Utils.getBID(worldObj, pos.offset(0, 0, 1)) == Blocks.air) {
					redir = PC_Direction.LEFT;
				} else if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))
						|| PC_Utils.getBID(worldObj, pos.offset(0, 0, -1)) == Blocks.air) {
					redir = PC_Direction.RIGHT;
				}

				break;
			}
		}

		if (redir == null) {
			switch (meta) {
			case 0:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))
						&& PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))
						&& !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 1:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))
						&& PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))
						&& !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 2:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))
						&& PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(1, 0, 0))
						&& !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))) {
					redir = PC_Direction.RIGHT;
				}

				break;

			case 3:
				if (PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, -1))
						&& PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(0, 0, 1))
						&& !PCtr_BeltHelper.isTransporterAt(worldObj, pos.offset(-1, 0, 0))) {
					redir = PC_Direction.RIGHT;
				}

				break;
			}
		}

		if (redir == null) {
			redir = PC_Direction.FRONT;
		}

		setItemDirection(entity, redir);
		return redir;
	}
}
