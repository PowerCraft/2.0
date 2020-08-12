package powercraft.logic.tile;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.logic.PClo_App;

public class PClo_TileEntityWire extends PC_TileEntity implements PC_ITileEntityAABB {

	private ArrayList<Integer> direction = new ArrayList<Integer>();
	private boolean isUpdated = false;
	@PC_ClientServerSync
	private int power = 0;

	@Override
	public void updateEntity() {
		ArrayList<Integer> dir = new ArrayList<Integer>();
		dir.add(-1);
		// west
		if (checkBlock(worldObj, xCoord + 1, yCoord, zCoord, 2))
			dir.add(0);
		if (checkLogical(worldObj, xCoord + 1, yCoord, zCoord, 2))
			dir.add(7);
		// east
		if (checkBlock(worldObj, xCoord - 1, yCoord, zCoord, 1))
			dir.add(1);
		if (checkLogical(worldObj, xCoord - 1, yCoord, zCoord, 1))
			dir.add(8);
		// north
		if (checkBlock(worldObj, xCoord, yCoord, zCoord + 1, 0))
			dir.add(2);
		if (checkLogical(worldObj, xCoord, yCoord, zCoord + 1, 0))
			dir.add(9);
		// south
		if (checkBlock(worldObj, xCoord, yCoord, zCoord - 1, 3))
			dir.add(3);
		if (checkLogical(worldObj, xCoord, yCoord, zCoord - 1, 3))
			dir.add(10);

		if (checkBlock(worldObj, xCoord, yCoord + 1, zCoord, 0)) {
			dir.add(4);
			dir.add(5);
		}
		if (checkBlock(worldObj, xCoord, yCoord - 1, zCoord, 0)) {
			dir.add(5);
			dir.add(6);
		}

		if (!dir.equals(direction)) {
			direction = dir;
			isUpdated = true;
		}
		// 0: NORTH 1: EAST 2: SOUTH 3: WEST
	}

	/*
	 * private int getAngle(PC_VecI pos) { int angle; if (worldObj.getBlock(pos.x,
	 * pos.y + 1, pos.z + 1) == PClo_App.wire) { angle = 2; } if
	 * (worldObj.getBlock(pos.x, pos.y + 1, pos.z - 1) == PClo_App.wire) { angle =
	 * 3; } if (worldObj.getBlock(pos.x + 1, pos.y + 1, pos.z) == PClo_App.wire) {
	 * angle = 0; } if (worldObj.getBlock(pos.x - 1, pos.y + 1, pos.z) ==
	 * PClo_App.wire) { angle = 1; }
	 * 
	 * if (worldObj.getBlock(pos.x, pos.y + 1, pos.z) == PClo_App.wire &&
	 * worldObj.getBlock(pos.x, pos.y + 1, pos.z + 1) == PClo_App.wire &&
	 * worldObj.getBlock(pos.x, pos.y + 1, pos.z - 1) == PClo_App.wire &&
	 * worldObj.getBlock(pos.x + 1, pos.y + 1, pos.z) == PClo_App.wire &&
	 * worldObj.getBlock(pos.x - 1, pos.y + 1, pos.z) == PClo_App.wire) { angle =
	 * -1; }
	 * 
	 * return 0; }
	 */

	public static boolean checkLogical(World world, int x, int y, int z, int side) {
		Block block = PC_Utils.getBID(world, x, y, z);
		if ((block == PC_BlockRegistry.getPCBlockByName("PClo_BlockFlipFlop.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockFlipFlop.On")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockDelayer.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockDelayer.On")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockGate.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockGate.On")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockSpecial.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockSpecial.On")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockRepeater.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockRepeater.On"))
				&& block.canConnectRedstone(world, x, y, z, side)) {
			return true;
		}
		return false;
	}

	public boolean check(int x, int y, int z, int side) {
		return checkBlock(worldObj, x, y, z, side) || checkLogical(worldObj, x, y, z, side);
	}

	public static boolean checkBlock(World world, int x, int y, int z, int side) {
		Block block = PC_Utils.getBID(world, x, y, z);
		if (block == PC_BlockRegistry.getPCBlockByName("PClo_BlockPulsar.Off")
				|| block == PC_BlockRegistry.getPCBlockByName("PClo_BlockPulsar.On") || block == PClo_App.wire
				|| block.canConnectRedstone(world, x, y, z, side) || block == Blocks.lit_redstone_lamp
				|| block == Blocks.redstone_lamp) {
			return true;
		}
		return false;
	}

	public int getPower() {
		return power;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public ArrayList<Integer> getDirection() {
		return direction;
	}

	public void setPower(int p) {
		power = p;
	}

	public void setUpdated(boolean u) {
		isUpdated = u;
	}

	public void setDirection(ArrayList<Integer> dir) {
		direction = dir;
	}

}
