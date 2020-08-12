package powercraft.logic.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.annotation.PC_BlockInfo;
import powercraft.api.block.PC_Block;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;
import powercraft.logic.tile.PClo_TileEntitySpecial;
import powercraft.logic.tile.PClo_TileEntityWire;
import powercraft.logic.type.PClo_SpecialType;

@PC_BlockInfo(name = "Wire", tileEntity = PClo_TileEntityWire.class, canPlacedRotated = false)
public class PClo_BlockWire extends PC_Block {

	public PClo_BlockWire(int id) {
		super(Material.circuits, "wire");
		setHardness(0.35F);
		setStepSound(Block.soundTypeStone);
		disableStats();
		setResistance(30.0F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	private boolean online = true;
	private Set chunks = new HashSet();

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return null;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Object renderer) {
		PC_VecI cords = new PC_VecI(x, y, z);
		PClo_TileEntityWire te = (PClo_TileEntityWire) world.getTileEntity(x, y, z);
		ArrayList<Integer> direction = te.getDirection();// 2 Default
		float h = 0.35F;// 0.75:0.0 - top/down
		float h2 = 0.65F;// 1.0:0.25 - top/down
		float xS = 0.35F;
		float xE = 0.65F;
		float zS = 0.35F;
		float zE = 0.65F;
		if (direction.contains(-2)) {
			h = 0.75F;
			h2 = 1.0F;
		}
		te.clearAABBList();
		if (direction.contains(-3)) {// Connector
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(xS, h, zS), new PC_VecF(xE, h2, zE), cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(-1)) {// zero direction render as point
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.35F, h, 0.35F), new PC_VecF(0.65F, h2, 0.65F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(0)) {// X+
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.65F, h, 0.35F), new PC_VecF(1.0F, h2, 0.65F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(1)) {// X-
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.0F, h, 0.35F), new PC_VecF(0.35F, h2, 0.65F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(2)) {// Z+
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.35F, h, 0.65F), new PC_VecF(0.65F, h2, 1.0F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(3)) {// Z-
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.35F, h, 0.0F), new PC_VecF(0.65F, h2, 0.35F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		// Wall direction
		if (direction.contains(4)) {// y+
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(xS, 0.65F, zS), new PC_VecF(xE, 1.0F, zE), cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(5)) {// y0
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(xS, 0.35F, zS), new PC_VecF(xE, 0.65F, zE),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(6)) {// y-
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(xS, 0.0F, zS), new PC_VecF(xE, 0.35F, zE), cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}

		if (direction.contains(7)) {// X+ connector to miniBlock
			float cs = getConnectorSize(world, x + 1, y, z);
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(1.0F, h, 0.35F), new PC_VecF(1F + cs, h2, 0.65F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(8)) {// X-
			float cs = getConnectorSize(world, x - 1, y, z);
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0F - cs, h, 0.35F), new PC_VecF(0.0F, h2, 0.65F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(9)) {// Z+
			float cs = getConnectorSize(world, x, y, z + 1);
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.35F, h, 1.0F), new PC_VecF(0.65F, h2, 1F + cs),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}
		if (direction.contains(10)) {// Z-
			float cs = getConnectorSize(world, x, y, z - 1);
			PC_Utils.setBlockBoundsAndCollision(this, te, new PC_VecF(0.35F, h, 0F - cs), new PC_VecF(0.65F, h2, 0.0F),
					cords);
			PC_Renderer.renderStandardBlock(renderer, this, x, y, z);
		}

		return true;
	}

	private float getConnectorSize(IBlockAccess world, int x, int y, int z) {
		float connectorSize = 0.25F;
		PC_TileEntity teS = PC_Utils.getTE(world, x, y, z);
		if (teS != null) {
			if (teS instanceof PClo_TileEntitySpecial) {
				int type = ((PClo_TileEntitySpecial) teS).getType();
				if (type == PClo_SpecialType.CHEST_EMPTY || type == PClo_SpecialType.CHEST_FULL
						|| type == PClo_SpecialType.SPECIAL)
					connectorSize = 0.5F;
			}
		}
		return connectorSize;
	}

	public boolean renderInventoryBlock(int modelID, Object renderer) {
		PC_Utils.setBlockBounds(this, 0.35F, 0.35F, 0.0F, 0.65F, 0.65F, 1.0F);// Z
		PC_Renderer.renderInvBox(renderer, this, 0);
		return true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return PC_Renderer.getRendererID(true);
	}

	private void notifyAndUpdatePower(World world, int x, int y, int z) {
		this.powerUpdate(world, x, y, z, x, y, z);
		ArrayList arraylist = new ArrayList(this.chunks);
		this.chunks.clear();

		for (int l = 0; l < arraylist.size(); ++l) {
			ChunkPosition chunkposition = (ChunkPosition) arraylist.get(l);
			world.notifyBlocksOfNeighborChange(chunkposition.chunkPosX, chunkposition.chunkPosY,
					chunkposition.chunkPosZ, this);
		}
	}

	private void powerUpdate(World world, int x, int y, int z, int x2, int y2, int z2) {
		PClo_TileEntityWire te = PC_Utils.getTE(world, x, y, z);
		int k1 = world.getBlockMetadata(x, y, z);
		if (te != null)
			k1 = te.getPower();
		byte b0 = 0;
		int i3 = this.getPowerFor(world, x2, y2, z2, b0);
		this.online = false;
		int l1 = world.getStrongestIndirectPower(x, y, z);
		this.online = true;

		if (l1 > 0 && l1 > i3 - 1)
			i3 = l1;

		int i2 = 0;

		for (int j2 = 0; j2 < 6; ++j2) {
			int k2 = x;
			int m2 = y;
			int l2 = z;

			if (j2 == 0)
				k2 = x - 1;

			if (j2 == 1)
				++k2;

			if (j2 == 2)
				l2 = z - 1;

			if (j2 == 3)
				++l2;

			if (j2 == 4)
				m2++;

			if (j2 == 5)
				m2--;

			if (k2 != x2 || l2 != z2 || m2 != z2)
				i2 = this.getPowerFor(world, k2, m2, l2, i2);
		}

		if (i2 > i3)
			i3 = i2 - 1;
		else if (i3 > 0)
			--i3;
		else
			i3 = 0;

		if (l1 > i3 - 1)
			i3 = l1;

		if (k1 != i3) {
			if (te != null) {
				te.setPower(i3);
				PC_Utils.hugeUpdate(world, x, y, z);
				world.markBlockForUpdate(x, y, z);

			} else {
				world.setBlockMetadataWithNotify(x, y, z, i3, 2);
			}
			this.chunks.add(new ChunkPosition(x, y, z));
			this.chunks.add(new ChunkPosition(x - 1, y, z));
			this.chunks.add(new ChunkPosition(x + 1, y, z));
			this.chunks.add(new ChunkPosition(x, y - 1, z));
			this.chunks.add(new ChunkPosition(x, y + 1, z));
			this.chunks.add(new ChunkPosition(x, y, z - 1));
			this.chunks.add(new ChunkPosition(x, y, z + 1));
		}

	}

	private void notifyNeighbors(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) == this) {
			PC_Utils.hugeUpdate(world, x, y, z);
			world.notifyBlocksOfNeighborChange(x, y, z, this);
			world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
			world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
			world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
			world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
		}
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (!world.isRemote) {
			this.notifyAndUpdatePower(world, x, y, z);
			world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
			world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
			this.notifyNeighbors(world, x - 1, y, z);
			this.notifyNeighbors(world, x + 1, y, z);
			this.notifyNeighbors(world, x, y, z - 1);
			this.notifyNeighbors(world, x, y, z + 1);

			if (world.getBlock(x - 1, y, z).isNormalCube())
				this.notifyNeighbors(world, x - 1, y + 1, z);
			else
				this.notifyNeighbors(world, x - 1, y - 1, z);

			if (world.getBlock(x + 1, y, z).isNormalCube())
				this.notifyNeighbors(world, x + 1, y + 1, z);
			else
				this.notifyNeighbors(world, x + 1, y - 1, z);

			if (world.getBlock(x, y, z - 1).isNormalCube())
				this.notifyNeighbors(world, x, y + 1, z - 1);
			else
				this.notifyNeighbors(world, x, y - 1, z - 1);

			if (world.getBlock(x, y, z + 1).isNormalCube())
				this.notifyNeighbors(world, x, y + 1, z + 1);
			else
				this.notifyNeighbors(world, x, y - 1, z + 1);
		}
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (!world.isRemote) {
			PClo_TileEntityWire te = (PClo_TileEntityWire) world.getTileEntity(x, y, z);
			te.setPower(0);
			PC_Utils.hugeUpdate(world, new PC_VecI(x, y, z));
		}
		super.breakBlock(world, x, y, z, block, meta);
		if (!world.isRemote) {
			this.notifyAndUpdatePower(world, x, y, z);
			this.notifyNeighbors(world, x - 1, y, z);
			this.notifyNeighbors(world, x + 1, y, z);
			this.notifyNeighbors(world, x, y, z - 1);
			this.notifyNeighbors(world, x, y, z + 1);
			world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
			world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
			world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
			world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
			world.notifyBlocksOfNeighborChange(x, y, z - 1, this);

			if (world.getBlock(x - 1, y, z).isNormalCube())
				this.notifyNeighbors(world, x - 1, y + 1, z);
			else
				this.notifyNeighbors(world, x - 1, y - 1, z);

			if (world.getBlock(x + 1, y, z).isNormalCube())
				this.notifyNeighbors(world, x + 1, y + 1, z);
			else
				this.notifyNeighbors(world, x + 1, y - 1, z);

			if (world.getBlock(x, y, z - 1).isNormalCube())
				this.notifyNeighbors(world, x, y + 1, z - 1);
			else
				this.notifyNeighbors(world, x, y - 1, z - 1);

			if (world.getBlock(x, y, z + 1).isNormalCube())
				this.notifyNeighbors(world, x, y + 1, z + 1);
			else
				this.notifyNeighbors(world, x, y - 1, z + 1);
		}
	}

