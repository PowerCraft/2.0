package powercraft.api.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;

import org.lwjgl.opengl.GL11;

import powercraft.api.registry.PC_OverlayRegistry;
import powercraft.api.utils.PC_ClientUtils;

public class PC_OverlayRenderer extends GuiIngame {

	public PC_OverlayRenderer(Minecraft minecraft) {
		super(minecraft);
	}

	@Override
	public void renderGameOverlay(float timeStamp, boolean screen, int mx, int my) {
		PC_ClientUtils.mc().entityRenderer.setupOverlayRendering();
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		PC_OverlayRegistry.preOverlayRendering(this, timeStamp, screen, mx, my);
		super.renderGameOverlay(timeStamp, screen, mx, my);
		PC_OverlayRegistry.postOverlayRendering(this, timeStamp, screen, mx, my);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
