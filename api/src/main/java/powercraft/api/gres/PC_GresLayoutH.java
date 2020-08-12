package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI horizontal layout
 * 
 * @author XOR19
 * @copy (c) 2012
 */
public class PC_GresLayoutH extends PC_GresWidget {

	/**
	 * horizontal layout
	 */
	public PC_GresLayoutH() {
		super();
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

		return size.copy();
	}

	@Override
	public void calcChildPositions() {
		if (!visible)
			return;
		int xx = 0, xSize = 0;
		int lastcm = 0;
		for (PC_GresWidget child : childs) {
			child.calcChildPositions();
			PC_VecI csize = child.calcSize();
			if (csize.x + xSize > size.x || csize.y > size.y) {
				if (csize.x + xSize > size.x) {
					size.x = csize.x + xSize;
				}
				if (csize.y > size.y) {
					size.y = csize.y + child.widgetMargin;
				}
				if (parent != null) {
					parent.calcChildPositions();
				}
				calcChildPositions();
				return;
			}
			xSize += csize.x + child.widgetMargin;
			lastcm = child.widgetMargin;
		}
		xSize -= lastcm;
		int numChilds = childs.size() - 1;
		int num = 0;
		double gap = 0;
		if (numChilds != 0)
			gap = (size.x - xSize) / numChilds;
		for (PC_GresWidget child : childs) {
			PC_VecI csize = child.getSize();
			int xPos = 0;
			int yPos = 0;
			switch (alignH) {
			case RIGHT:
				xPos = size.x - xSize + xx;
				break;
			case CENTER:
				xPos = size.x / 2 - xSize / 2 + xx;
				break;
			case STRETCH:
				xPos = xx;
				csize.x = (int) (size.x / (double) xSize * csize.x + 0.5);
				child.setSize(csize.x, csize.y, false);
				break;
			case JUSTIFIED:
				xPos = xx;
				csize.x += gap;
				break;
			default:
			case LEFT:
				xPos = xx;
				break;
			}
			switch (alignV) {
			case BOTTOM:
				yPos = size.y - csize.y;
				break;
			case CENTER:
				yPos = size.y / 2 - csize.y / 2;
				break;
			case STRETCH:
				yPos = 0;
				child.setSize(child.getSize().x, size.y, false);
				break;
			default:
			case TOP:
				yPos = 0;
				break;
			}
			child.setPosition(xPos, yPos);
			xx += csize.x + child.widgetMargin;
			num++;
		}
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {
		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.CHILD;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		return false;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mpos) {
	}

	@Override
	public PC_VecI getMinSize() {
		if (!visible)
			return zerosize;
		return calcSize();
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public void addedToWidget() {
	}
}
