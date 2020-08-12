package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import powercraft.api.utils.PC_VecF;

public class PC_Renderer {
	protected static int render3dId;
	protected static int render2dId;
	protected static PC_Renderer renderer3d;
	protected static PC_Renderer renderer2d;
	protected boolean render3d;

	public PC_Renderer(boolean render3d) {
		this.render3d = render3d;

		if (render3d) {
			renderer3d = this;
			render3dId = 0;
		} else {
			renderer2d = this;
			render2dId = 0;
		}
	}

	public static int getRendererID(boolean renderer3d) {
		if (renderer3d) {
			return render3dId;
		}

		return render2dId;
	}

	protected void iTessellatorDraw() {
	}

	public static void tessellatorDraw() {
		renderer2d.iTessellatorDraw();
	}

	protected void iTessellatorStartDrawingQuads() {
	}

	public static void tessellatorStartDrawingQuads() {
		renderer2d.iTessellatorStartDrawingQuads();
	}

	protected void iTessellatorStartDrawing(int i) {
	}

	public static void tessellatorStartDrawing(int i) {
		renderer2d.iTessellatorStartDrawing(i);
	}

	protected void iTessellatorSetColorOpaque_I(int i) {
	}

	public static void tessellatorSetColorOpaque_I(int i) {
		renderer2d.iTessellatorSetColorOpaque_I(i);
	}

	protected void iTessellatorSetColor(int r, int g, int b, int a) {
	}

	public static void tessellatorSetColor(int r, int g, int b, int a) {
		renderer2d.iTessellatorSetColor(r, g, b, a);
	}

	protected void iTessellatorAddVertex(double x, double y, double z) {
	}

	public static void tessellatorAddVertex(double x, double y, double z) {
		renderer2d.iTessellatorAddVertex(x, y, z);
	}

	protected void iBindTexture(String texture) {
	};

	public static void bindTexture(String texture) {
		renderer2d.iBindTexture(texture);
	}

	protected void iRenderStandardBlock(Object renderer, Block block, int x, int y, int z) {
	}

	public static void renderStandardBlock(Object renderer, Block block, int x, int y, int z) {
		renderer2d.iRenderStandardBlock(renderer, block, x, y, z);
	}

	protected void iRenderBlockAllFaces(Object renderer, Block block, int x, int y, int z) {
	}

	public static void renderBlockAllFaces(Object renderer, Block block, int x, int y, int z) {
		renderer2d.iRenderBlockAllFaces(renderer, block, x, y, z);
	}

	protected void iRenderInvBox(Object renderer, Block block, int metadata) {
	}

	public static void renderInvBox(Object renderer, Block block, int metadata) {
		renderer2d.iRenderInvBox(renderer, block, metadata);
	}

	protected void iRenderBlockRotatedBox(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			Object renderer) {
	}

	public static void renderBlockRotatedBox(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			Object renderer) {
		renderer2d.iRenderBlockRotatedBox(world, x, y, z, block, modelId, renderer);
	};

	protected void iRenderInvBlockRotatedBox(Block block, int metadata, int modelID, Object renderer) {
	}

	public static void renderInvBlockRotatedBox(Block block, int metadata, int modelID, Object renderer) {
		renderer2d.iRenderInvBlockRotatedBox(block, metadata, modelID, renderer);
	}

	protected void iRenderInvBoxWithTexture(Object renderer, Block block, IIcon icon) {
	}

	public static void renderInvBoxWithTexture(Object renderer, Block block, IIcon icon) {
		renderer2d.iRenderInvBoxWithTexture(renderer, block, icon);
	}

	protected void iRenderInvBoxWithTextures(Object renderer, Block block, IIcon[] icon) {
	}

	public static void renderInvBoxWithTextures(Object renderer, Block block, IIcon[] icon) {
		renderer2d.iRenderInvBoxWithTextures(renderer, block, icon);
	}

	protected void iRenderBlockByMeta(Object renderer, Block block, int x, int y, int z, int meta) {
	}

	public static void renderBlockByMeta(Object renderer, Block block, int x, int y, int z, int meta) {
		renderer2d.iRenderBlockByMeta(renderer, block, x, y, z, meta);
	}

	protected void iRenderBlockByRenderType(Object renderer, Block block, int x, int y, int z) {
	}

	public static void renderBlockByRenderType(Object renderer, Block block, int x, int y, int z) {
		renderer2d.iRenderBlockByRenderType(renderer, block, x, y, z);
	}

	public static void glColor3f(float r, float g, float b) {
		glColor4f(r, g, b, 1.0f);
	}

	protected void iglColor4f(float r, float g, float b, float a) {
	}

	public static void glColor4f(float red, float green, float blue, float a) {
		renderer2d.iglColor4f(red, green, blue, a);
	}

	protected void iglPushMatrix() {
	}

	public static void glPushMatrix() {
		renderer2d.iglPushMatrix();
	}

	protected void iglPopMatrix() {
	}

	public static void glPopMatrix() {
		renderer2d.iglPopMatrix();
	}

	protected void iglTranslatef(float x, float y, float z) {
	}

	public static void glTranslatef(float x, float y, float z) {
		renderer2d.iglTranslatef(x, y, z);
	}

	protected void iglRotatef(float angel, float x, float y, float z) {
	}

	public static void glRotatef(float angel, float x, float y, float z) {
		renderer2d.iglRotatef(angel, x, y, z);
	}

	protected void iglScalef(float x, float y, float z) {
	}

	public static void glScalef(float x, float y, float z) {
		renderer2d.iglScalef(x, y, z);
	}

	protected void iglEnable(int i) {
	}

	public static void glEnable(int i) {
		renderer2d.iglEnable(i);
	}

	protected void iglDisable(int i) {
	}

	public static void glDisable(int i) {
		renderer2d.iglDisable(i);
	}

	protected void iglBlendFunc(int i, int j) {
	}

	public static void glBlendFunc(int i, int j) {
		renderer2d.iglBlendFunc(i, j);
	}

	protected void iglNormal3f(float x, float y, float z) {
	}

	public static void glNormal3f(float x, float y, float z) {
		renderer2d.iglNormal3f(x, y, z);
	}

	protected void iglDepthMask(boolean state) {
	}

	public static void glDepthMask(boolean state) {
		renderer2d.iglDepthMask(state);
	}

	protected void irenderEntityLabelAt(String label, PC_VecF realPos, int viewDistance, float yOffset, double x,
			double y, double z) {
	}

	public static void renderEntityLabelAt(String label, PC_VecF realPos, int viewDistance, float yOffset, double x,
			double y, double z) {
		renderer2d.irenderEntityLabelAt(label, realPos, viewDistance, yOffset, x, y, z);
	}

	protected FontRenderer igetFontRenderer() {
		return null;
	}

	public static FontRenderer getFontRenderer() {
		return renderer2d.igetFontRenderer();
	}

}
