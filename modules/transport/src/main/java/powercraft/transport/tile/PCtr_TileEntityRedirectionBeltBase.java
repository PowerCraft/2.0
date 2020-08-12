package powercraft.transport.tile;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import net.minecraft.entity.Entity;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Struct2;

public abstract class PCtr_TileEntityRedirectionBeltBase extends PC_TileEntity {
	protected Random rand = new Random();

	public PCtr_TileEntityRedirectionBeltBase() {
	}

	@Override
	public final boolean canUpdate() {
		return true;
	}

	private Hashtable<Integer, PC_Direction> redirList = new Hashtable<Integer, PC_Direction>();

	@Override
	public final void updateEntity() {
		Enumeration<Integer> enumer = redirList.keys();

		while (enumer.hasMoreElements()) {
			int id = enumer.nextElement();
			Entity thisItem = worldObj.getEntityByID(id);

			if (thisItem == null) {
				redirList.remove(id);
			} else {
				if (thisItem.posX < xCoord - 0.2F || thisItem.posY < yCoord - 0.2F || thisItem.posZ < zCoord - 0.2F
						|| thisItem.posX > xCoord + 1.2F || thisItem.posY > yCoord + 2.2F
						|| thisItem.posZ > zCoord + 1.2F) {
					redirList.remove(id);
				}
			}
		}
	}

	public final PC_Direction getDirection(Entity entity) {
		if (redirList.containsKey(entity.getEntityId())) {
			return redirList.get(entity.getEntityId());
		} else {
			return calculateItemDirection(entity);
		}
	}

	protected abstract PC_Direction calculateItemDirection(Entity entity);

	protected final void setItemDirection(Entity entity, PC_Direction direction) {
		setItemDirection(entity.getEntityId(), direction);
	}

	protected final void setItemDirection(int entityID, PC_Direction direction) {
		redirList.put(entityID, direction);
		if (!worldObj.isRemote)
			call("newID", new PC_Struct2<Integer, PC_Direction>(entityID, direction));
	}

}
