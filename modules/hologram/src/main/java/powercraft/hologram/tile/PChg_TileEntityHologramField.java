package powercraft.hologram.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.hologram.PChg_App;

public class PChg_TileEntityHologramField extends PC_TileEntity implements PC_ITileEntityRenderer, PC_ITileEntityAABB {

	public int glList = 0;
	public int tick;
	public static List<PChg_TileEntityHologramField> mapToUpdate = new ArrayList<PChg_TileEntityHologramField>();

	@PC_ClientServerSync
	private PC_VecI offsets = new PC_VecI();

	public PC_VecI getOffset() {
		return offsets;
	}

	public void setOffset(PC_VecI coordOffset) {
		offsets = coordOffset.copy();
		notifyChanges("offsets");
		tick = 0;
	}

	@Override
	protected void dataChanged(String key, Object value) {
		if (key.equals("offsets"))
			tick = 0;
	}

	@Override
	public void updateEntity() {
		if (tick % 20 == 0) {
			if (!mapToUpdate.contains(this))
				mapToUpdate.add(this);
			tick = 0;
		}
		tick++;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord - 15, yCoord - 15, zCoord - 15, xCoord + 15,
				yCoord + 15, zCoord + 15);
		if (!PC_Utils.isBlockInBB(this.worldObj, this.getBlockType(), aabb, 1))
			PChg_App.getInstance().renderHologramField(this, x, y, z);
	}

}