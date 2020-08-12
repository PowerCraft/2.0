package powercraft.api.gres;

import powercraft.api.gres.PC_GresWidget.MouseOver;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

public class PC_GresScrollBar extends PC_GresWidget {

	private int scrollSize = 0, scroll = 0, maxScrollSize = 0;
	private float scrollPos = 0;
	private boolean mousePressed = false;
	private PC_VecI lastMousePosition = new PC_VecI(0, 0);

	public PC_GresScrollBar(int maxScrollSize) {
		super(12, 32);
		canAddWidget = false;
		this.maxScrollSize = maxScrollSize;
		calcScrollSize();
	}

	@Override
	public PC_GresWidget setSize(int width, int height, boolean calcParent) {
		super.setSize(width, height, calcParent);
		calcScrollSize();
		return this;
	}

	@Override
	public PC_GresWidget setSize(PC_VecI size) {
		return setSize(size.x, size.y, true);
	}

	private void calcScrollSize() {
		int sizeY = size.y - 1;
		int maxSizeY = maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame <= 0) {
			scrollSize = sizeY;
		} else {
			scrollSize = sizeY * sizeY / maxSizeY;
			if (scrollSize < 10) {
				scrollSize = 10;
			}
		}
	}

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		int sizeY = size.y - 1;
		int maxSizeY = maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		scrollPos = scroll * prozent * sizeY / sizeOutOfFrame;
		updateScrollPosition();
	}

	public void setMaxScollSize(int maxScrollSize) {
		this.maxScrollSize = maxScrollSize;
		calcScrollSize();
		updateScrollPosition();
	}

	private void updateScrollPosition() {

		int sizeY = size.y - 1;
		int maxSizeY = maxScrollSize;
		int sizeOutOfFrame = maxSizeY - sizeY + 6;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeY > 0 ? ((float) sizeOutOfFrame / (maxSizeY)) : 0;
		if (scrollPos < 0) {
			scrollPos = 0;
		}
		if (scrollPos > sizeY - scrollSize) {
			scrollPos = sizeY - scrollSize;
		}
		scroll = (int) (scrollPos / prozent / sizeY * sizeOutOfFrame + 0.5);

		if (gui != null)
			gui.registerAction(this);

	}

	@Override
	public PC_VecI calcSize() {
		return size.copy();
	}

	@Override
	public void calcChildPositions() {
	}

	@Override
	protected PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
		drawHorizontalLine(posOffset.x + pos.x, posOffset.x + pos.x + size.x - 1, posOffset.y + pos.y, 0xffA0A0A0);
		drawHorizontalLine(posOffset.x + pos.x, posOffset.x + pos.x + size.x - 1, posOffset.y + pos.y + size.y - 1,
				0xffA0A0A0);
		drawVerticalLine(posOffset.x + pos.x, posOffset.y + pos.y, posOffset.y + pos.y + size.y - 1, 0xffA0A0A0);
		drawVerticalLine(posOffset.x + pos.x + size.x - 1, posOffset.y + pos.y, posOffset.y + pos.y + size.y - 1,
				0xffA0A0A0);

		renderTextureSliced(posOffset.copy().add(size.x - 11, 1 + (int) scrollPos, 0), imgdir + "scrollbar_handle.png",
				new PC_VecI(10, scrollSize - 1), new PC_VecI(0, 0), new PC_VecI(256, 256), new PC_RectI(1, 1, 1, 1));
		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		mousePressed = false;
		lastMousePosition.setTo(mousePos);
		if (key != -1) {
			if (mousePos.y < size.y - 1 && mousePos.x > size.x - 12) {
				if (mousePos.y - 1 < scrollPos) {
					scroll -= 5;
					return true;
				}
				if (mousePos.y - 1 >= scrollPos + scrollSize) {
					scroll += 5;
					return true;
				}
				mousePressed = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		if (mousePressed) {
			scrollPos += mousePos.y - lastMousePosition.y;
			updateScrollPosition();
		}
		lastMousePosition.setTo(mousePos);
	}

	@Override
	public void mouseWheel(int i) {
		scrollPos -= i * 3;
		updateScrollPosition();
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void addedToWidget() {
	}

}
