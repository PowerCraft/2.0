package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

public class PC_GresScrollArea extends PC_GresWidget {

	public static final int HSCROLL = 1, VSCROLL = 2;
	private int type = (HSCROLL | VSCROLL);
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;
	private PC_VecI scroll = new PC_VecI(0, 0);
	private int mousePressed = -1;
	private PC_VecI lastMousePosition = new PC_VecI(0, 0);

	public PC_GresScrollArea() {
		super();
		canAddWidget = false;
	}

	public PC_GresScrollArea(PC_GresWidget child) {
		super();
		canAddWidget = false;
		setChild(child);
	}

	public PC_GresScrollArea(PC_GresWidget child, int type) {
		super();
		canAddWidget = false;
		this.type = type;
		setChild(child);
	}

	public PC_GresScrollArea(int type) {
		super();
		canAddWidget = false;
		this.type = type;
	}

	public PC_GresScrollArea(int width, int height, PC_GresWidget child, int type) {
		super(width, height);
		canAddWidget = false;
		this.type = type;
		setChild(child);
	}

	public PC_GresScrollArea setChild(PC_GresWidget child) {
		if (child == null) {
			if (childs.size() != 0)
				childs.remove(0);
		} else {
			if (childs.size() == 0)
				childs.add(child);
			else
				childs.set(0, child);
		}
		calcChildPositions();
		return this;
	}

	public PC_GresWidget getChild() {
		return childs.size() > 0 ? childs.get(0) : null;
	}

	public PC_GresScrollArea setType(int type) {
		this.type = type;
		return this;
	}

	public int getType() {
		return type & (HSCROLL | VSCROLL);
	}

	@Override
	public PC_VecI calcSize() {
		if (childs.size() > 0) {
			PC_VecI cSize = childs.get(0).calcSize();
			if ((type & VSCROLL) == 0)
				size.y = cSize.y + 18;
			if ((type & HSCROLL) == 0)
				size.x = cSize.x + 18;
		}
		if (size.x < minSize.x)
			size.x = minSize.x;
		if (size.y < minSize.y)
			size.y = minSize.y;
		return size.copy();
	}

	@Override
	public void calcChildPositions() {
		if (childs.size() > 0)
			childs.get(0).setPosition(2 - scroll.x, 2 - scroll.y);
		calcSize();
	}

