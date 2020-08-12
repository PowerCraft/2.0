package powercraft.api.gres;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

public class PC_GresWidgetTab extends PC_GresWidget {

	protected static PC_GresWidgetTab selectTab;
	protected boolean isClicked = false;
	protected double count = 0;
	private IIcon icon;
	private String texture;
	private Item item;

	public PC_GresWidgetTab(int color, String texture, IIcon icon) {
		super(21, 20);
		this.color = new int[] { color, color, color, color, color, color };
		this.icon = icon;
		setMinSize(new PC_VecI(21, 20));
		this.texture = texture;
	}

	public PC_GresWidgetTab(int color, Item item) {
		super(21, 20);
		this.color = new int[] { color, color, color, color, color, color };
		this.item = item;
		setMinSize(new PC_VecI(21, 20));
	}

	@Override
	public PC_VecI getMinSize() {
		return minSize.copy();
	}

	@Override
	public PC_VecI getSize() {
		if (!visible)
			return new PC_VecI(0, 0);
		return minSize.copy().add(size.copy().sub(minSize).mul(count));
	}

	@Override
	public PC_VecI calcSize() {
		if (!visible)
			return zerosize;
		calcChildPositions();
		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		if (size.y < minSize.y) {
			size.y = minSize.y;
		}
		return getSize();
	}

	@Override
	public void calcChildPositions() {
		if (!visible)
			return;
		int yy = minSize.y, ySize = minSize.y;
		int lastmargin = 0;
		for (PC_GresWidget w : childs) {
			if (!w.visible)
				continue;
			w.calcChildPositions();
			PC_VecI csize = w.calcSize();
			if (csize.x + 5 > size.x || ySize + csize.y + 4 > size.y) {
				if (csize.x + 5 > size.x) {
					size.x = csize.x + 5;
				}
				if (ySize + csize.y + 4 > size.y) {
					size.y = ySize + csize.y + 4;
				}
				if (parent != null) {
					parent.calcChildPositions();
				}
				calcChildPositions();
			}
			lastmargin = w.widgetMargin;
			ySize += csize.y + w.widgetMargin;
		}
		ySize -= minSize.y;
		size.y -= minSize.y + 4;
		size.x -= 5;
		ySize -= lastmargin;
		int numChilds = childs.size() - 1;
		int num = 0;
		double gap = 0;
		if (numChilds != 0)
			gap = (size.y - ySize) / numChilds;
		for (PC_GresWidget w : childs) {
			if (!w.visible)
				continue;
			PC_VecI csize = w.getSize();
			int xPos = 0;
			int yPos = 0;
			switch (alignH) {
			case RIGHT:
				xPos = size.x - csize.x + 3;
				break;
			case CENTER:
				xPos = size.x / 2 - csize.x / 2 + 3;
				break;
			case STRETCH:
				xPos = 0;
				w.setSize(size.x + 3, w.getSize().y, false);
				break;
			default:
			case LEFT:
				xPos = 3;
				break;
			}
			switch (alignV) {
			case BOTTOM:
				yPos = size.y - ySize + yy + 2;
				break;
			case CENTER:
				yPos = size.y / 2 - ySize / 2 + yy + 2;
				break;
			case STRETCH:
				yPos = yy;
				int realY = size.y;
				csize.y = (int) (realY / (double) ySize * csize.y + 0.5);
				w.setSize(csize.x + 2, csize.y, false);
				break;
			case JUSTIFIED:
				yPos = yy + 2;
				csize.y += gap;
				break;
			default:
			case TOP:
				yPos = yy + 2;
				break;
			}
			w.setPosition(xPos, yPos);
			yy += csize.y + w.widgetMargin;
			num++;
		}
		size.y += minSize.y + 4;
		size.x += 5;
	}

	@Override
	protected PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale) {

		renderTextureSlicedColored(posOffset, imgdir + "frame.png", getSize(), new PC_VecI(0, 0), new PC_VecI(256, 256),
				new PC_RectI(1, 1, 1, 1));
		// mc.renderEngine.bindTexture(new ResourceLocation(texture));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// drawTexturedModelRectFromIcon(pos.x + posOffset.x + 3, pos.y + posOffset.y +
		// 2, icon, 16, 16);
		// drawTexturedModalRectWithIcon(pos.x + posOffset.x + 3, pos.y + posOffset.y +
		// 2, 16, 16, icon, texture);
		RenderItem itemRenderer = RenderItem.getInstance();
		Minecraft mc = Minecraft.getMinecraft();
		RenderHelper.enableGUIStandardItemLighting();
		itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(item),
				pos.x + posOffset.x + 3, pos.y + posOffset.y + 2);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_BLEND);
		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		if (count >= 1)
			return MouseOver.CHILD;
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		if (mpos.x < 0 || mpos.x >= getSize().x || mpos.y < 0 || mpos.y >= minSize.y)
			return false;
		if (!enabled || !visible) {
			return false;
		}
		if (!parent.enabled) {
			return false;
		}
		if (isClicked && key == -1) {
			isClicked = false;
			if (selectTab == this) {
				selectTab = null;
			} else {
				selectTab = this;
			}
			return true;
		}
		isClicked = key == -1 ? false : true;
		// if (key != -1) mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mpos) {
		if (mpos.x < 0 || mpos.x >= getSize().x || mpos.y < 0 || mpos.y >= minSize.y
				|| mouseOver(mpos) == MouseOver.NON) {
			isClicked = false;
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

	@Override
	protected void onTick() {
		if (selectTab != this) {
			count -= 0.1;
			if (count <= 0) {
				count = 0;
			}
		} else {
			count += 0.1;
			if (count >= 1) {
				count = 1;
			}
		}
	}

	@Override
	public void updateRenderer(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
		if (!visible)
			return;

		if (count >= 1)
			tabRenderer(posOffset.copy().add(pos), null, scale);

		PC_RectI scissorNew = setDrawRect(scissorOld,
				new PC_RectI(posOffset.x + pos.x, posOffset.y + pos.y, size.x, size.y), scale);
		if (scissorNew == null)
			return;

		PC_RectI rect = render(posOffset, scissorNew, scale);
		if (rect != null)
			scissorNew = setDrawRect(scissorNew, rect, scale);

		if (count >= 1)
			childRenderer(posOffset.copy().add(pos), scissorNew, scale);
	}

}
