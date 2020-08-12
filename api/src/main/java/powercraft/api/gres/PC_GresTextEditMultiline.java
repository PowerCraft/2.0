package powercraft.api.gres;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Multiline text edit
 * 
 * @author XOR19
 */
@SuppressWarnings("javadoc")
public class PC_GresTextEditMultiline extends PC_GresWidget {
	public static interface AutoAdd {

		public StringAdd charAdd(PC_GresTextEditMultiline te, char c, Keyword kw, int blocks, String textBevore,
				String textBehind);

	}

	public class TextFile {
		private Line[] lines = { new Line("", null, 0) };

		public TextFile(String s) {
			addString(s, 0, 0, 0, 0);
		}

		public TextFile addString(String s, int line1, int pos1, int line2, int pos2) {
			if (line1 > line2) {
				int tmp = line1;
				line1 = line2;
				line2 = tmp;
				tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			} else if (line1 == line2 && pos1 > pos2) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			if (line1 < 0) {
				line1 = 0;
				pos1 = 0;
			}
			if (line2 >= getNumLines()) {
				line2 = getNumLines() - 1;
				pos2 = lines[line2].toString().length();
			}
			if (line1 < getNumLines() && line2 >= 0) {
				String sl = lines[line1].toString().substring(0, pos1);
				String sr = lines[line2].toString().substring(pos2);
				String[] se = (sl + s + sr + br + " ").split("" + br);
				int numLines = line1 + se.length - 1 + lines.length - line2 - 1;
				Line[] newLines = new Line[numLines];
				int i = 0;
				Keyword notEndedKeywordBevore = null;
				int notEndedBlockBevore = 0;
				Keyword kwbev = null;
				int bbev = 0;
				if (line1 > 0) {
					notEndedKeywordBevore = lines[line1 - 1].getNotEndedKeyword();
					notEndedBlockBevore = lines[line1 - 1].getNotEndedBlock();
				}
				for (; i < line1; i++)
					newLines[i] = lines[i];
				for (int j = 0; i < line1 + se.length - 1; i++, j++) {
					newLines[i] = new Line(se[j], notEndedKeywordBevore, notEndedBlockBevore);
					notEndedKeywordBevore = newLines[i].getNotEndedKeyword();
					notEndedBlockBevore = newLines[i].getNotEndedBlock();
				}
				kwbev = lines[line2].getNotEndedKeyword();
				bbev = lines[line2].getNotEndedBlock();
				for (int j = line2; i < numLines; i++, j++) {
					if (kwbev != notEndedKeywordBevore || bbev != notEndedBlockBevore) {
						kwbev = lines[j + 1].getNotEndedKeyword();
						bbev = lines[j + 1].getNotEndedBlock();
						notEndedKeywordBevore = lines[j + 1].update(notEndedKeywordBevore, notEndedBlockBevore);
						notEndedBlockBevore = lines[j + 1].getNotEndedBlock();
					}
					newLines[i] = lines[j + 1];
				}
				lines = newLines;
			}
			return this;
		}

		public void clear() {
			lines = new Line[] { new Line("", null, 0) };
		}

		public void drawCharAt(int x, int y, char c, int color) {
			drawStringColor("" + c, x, y, color);
		}

		public int getBlocksForLine(int line) {
			if (line >= 0 && line < getNumLines()) {
				return lines[line].getNotEndedBlock();
			}
			return 0;
		}

		public Keyword getKeywordForChar(int line, int pos) {
			if (line >= 0 && line < getNumLines()) {
				return lines[line].getKeywordForChar(pos);
			}
			return null;
		}

		public String getLine(int line) {
			if (line >= 0 && line < getNumLines())
				return lines[line].toString();
			return "";
		}

		public int getNumLines() {
			return lines.length;
		}

		public String getString(int line1, int pos1, int line2, int pos2) {
			if (line1 > line2) {
				int tmp = line1;
				line1 = line2;
				line2 = tmp;
				tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			} else if (line1 == line2 && pos1 > pos2) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			String s = "";
			if (line1 < 0) {
				line1 = 0;
				pos1 = 0;
			}
			if (line2 >= getNumLines()) {
				line2 = getNumLines() - 1;
				pos2 = lines[line2].toString().length();
			}
			if (line1 < getNumLines() && line2 >= 0) {
				if (line1 == line2) {
					return lines[line1].getString().substring(pos1, pos2);
				} else {
					s = lines[line1].getString().substring(pos1) + br;
					for (int i = line1 + 1; i < line2; i++)
						s += lines[i].getString() + br;
					s += lines[line2].getString().substring(0, pos2);
				}
			}
			return s;
		}

		public void render(PC_VecI offsetPos) {
			offsetPos = offsetPos.copy();
			offsetPos.x += pos.x + 6;
			offsetPos.y += pos.y + 6;
			for (int i = 0; i < shownLines() && i + scroll.y < lines.length; i++) {
				lines[i + scroll.y].render(offsetPos);
				offsetPos.y += getFontRenderer().FONT_HEIGHT;
			}
		}

