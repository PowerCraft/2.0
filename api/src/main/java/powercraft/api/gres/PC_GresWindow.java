package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Window for GUI
 * 
 * @authors XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */

public class PC_GresWindow extends PC_GresWidget {

	/**
	 * distance to the window frame
	 */
	public PC_VecI padding = new PC_VecI(10, 4);
	/**
	 * The gap right under the top title.<br>
	 * Applies only if title != ""
	 */
	public int gapUnderTitle = 10;

	/**
	 * @param minX  minimal X size
	 * @param minY  minimal Y size
	 * @param title title of the window
	 */
	public PC_GresWindow(int minX, int minY, String title) {
		super(minX, minY, title);
	}

	/**
	 * Create window of width 240 and height auto.
	 * 
	 * @param title title of the window
	 */
	public PC_GresWindow(String title) {
		super(120, 0, title);
	}

	/**
	 * Create window of width 240 and height auto, no title.
	 */
	public PC_GresWindow() {
		super(120, 0, "");
	}

	/**
	 * Set standard width and stuff for an inventory screen.<br>
	 * Used to look exactly like normal inventory screens.
	 * 
	 * @return this
	 */
	public PC_GresWidget setWidthForInventory() {
		setMinWidth(176);
		padding.setTo(7, 7, 0);
		calcSize();
		return this;
	}

	@Override
	public PC_VecI calcSize() {
		int textWidth = mc.fontRenderer.getStringWidth(text);
		if (size.x < textWidth + padding.x * 2 + 48) {
			size.x = textWidth + padding.x * 2 + 48;
		}
		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		if (size.y < minSize.y) {
			size.y = minSize.y;
		}
		calcChildPositions();
		return size.copy();
	}

	@Override
	public PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		renderTextureSliced(offsetPos, imgdir + "dialog.png", size, new PC_VecI(0, 0), new PC_VecI(256, 256),
				new PC_RectI(32, 4, 18, 4));

