package powercraft.api.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class PC_BBRenderer {

	@SubscribeEvent
	public void renderBBox(DrawBlockHighlightEvent e) {// TODO: may be unstable
		if (e.target.typeOfHit == MovingObjectType.BLOCK) {
			World world = e.player.getEntityWorld();
			if (world.getTileEntity(e.target.blockX, e.target.blockY, e.target.blockZ) == null) {
				return;
			}
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			float f1 = 0.002F;
			double d0 = e.player.lastTickPosX + (e.player.posX - e.player.lastTickPosX) * (double) e.partialTicks;
			double d1 = e.player.lastTickPosY + (e.player.posY - e.player.lastTickPosY) * (double) e.partialTicks;
			double d2 = e.player.lastTickPosZ + (e.player.posZ - e.player.lastTickPosZ) * (double) e.partialTicks;
			Block b = world.getBlock(e.target.blockX, e.target.blockY, e.target.blockZ);
			ArrayList<AxisAlignedBB> aabbs = null;

			try {
				aabbs = ReflectionHelper.getPrivateValue(TileEntity.class,
						world.getTileEntity(e.target.blockX, e.target.blockY, e.target.blockZ), "aabbs");
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			if (aabbs == null)
				aabbs = new ArrayList<AxisAlignedBB>();
			// int i = 0;
			for (AxisAlignedBB aabb : aabbs) {
				if (aabb.maxX - e.target.blockX > 1 || aabb.maxZ - e.target.blockZ > 1
						|| aabb.maxY - e.target.blockY > 1 || aabb.minX - e.target.blockX < 0
						|| aabb.minZ - e.target.blockZ < 0 || aabb.minY - e.target.blockY < 0)
					continue;
				AxisAlignedBB aabbSaved = aabb;
				aabb = aabb.expand((double) f1, (double) f1, (double) f1).getOffsetBoundingBox(-d0, -d1, -d2);
				// RenderGlobal.drawOutlinedBoundingBox(aabb, -1);
				// Map<AxisAlignedBB, ArrayList<Boolean>> isDraw = isDraw(aabbs);
				drawOutlinedBoundingBox(aabb, new ArrayList<Boolean>(), -1);
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			if (aabbs.size() == 0) {
				return;
			}
			e.setCanceled(true);
		}
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB aabb, ArrayList<Boolean> isDrawAll, int aabbsCount) {
		GL11.glBegin(GL11.GL_LINES);
		// Base points B|D|C1|A1
		// Maybe it more faster than tessellator?
		/*
		 * ArrayList<Boolean> isDraw = new ArrayList<Boolean>(); for(int i = 0; i < 12;
		 * i++) { boolean collided = false; for(int j = 1; j < aabbsCount; j++) {
		 * //System.out.println(isDrawAll.get(i)); if(isDrawAll.get(i) ||
		 * isDrawAll.get(i+12*j)) collided = false; else collided = true; }
		 * isDraw.add(collided); }
		 */
		// if(isDraw.get(0)) {
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ); // A
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ); // B
		// }
		// if(isDraw.get(1)) {
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ); // C
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ); // B
		// }

		// if(isDraw.get(9)) {
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ); // B1
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ); // B
		// }

		// if(isDraw.get(11)) {
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ); // D1
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ); // D
		// }
		// if(isDraw.get(2)) {
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ); // C
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ); // D
		// }
		// if(isDraw.get(3)) {
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ); // A
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ); // D
		// }
		// if(isDraw.get(10)) {
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ); // С
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ); // C1
		// }
		// if(isDraw.get(5)) {
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ); // B1
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ); // C1
		// }
		// if(isDraw.get(6)) {
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ); // D1
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ); // C1
		// }
		// if(isDraw.get(8)) {
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ); // A
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ); // A1
		// }
		// if(isDraw.get(4)) {
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ); // B1
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ); // A1
		// }
		// if(isDraw.get(7)) {
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ); // D1
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ); // A1
		// }
		GL11.glEnd();

	}

	/*
	 * in the distant future, the intersection of boundboxes vectors will be
	 * completed, and drawing extra vectors in the boundboxes removed... maybe
	 */
	public Map<AxisAlignedBB, ArrayList<Boolean>> isDraw(ArrayList<AxisAlignedBB> listAABB) {
		Map<AxisAlignedBB, ArrayList<Boolean>> isDraw = new HashMap<AxisAlignedBB, ArrayList<Boolean>>();
		ArrayList<Boolean> isDrawB = new ArrayList<Boolean>();
		for (int i = 0; i < listAABB.size(); i++) {
			AxisAlignedBB aabb = listAABB.get(i);
			isDrawB.clear();
			for (int j = 0; j < listAABB.size(); j++) {
				AxisAlignedBB aabbC = listAABB.get(j);
				if (aabbC == aabb) {
					for (int k = 0; k < 12; k++)
						isDrawB.add(true);
					continue;
				}
				Vec3 A_a = Vec3.createVectorHelper(aabb.minX, aabb.minY, aabb.minZ);
				Vec3 B_a = Vec3.createVectorHelper(aabb.maxX, aabb.minY, aabb.minZ);
				Vec3 C_a = Vec3.createVectorHelper(aabb.maxX, aabb.minY, aabb.maxZ);
				Vec3 D_a = Vec3.createVectorHelper(aabb.minX, aabb.minY, aabb.maxZ);
				Vec3 A1_a = Vec3.createVectorHelper(aabb.minX, aabb.maxY, aabb.minZ);
				Vec3 B1_a = Vec3.createVectorHelper(aabb.maxX, aabb.maxY, aabb.minZ);
				Vec3 C1_a = Vec3.createVectorHelper(aabb.maxX, aabb.maxY, aabb.maxZ);
				Vec3 D1_a = Vec3.createVectorHelper(aabb.minX, aabb.maxY, aabb.maxZ);

				Vec3 A_b = Vec3.createVectorHelper(aabbC.minX, aabbC.minY, aabbC.minZ);
				Vec3 B_b = Vec3.createVectorHelper(aabbC.maxX, aabbC.minY, aabbC.minZ);
				Vec3 C_b = Vec3.createVectorHelper(aabbC.maxX, aabbC.minY, aabbC.maxZ);
				Vec3 D_b = Vec3.createVectorHelper(aabbC.minX, aabbC.minY, aabbC.maxZ);
				Vec3 A1_b = Vec3.createVectorHelper(aabbC.minX, aabbC.maxY, aabbC.minZ);
				Vec3 B1_b = Vec3.createVectorHelper(aabbC.maxX, aabbC.maxY, aabbC.minZ);
				Vec3 C1_b = Vec3.createVectorHelper(aabbC.maxX, aabbC.maxY, aabbC.maxZ);
				Vec3 D1_b = Vec3.createVectorHelper(aabbC.minX, aabbC.maxY, aabbC.maxZ);

				// AB
				if (!isCollided(A_a, B_a, A1_b, B1_b) && !isCollided(A_a, B_a, D1_b, C1_b)
						&& !isCollided(A_a, B_a, A_b, B_b) && !isCollided(A_a, B_a, D_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// BC
				if (!isCollided(B_a, C_a, B1_b, C1_b) && !isCollided(B_a, C_a, A1_b, D1_b)
						&& !isCollided(B_a, C_a, A_b, D_b) && !isCollided(B_a, C_a, B_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);

				// CD
				if (!isCollided(C_a, D_a, A1_b, B1_b) && !isCollided(C_a, D_a, D1_b, C1_b)
						&& !isCollided(C_a, D_a, A_b, B_b) && !isCollided(C_a, D_a, D_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);

				// AD
				if (!isCollided(A_a, D_a, B1_b, C1_b) && !isCollided(A_a, D_a, A1_b, D1_b)
						&& !isCollided(A_a, D_a, A_b, D_b) && !isCollided(A_a, D_a, B_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);

				// A1B1
				if (!isCollided(A1_a, B1_a, A1_b, B1_b) && !isCollided(A1_a, B1_a, D1_b, C1_b)
						&& !isCollided(A1_a, B1_a, A_b, B_b) && !isCollided(A1_a, B1_a, D_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);

				// B1C1
				if (!isCollided(B1_a, C1_a, B1_b, C1_b) && !isCollided(B1_a, C1_a, A1_b, D1_b)
						&& !isCollided(B1_a, C1_a, A_b, D_b) && !isCollided(B1_a, C1_a, B_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// C1D1
				if (!isCollided(C1_a, D1_a, A1_b, B1_b) && !isCollided(C1_a, D1_a, D1_b, C1_b)
						&& !isCollided(C1_a, D1_a, A_b, B_b) && !isCollided(C1_a, D1_a, D_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// A1D1
				if (!isCollided(A1_a, D1_a, B1_b, C1_b) && !isCollided(A1_a, D1_a, A1_b, D1_b)
						&& !isCollided(A1_a, D1_a, A_b, D_b) && !isCollided(A1_a, D1_a, B_b, C_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// AA1
				if (!isCollided(A_a, A1_a, B_b, B1_b) && !isCollided(A_a, A1_a, C_b, C1_b)
						&& !isCollided(A_a, A1_a, D_b, D1_b) && !isCollided(A_a, A1_a, A_b, A1_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// BB1
				if (!isCollided(B_a, B1_a, B_b, B1_b) && !isCollided(B_a, B1_a, C_b, C1_b)
						&& !isCollided(B_a, B1_a, D_b, D1_b) && !isCollided(B_a, B1_a, A_b, A1_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
				// CC1
				if (!isCollided(C_a, C1_a, B_b, B1_b) && !isCollided(C_a, C1_a, C_b, C1_b)
						&& !isCollided(C_a, C1_a, D_b, D1_b) && !isCollided(C_a, C1_a, A_b, A1_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);

				// DD1
				if (!isCollided(D_a, D1_a, B_b, B1_b) && !isCollided(D_a, D1_a, C_b, C1_b)
						&& !isCollided(D_a, D1_a, D_b, D1_b) && !isCollided(D_a, D1_a, A_b, A1_b))
					isDrawB.add(true);
				else
					isDrawB.add(false);
			}
			isDraw.put(aabb, isDrawB);
		}
		return isDraw;
	}

	public Boolean isCollided(Vec3 start1, Vec3 end1, Vec3 start2, Vec3 end2) {
		// (x1-x2) (y3-y4)- (y1- y2)(x3-x4)
		if ((start1.zCoord == start2.zCoord) && (end1.zCoord == end2.zCoord)) {
			if (((start1.xCoord - end1.xCoord) * (start2.yCoord - end2.yCoord)
					- (start1.yCoord - end1.yCoord) * (start2.xCoord - end2.xCoord)) <= 0) {
				return true;
			}
		}
		if ((start1.yCoord == start2.yCoord) && (end1.yCoord == end2.yCoord)) {
			if (((start1.xCoord - end1.xCoord) * (start2.zCoord - end2.zCoord)
					- (start1.zCoord - end1.zCoord) * (start2.xCoord - end2.xCoord)) <= 0) {
				return true;
			}
		}
		if ((start1.xCoord == start2.xCoord) && (end1.xCoord == end2.xCoord)) {
			if (((start1.zCoord - end1.zCoord) * (start2.yCoord - end2.yCoord)
					- (start1.yCoord - end1.yCoord) * (start2.zCoord - end2.zCoord)) <= 0) {
				return true;
			}
		}
		// Check if vectors in one plane
		/*
		 * boolean x1 = false, x2 = false, y1 = false, y2 = false, z1 = false, z2 =
		 * false; if(start1.xCoord == start2.xCoord) x1 = true; if(start1.yCoord ==
		 * start2.yCoord) y1 = true; if(start1.zCoord == start2.zCoord) z1 = true;
		 * 
		 * if(end1.xCoord == end2.xCoord) x2 = true; if(end1.yCoord == end2.yCoord) y2 =
		 * true; if(end1.zCoord == end2.zCoord) z2 = true;
		 * 
		 * if(x1 && y1 || x2 && y1 && y2) {//in z line if((start1.zCoord >=
		 * start2.zCoord && end1.zCoord >= end2.zCoord) || (start1.zCoord <=
		 * start2.zCoord && end1.zCoord >= end2.zCoord)|| (start1.zCoord >=
		 * start2.zCoord && end1.zCoord <= end2.zCoord)) return true; }if(x1 && z1 && x2
		 * && z2) {//in y place if((start1.yCoord >= start2.yCoord && end1.yCoord >=
		 * end2.yCoord) || (start1.yCoord <= start2.yCoord && end1.zCoord >=
		 * end2.yCoord)|| (start1.yCoord >= start2.yCoord && end1.zCoord <=
		 * end2.yCoord)) return true; }if(y1 && z1 && y2 && z2) {//in x place
		 * if((start1.xCoord >= start2.xCoord && end1.xCoord >= end2.xCoord) ||
		 * (start1.xCoord <= start2.xCoord && end1.xCoord >= end2.xCoord)||
		 * (start1.xCoord >= start2.xCoord && end1.xCoord <= end2.xCoord)) return true;
		 * }
		 */
		return false;
	}
}