		public TextFile setString(String s, int line) {
			if (line >= 0 && line < getNumLines()) {
				Keyword notEndedKeywordBevore = null;
				Keyword kwbev = null;
				int notEndedBlockBevore = 0;
				int bbev = 0;
				if (line > 0) {
					notEndedKeywordBevore = lines[line - 1].getNotEndedKeyword();
					notEndedBlockBevore = lines[line - 1].getNotEndedBlock();
				}
				kwbev = lines[line].getNotEndedKeyword();
				bbev = lines[line].getNotEndedBlock();
				notEndedKeywordBevore = lines[line].setString(s, notEndedKeywordBevore, notEndedBlockBevore);
				notEndedBlockBevore = lines[line].getNotEndedBlock();
				if (kwbev != notEndedKeywordBevore || bbev != notEndedBlockBevore)
					update(line + 1);
			}
			return this;
		}

		@Override
		public String toString() {
			String s1 = "";
			String s2 = "";
			for (int i = 0; i < getNumLines(); i++) {
				s1 += getLine(i).toString();
				s2 = s1;
				s1 += br;
			}
			return s2;
		}

		public void update() {
			Keyword notEndedKeywordBevore = null;
			int notEndedBlockBevore = 0;
			for (int i = 0; i < getNumLines(); i++) {
				notEndedKeywordBevore = lines[i].update(notEndedKeywordBevore, notEndedBlockBevore);
				notEndedBlockBevore = lines[i].getNotEndedBlock();
			}
		}

		public void update(int line) {
			if (line >= 0 && line < getNumLines()) {
				Keyword notEndedKeywordBevore = null;
				Keyword kwbev = null;
				int notEndedBlockBevore = 0;
				int bbev = 0;
				if (line > 0) {
					notEndedKeywordBevore = lines[line - 1].getNotEndedKeyword();
					notEndedBlockBevore = lines[line - 1].getNotEndedBlock();
				}
				kwbev = lines[line].getNotEndedKeyword();
				bbev = lines[line].getNotEndedBlock();
				notEndedKeywordBevore = lines[line].update(notEndedKeywordBevore, notEndedBlockBevore);
				notEndedBlockBevore = lines[line].getNotEndedBlock();
				if (kwbev != notEndedKeywordBevore || bbev != notEndedBlockBevore)
					update(line + 1);
			}
		}
	}

	/**
	 * Keyword colouring settings
	 * 
	 * @author XOR19
	 */
	public static class Keyword implements Serializable {
		/** the keyword */
		public String word;

		/** the end */
		public String end;

		/** rgb color */
		public int color;

		public boolean openBlock;

		public boolean closeBlock;

		public int nextWordKeywordColor;
		/** flag that this word uses a regular expresison for matching. */
		public boolean isRegexp;

		/**
		 * Keyword coloring of plain word
		 * 
		 * @param word  keyword string
		 * @param color color
		 */
		public Keyword(String word, int color) {
			this.word = word;
			this.color = color;
		}

		/**
		 * keyword coloring
		 * 
		 * @param word   the word string or regexp pattern
		 * @param color  color
		 * @param regexp is string a regexp pattern?
		 */
		public Keyword(String word, int color, boolean regexp) {
			this.word = word;
			this.color = color;
			this.isRegexp = regexp;
		}

		public Keyword(String word, int color, boolean regexp, boolean c, boolean d) {
			this.word = word;
			this.color = color;
			this.isRegexp = regexp;
			this.openBlock = c;
			this.closeBlock = d;
		}

		/**
		 * keyword coloring
		 * 
		 * @param word          the word string or regexp pattern
		 * @param color         color
		 * @param regexp        is string a regexp pattern?
		 * @param nextWordColor color
		 */
		public Keyword(String word, int color, boolean regexp, int nextWordKeywordColor) {
			this.word = word;
			this.color = color;
			this.isRegexp = regexp;
			this.nextWordKeywordColor = nextWordKeywordColor;
		}

		/**
		 * keyword coloring - sequence
		 * 
		 * @param start  the string the sequence starts with
		 * @param end    the end of the sequence
		 * @param color  color
		 * @param regexp is string a regexp pattern?
		 */
		public Keyword(String start, String end, int color, boolean regexp) {
			this.word = start;
			this.end = end;
			this.color = color;
			this.isRegexp = regexp;
		}
	}

	public class Line {
		private class NextText {
			String text;
			int type;
			int space;
			int size;
		}

		private LineChar[] line;
		private Keyword notEndedKeyword;
		private int notEndedBlock;

		public Line(String s, Keyword notEndedKeywordBevore, int notEndedBlockBevore) {
			setString(s, notEndedKeywordBevore, notEndedBlockBevore);
		}

		public Keyword getKeyword(String word) {
			if (keyWords == null)
				return null;
			for (Keyword kw : keyWords) {
				if (!kw.isRegexp && word.equals(kw.word))
					return kw;
				if (kw.isRegexp && word.matches(kw.word))
					return kw;
			}
			return null;
		}

		public Keyword getKeywordForChar(int i) {
			if (i >= 0 && i < line.length)
				return line[i].kw;
			if (i >= line.length)
				return notEndedKeyword;
			return null;
		}

