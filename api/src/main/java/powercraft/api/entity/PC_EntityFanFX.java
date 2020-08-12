package powercraft.api.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_VecF;
import powercraft.launcher.mod_PowerCraft;

public class PC_EntityFanFX extends EntityFX {

	private PC_VecF end = new PC_VecF();

	public PC_EntityFanFX(World world, PC_VecF cnt, PC_VecF end, PC_VecF move, float strength, PC_Color color) {
		super(world, cnt.x + 0.5, cnt.y + 0.5, cnt.z + 0.5);
		this.end = end.copy();
		motionX = move.x;
		motionY = move.y;
		motionZ = move.z;
		setRBGColorF(color.x, color.y, color.z);
		particleScale = strength * 10.0F;
		particleMaxAge = 30;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float tickTime, float rotationX, float rotationXZ,
			float rotationZ, float rotationYZ, float rotationXY) {
		float size = 0.05F * this.particleScale;

		float x1 = (float) (posX - interpPosX);
		float y1 = (float) (posY - interpPosY);
		float z1 = (float) (posZ - interpPosZ);

		float x2 = x1 + (float) end.x;
		float y2 = y1 + (float) end.y;
		float z2 = z1 + (float) end.z;

		float x3 = -x1;
		float y3 = -y1;
		float z3 = -z1;

		float x4 = (float) (end.x);
		float y4 = (float) (end.y);
		float z4 = (float) (end.z);

		float xn = y3 * z4 - z3 * y4;
		float yn = z3 * x4 - x3 * z4;
		float zn = x3 * y4 - y3 * x4;

		float length = (float) Math.sqrt(xn * xn + yn * yn + zn * zn);
		xn /= length;
		yn /= length;
		zn /= length;

		xn *= size;
		yn *= size;
		zn *= size;

		float alpha = 1.0F;
		if (particleAge < 5) {
			alpha = particleAge / 5.0f;
		}
		if (particleAge > 20) {
			alpha = (30 - particleAge) / 10.0f;
		}

		tessellator.draw();
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(false);
		int tex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		PC_ClientUtils.mc().renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID,
				PC_TextureRegistry.getPowerCraftImageDir() + "Api/fan.png"));
		tessellator.startDrawingQuads();
		tessellator.setBrightness(128);
		tessellator.setColorRGBA_F(particleRed * alpha, particleGreen * alpha, particleBlue * alpha, 1.0F);
		tessellator.addVertexWithUV((double) (x1), (double) (y1), (double) (z1), 0, 0);
		tessellator.addVertexWithUV((double) (x1), (double) (y1), (double) (z1), 0, 0);
		tessellator.addVertexWithUV((double) (x2), (double) (y2), (double) (z2), 1, 0.5);
		tessellator.addVertexWithUV((double) (x2 + xn), (double) (y2 + yn), (double) (z2 + zn), 1, 0);
		tessellator.addVertexWithUV((double) (x1), (double) (y1), (double) (z1), 0, 1);
		tessellator.addVertexWithUV((double) (x1), (double) (y1), (double) (z1), 0, 1);
		tessellator.addVertexWithUV((double) (x2 - xn), (double) (y2 - yn), (double) (z2 - zn), 1, 1);
		tessellator.addVertexWithUV((double) (x2), (double) (y2), (double) (z2), 1, 0.5);
		tessellator.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(true);
		tessellator.startDrawingQuads();
	}

	@Override
	public void onUpdate() {
		double length = end.length();
		end.add((float) motionX, (float) motionY, (float) motionZ);
		length = length / end.length();
		end.mul((float) length);
		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
	}

}
