package powercraft.api.gres;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI multi-line text label with fixed width and dynamic height
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresLabelMultiline extends PC_GresWidget {

	private int minRows = 1;
	private int maxRows = 100;

	/**
	 * Multiline label
	 * 
	 * @param text  text
	 * @param width fixed widget width (text will wrap)
	 */
	public PC_GresLabelMultiline(String text, int width) {
		super(width, 10, text);
		canAddWidget = false;
		alignH = PC_GresAlign.LEFT;
	}

	/**
	 * @param minRows set minimal no. of rows
	 * @return this
	 */
	public PC_GresLabelMultiline setMinRows(int minRows) {
		this.minRows = minRows;
		return this;
	}

	/**
	 * @param maxRows set max no. of rows
	 * @return this
	 */
	public PC_GresLabelMultiline setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		return this;
	}

	@Override
	public PC_VecI calcSize() {
		getMinSize();
		if (size.y < minSize.y) {
			size.y = minSize.y;
		}
		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		return size.copy();
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		FontRenderer fontRenderer = getFontRenderer();

		int cnt = 0;

		String[] lines_nl = text.split("\n");

		l1: for (String s : lines_nl) {
			s.trim();
			if (s.length() > 0) {
				List<String> lines = fontRenderer.listFormattedStringToWidth(s, getMinSize().x);

				for (String ss : lines) {
					if (cnt == maxRows)
						break l1;
					ss.trim();
					if (ss.length() > 0) {
						int wid = getStringWidth(ss);
						int xstart = offsetPos.x + pos.x;

						switch (alignH) {
						case CENTER:
							xstart = xstart + size.x / 2 - wid / 2;
							break;
						case RIGHT:
							xstart = xstart + size.x - wid;
						default:
						case LEFT:
							break;
						}

						drawString(ss, xstart, offsetPos.y + pos.y + (fontRenderer.FONT_HEIGHT + 1) * cnt);
						cnt++;
					}
				}
			}
		}

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.THIS;
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
	public void calcChildPositions() {

	}

	@Override
	public void mouseMove(PC_VecI mpos) {

	}

	@Override
	public PC_VecI getMinSize() {

		FontRenderer fontRenderer = getFontRenderer();

		int cnt = 0;

		String[] lines_nl = text.split("\n");

		l1: for (String s : lines_nl) {
			s.trim();
			if (s.length() > 0) {
				List<String> lines = fontRenderer.listFormattedStringToWidth(s, minSize.x);

				for (String ss : lines) {
					if (cnt == maxRows)
						break l1;
					ss.trim();
					if (s.length() > 0) {
						cnt++;
					}
				}
			}
		}

		minSize.setTo(minSize.x, (fontRenderer.FONT_HEIGHT + 1) * Math.max(minRows, cnt), 0);

		return minSize;
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public void addedToWidget() {
	}
}
