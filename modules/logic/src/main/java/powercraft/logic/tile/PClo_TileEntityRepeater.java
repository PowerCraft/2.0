package powercraft.logic.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.logic.type.PClo_RepeaterType;

public class PClo_TileEntityRepeater extends PC_TileEntity implements PC_ITileEntityAABB {

	@PC_ClientServerSync(clientChangeAble = false)
	private int type = 0;
	@PC_ClientServerSync(clientChangeAble = false)
	private int state = 0;
	@PC_ClientServerSync
	private int inp = 0;

	public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
			float hitY, float hitZ) {
		type = stack.getItemDamage();
	}

	public int getType() {
		return type;
	}

	public int getState() {
		return state;
	}

	public void setState(int b) {
		if (state != b) {
			state = b;
			notifyChanges("state");
			PC_Utils.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public int getInp() {
		return inp;
	}

	@Override
	protected void dataRecieved() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void change() {
		inp = PClo_RepeaterType.change(type, inp);
		notifyChanges("inp");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		PC_Utils.notifyBlockOfNeighborChange(worldObj, xCoord, yCoord, zCoord,
				worldObj.getBlock(xCoord, yCoord, zCoord));
		PC_Utils.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public int getPickMetadata() {
		return type;
	}

}