	private void calcScrollPosition() {

		int sizeX = size.x - ((type & VSCROLL) != 0 ? 12 : 1);
		int maxSizeX = childs.size() > 0 ? childs.get(0).size.x : 0;
		int sizeOutOfFrame = maxSizeX - sizeX + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		hScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int sizeY = size.y - ((type & HSCROLL) != 0 ? 12 : 1);
		int maxSizeY = childs.size() > 0 ? childs.get(0).size.y : 0;
		sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / maxSizeY) : 0;
		vScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.y / sizeOutOfFrame : 0) * prozent * sizeY;
		vScrollSize = (int) ((1 - prozent) * sizeY + 0.5);

		updateScrollPosition();
	}

	private void updateScrollPosition() {

		int sizeX = size.x - ((type & VSCROLL) != 0 ? 12 : 1);
		int maxSizeX = childs.size() > 0 ? childs.get(0).size.x : 0;
		int sizeOutOfFrame = maxSizeX - sizeX + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / (maxSizeX)) : 0;
		if (hScrollPos < 0) {
			hScrollPos = 0;
		}
		if (hScrollPos > sizeX - hScrollSize) {
			hScrollPos = sizeX - hScrollSize;
		}
		scroll.x = (int) (hScrollPos / prozent / sizeX * sizeOutOfFrame + 0.5);

		int sizeY = size.y - ((type & HSCROLL) != 0 ? 12 : 1);
		int maxSizeY = childs.size() > 0 ? childs.get(0).size.y : 0;
		sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		if (vScrollPos < 0) {
			vScrollPos = 0;
		}
		if (vScrollPos > sizeY - vScrollSize) {
			vScrollPos = sizeY - vScrollSize;
		}
		scroll.y = (int) (vScrollPos / prozent / sizeY * sizeOutOfFrame + 0.5);

		if (getChild() != null) {
			int x = 2, y = 2;
			PC_GresWidget w = getChild();
			if ((type & HSCROLL) != 0)
				x -= scroll.x;
			if ((type & VSCROLL) != 0)
				y -= scroll.y;
			w.setPosition(x, y);
		}
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		boolean hScroll = (type & HSCROLL) != 0, vScroll = (type & VSCROLL) != 0;
		int xV = vScroll ? 12 : 1, yV = hScroll ? 12 : 1;

		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, 0xffA0A0A0);

		if (hScroll)
			drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - xV, offsetPos.y + pos.y + size.y - 1,
					0xffA0A0A0);

		drawVerticalLine(offsetPos.x + pos.x, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1, 0xffA0A0A0);

		if (vScroll)
			drawVerticalLine(offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - yV,
					0xffA0A0A0);

		int scrollbarBg = 0x909090;

		drawRect(offsetPos.x + pos.x + 1, offsetPos.y + pos.y + 1, offsetPos.x + pos.x + size.x - xV,
				offsetPos.y + pos.y + size.y - yV, 0x909090);

		drawRect(offsetPos.x + pos.x + 1, offsetPos.y + pos.y + size.y - yV + 1, offsetPos.x + pos.x + size.x - xV,
				offsetPos.y + pos.y + size.y - 1, scrollbarBg);

		drawRect(offsetPos.x + pos.x + size.x - xV + 1, offsetPos.y + pos.y + 1, offsetPos.x + pos.x + size.x - 1,
				offsetPos.y + pos.y + size.y - yV, scrollbarBg);

		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y + size.y - yV,
				0xffA0A0A0);

		drawVerticalLine(offsetPos.x + pos.x + size.x - xV, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1,
				0xffA0A0A0);

		if (mousePressed == -1) {
			calcScrollPosition();
		}

		if (hScroll)
			renderTextureSliced(offsetPos.copy().add((int) hScrollPos + 1, size.y - 11, 0),
					imgdir + "scrollbar_handle.png", new PC_VecI(hScrollSize - 1, 10), new PC_VecI(0, 0),
					new PC_VecI(256, 256), new PC_RectI(1, 1, 1, 1));

		if (vScroll)
			renderTextureSliced(offsetPos.copy().add(size.x - 11, 1 + (int) vScrollPos, 0),
					imgdir + "scrollbar_handle.png", new PC_VecI(10, vScrollSize - 1), new PC_VecI(0, 0),
					new PC_VecI(256, 256), new PC_RectI(1, 1, 1, 1));
		return new PC_RectI(offsetPos.x + pos.x + 2, offsetPos.y + pos.y + 2, size.x - ((type & VSCROLL) != 0 ? 15 : 4),
				size.y - ((type & HSCROLL) != 0 ? 15 : 4));
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		if ((mousePos.y < size.y - ((type & HSCROLL) != 0 ? 12 : 1) && mousePos.x > size.x - 12)
				|| (mousePos.x < size.x - ((type & VSCROLL) != 0 ? 12 : 1) && mousePos.y > size.y - 12)) {
			return MouseOver.THIS;
		}
		return MouseOver.CHILD;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		mousePressed = -1;
		lastMousePosition.setTo(mousePos);
		if (key != -1) {
			if (mousePos.y < size.y - ((type & HSCROLL) != 0 ? 12 : 1) && mousePos.x > size.x - 12) {
				if (mousePos.y - 1 < vScrollPos) {
					scroll.y -= 5;
					return true;
				}
				if (mousePos.y - 1 >= vScrollPos + vScrollSize) {
					scroll.y += 5;
					return true;
				}
				mousePressed = 0;
				return true;
			} else if (mousePos.x < size.x - ((type & VSCROLL) != 0 ? 12 : 1) && mousePos.y > size.y - 12) {
				if (mousePos.x - 1 < hScrollPos) {
					scroll.x -= 5;
					return true;
				}
				if (mousePos.x - 1 >= hScrollPos + hScrollSize) {
					scroll.x += 5;
					return true;
				}
				mousePressed = 1;
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		switch (mousePressed) {
		case 0:
			vScrollPos += mousePos.y - lastMousePosition.y;
			updateScrollPosition();
			break;
		case 1:
			hScrollPos += mousePos.x - lastMousePosition.x;
			updateScrollPosition();
			break;
		}
		lastMousePosition.setTo(mousePos);
	}

	@Override
	public void mouseWheel(int i) {
		if ((type & VSCROLL) != 0) {
			vScrollPos -= i * 3;
			updateScrollPosition();
		} else if ((type & HSCROLL) != 0) {
			hScrollPos -= i * 3;
			updateScrollPosition();
		}
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void addedToWidget() {
	}

}