		public NextText getNextText(String s) {
			NextText ret = new NextText();
			ret.text = "";
			ret.type = 0;
			ret.size = 0;
			ret.space = 0;
			char c;
			for (int i = 0; i < s.length(); i++) {
				c = s.charAt(i);
				if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
					if (ret.type == 0)
						ret.type = 1;
					if (ret.type != 1)
						return ret;
				} else if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
					if (ret.type == 0)
						ret.type = 2;
					if (ret.type != 2)
						return ret;
				} else {
					if (ret.type != 0)
						return ret;
				}
				if (ret.type != 0) {
					ret.text += c;
					ret.size++;
				} else
					ret.space++;
			}
			return ret;
		}

		public int getNotEndedBlock() {
			return notEndedBlock;
		}

		public Keyword getNotEndedKeyword() {
			return notEndedKeyword;
		}

		public String getString() {
			String s = "";
			for (int i = 0; i < line.length; i++)
				s += line[i].c;
			return s;
		}

		public void render(PC_VecI offsetPos) {
			offsetPos = offsetPos.copy();
			char c;
			int xV = 0;
			int charWidth = 0;
			for (int i = 0; i < line.length; i++) {
				c = line[i].c;
				charWidth = getFontRenderer().getCharWidth(c);
				if (xV >= scroll.x) {
					if (xV + charWidth > size.x + scroll.x - 24)
						return;
					if (c != '\t')
						drawStringColor("" + c, offsetPos.x + xV - scroll.x, offsetPos.y, fg != null ? 0xff000000 | fg
								: line[i].kw == null ? 0xff000000 | colorDefault : line[i].kw.color);
				}
				if (c == '\t')
					xV = (xV / 10 + 1) * 10;
				else
					xV += charWidth;
			}
		}

		public Keyword setString(String s, Keyword notEndedKeywordBevore, int notEndedBlockBevore) {
			line = new LineChar[s.length()];
			for (int i = 0; i < s.length(); i++)
				line[i] = new LineChar(s.charAt(i));
			return update(notEndedKeywordBevore, notEndedBlockBevore);
		}

		@Override
		public String toString() {
			return getString();
		}

		public Keyword update(Keyword notEndedKeywordBevore, int notEndedBlockBevore) {
			String s = getString();
			String ending = "";
			String eqal;
			char c;
			NextText text;
			int color;
			notEndedKeyword = notEndedKeywordBevore;
			notEndedBlock = notEndedBlockBevore;
			if (notEndedKeyword != null) {
				ending = notEndedKeyword.end;
			}
			for (int i = 0; i < s.length(); i++) {
				if (notEndedKeyword != null) {
					line[i].kw = notEndedKeyword;
					if (i + ending.length() <= s.length()) {
						eqal = s.substring(i, i + ending.length());
						if (!notEndedKeyword.isRegexp && eqal.equals(ending)) {
							for (int n = 0; n < ending.length() - 1; n++) {
								i++;
								line[i].kw = notEndedKeyword;
							}
							notEndedKeyword = null;
						} else if (notEndedKeyword.isRegexp && eqal.matches(ending)) {
							for (int n = 0; n < ending.length() - 1; n++) {
								i++;
								line[i].kw = notEndedKeyword;
							}
							notEndedKeyword = null;
						}
					}
				} else {
					text = getNextText(s.substring(i));
					if (text.type == 1) {
						Keyword kw = getKeyword(text.text);
						i += text.space;
						for (int j = 0; j < text.size; j++) {
							line[i].kw = kw;
							i++;
						}
						if (kw != null) {
							if (kw.openBlock)
								notEndedBlock++;
							if (kw.closeBlock)
								notEndedBlock--;
							if (notEndedBlock < 0)
								notEndedBlock = 0;
						}
						i--;
					} else if (text.type == 2) {
						i += text.space;
						int j = 0;
						for (; j < text.text.length(); j++) {
							if (notEndedKeyword != null) {
								break;
							}
							for (int l = text.text.length(); l > j; l--) {
								Keyword kw = getKeyword(text.text.substring(j, l));
								if (kw != null) {
									for (int p = j; p < l; p++) {
										line[i + p].kw = kw;
									}
									j = l - 1;

									if (kw.end != null) {
										notEndedKeyword = kw;
										ending = notEndedKeyword.end;
									}
									if (kw.openBlock)
										notEndedBlock++;
									if (kw.closeBlock)
										notEndedBlock--;
									if (notEndedBlock < 0)
										notEndedBlock = 0;

									break;
								}
							}
						}
						i += j;
						i--;
					}
				}
			}
			if (notEndedKeyword != null) {
				ending = notEndedKeyword.end;
				eqal = "" + br;
				if (!notEndedKeyword.isRegexp && eqal.equals(ending))
					notEndedKeyword = null;
				else if (notEndedKeyword.isRegexp && eqal.matches(ending))
					notEndedKeyword = null;
			}
			return notEndedKeyword;
		}
	}

	public static class LineChar {
		public char c;
		public Keyword kw;

		public LineChar(char c) {
			this.c = c;
		}
	}

	public static class StringAdd {
		public String addString;

		public boolean jumpToEnd;

		public StringAdd(String addString, boolean jumpToEnd) {
			this.addString = addString;
			this.jumpToEnd = jumpToEnd;
		}
	}

	private static final char br = '\n';

	/**
	 * static version of getFontRenderer
	 * 
	 * @return font renderer
	 */
	public static FontRenderer getFR() {
		return mc.fontRenderer;
	}

	private PC_VecI lastMousePosition = new PC_VecI(0, 0);
	private PC_VecI mouseSelectStart = new PC_VecI(0, 0);
	private PC_VecI mouseSelectEnd = new PC_VecI(0, 0);
	private int mousePressed = 0;
	private PC_VecI scroll = new PC_VecI(0, 0);
	private List<Keyword> keyWords = null;

	public void setKeywords(List<Keyword> kw) {
		this.keyWords = kw;
		text.update();
	}

	// private ArrayList<Keyword> oneFrameKeyWords = new ArrayList<Keyword>();
	// private ArrayList<Keyword> newOneFrameKeyWords;
	private AutoAdd autoAdd = null;
	private int vScrollSize = 0, hScrollSize = 0;
	private float vScrollPos = 0, hScrollPos = 0;

	// private int nextWordKeywordColor = 0;

	private TextFile text;
	private Integer bg = null, fg = null;
	private int colorDefault;
	private int colorBackground;

	public PC_GresTextEditMultiline setBgColor(int color) {
		bg = color;
		return this;
	}

	public PC_GresTextEditMultiline setFgColor(int color) {
		this.color[textColorEnabled] = 0xff000000 | color;
		this.color[textColorClicked] = 0xff000000 | color;
		this.color[textColorHover] = 0xff000000 | color;
		this.color[textColorDisabled] = 0xff000000 | color;
		fg = color;
		return this;
	}

	/**
	 * Multi-row text edit
	 * 
	 * @param text      initial text
	 * @param minWidth  width
	 * @param minHeight height
	 */
	public PC_GresTextEditMultiline(String text, int minWidth, int minHeight, int colorDefault, int colorBackground) {
		super(minWidth > 20 ? minWidth : 20,
				minHeight > getFR().FONT_HEIGHT + 26 ? minHeight : getFR().FONT_HEIGHT + 26, "");
		canAddWidget = false;
		this.colorDefault = colorDefault;
		this.colorBackground = colorBackground;
		color[textColorEnabled] = 0xff000000 | colorDefault;
		color[textColorShadowEnabled] = 0; // 0xff383838;
		color[textColorClicked] = 0xff000000 | colorDefault;
		color[textColorHover] = 0xff000000 | colorDefault;
		color[textColorDisabled] = 0xff000000 | colorDefault;
		color[textColorShadowDisabled] = 0; // 0xff383838;
		this.text = new TextFile(text);
	}

	/**
	 * Multi-row text edit
	 * 
	 * @param text      initial text
	 * @param minWidth  width
	 * @param minHeight height
	 * @param keyWords  list of keywords
	 */
	public PC_GresTextEditMultiline(String text, int minWidth, int minHeight, int colorDefault, int colorBackground,
			List<Keyword> keyWords) {
		super(minWidth > 20 ? minWidth : 20,
				minHeight > getFR().FONT_HEIGHT + 26 ? minHeight : getFR().FONT_HEIGHT + 26, "");
		canAddWidget = false;
		this.colorDefault = colorDefault;
		this.colorBackground = colorBackground;
		color[textColorEnabled] = 0xff000000 | colorDefault;
		color[textColorShadowEnabled] = 0; // 0xff383838;
		color[textColorClicked] = 0xff000000 | colorDefault;
		color[textColorHover] = 0xff000000 | colorDefault;
		color[textColorDisabled] = 0xff000000 | colorDefault;
		color[textColorShadowDisabled] = 0; // 0xff383838;
		this.keyWords = keyWords;
		this.text = new TextFile(text);
	}

	/**
	 * Multi-row text edit
	 * 
	 * @param text      initial text
	 * @param minWidth  width
	 * @param minHeight height
	 * @param keyWords  list of keywords
	 * @param autoAdd   autoAdd function
	 */
	public PC_GresTextEditMultiline(String text, int minWidth, int minHeight, int colorDefault, int colorBackground,
			List<Keyword> keyWords, AutoAdd autoAdd) {
		super(minWidth > 20 ? minWidth : 20,
				minHeight > getFR().FONT_HEIGHT + 26 ? minHeight : getFR().FONT_HEIGHT + 26, "");
		canAddWidget = false;
		this.colorDefault = colorDefault;
		this.colorBackground = colorBackground;
		color[textColorEnabled] = 0xff000000 | colorDefault;
		color[textColorShadowEnabled] = 0; // 0xff383838;
		color[textColorClicked] = 0xff000000 | colorDefault;
		color[textColorHover] = 0xff000000 | colorDefault;
		color[textColorDisabled] = 0xff000000 | colorDefault;
		color[textColorShadowDisabled] = 0; // 0xff383838;
		this.keyWords = keyWords;
		this.autoAdd = autoAdd;
		this.text = new TextFile(text);
	}

	private int _getStringWidth(String text) {
		int l = 0;
		char c;
		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			if (c == '\t')
				l = (l / 10 + 1) * 10;
			else
				l += getFontRenderer().getCharWidth(c);
		}
		return l;
	}

	@Override
	public void addedToWidget() {
	}

	/**
	 * Replace selection with given key
	 * 
	 * @param c key
	 */
	public void addKey(char c) {
		// text.addString(""+c, mouseSelectStart.y, mouseSelectStart.x,
		// mouseSelectEnd.y, mouseSelectEnd.x);
		// moveCursor(1);
		deleteSelected();

		String s = text.getString(mouseSelectEnd.y, mouseSelectEnd.x, text.getNumLines(), 0);
		StringAdd sa = null;
		if (autoAdd != null) {

			sa = autoAdd.charAdd(this, c, text.getKeywordForChar(mouseSelectEnd.y, mouseSelectEnd.x),
					text.getBlocksForLine(mouseSelectEnd.y), text.getString(0, 0, mouseSelectEnd.y, mouseSelectEnd.x),
					s);
		}
		if (sa == null) {
			text.addString("" + c, mouseSelectEnd.y, mouseSelectEnd.x, mouseSelectEnd.y, mouseSelectEnd.x);
			moveCursor(1);
		} else {
			text.addString(c + sa.addString, mouseSelectEnd.y, mouseSelectEnd.x, mouseSelectEnd.y, mouseSelectEnd.x);
			moveCursor(1);
			if (sa.jumpToEnd)
				moveCursor(sa.addString.length());
		}
		mouseSelectStart.setTo(mouseSelectEnd);
	}

	@Override
	public void calcChildPositions() {
	}

	private void calcScrollPosition() {

		int sizeX = size.x - 12;
		int maxSizeX = getMaxLineLength();
		int sizeOutOfFrame = maxSizeX - sizeX + 14;
		if (sizeOutOfFrame < 0) {
			sizeOutOfFrame = 0;
		}
		float prozent = maxSizeX > 0 ? ((float) sizeOutOfFrame / maxSizeX) : 0;
		hScrollPos = (sizeOutOfFrame > 0 ? (float) scroll.x / sizeOutOfFrame : 0) * prozent * sizeX;
		hScrollSize = (int) ((1 - prozent) * sizeX + 0.5);

		int lineNumbers = getLineNumbers();
		int linesNotToSee = lineNumbers - shownLines();
		if (linesNotToSee < 0) {
			linesNotToSee = 0;
		}

		prozent = lineNumbers > 0 ? ((float) linesNotToSee / lineNumbers) : 0;
		int sizeY = size.y - 12;
		vScrollPos = (linesNotToSee > 0 ? (float) scroll.y / linesNotToSee : 0) * prozent * sizeY;
		vScrollSize = (int) ((1 - prozent) * sizeY + 0.5);

		updateScrollPosition();
	}

	@Override
	public PC_VecI calcSize() {
		return size;
	}

	private boolean coordsInDrawRect(PC_VecI c) {
		if (!yCoordsInDrawRect(c.y)) {
			return false;
		}
		int cx = _getStringWidth(getLine(c.y).substring(0, c.x));
		return cx >= scroll.x && cx < scroll.x + size.x - 26;
	}

	private void deleteSelected() {
		text.addString("", mouseSelectStart.y, mouseSelectStart.x, mouseSelectEnd.y, mouseSelectEnd.x);
		if (mouseSelectStart.y > mouseSelectEnd.y) {
			mouseSelectStart.setTo(mouseSelectEnd);
		} else if (mouseSelectStart.y == mouseSelectEnd.y && mouseSelectStart.x > mouseSelectEnd.x) {
			mouseSelectStart.setTo(mouseSelectEnd);
		} else
			mouseSelectEnd.setTo(mouseSelectStart);
	}

	private void drawSelect(PC_VecI offsetPos, int sx, int ex, int y) {
		if (!yCoordsInDrawRect(y)) {
			return;
		}
		String line = getLine(y);
		if (sx < 0) {
			sx = 0;
		}
		if (ex < 0) {
			ex = line.length();
		}
		int cy = y - scroll.y;
		int sxx = _getStringWidth(line.substring(0, sx)) - scroll.x;
		int exx = _getStringWidth(line.substring(0, ex)) - scroll.x;
		if (sxx < 0) {
			sxx = 0;
		} else if (sxx > size.x - 24) {
			return;
		}
		if (exx < 0) {
			return;
		} else if (exx > size.x - 24) {
			exx = size.x - 24;
		}

		drawRect(offsetPos.x + pos.x + sxx + 6, offsetPos.y + pos.y + 6 + cy * getFR().FONT_HEIGHT,
				offsetPos.x + pos.x + exx + 6, offsetPos.y + pos.y + 6 + (cy + 1) * getFR().FONT_HEIGHT, 0xff3399FF);
	}

	@Override
	public FontRenderer getFontRenderer() {
		return getFR();
	}

	private String getLine(int line) {
		return text.getLine(line);
	}

	private int getLineNumbers() {
		return text.getNumLines();
	}

	private int getMaxLineLength() {
		int maxLength = 0, length = 0;
		for (int i = 0; i < getLineNumbers(); i++) {
			length = _getStringWidth(getLine(i));
			if (length > maxLength) {
				maxLength = length;
			}
		}
		return maxLength;
	}

	@Override
	public PC_VecI getMinSize() {
		return calcSize();
	}

	private PC_VecI getMousePositionInString(PC_VecI pos) {
		int charSize;
		int row = scroll.y;
		PC_VecI coord = null;
		pos = pos.copy();
		pos.x -= 6;
		pos.y -= 6;
		row += pos.y < 0 ? -1 : pos.y / getFR().FONT_HEIGHT;
		pos.x += scroll.x;
		if (row < 0) {
			row = 0;
		}
		String rowText = getLine(row);
		int i = 0;
		for (; i < rowText.length(); i++) {
			charSize = _getStringWidth("" + rowText.charAt(i));
			if (pos.x - charSize / 2 < 0) {
				return new PC_VecI(i, row);
			}
			pos.x -= charSize;
		}
		return new PC_VecI(i, row);
	}

	private String getSelect() {
		return text.getString(mouseSelectStart.y, mouseSelectStart.x, mouseSelectEnd.y, mouseSelectEnd.x);
	}

	@Override
	public String getText() {
		return text.toString();
	}

	private void key_backspace() {
		if ((mouseSelectStart.x == mouseSelectEnd.x && mouseSelectStart.y == mouseSelectEnd.y))
			moveCursor(-1);
		deleteSelected();
	}

	private void key_delete() {
		if ((mouseSelectStart.x == mouseSelectEnd.x && mouseSelectStart.y == mouseSelectEnd.y))
			moveCursor(1);
		if ((mouseSelectStart.x == mouseSelectEnd.x && mouseSelectStart.y == mouseSelectEnd.y))
			moveCursor(-1);
		deleteSelected();
	}

	@Override
	public boolean keyTyped(char c, int key) {
		int p;
		if (!enabled || !hasFocus || (mousePressed != 0 && mousePressed != 1)) {
			return false;
		}
		switch (c) {
		case 3:
			GuiScreen.setClipboardString(getSelect());
			setScrollToCursor();
			return true;

		case 22:
			setSelected(GuiScreen.getClipboardString());
			setScrollToCursor();
			return true;

		case 24:
			GuiScreen.setClipboardString(getSelect());
			deleteSelected();
			setScrollToCursor();
			return true;
		}
		switch (key) {
		case Keyboard.KEY_RETURN:
			addKey(br);
			setScrollToCursor();
			return true;
		case Keyboard.KEY_BACK:
			key_backspace();
			setScrollToCursor();
			return true;
		case Keyboard.KEY_DELETE:
			key_delete();
			setScrollToCursor();
			return true;
		case Keyboard.KEY_LEFT:
			moveCursor(-1);
			if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				mouseSelectStart.setTo(mouseSelectEnd);
			}
			setScrollToCursor();
			return true;
		case Keyboard.KEY_RIGHT:
			moveCursor(1);
			if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				mouseSelectStart.setTo(mouseSelectEnd);
			}
			setScrollToCursor();
			return true;
		case Keyboard.KEY_UP:
			mouseSelectEnd.setTo(lineUp());
			if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				mouseSelectStart.setTo(mouseSelectEnd);
			}
			setScrollToCursor();
			return true;
		case Keyboard.KEY_DOWN:
			mouseSelectEnd.setTo(lineDown());
			if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				mouseSelectStart.setTo(mouseSelectEnd);
			}
			setScrollToCursor();
			return true;
		default:
			if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '\t') {
				addKey(c);
				setScrollToCursor();
				return true;
			}
			return false;
		}
	}

	private PC_VecI lineDown() {
		int xP = _getStringWidth(getLine(mouseSelectEnd.y).substring(0, mouseSelectEnd.x));
		String rowText = getLine(mouseSelectEnd.y + 1);
		int charSize;
		PC_VecI coord = null;
		for (int i = 0; i < rowText.length(); i++) {
			charSize = _getStringWidth("" + rowText.charAt(i));
			if (xP - charSize / 2 < 0) {
				coord = new PC_VecI(i, mouseSelectEnd.y + 1);
				break;
			}
			xP -= charSize;
		}
		return coord == null ? new PC_VecI(rowText.length(), mouseSelectEnd.y + 1) : coord;
	}

	private PC_VecI lineUp() {
		if (mouseSelectEnd.y <= 0) {
			return new PC_VecI(0, 0);
		}
		int xP = _getStringWidth(getLine(mouseSelectEnd.y).substring(0, mouseSelectEnd.x));
		String rowText = getLine(mouseSelectEnd.y - 1);
		int charSize;
		PC_VecI coord = null;
		for (int i = 0; i < rowText.length(); i++) {
			charSize = _getStringWidth("" + rowText.charAt(i));
			if (xP - charSize / 2 < 0) {
				coord = new PC_VecI(i, mouseSelectEnd.y - 1);
				break;
			}
			xP -= charSize;
		}
		return coord == null ? new PC_VecI(rowText.length(), mouseSelectEnd.y - 1) : coord;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		mousePressed = 0;
		lastMousePosition.setTo(mousePos);
		if (key != -1) {
			if (enabled && mousePos.x < size.x - 12 && mousePos.y < size.y - 12) {
				mouseSelectEnd.setTo(getMousePositionInString(mousePos));
				moveCursor(0);
				if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					mouseSelectStart.setTo(mouseSelectEnd);
				}
				mousePressed = 1;
				setScrollToCursor();
				return true;
			} else if (mousePos.y < size.y - 12) {
				if (mousePos.y - 1 < vScrollPos) {
					scroll.y--;
					return true;
				}
				if (mousePos.y - 1 >= vScrollPos + vScrollSize) {
					scroll.y++;
					return true;
				}
				mousePressed = 2;
				return true;
			} else if (mousePos.x < size.x - 12) {
				if (mousePos.x - 1 < hScrollPos) {
					scroll.x -= 5;
					return true;
				}
				if (mousePos.x - 1 >= hScrollPos + hScrollSize) {
					scroll.x += 5;
					return true;
				}
				mousePressed = 3;
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		switch (mousePressed) {
		case 1:
			mouseSelectEnd.setTo(getMousePositionInString(mousePos));
			setScrollToCursor();
			break;
		case 2:
			vScrollPos += mousePos.y - lastMousePosition.y;
			updateScrollPosition();
			break;
		case 3:
			hScrollPos += mousePos.x - lastMousePosition.x;
			updateScrollPosition();
			break;
		}
		lastMousePosition.setTo(mousePos);
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		return MouseOver.THIS;
	}

	public void scrollToBottom() {
		int maxY = getLineNumbers() - shownLines() + 1;
		if (maxY < 0) {
			maxY = 0;
		}
		scroll.y = maxY;
	}

	@Override
	public void mouseWheel(int i) {
		scroll.y -= i * 3;
		if (scroll.y < 0) {
			scroll.y = 0;
		}
		int maxY = getLineNumbers() - shownLines() + 1;
		if (maxY < 0) {
			maxY = 0;
		}
		if (scroll.y > maxY) {
			scroll.y = maxY;
		}
	}

	public void moveCursor(int x) {
		int xPos = mouseSelectEnd.x + x;
		int yPos = mouseSelectEnd.y;
		if (xPos < 0) {
			yPos--;
			xPos = getLine(yPos).length();
		}
		if (xPos > getLine(yPos).length()) {
			xPos = 0;
			yPos++;
		}
		if (yPos < 0) {
			yPos = 0;
			xPos = 0;
		}
		if (yPos >= getLineNumbers()) {
			yPos = getLineNumbers() - 1;
			xPos = getLine(yPos).length();
		}
		mouseSelectEnd.setTo(xPos, yPos, 0);
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, 0xffA0A0A0);
		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 12, offsetPos.y + pos.y + size.y - 1,
				0xffA0A0A0);

		drawVerticalLine(offsetPos.x + pos.x, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1, 0xffA0A0A0);
		drawVerticalLine(offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 12,
				0xffA0A0A0);

		drawRect(offsetPos.x + pos.x + 1, offsetPos.y + pos.y + 1, offsetPos.x + pos.x + size.x - 12,
				offsetPos.y + pos.y + size.y - 12, 0xff000000 | (bg != null ? bg : colorBackground));

		int scrollbarBg = 0x909090;

		drawRect(offsetPos.x + pos.x + 1, offsetPos.y + pos.y + size.y - 11, offsetPos.x + pos.x + size.x - 12,
				offsetPos.y + pos.y + size.y - 1, scrollbarBg);

		drawRect(offsetPos.x + pos.x + size.x - 11, offsetPos.y + pos.y + 1, offsetPos.x + pos.x + size.x - 1,
				offsetPos.y + pos.y + size.y - 12, scrollbarBg);

		drawHorizontalLine(offsetPos.x + pos.x, offsetPos.x + pos.x + size.x - 1, offsetPos.y + pos.y + size.y - 12,
				0xffA0A0A0);

		drawVerticalLine(offsetPos.x + pos.x + size.x - 12, offsetPos.y + pos.y, offsetPos.y + pos.y + size.y - 1,
				0xffA0A0A0);

		if (mousePressed == 0 || mousePressed == 1) {
			calcScrollPosition();
		}

		renderTextureSliced(offsetPos.copy().add((int) hScrollPos + 1, size.y - 11, 0), imgdir + "scrollbar_handle.png",
				new PC_VecI(hScrollSize - 1, 10), new PC_VecI(0, 0), new PC_VecI(256, 256), new PC_RectI(1, 1, 1, 1));

		renderTextureSliced(offsetPos.copy().add(size.x - 11, 1 + (int) vScrollPos, 0), imgdir + "scrollbar_handle.png",
				new PC_VecI(10, vScrollSize - 1), new PC_VecI(0, 0), new PC_VecI(256, 256), new PC_RectI(1, 1, 1, 1));

		if ((!(mouseSelectStart.x == mouseSelectEnd.x && mouseSelectStart.y == mouseSelectEnd.y)) && hasFocus) {
			PC_VecI cs = mouseSelectStart, ce = mouseSelectEnd;
			if (mouseSelectStart.y > mouseSelectEnd.y
					|| (mouseSelectStart.y == mouseSelectEnd.y && mouseSelectStart.x > mouseSelectEnd.x)) {
				cs = mouseSelectEnd;
				ce = mouseSelectStart;
			}

			if (mouseSelectStart.y == mouseSelectEnd.y) {
				if (yCoordsInDrawRect(mouseSelectStart.y)) {
					drawSelect(offsetPos, cs.x, ce.x, cs.y);
				}
			} else {
				if (yCoordsInDrawRect(cs.y)) {
					drawSelect(offsetPos, cs.x, -1, cs.y);
				}
				for (int i = cs.y + 1; i < ce.y; i++) {
					if (yCoordsInDrawRect(i)) {
						drawSelect(offsetPos, 0, -1, i);
					}
				}
				if (yCoordsInDrawRect(ce.y)) {
					drawSelect(offsetPos, 0, ce.x, ce.y);
				}
			}

		}

		text.render(offsetPos);

		if (enabled && hasFocus && (cursorCounter / 6) % 2 == 0) {
			if (coordsInDrawRect(new PC_VecI(mouseSelectEnd.x > 0 ? mouseSelectEnd.x - 1 : 0, mouseSelectEnd.y))) {
				try {
					drawVerticalLine(offsetPos.x + pos.x
							+ _getStringWidth(getLine(mouseSelectEnd.y).substring(0, mouseSelectEnd.x)) + 5 - scroll.x,
							offsetPos.y + pos.y + 6 + (mouseSelectEnd.y - scroll.y) * getFR().FONT_HEIGHT,
							offsetPos.y + pos.y + 6 + (mouseSelectEnd.y - scroll.y + 1) * getFR().FONT_HEIGHT,
							color[enabled ? textColorEnabled : textColorDisabled]);
				} catch (StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private void setScrollToCursor() {
		moveCursor(0);
		int cy = mouseSelectEnd.y - scroll.y;
		if (cy < 0) {
			scroll.y = mouseSelectEnd.y;
		} else if (cy >= shownLines()) {
			scroll.y = mouseSelectEnd.y - shownLines() + 1;
		}
		String line = getLine(mouseSelectEnd.y);
		int cxs = _getStringWidth(line.substring(0, mouseSelectEnd.x));
		int cxb = mouseSelectEnd.x > 0 ? _getStringWidth(line.substring(mouseSelectEnd.x - 1, mouseSelectEnd.x)) : 0;
		int cx = cxs - scroll.x;
		if (cx <= cxb) {
			scroll.x = cxs - cxb;
		} else if (cx >= size.x - 25) {
			scroll.x = cxs - size.x + 27;
		}
	}

	private void setSelected(String stri) {
		if (mouseSelectStart.y > mouseSelectEnd.y) {
			PC_VecI tmp = mouseSelectStart;
			mouseSelectStart = mouseSelectEnd;
			mouseSelectEnd = tmp;
		} else if (mouseSelectStart.y == mouseSelectEnd.y && mouseSelectStart.x > mouseSelectEnd.x) {
			PC_VecI tmp = mouseSelectStart;
			mouseSelectStart = mouseSelectEnd;
			mouseSelectEnd = tmp;
		}
		text.addString(stri, mouseSelectStart.y, mouseSelectStart.x, mouseSelectEnd.y, mouseSelectEnd.x);
		moveCursorJumping(stri.length());

		mouseSelectEnd.setTo(mouseSelectStart);
	}

	private void moveCursorJumping(int x) {
		int yPos = mouseSelectStart.y;
		x += mouseSelectStart.x;
		while (x > getLine(yPos).length()) {
			x -= getLine(yPos).length() + 1;
			yPos++;
		}
		int xPos = x;
		mouseSelectStart.setTo(xPos, yPos, 0);
	}

	@Override
	public PC_GresWidget setText(String text) {
		this.text.clear();
		this.text.addString(text, 0, 0, 0, 0);
		return this;
	}

	private int shownLines() {
		return (size.y - 26) / getFR().FONT_HEIGHT;
	}

	private void updateScrollPosition() {

		int sizeX = size.x - 12;
		int maxSizeX = getMaxLineLength();
		int sizeOutOfFrame = maxSizeX - sizeX + 14;
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

		int sizeY = size.y - 12;
		int lineNumbers = getLineNumbers();
		int linesNotToSee = lineNumbers - shownLines();
		if (linesNotToSee < 0) {
			linesNotToSee = 0;
		}

		prozent = lineNumbers > 0 ? ((float) linesNotToSee / (lineNumbers)) : 0;
		if (vScrollPos < 0) {
			vScrollPos = 0;
		}
		if (vScrollPos > sizeY - vScrollSize) {
			vScrollPos = sizeY - vScrollSize;
		}
		scroll.y = (int) (vScrollPos / prozent / sizeY * linesNotToSee + 0.5);
	}

	private boolean yCoordsInDrawRect(int cy) {
		return cy >= scroll.y && cy < scroll.y + shownLines();
	}

	@Override
	public List<String> getTooltip(PC_VecI mousePos) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			return Arrays.asList("Line: " + (getMousePositionInString(mousePos).y + 1));
		return null;
	}

}
