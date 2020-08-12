package powercraft.api.gres;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.tileentity.PC_ITileEntityWatcher;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * GuiScreen class
 * 
 * @authors XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public class PC_GresGui extends GuiScreen implements PC_IGresGui, PC_ITileEntityWatcher {

	/** The wrapped GUI */
	private PC_IGresClient gui;
	private PC_GresLayoutV child;
	private PC_GresWidget lastFocus;
	private boolean isContainer;
	private boolean pauseGame = false;
	private boolean shiftTransfer = false;
	private int xSize = 176;
	private int ySize = 166;
	private int guiLeft;
	private int guiTop;
	private TileEntity tileEntity;

	/**
	 * Constructor for creating a gui
	 * 
	 * @param gui the gui
	 */
	public PC_GresGui(TileEntity te, PC_IGresClient gui) {
		this.gui = (PC_IGresClient) gui;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		tileEntity = te;
		// if(tileEntity!=null)
		// tileEntity.addTileEntityWatcher(this);
	}

	@Override
	public PC_GresWidget add(PC_GresWidget widget) {
		PC_GresWidget c = child.add(widget);
		xSize = child.getSize().x;
		ySize = child.getSize().y;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		child.setPosition(guiLeft, guiTop);
		return c;
	}

	@Override
	public void setPausesGame(boolean b) {
		pauseGame = b;
	}

	@Override
	public void close() {
		mc.displayGuiScreen(null);
		mc.setIngameFocus();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		child.tick();
		if (lastFocus != null) {
			lastFocus.updateCursorCounter();
		}
		gui.updateScreen(this);
	}

	@Override
	public void setFocus(PC_GresWidget widget) {
		if (widget != lastFocus) {
			if (lastFocus != null) {
				lastFocus.setFocus(false);
			}
			if (widget != null) {
				widget.setFocus(true);
			}
			lastFocus = widget;
		}
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		child = new PC_GresLayoutV();
		child.setFontRenderer(fontRendererObj);
		child.setGui(this);
		child.setSize(0, 0);
		gui.initGui(this);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		gui.onGuiClosed(this);
		// if(tileEntity!=null)
		// tileEntity.removeTileEntityWatcher(this);
		super.onGuiClosed();
	}

	@Override
	protected void keyTyped(char c, int i) {

		if (i == Keyboard.KEY_F11) {
		}

		if (lastFocus != null && lastFocus.visible) {
			if (lastFocus.keyTyped(c, i)) {
				registerAction(lastFocus);
				return;
			}
		}

		gui.onKeyPressed(this, c, i);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {

		PC_GresWidget newFocus = child.getWidgetUnderMouse(new PC_VecI(x, y));
		if (newFocus != null && !newFocus.visible)
			newFocus = null;

		if (newFocus != lastFocus) {
			if (lastFocus != null) {
				lastFocus.setFocus(false);
			}
			if (newFocus != null) {
				newFocus.setFocus(true);
			}
			lastFocus = newFocus;
		}
		boolean makeAction = false;
		if (newFocus != null) {
			PC_VecI fpos = newFocus.getPositionOnScreen();
			if (newFocus.mouseClick(new PC_VecI(x - fpos.x, y - fpos.y), button)) {
				makeAction = true;
			}
		}
		super.mouseClicked(x, y, button);
		if (makeAction)
			registerAction(newFocus);

	}

	private void mouseMoved(int x, int y) {
		int wheel = Mouse.getDWheel();
		if (wheel < 0) {
			wheel = -1;
		}
		if (wheel > 0) {
			wheel = 1;
		}
		if (lastFocus != null) {
			PC_VecI fpos = lastFocus.getPositionOnScreen();
			lastFocus.mouseMove(new PC_VecI(x - fpos.x, y - fpos.y));
			lastFocus.mouseWheel(wheel);
		}
		child.getWidgetUnderMouse(new PC_VecI(x, y));
	}

	private void mouseUp(int x, int y, int state) {
		if (lastFocus != null) {
			PC_VecI fpos = lastFocus.getPositionOnScreen();
			if (lastFocus.mouseClick(new PC_VecI(x - fpos.x, y - fpos.y), -1)) {
				registerAction(lastFocus);
			}
		}
	}

	/**
	 * state = -1 ... move, other ... up
	 */
	@Override
	protected void mouseMovedOrUp(int x, int y, int state) {
		super.mouseMovedOrUp(x, y, state);

		if (state != -1) {
			mouseUp(x, y, state);
		} else {
			mouseMoved(x, y);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return pauseGame;
	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		ScaledResolution sr = new ScaledResolution(PC_ClientUtils.mc(), PC_ClientUtils.mc().displayWidth,
				PC_ClientUtils.mc().displayHeight);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		child.updateRenderer(new PC_VecI(0, 0), new PC_RectI(0, 0, sr.getScaledWidth(), sr.getScaledHeight()),
				sr.getScaleFactor());
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();
	}

	private int bgColor1 = 0xa0101010;
	private int bgColor2 = 0x50101010;

	@Override
	public void setBackground(int top, int bottom) {
		bgColor1 = top;
		bgColor2 = bottom;
	}

	@Override
	public void drawWorldBackground(int par1) {
		if (mc.theWorld != null) {
			drawGradientRect(0, 0, width, height, bgColor1, bgColor2);
		} else {
			drawBackground(par1);
		}
	}

	/**
	 * Draws the screen and all the components in it. COPY FROM GuiContainer!<BR>
	 * NEEDED TO OVERRIDE render() AND FOR CUSTOM SLOT RENDERING.<br>
	 * <br>
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {

		gui.updateTick(this);

		if (!gui.drawBackground(this, par1, par2, par3))
			drawDefaultBackground();

		int i = guiLeft;
		int j = guiTop;
		drawGuiContainerBackgroundLayer(par3, par1, par2);

		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		List list = getTooltipAtPosition(par1, par2);

		if (list != null && list.size() > 0) {
			int l1 = 0;

			for (int i2 = 0; i2 < list.size(); i2++) {
				int k2 = fontRendererObj.getStringWidth((String) list.get(i2));

				if (k2 > l1) {
					l1 = k2;
				}
			}

			int j2 = (par1 - i) + 12;
			int l2 = par2 - j - 12;
			int i3 = l1;
			int j3 = 8;

			if (list.size() > 1) {
				j3 += 2 + (list.size() - 1) * 10;
			}

			zLevel = 300F;
			int k3 = 0xf0100010;
			drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
			drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3, k3);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3, k3);
			int l3 = 0x505000ff;
			int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
			drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1, (l2 + j3 + 3) - 1, l3, i4);
			drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2 + j3 + 3) - 1, l3, i4);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3, l3);
			drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

			for (int j4 = 0; j4 < list.size(); j4++) {
				String s = (String) list.get(j4);

				fontRendererObj.drawStringWithShadow(s, j2, l2, -1);

				if (j4 == 0) {
					l2 += 2;
				}

				l2 += 10;
			}

			zLevel = 0.0F;
		}

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

	}

	/**
	 * COPY FROM GuiContainer!<BR>
	 * NEEDED TO OVERRIDE render() AND FOR CUSTOM SLOT RENDERING.<br>
	 * <br>
	 * Returns if the passed mouse position is over the specified slot.
	 * 
	 * @param par1Slot the slot to check
	 * @param par2     x ?
	 * @param par3     y ?
	 * @return is over
	 */
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
		int i = guiLeft;
		int j = guiTop;
		par2 -= i;
		par3 -= j;
		return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1
				&& par3 >= par1Slot.yDisplayPosition - 1 && par3 < par1Slot.yDisplayPosition + 16 + 1;
	}

	@Override
	public PC_VecI getSize() {
		return new PC_VecI(width, height);
	}

	@Override
	public void registerAction(PC_GresWidget widget) {
		gui.actionPerformed(widget, this);
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() {
		PC_VecI mp = getMousePos();

		if (Mouse.getEventButtonState()) {
			this.mouseClicked(mp.x, mp.y, Mouse.getEventButton());
		} else {
			this.mouseMovedOrUp(mp.x, mp.y, Mouse.getEventButton());
		}
	}

	@Override
	public PC_GresBaseWithInventory getContainer() {
		return null;
	}

	public List<String> getTooltipAtPosition(int x, int y) {
		PC_GresWidget w = child.getWidgetUnderMouse(new PC_VecI(x, y));
		if (w == null)
			return null;
		PC_VecI fpos = w.getPositionOnScreen();
		return w.getTooltip(new PC_VecI(x - fpos.x, y - fpos.y));
	}

	@Override
	public void keyChange(String key, Object value) {
		child.keyChange(key, value);
		gui.keyChange(key, value);
	}

	@Override
	public TileEntity getTE() {
		return tileEntity;
	}

	@Override
	public Slot getSlotAt(int x, int y) {
		return null;
	}

	@Override
	public PC_VecI getMousePos() {
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		return new PC_VecI(x, y);
	}

}