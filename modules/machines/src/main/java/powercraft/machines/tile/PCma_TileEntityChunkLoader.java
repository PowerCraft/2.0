package powercraft.machines.tile;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import powercraft.api.PC_BeamTracer;
import powercraft.api.interfaces.PC_IBeamHandler;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;

public class PCma_TileEntityChunkLoader extends PC_TileEntity implements PC_IBeamHandler {

	private int tick = 0;

	@Override
	public void updateEntity() {

		PC_VecI pos = getCoord().copy();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((i == j || -i == j) && i != 0) {
					if (PC_Utils.getBID(worldObj, pos.offset(i, 0, j)) != Blocks.glass) {
						PC_Utils.setBID(worldObj, pos, Blocks.glass, 0);
						return;
					}
				} else if (i != j) {
					if (PC_Utils.getBID(worldObj, pos.offset(i, 0, j)) != Blocks.obsidian) {
						PC_Utils.setBID(worldObj, pos, Blocks.glass, 0);
						return;
					}
				}
				if (!worldObj.canBlockSeeTheSky(pos.x + i, pos.y + 1, pos.z + j)) {
					PC_Utils.setBID(worldObj, pos, Blocks.glass, 0);
					return;
				}
			}
		}

		if (tick % 40 < 10) {
			pos.add(1, 0, 1);
		} else if (tick % 40 < 20) {
			pos.add(-1, 0, 1);
		} else if (tick % 40 < 30) {
			pos.add(-1, 0, -1);
		} else {
			pos.add(1, 0, -1);
		}

		if (worldObj.isRemote) {
			PC_Utils.spawnParticle("PC_EntityLaserFX", worldObj, pos, new PC_VecI(0, 50, 0), 0.5f,
					new PC_Color(1.0f, 0.001f, 0.2f));
			PC_Utils.spawnParticle("PC_EntityLaserFX", worldObj, getCoord(), new PC_VecI(0, 50, 0), 0.5f,
					new PC_Color(1.0f, 0.001f, 0.2f));

			Random rand = new Random();
			PC_Color color = new PC_Color(0.7f + rand.nextFloat() * 0.3f, rand.nextFloat() * 0.3f,
					0.2f + rand.nextFloat() * 0.3f);
			PC_Utils.spawnParticle("PC_EntityFanFX", worldObj, new PC_VecF(getCoord()),
					new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f),
					new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f).div(10.0f),
					0.05f + rand.nextFloat() * 0.1f, color);

			PC_Utils.spawnParticle("PC_EntityFanFX", worldObj, new PC_VecF(getCoord().offset(0, 50, 0)),
					new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f).mul(20.0f),
					new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f),
					2f + rand.nextFloat() * 1f, color);

		}

		tick++;

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_VecI coord) {
		return false;
	}

	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord) {
		return false;
	}

}
