package powercraft.api.gres;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Text editor
 * 
 * @author XOR19
 * @copy (c) 2012
 */
public class PC_GresTextEdit extends PC_GresWidget {

	/**
	 * Input type for Text Edit field.
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	public enum PC_GresInputType {
		/** accept all characters */
		TEXT,
		/** accept signed number */
		INT,
		/** accept unsigned number */
		UNSIGNED_INT,
		/** accept signed number with dot */
		SIGNED_FLOAT,
		/** accept unsigned number with dot */
		UNSIGNED_FLOAT,
		/** Disable user input */
		NONE,
		/** [a-zA-Z_][a-zA-Z0-9_] */
		IDENTIFIER;
	}

	private int maxChars;
	private int mouseSelectStart = 0;
	private int mouseSelectEnd = 0;
	private boolean mousePressed = false;
	private PC_GresInputType type = PC_GresInputType.TEXT;

	/**
	 * Text Edit
	 * 
	 * @param label text
	 * @param chars max number of characters
	 */
	public PC_GresTextEdit(String label, int chars) {
		super((chars + 1) * 7, mc.fontRenderer.FONT_HEIGHT + 12, label);
		maxChars = chars;
		canAddWidget = false;
		color[textColorEnabled] = 0xffffffff;
		color[textColorClicked] = 0xffffffff;
		color[textColorHover] = 0xffffffff;
		color[textColorShadowEnabled] = 0; // 0xff383838;
		color[textColorDisabled] = 0xffffffff;
		color[textColorShadowDisabled] = 0; // 0xff383838;
	}

	/**
	 * Text Edit
	 * 
	 * @param initText text
	 * @param chars    max no. of characters
	 * @param type     input type allowed.
	 */
	public PC_GresTextEdit(String initText, int chars, PC_GresInputType type) {
		super((chars + 1) * 7, mc.fontRenderer.FONT_HEIGHT + 12, initText);
		maxChars = chars;
		canAddWidget = false;
		color[textColorEnabled] = 0xffffffff;
		color[textColorClicked] = 0xffffffff;
		color[textColorHover] = 0xffffffff;
		color[textColorShadowEnabled] = 0; // 0xff383838;
		color[textColorDisabled] = 0xffffffff;
		color[textColorShadowDisabled] = 0; // 0xff383838;
		this.type = type;
	}

	@Override
	public PC_VecI calcSize() {
		size.setTo(getMinSize());
		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		return size.copy();
	}

	@Override
	public void calcChildPositions() {
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		if (mouseSelectEnd > text.length()) {
			mouseSelectEnd = text.length();
		}
		if (mouseSelectStart > text.length()) {
			mouseSelectStart = text.length();
		}

		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, 0xffA0A0A0);
		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y + size.y - 1,
				0xffA0A0A0);

		drawVerticalLine(offsetPos.x + pos.x, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1, 0xffA0A0A0);
		drawVerticalLine(offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1,
				0xffA0A0A0);

		drawRect(offsetPos.x + pos.x + 1, offsetPos.y + pos.y + 1, offsetPos.x + pos.x + size.x - 1,
				offsetPos.y + pos.y + size.y - 1, 0xff000000);

		if (text.length() > maxChars) {
			text = text.substring(0, maxChars);
		}

		if (mouseSelectStart != mouseSelectEnd && hasFocus) {
			int s = mouseSelectStart, e = mouseSelectEnd;
			if (s > e) {
				e = mouseSelectStart;
				s = mouseSelectEnd;
			}

			drawRect(offsetPos.x + pos.x + getStringWidth(text.substring(0, s)) + 6, offsetPos.y + pos.y + 4,
					offsetPos.x + pos.x + getStringWidth(text.substring(0, e)) + 6, offsetPos.y + pos.y + size.y - 5,
					0xff3399FF);

		}

		drawString(text, offsetPos.x + pos.x + 6, offsetPos.y + pos.y + (size.y - 8) / 2);

		if (mouseSelectEnd == text.length()) {
			if (hasFocus && (cursorCounter / 6) % 2 == 0) {
				drawString("_", offsetPos.x + pos.x + getStringWidth(text) + 6, offsetPos.y + pos.y + (size.y - 8) / 2);
			}
		} else if (hasFocus && (cursorCounter / 6) % 2 == 0) {
			drawVerticalLine(offsetPos.x + pos.x + getStringWidth(text.substring(0, mouseSelectEnd)) + 5,
					offsetPos.y + pos.y + 3, offsetPos.y + pos.y + size.y - 5,
					a(color[enabled ? textColorEnabled : textColorDisabled]));
		}

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.THIS;
	}

	private int a(int aa) {
		return aa | 0xff000000;
	}

	private int getMousePositionInString(int x) {
		int charSize;
		x -= 6;
		for (int i = 0; i < text.length(); i++) {
			charSize = getStringWidth("" + text.charAt(i));
			if (x - charSize / 2 < 0) {
				return i;
			}
			x -= charSize;
		}
		return text.length();
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		mousePressed = false;
		if (!enabled) {
			return false;
		}
		if (key != -1) {
			mouseSelectStart = getMousePositionInString(mpos.x);
			mouseSelectEnd = mouseSelectStart;
			mousePressed = true;
			return true;
		}
		return false;
	}

	/**
	 * Add a character instead of current selection (or in place of, if start ==
	 * end)
	 * 
	 * @param c character
	 */
	protected void addKey(char c) {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		try {
			String s1 = text.substring(0, s);
			String s2 = text.substring(e);
			if ((s1 + c + s2).length() > maxChars) {
				return;
			}
			text = s1 + c + s2;
			mouseSelectEnd += 1;
			mouseSelectStart = mouseSelectEnd;
		} catch (StringIndexOutOfBoundsException ss) {
			ss.printStackTrace();
		}
	}

	private void deleteSelected() {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		String s1 = text.substring(0, s);
		String s2 = text.substring(e);
		text = s1 + s2;
		mouseSelectEnd = s;
		mouseSelectStart = s;
	}

	private void key_backspace() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
			return;
		}
		if (mouseSelectEnd <= 0) {
			return;
		}
		String s1 = text.substring(0, mouseSelectEnd - 1);
		String s2 = text.substring(mouseSelectEnd);
		text = s1 + s2;
		mouseSelectEnd -= 1;
		mouseSelectStart = mouseSelectEnd;
	}

	private void key_delete() {
		if (mouseSelectStart != mouseSelectEnd) {
			deleteSelected();
			return;
		}
		if (mouseSelectEnd >= text.length()) {
			return;
		}
		String s1 = text.substring(0, mouseSelectEnd);
		String s2 = text.substring(mouseSelectEnd + 1);
		text = s1 + s2;
	}

	private String getSelect() {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		return text.substring(s, e);
	}

	/**
	 * Replace selected part of the text
	 * 
	 * @param stri replacement
	 */
	private void setSelected(String stri) {
		int s = mouseSelectStart, e = mouseSelectEnd;
		if (s > e) {
			e = mouseSelectStart;
			s = mouseSelectEnd;
		}
		String s1 = text.substring(0, s);
		String s2 = text.substring(e);
		String ss = "";
		switch (type) {
		case UNSIGNED_INT:
			for (int i = 0; i < stri.length(); i++) {
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case INT:
			if (text.length() > 0) {
				if (text.charAt(0) == '-') {
					if (mouseSelectStart == 0 && mouseSelectEnd == 0) {
						break;
					}
				}
			}
			for (int i = 0; i < stri.length(); i++) {
				if (i == 0) {
					if (stri.charAt(0) == '-') {
						if (s == 0) {
							ss += stri.charAt(i);
						}
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case SIGNED_FLOAT:
			if (text.length() > 0) {
				if (text.charAt(0) == '-') {
					if (mouseSelectStart == 0 && mouseSelectEnd == 0) {
						break;
					}
				}
			}
			for (int i = 0; i < stri.length(); i++) {
				if (i == 0) {
					if (stri.charAt(0) == '-') {
						if (s == 0) {
							ss += stri.charAt(i);
						}
					}
				}
				if (stri.charAt(i) == '.') {
					if (!(s1.contains(".") || s2.contains(".") || ss.contains("."))) {
						ss += ".";
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case UNSIGNED_FLOAT:
			for (int i = 0; i < stri.length(); i++) {
				if (stri.charAt(i) == '.') {
					if (!(s1.contains(".") || s2.contains(".") || ss.contains("."))) {
						ss += ".";
					}
				}
				if (Character.isDigit(Character.valueOf(stri.charAt(i)))) {
					ss += stri.charAt(i);
				}
			}
			break;

		case NONE:
			break;

		default:
			for (int i = 0; i < stri.length(); i++) {
				if (ChatAllowedCharacters.isAllowedCharacter(stri.charAt(i))) {
					ss += stri.charAt(i);
				}
			}
			break;
		}
		if ((s1 + ss + s2).length() > maxChars) {
			return;
		}
		text = s1 + ss + s2;
		mouseSelectEnd = s + ss.length();
		mouseSelectStart = s;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		if (!enabled || !hasFocus) {
			return false;
		}
		switch (c) {
		case 3:
			GuiScreen.setClipboardString(getSelect());
			return true;

		case 22:
			setSelected(GuiScreen.getClipboardString());
			return true;

		case 24:
			GuiScreen.setClipboardString(getSelect());
			deleteSelected();
			return true;
		}

		if (type == PC_GresInputType.NONE)
			return true;

		switch (key) {
		case Keyboard.KEY_RETURN:
			return false;
		case Keyboard.KEY_BACK:
			key_backspace();
			return true;
		case Keyboard.KEY_HOME:
			mouseSelectEnd = mouseSelectStart = 0;
			return true;
		case Keyboard.KEY_END:
			mouseSelectEnd = mouseSelectStart = text.length();
			return true;
		case Keyboard.KEY_DELETE:
			key_delete();
			return true;
		case Keyboard.KEY_LEFT:
			if (mouseSelectEnd > 0) {
				mouseSelectEnd -= 1;
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}

			}
			return true;
		case Keyboard.KEY_RIGHT:
			if (mouseSelectEnd < text.length()) {
				mouseSelectEnd += 1;
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}
			}
			return true;
		default:
			switch (type) {
			case UNSIGNED_INT:
				if (Character.isDigit(c)) {
					addKey(c);
					return true;
				} else {
					return false;
				}

			case INT:
				// writing before minus
				if (text.length() > 0 && text.charAt(0) == '-' && mouseSelectStart == 0 && mouseSelectEnd == 0) {
					return true;
				}

				if (Character.isDigit(c)) {
					addKey(c);
					return true;
				} else if ((mouseSelectStart == 0 || mouseSelectEnd == 0) && c == '-') {
					addKey(c);
					return true;
				}
				return false;

			case SIGNED_FLOAT:

				if (c == '.') {
					if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
						return true;
					}
					if (text.length() > 0 && (mouseSelectStart == 1 || mouseSelectEnd == 1) && text.charAt(0) == '-') {
						return true;
					}
					if (text.length() > 0 && text.contains(".")) {
						return true;
					}
					addKey(c);
					return true;
				}

				if (text.length() > 0 && text.charAt(0) == '-' && mouseSelectStart == 0 && mouseSelectEnd == 0) {
					return true;
				}

				if (Character.isDigit(c)) {
					addKey(c);
					return true;
				} else if ((mouseSelectStart == 0 || mouseSelectEnd == 0) && c == '-') {
					addKey(c);
					return true;
				}

				return false;

			case UNSIGNED_FLOAT:

				if (c == '.') {
					if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
						return true;
					}
					if (text.length() > 0 && text.contains(".")) {
						return true;
					}
					addKey(c);
					return true;
				}

				if (Character.isDigit(c)) {
					addKey(c);
					return true;
				}

				return false;

			case IDENTIFIER:

				if (Character.isDigit(c)) {
					if (mouseSelectStart == 0 || mouseSelectEnd == 0) {
						return true;
					}
					addKey(c);
					return true;
				}

				if (Character.isLetter(c) || c == '_') {
					addKey(c);
					return true;
				}

				return false;

			case TEXT:
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(c)) {
					addKey(c);
					return true;
				}
				return false;
			}
		}
	}

	@Override
	public void mouseMove(PC_VecI mpos) {
		if (mousePressed) {
			mouseSelectEnd = getMousePositionInString(mpos.x);
		}
	}

	@Override
	public PC_VecI getMinSize() {
		if (minSize.x == 0)
			return new PC_VecI((maxChars + 1) * 7, getFontRenderer().FONT_HEIGHT + 12);
		return minSize.setY(getFontRenderer().FONT_HEIGHT + 12);
	}

	@Override
	public void mouseWheel(int i) {
		if (i > 0) {
			if (mouseSelectEnd > 0) {
				mouseSelectEnd -= 1;
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}
			}
		}
		if (i < 0) {
			if (mouseSelectEnd < text.length()) {
				mouseSelectEnd += 1;
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart = mouseSelectEnd;
				}
			}
		}
	}

	@Override
	public void addedToWidget() {
	}
}
