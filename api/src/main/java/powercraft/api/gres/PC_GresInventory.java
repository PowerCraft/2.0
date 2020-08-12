package powercraft.api.gres;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

public class PC_GresInventory extends PC_GresWidget {

	/** The slots */
	protected Slot slots[][];

	/** Inventory grid width */
	protected int gridWidth = 0;

	/** Inventory grid height */
	protected int gridHeight = 0;

	protected int slotWidth = 0;

	protected int slotHeight = 0;

	public Slot slotOver = null;

	public boolean isMouseDown;

	/**
	 * Inventory widget, with empty slot grid. To be filled using setSlot()
	 * 
	 * @param width  grid width
	 * @param height grid height
	 */
	public PC_GresInventory(int width, int height) {
		super(width * 18, height * 18);

		gridHeight = height;
		gridWidth = width;

		slotWidth = 18;
		slotHeight = 18;

		canAddWidget = false;
		slots = new Slot[gridWidth][gridHeight];
		for (int i = 0; i < 6; i++) {
			color[i] = 0xFFFFFF;
		}
	}

	public PC_GresInventory(int width, int height, int slotWidth, int slotHeight) {
		super(width * slotWidth, height * slotHeight);

		gridHeight = height;
		gridWidth = width;

		this.slotWidth = slotWidth;
		this.slotHeight = slotHeight;

		canAddWidget = false;
		slots = new Slot[gridWidth][gridHeight];
		for (int i = 0; i < 6; i++) {
			color[i] = 0xFFFFFF;
		}
	}

	public PC_GresInventory(Slot slot) {
		this(1, 1, 26, 26);
		slots[0][0] = slot;
	}

	@Override
	public PC_VecI getMinSize() {
		return calcSize();
	}

	@Override
	public PC_VecI calcSize() {
		return new PC_VecI(gridWidth * slotWidth, gridHeight * slotHeight);
	}

	public PC_VecI getGridSize() {
		return new PC_VecI(gridWidth, gridHeight);
	}

	@Override
	public void calcChildPositions() {
	}

	@Override
	protected PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
		PC_VecI widgetPos = null;
		PC_GresWidget w = this;

		while (w != null) {
			widgetPos = w.getPosition();
			w = w.getParent();
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				renderTextureSlicedColored(posOffset.offset(x * slotWidth, y * slotHeight, 0), imgdir + "widgets.png",
						new PC_VecI(slotWidth, slotHeight), new PC_VecI(0, 66), new PC_VecI(18, 18),
						new PC_RectI(1, 1, 1, 1));
			}
		}

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int k = 240;
		int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int x = 0, xp = pos.x + posOffset.x + 1 + (slotWidth - 18) / 2; x < gridWidth; x++, xp += slotWidth) {
			for (int y = 0, yp = pos.y + posOffset.y + 1
					+ (slotHeight - 18) / 2; y < gridHeight; y++, yp += slotHeight) {
				if (slots[x][y] != null) {
					Slot slot = slots[x][y];
					drawSlotInventory(xp, yp, slot);

					if (slot == slotOver && isMouseOver) {
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						drawGradientRect(xp, yp, xp + 16, yp + 16, 0x80ffffff, 0x80ffffff);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
					}
				}
			}
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		mouseMove(mousePos);
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		isMouseDown = key != -1;
		return true;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		isMouseOver = true;
		Slot slot = getSlotUnderMouse(mousePos);
		if (slot == null) {
			slotOver = null;
		} else {
			slotOver = slot;
		}
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void addedToWidget() {

	}

	/**
	 * Set single slot
	 * 
	 * @param slot the slot
	 * @param x    x position in grid
	 * @param y    y position in grid
	 * @return this
	 */
	public PC_GresInventory setSlot(int x, int y, Slot slot) {
		if (x >= 0 && x < this.slots.length && y >= 0 && y < this.slots[x].length) {
			this.slots[x][y] = slot;
		}
		return this;
	}

	/**
	 * Get slot
	 * 
	 * @param x x position in grid
	 * @param y y position in grid
	 * @return this
	 */
	public Slot getSlot(int x, int y) {
		if (x >= 0 && x < this.slots.length && y >= 0 && y < this.slots[x].length) {
			return this.slots[x][y];
		}
		return null;
	}

	protected void drawSlotInventory(int x, int y, Slot slot) {
		ItemStack itemstack = slot.getStack();
		boolean isNull = false;
		zLevel = 100F;
		RenderItem itemRenderer = PC_GresContainerGui.getItemRenderer();
		itemRenderer.zLevel = 100F;

		if (slot instanceof PC_Slot) {
			if (((PC_Slot) slot).useAlwaysBackground())
				itemstack = null;
		}

		if (itemstack == null) {
			IIcon icon = slot.getBackgroundIconIndex();

			if (icon != null) {
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, "/gui/items.png"));
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(x, y + 16, zLevel, icon.getMinU(), icon.getMaxV());
				tessellator.addVertexWithUV(x + 16, y + 16, zLevel, icon.getMaxU(), icon.getMaxV());
				tessellator.addVertexWithUV(x + 16, y, zLevel, icon.getMaxU(), icon.getMinV());
				tessellator.addVertexWithUV(x, y, zLevel, icon.getMinU(), icon.getMinV());
				tessellator.draw();
				GL11.glEnable(GL11.GL_LIGHTING);
				isNull = true;
			}
		}

		if (isNull || itemstack == null) {

			if (slot instanceof PC_Slot) {
				PC_Slot dirslot = (PC_Slot) slot;
				if (dirslot.getBackgroundStack() != null) {
					itemRenderer.zLevel = 99F;
					zLevel = 99F;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
					itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, dirslot.getBackgroundStack(),
							x, y);

					if (dirslot.renderGrayWhenEmpty()) {
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						drawGradientRect(x, y, x + 16, y + 16, 0xbb999999, 0xbb999999);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
					}

					zLevel = 100F;
					itemRenderer.zLevel = 100F;
				}

			}

		} else {
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, itemstack, x, y);
			itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, x, y);
		}

		itemRenderer.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	@Override
	public Slot getSlotUnderMouse(PC_VecI mousePos) {
		int x = mousePos.x / slotWidth;
		int y = mousePos.y / slotHeight;
		if (x >= 0 && y >= 0 && x < slots.length && y < slots[x].length) {
			return slots[x][y];
		}
		return null;
	}

	@Override
	public List<String> getTooltip(PC_VecI mousePos) {
		Slot slot = getSlotUnderMouse(mousePos);
		if (slot != null) {
			ItemStack itemstack = null;

			if (slot.getHasStack())
				itemstack = slot.getStack();

			if (slot instanceof PC_Slot && ((PC_Slot) slot).getBackgroundStack() != null
					&& ((PC_Slot) slot).renderTooltipWhenEmpty())
				itemstack = ((PC_Slot) slot).getBackgroundStack();

			if (itemstack != null) {
				List<String> l = itemstack.getTooltip(mc.thePlayer, false);
				l.set(0, (new StringBuilder()).append(l.get(0)).toString());
				return l;
			}
		}
		return super.getTooltip(mousePos);
	}

}
