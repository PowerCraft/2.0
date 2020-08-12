package powercraft.mobile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.launcher.mod_PowerCraft;

public class PCmo_RenderMiner extends Render {

	/** model */
	protected ModelBase modelMiner;

	/**
	 * miner model
	 */
	public PCmo_RenderMiner() {
		shadowSize = 0.5F;
		modelMiner = new PCmo_ModelMiner();
	}

	/**
	 * Do render miner
	 * 
	 * @param entityminer miner
	 * @param d           relative x
	 * @param d1          relative y
	 * @param d2          relative z
	 * @param f           angle y
	 * @param f1          wobble time
	 */
	public void renderMiner(PCmo_EntityMiner entityminer, double d, double d1, double d2, float f, float f1) {
		Minecraft mc = Minecraft.getMinecraft();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glRotatef(180F - f, 0.0F, 1.0F, 0.0F);
		float f2 = entityminer.getTimeSinceHit() - f1;
		float f3 = entityminer.getDamageTaken() - f1;
		if (f3 < 0.0F) {
			f3 = 0.0F;
		}
		if (f2 > 0.0F) {
			GL11.glRotatef(((MathHelper.sin(f2) * f2 * f3) / 10F) * entityminer.getForwardDirection(), 0.8F, 0.0F,
					0.0F);
		}
		// loadTexture("/terrain.png");
		float f4 = 0.75F;
		GL11.glScalef(f4, f4, f4);
		GL11.glScalef(1.0F / f4, 1.0F / f4, 1.0F / f4);
		mc.getTextureManager()
				.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, PC_TextureRegistry.getPowerCraftImageDir()
						+ PC_TextureRegistry.getTextureName(PCmo_App.instance, "miner_base.png")));
		GL11.glScalef(-1F, -1F, 1.0F);
		modelMiner.render(entityminer, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		mc.getTextureManager()
				.bindTexture(new ResourceLocation(mod_PowerCraft.MODID,
						PC_TextureRegistry.getPowerCraftImageDir()
								+ PC_TextureRegistry.getTextureName(PCmo_App.instance,
										"miner_overlay_" + (Integer.toString(entityminer.st.level)) + ".png")));
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glDisable(3008 /* GL_ALPHA_TEST */);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		modelMiner.render(entityminer, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if (entityminer.hasPlayer()) {
			mc.getTextureManager()
					.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, PC_TextureRegistry.getPowerCraftImageDir()
							+ PC_TextureRegistry.getTextureName(PCmo_App.instance, "miner_overlay_keyboard.png")));
			modelMiner.render(entityminer, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		}

		GL11.glDisable(3042 /* GL_BLEND */);
		GL11.glEnable(3008 /* GL_ALPHA_TEST */);

		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		renderMiner((PCmo_EntityMiner) entity, d, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}

}