	private int getPowerFor(World world, int x, int y, int z, int side) {
		if (world.getBlock(x, y, z) != this)
			return side;
		else {
			PClo_TileEntityWire te = PC_Utils.getTE(world, x, y, z);
			int i1 = 0;
			if (te != null)
				i1 = te.getPower();
			else {
				if (!world.getBlock(x, y, z).equals(Blocks.redstone_wire))
					i1 = world.getBlockMetadata(x, y, z);
			}
			return i1 > side ? i1 : side;
		}
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			this.notifyAndUpdatePower(world, x, y, z);
			super.onNeighborBlockChange(world, x, y, z, block);
		}
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return !this.online ? 0 : this.isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		if (!this.online)
			return 0;
		else {
			int i1 = 0;
			if (PC_Utils.getTE(world, new PC_VecI(x, y, z)) != null
					&& PC_Utils.getTE(world, new PC_VecI(x, y, z)) instanceof PClo_TileEntityWire) {
				PClo_TileEntityWire te = PC_Utils.getTE(world, new PC_VecI(x, y, z));
				i1 = te.getPower();
				if ((side == 0 && world.getBlock(x, y + 1, z) != Blocks.redstone_wire && te.check(x, y + 1, z, side))
						|| (side == 1 && world.getBlock(x, y - 1, z) != Blocks.redstone_wire
								&& te.check(x, y - 1, z, side))
						|| (side == 2 && world.getBlock(x, y, z + 1) != Blocks.redstone_wire
								&& te.check(x, y, z + 1, side))
						|| (side == 3 && world.getBlock(x, y, z - 1) != Blocks.redstone_wire
								&& te.check(x, y, z - 1, side))
						|| (side == 4 && world.getBlock(x + 1, y, z) != Blocks.redstone_wire
								&& te.check(x + 1, y, z, side))
						|| (side == 5 && world.getBlock(x - 1, y, z) != Blocks.redstone_wire
								&& te.check(x - 1, y, z, side)))
					return i1;
				return 0;
			}
			return 0;
		}
	}

	public boolean canProvidePower() {
		return true;
	}

	public static boolean isPowerProvider(IBlockAccess world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.redstone_wire)
			return false;
		else if (!Blocks.unpowered_repeater.func_149907_e(block)) {
			return block.canConnectRedstone(world, x, y, z, side);
		} else {
			int i1 = world.getBlockMetadata(x, y, z);
			return side == (i1 & 3) || side == Direction.rotateOpposite[i1 & 3];
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		PClo_TileEntityWire te = PC_Utils.getTE(world, x, y, z);
		if (te != null) {
			if (te.getPower() > 0) {
				double d0 = (double) x + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
				double d1 = (double) ((float) y + 0.55F);
				double d2 = (double) z + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
				float f = (float) 1F;
				float f1 = f * 0.6F + 0.4F;

				float f2 = f * f * 0.7F - 0.5F;
				float f3 = f * f * 0.6F - 0.7F;

				if (f2 < 0.0F)
					f2 = 0.0F;

				if (f3 < 0.0F)
					f3 = 0.0F;

				world.spawnParticle("reddust", d0, d1, d2, (double) f1, f2, (double) f3);
			}
		}
	}

	public static boolean isGoodForPower(IBlockAccess world, int x, int y, int z, int side) {
		if (isPowerProvider(world, x, y, z, side))
			return true;
		else if (world.getBlock(x, y, z) == Blocks.powered_repeater) {
			int i1 = world.getBlockMetadata(x, y, z);
			return side == (i1 & 3);
		} else
			return false;
	}

}