		if (text.length() > 0) {
			getFontRenderer().drawString(text,
					offsetPos.x + pos.x + (size.x) / 2 - fontRenderer.getStringWidth(text) / 2, offsetPos.y + pos.y + 8,
					0x404040);
		}

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI pos) {
		return MouseOver.CHILD;
	}

	@Override
	public boolean mouseClick(PC_VecI pos, int key) {
		return false;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void calcChildPositions() {
		int yy = 0, minySize = 0, minmaxxSize = 0, maxxSize = 0, ySize = 0,
				yPlus = getFontRenderer().FONT_HEIGHT + gapUnderTitle;

		if (text.length() == 0) {
			yPlus = 0;
		}

		int childNum = childs.size();
		for (int i = 0; i < childNum; i++) {
			PC_GresWidget child = childs.get(i);
			child.calcChildPositions();
			PC_VecI csize = child.calcSize();
			PC_VecI cminSize = child.getMinSize();
			ySize += csize.y + child.widgetMargin;
			minySize += cminSize.y + child.widgetMargin;
			if (maxxSize < csize.x) {
				maxxSize = csize.x;
			}
			if (minmaxxSize < cminSize.x) {
				minmaxxSize = cminSize.x;
			}
		}

		if (alignV == PC_GresAlign.STRETCH) {
			maxxSize = minmaxxSize;
			ySize = minySize;
			for (int i = 0; i < childNum; i++) {
				PC_VecI cminSize = childs.get(i).getMinSize();
				childs.get(i).setSize(cminSize.x, cminSize.y, false);
			}
		}

		if (maxxSize + padding.x * 2 > size.x || ySize + yPlus + padding.y > size.y) {
			if (maxxSize + padding.x * 2 > size.x) {
				size.x = maxxSize + padding.x * 2;
			}
			if (ySize + yPlus + padding.y > size.y) {
				size.y = ySize + yPlus + padding.y;
			}

			if (parent != null) {
				parent.calcChildPositions();
			}
			// calcChildPositions();
			return;
		}

		ySize -= widgetMargin;
		int numChilds = childs.size() - 1;
		int num = 0;

		for (int i = 0; i < childNum; i++) {
			PC_GresWidget child = childs.get(i);

			PC_VecI csize = child.getSize();
			int xPos = 0;
			int yPos = 0;
			int s = 0;

			switch (alignH) {
			case RIGHT:
				xPos = size.x - child.getSize().x - padding.x;
				break;
			case CENTER:
				xPos = size.x / 2 - child.getSize().x / 2;
				break;
			case STRETCH:
				xPos = padding.x;
				child.setSize(size.x - padding.x * 2, child.getSize().y, false);
				break;
			case LEFT:
			default:
				xPos = padding.x;
				break;
			}

			switch (alignV) {
			case BOTTOM:
				yPos = size.y - padding.y - ySize + yy;
				break;
			case CENTER:
				yPos = (size.y + yPlus - padding.y) / 2 - ySize / 2 + yy;
				break;
			case STRETCH:
				yPos = yy;
				int realY = size.y;
				csize.y = (int) (realY / (double) ySize * csize.y + 0.5);
				child.setSize(csize.x, csize.y, false);
				break;
			case JUSTIFIED:
				double sym = (size.y / (double) ySize);
				int nsy = (int) (sym * csize.y + 0.5);
				int syp = nsy - csize.y;
				if (numChilds != 0)
					yPos = yy + num / numChilds * syp;
				else
					yPos = yy;
				csize.y = nsy;
				break;
			case TOP:
			default:
				yPos = yPlus + yy;
				break;
			}

			child.setPosition(xPos, yPos);
			yy += csize.y + widgetMargin + s;
			num++;
		}

		/*
		 * int yy = 0, minySize = 0, minmaxxSize = 0, maxxSize = 0, ySize = 0, yPlus =
		 * getFontRenderer().FONT_HEIGHT + gapUnderTitle;
		 * 
		 * if (text.length() == 0) { yPlus = 0; }
		 * 
		 * int childNum = childs.size(); for (int i = 0; i < childNum; i++) {
		 * childs.get(i).calcChildPositions(); PC_VecI childSize =
		 * childs.get(i).calcSize(); PC_VecI childMinSize = childs.get(i).getMinSize();
		 * ySize += childSize.y + childs.get(i).widgetMargin; minySize += childMinSize.y
		 * + childs.get(i).widgetMargin; if (maxxSize < childSize.x) { maxxSize =
		 * childSize.x; } if (minmaxxSize < childMinSize.x) { minmaxxSize =
		 * childMinSize.x; } }
		 * 
		 * if (alignV == PC_GresAlign.STRETCH) { maxxSize = minmaxxSize; ySize =
		 * minySize; for (int i = 0; i < childNum; i++) { PC_VecI cminSize =
		 * childs.get(i).getMinSize(); childs.get(i).setSize(cminSize.x, cminSize.y,
		 * false); } }
		 * 
		 * if (maxxSize + padding.x * 2 > size.x || ySize + yPlus + padding.y > size.y)
		 * { if (maxxSize + padding.x * 2 > size.x) { size.x = maxxSize + padding.x * 2;
		 * } if (ySize + yPlus + padding.y > size.y) { size.y = ySize + yPlus +
		 * padding.y; } if (parent != null) { parent.calcChildPositions(); }
		 * calcChildPositions(); return; }
		 * 
		 * //ySize -= widgetMargin;
		 * 
		 * for (int i = 0; i < childNum; i++) { PC_VecI csize = childs.get(i).getSize();
		 * int xPos = 0; int yPos = 0; int s = 0;
		 * 
		 * switch (alignH) { case LEFT: xPos = padding.x; break; case RIGHT: xPos =
		 * size.x - childs.get(i).getSize().x - padding.x; break; case CENTER: xPos =
		 * size.x / 2 - childs.get(i).getSize().x / 2; break; case STRETCH: xPos =
		 * padding.x; childs.get(i).setSize(size.x - padding.x * 2,
		 * childs.get(i).getSize().y, false); break; }
		 * 
		 * switch (alignV) { case TOP: yPos = yPlus + yy; break; case BOTTOM: yPos =
		 * size.y + yPlus - padding.y - ySize + yy; break; case CENTER: yPos = (size.y +
		 * yPlus - padding.y) / 2 - ySize / 2 + yy; break; case STRETCH: s = (size.y -
		 * yPlus - padding.y - ySize + widgetMargin - widgetMargin * childNum) /
		 * childNum; childs.get(i).setSize(childs.get(i).getSize().x,
		 * childs.get(i).getSize().y + s, false); yPos = yPlus + yy; break; }
		 * 
		 * childs.get(i).setPosition(xPos, yPos); yy += csize.y +
		 * childs.get(i).widgetMargin + s; }
		 * 
		 */

	}

	@Override
	public void mouseMove(PC_VecI pos) {
	}

	@Override
	public PC_VecI getMinSize() {
		return calcSize();
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public void addedToWidget() {
	}
}
