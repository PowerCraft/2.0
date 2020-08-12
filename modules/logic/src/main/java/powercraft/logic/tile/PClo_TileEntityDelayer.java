package powercraft.logic.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.block.PC_Block;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.logic.PClo_App;
import powercraft.logic.type.PClo_DelayerType;

public class PClo_TileEntityDelayer extends PC_TileEntity implements PC_IPacketHandler, PC_ITileEntityAABB {
	@PC_ClientServerSync(clientChangeAble = false)
	private int type = 0;
	@PC_ClientServerSync
	private boolean stateBuffer[] = new boolean[20];
	private int remainingTicks = 0;
	private int ticks = 20;

	public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
			float hitY, float hitZ) {
		type = stack.getItemDamage();
	}

	public int getType() {
		return type;
	}

	public boolean[] getStateBuffer() {
		return stateBuffer;
	}

	public int getDelay() {
		return getStateBuffer().length;
	}

	public void setDelay(int delay) {
		stateBuffer = new boolean[delay];
		ticks = delay;
		notifyChanges("stateBuffer");
	}

	public void resetRemainingTicks() {
		remainingTicks = ticks;
	}

	public boolean decRemainingTicks() {
		if (remainingTicks > 0) {
			remainingTicks--;
			if (remainingTicks == 0) {
				remainingTicks = 0;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateEntity() {
		boolean stop = false;
		boolean reset = false;

		Block b = PC_Utils.getBlock(worldObj, getCoord());

		if (b instanceof PC_Block && getType() == PClo_DelayerType.FIFO) {
			stop = ((PC_Block) b).getRedstonePowerValueFromInput(worldObj, xCoord, yCoord, zCoord,
					PC_Direction.RIGHT) > 0;
			reset = ((PC_Block) b).getRedstonePowerValueFromInput(worldObj, xCoord, yCoord, zCoord,
					PC_Direction.LEFT) > 0;
		}

		if (!stop || reset) {
			worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, PC_Utils.getBID(worldObj, xCoord, yCoord, zCoord),
					PClo_App.delayer.tickRate(worldObj));
			PC_Utils.getBID(worldObj, getCoord()).onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, blockType);
		}
	}

	@Override
	public int getPickMetadata() {
		return type;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		setDelay((Integer) o[2]);
		return false;
	}

}
