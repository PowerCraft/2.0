package powercraft.api.utils;

import java.io.Serializable;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.interfaces.PC_INBT;

public class PC_Direction implements Serializable, PC_INBT<PC_Direction> {

	public static final long serialVersionUID = 1522073818686692234L;

	public static final PC_Direction BACK = new PC_Direction(0), RIGHT = new PC_Direction(1),
			LEFT = new PC_Direction(2), FRONT = new PC_Direction(3), BOTTOM = new PC_Direction(4),
			TOP = new PC_Direction(5);

	public static final int[] dir2Side = { 4, 5, 0, 3, 1, 2 };
	public static final int[] side2Dir = { 2, 4, 5, 3, 0, 1 };
	public static final int[] side2XOffset = { 0, -1, 1, 0, 0, 0 };
	public static final int[] side2YOffset = { 0, 0, 0, 0, -1, 1 };
	public static final int[] side2ZOffset = { -1, 0, 0, 1, 0, 0 };
	public static final int[] rrot = { 1, 3, 0, 2, 4, 5 };
	public static final int[] lrot = { 2, 0, 3, 1, 4, 5 };
	public static final int[] frot = { 3, 2, 1, 0, 4, 5 };
	public static final int[] mirror = { 3, 2, 1, 0, 5, 4 };
	public static final int[] playerDir2Side = { 3, 1, 0, 2 };
	public static final PC_Direction[] side2PCDir = { BACK, RIGHT, LEFT, FRONT, BOTTOM, TOP };
	public static final String[] names = { "BACK", "RIGHT", "LEFT", "FRONT", "BOTTOM", "TOP" };

	private final int mcSide;

	public PC_Direction() {
		mcSide = -1;
	}

	private PC_Direction(int dir) {
		mcSide = dir;
	}

	public int getMCSide() {
		return mcSide;
	}

	public int getMCDir() {
		return side2Dir[mcSide];
	}

	public PC_VecI getOffset() {
		return new PC_VecI(side2XOffset[mcSide], side2YOffset[mcSide], side2ZOffset[mcSide]);
	}

	public static PC_Direction getFromVec(PC_VecI vec) {
		int max = vec.x;
		PC_Direction side = PC_Direction.LEFT;
		if (Math.abs(max) < Math.abs(vec.y)) {
			max = vec.y;
			side = PC_Direction.TOP;
		}
		if (Math.abs(max) < Math.abs(vec.z)) {
			max = vec.z;
			side = PC_Direction.FRONT;
		}
		if (max < 0) {
			side = side.mirror();
		}
		return side;
	}

	public PC_Direction rotateRight() {
		return useTable(rrot);
	}

	public PC_Direction rotateLeft() {
		return useTable(lrot);
	}

	public PC_Direction rotateFull() {
		return useTable(frot);
	}

	public PC_Direction mirror() {
		return useTable(mirror);
	}

	public static PC_Direction getFromMCSide(int side) {
		return side2PCDir[side];
	}

	public static PC_Direction getFromMCDir(int dir) {
		return getFromMCSide(dir2Side[dir]);
	}

	public static PC_Direction getFromPlayerDir(int dir) {
		return getFromMCSide(playerDir2Side[dir]);
	}

	@Override
	public PC_Direction readFromNBT(NBTTagCompound nbttag) {
		return PC_Direction.getFromMCSide(nbttag.getInteger("dir"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttag) {
		nbttag.setInteger("dir", mcSide);
		return nbttag;
	}

	@Override
	public String toString() {
		return "Direction: " + getSideName();
	}

	public String getSideName() {
		return names[mcSide];
	}

	public PC_Direction useTable(int[] table) {
		return getFromMCSide(table[mcSide]);
	}

	public PC_Direction rotate(PC_Direction rotation) {
		switch (rotation.mcSide) {
		case 0:
			return rotateFull();
		case 1:
			return rotateRight();
		case 2:
			return rotateLeft();
		}
		return this;
	}

	public PC_Direction rotateRev(PC_Direction rotation) {
		switch (rotation.mcSide) {
		case 0:
			return rotateFull();
		case 1:
			return rotateLeft();
		case 2:
			return rotateRight();
		}
		return this;
	}

}
