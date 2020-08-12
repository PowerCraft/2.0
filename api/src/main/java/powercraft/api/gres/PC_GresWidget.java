package powercraft.api.gres;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import powercraft.api.PC_Lang;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.tileentity.PC_ITileEntityWatcher;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

/**
 * Base class for GUI-system
 * 
 * @authors XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public abstract class PC_GresWidget extends Gui implements PC_ITileEntityWatcher {

	/** zero coord */
	public static final PC_VecI zerosize = new PC_VecI(0, 0);

	/** Minecraft instance */
	protected static Minecraft mc = PC_ClientUtils.mc();

	protected static String imgdir = PC_TextureRegistry.getGresImgDir();

	protected enum MouseOver {
		NON, THIS, CHILD
	}

	/**
	 * align vertical
	 * 
	 * @authors XOR19 & Rapus95
	 * @copy (c) 2012
	 */
	public enum PC_GresAlign {
		/** LEFT */
		LEFT,
		/** RIGHT */
		RIGHT,
		/** TOP */
		TOP,
		/** BOTTOM */
		BOTTOM,
		/** CENTER */
		CENTER,
		/** STRETCH */
		STRETCH,
		/** JUSTIFIED */
		JUSTIFIED
	}

	@SuppressWarnings("javadoc")
	public static final int textColorEnabled = 0, textColorShadowEnabled = 1, textColorDisabled = 2,
			textColorShadowDisabled = 3, textColorHover = 4, textColorClicked = 5;

	/** Array of text colors */
	protected int color[] = { 0x000000, 0, 0x333333, 0, 0x000000, 0x000000 };

	/** Parent widget */
	protected PC_GresWidget parent = null;

	/** List of children */
	protected ArrayList<PC_GresWidget> childs = new ArrayList<PC_GresWidget>();

	protected ArrayList<PC_GresWidgetTab> tabs = new ArrayList<PC_GresWidgetTab>();

	/** Font renderer */
	protected FontRenderer fontRenderer = null;

	/** pos of left top corner */
	protected PC_VecI pos = new PC_VecI(0, 0);

	/** Widget size */
	protected PC_VecI size = new PC_VecI(0, 0);

	/** Minimal allowed widget size */
	protected PC_VecI minSize = new PC_VecI(0, 0);

	/** Distance from other widgets in group. */
	protected int widgetMargin = 4;

	/** Counter used for the automatic resizing */
	protected int cursorCounter = 0;

	/** Can add child widgets */
	protected boolean canAddWidget = true;

	/** Is mouse over this widget? */
	protected boolean isMouseOver = false;

	/** Is widget enabled = clickable */
	protected boolean enabled = true;

	/** Is widget focused (used mainly for text edits) */
	protected boolean hasFocus = false;

	/** Is visible */
	protected boolean visible = true;

	protected String textLangKey = "";
	protected String textValue = "";
	/** Widget's label (text in title or on button or whatever) */
	protected String text = "";

	/** Horizontal Align */
	protected PC_GresAlign alignH = PC_GresAlign.CENTER;

	/** Vertical Align */
	protected PC_GresAlign alignV = PC_GresAlign.CENTER;

	/** Container Manager */
	protected PC_IGresGui gui = null;

	/** Widget ID (general purpose) */
	public int id = -1;

	/** Additional widget tag (general purpose) */
	public String tag = "";

	protected String tooltipLangKey = "";
	protected String tooltipValue = "";
	protected String tooltip;

	protected String tileEnityObjectKey;

	/**
	 * A widget
	 */
	public PC_GresWidget() {
		PC_VecI minSize = getMinSize();
		this.size = minSize.copy();
		this.minSize = minSize.copy();
	}

	/**
	 * A widget
	 * 
	 * @param labelKey widget's label / text
	 */
	public PC_GresWidget(String labelKey) {
		setTextLangKey(labelKey);
		PC_VecI minSize = getMinSize();
		this.size = minSize.copy();
		this.minSize = minSize.copy();
	}

	/**
	 * A widget
	 * 
	 * @param labelKey   widget's label / text
	 * @param labelValue widget's label / text
	 */
	public PC_GresWidget(String labelKey, String langValue) {
		setText(labelKey, langValue);
		PC_VecI minSize = getMinSize();
		this.size = minSize.copy();
		this.minSize = minSize.copy();
	}

	/**
	 * A widget
	 * 
	 * @param width  widget minWidth
	 * @param height widget minHeight
	 */
	public PC_GresWidget(int width, int height) {
		PC_VecI minSize = new PC_VecI(width, height);
		this.size = minSize.copy();
		this.minSize = minSize.copy();
	}

	/**
	 * A widget
	 * 
	 * @param width  widget minWidth
	 * @param height widget minHeight
	 * @param label  widget label / text
	 */
	public PC_GresWidget(int width, int height, String labelKey) {
		this(width, height);
		setTextLangKey(labelKey);
	}

	/**
	 * A widget
	 * 
	 * @param width  widget minWidth
	 * @param height widget minHeight
	 * @param label  widget label / text
	 */
	public PC_GresWidget(int width, int height, String labelKey, String langValue) {
		this(width, height);
		setText(labelKey, langValue);
	}

	/**
	 * Set visibility. Invisible widgets dont take space in layouts. Same as css
	 * display:none
	 * 
	 * @param show flag visible
	 * @return this
	 */
	public PC_GresWidget setVisible(boolean show) {
		visible = show;
		updateVisible(show);
		return this;
	}

	private void updateVisible(boolean show) {
		visibleChanged(show);
		for (PC_GresWidget w : childs) {
			w.updateVisible(show);
		}
		for (PC_GresWidget w : tabs) {
			w.updateVisible(show);
		}
	}

	protected void visibleChanged(boolean show) {
	}

	public void setTileEnityObjectKey(String tileEnityObjectKey) {
		this.tileEnityObjectKey = tileEnityObjectKey;
	}

	/**
	 * @return true if is visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set widget ID
	 * 
	 * @param id
	 * @return this
	 */
	public PC_GresWidget setId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Get widget ID
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Get widget tag
	 * 
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Set widget tag
	 * 
	 * @param tag the tag
	 * @return this
	 */
	public PC_GresWidget setTag(String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Get widget tooltip
	 * 
	 * @return tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Set widget tooltip
	 * 
	 * @param tooltip the tooltip
	 * @return this
	 */
	public PC_GresWidget setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public PC_GresWidget setTooltip(String tooltipLangKey, String tooltipValue) {
		this.tooltipLangKey = tooltipLangKey;
		this.tooltipValue = tooltipValue;
		tooltip = PC_Lang.tr(tooltipLangKey, tooltipValue);
		return this;
	}

	public String getTooltipLangKey() {
		return tooltipLangKey;
	}

	public PC_GresWidget setTooltipLangKey(String tooltipLangKey) {
		this.tooltipLangKey = tooltipLangKey;
		tooltip = PC_Lang.tr(tooltipLangKey, tooltipValue);
		return this;
	}

	public String getTooltipValue() {
		return tooltipValue;
	}

	public PC_GresWidget setTooltipValue(String tooltipValue) {
		this.tooltipValue = tooltipValue;
		tooltip = PC_Lang.tr(tooltipLangKey, tooltipValue);
		return this;
	}

	/**
	 * @return widget's text / label
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set widget's label, resize if needed
	 * 
	 * @param text new text / label
	 * @return this
	 */
	public PC_GresWidget setText(String text) {
		this.text = text;
		return this;
	}

	public PC_GresWidget setText(String textLangKey, String textValue) {
		this.textLangKey = textLangKey;
		this.textValue = textValue;
		text = PC_Lang.tr(textLangKey, textValue);
		return this;
	}

	public String getTextLangKey() {
		return textLangKey;
	}

	public PC_GresWidget setTextLangKey(String textLangKey) {
		this.textLangKey = textLangKey;
		text = PC_Lang.tr(textLangKey, textValue);
		return this;
	}

	public String getTextValue() {
		return textValue;
	}

	public PC_GresWidget setTextValue(String textValue) {
		this.textValue = textValue;
		text = PC_Lang.tr(textLangKey, textValue);
		return this;
	}

	/**
	 * @return widget's font renderer
	 */
	public FontRenderer getFontRenderer() {
		if (fontRenderer == null) {
			return mc.fontRenderer;
		}
		return fontRenderer;
	}

	/**
	 * Set widget's font renderer
	 * 
	 * @param fontRenderer the font renderer
	 * @return this
	 */
	public PC_GresWidget setFontRenderer(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		for (PC_GresWidget w : childs) {
			w.setFontRenderer(fontRenderer);
		}
		for (PC_GresWidget w : tabs) {
			w.setFontRenderer(fontRenderer);
		}
		return this;
	}

	/**
	 * Get horizontal align
	 * 
	 * @return horizontal align
	 */
	public PC_GresAlign getAlignH() {
		return alignH;
	}

	/**
	 * Set horizontal align
	 * 
	 * @param alignHorizontal horizontal align
	 * @return this
	 */
	public PC_GresWidget setAlignH(PC_GresAlign alignHorizontal) {
		this.alignH = alignHorizontal;
		return this;
	}

	/**
	 * Get vertical align
	 * 
	 * @return vertical align
	 */
	public PC_GresAlign getAlignV() {
		return alignV;
	}

	/**
	 * Set vertical align
	 * 
	 * @param alignVertical vertical align
	 * @return this
	 */
	public PC_GresWidget setAlignV(PC_GresAlign alignVertical) {
		this.alignV = alignVertical;
		return this;
	}

	/**
	 * @return has focus
	 */
	public boolean getFocus() {
		return hasFocus;
	}

	/**
	 * Set focus state
	 * 
	 * @param focus focused
	 * @return this
	 */
	public PC_GresWidget setFocus(boolean focus) {
		this.hasFocus = focus;
		return this;
	}

	/**
	 * Increment cursor counter, used for text field animations
	 */
	public void updateCursorCounter() {
		cursorCounter++;
	}

	/**
	 * @return minimal size, {width,height}
	 */
	public PC_VecI getMinSize() {
		if (!visible)
			return new PC_VecI(0, 0);
		return calcSize().copy();
	}

	/**
	 * @param minSize the minSize to set
	 * @return this
	 */
	public PC_GresWidget setMinSize(PC_VecI minSize) {
		this.minSize = minSize;
		return this;
	}

	/**
	 * set min size
	 * 
	 * @param w width
	 * @param h height
	 * @return this
	 */
	public PC_GresWidget setMinSize(int w, int h) {
		this.minSize.setTo(w, h, 0);
		return this;
	}

	/**
	 * set min size width
	 * 
	 * @param w width
	 * @return this
	 */
	public PC_GresWidget setMinWidth(int w) {
		this.minSize.setTo(w, this.minSize.y, 0);
		return this;
	}

	/**
	 * set min size height
	 * 
	 * @param h height
	 * @return this
	 */
	public PC_GresWidget setMinHeight(int h) {
		this.minSize.setTo(this.minSize.x, h, 0);
		return this;
	}

	/**
	 * Set widget margin
	 * 
	 * @param widgetMargin
	 * @return this
	 */
	public PC_GresWidget setWidgetMargin(int widgetMargin) {
		this.widgetMargin = widgetMargin;
		return this;
	}

	/**
	 * @return newly calculated size, {width, height}
	 */
	public abstract PC_VecI calcSize();

	/**
	 * Get the Container Manager
	 * 
	 * @return the Container Manager
	 */
	public PC_IGresGui getGui() {
		return gui;
	}

	/**
	 * Set the Container Manager
	 * 
	 * @param containerManager the new Container Manager
	 * @return this
	 */
	public PC_GresWidget setGui(PC_IGresGui gui) {
		this.gui = gui;
		for (PC_GresWidget w : childs) {
			w.setGui(gui);
		}
		for (PC_GresWidget w : tabs) {
			w.setGui(gui);
		}
		return this;
	}

	/**
	 * Get widget size
	 * 
	 * @return {width, height}
	 */
	public PC_VecI getSize() {
		if (!visible)
			return new PC_VecI(0, 0);
		return size.copy();
	}

	/**
	 * Set widget size
	 * 
	 * @param width      width
	 * @param height     height
	 * @param calcParent flag whether to ask parent for position recalculation
	 * @return this
	 */
	public PC_GresWidget setSize(int width, int height, boolean calcParent) {
		this.size.setTo(width, height, 0);
		if (parent != null && calcParent) {
			parent.calcChildPositions();
		}
		return this;
	}

	/**
	 * Set size, recalculate position
	 * 
	 * @param width  width
	 * @param height height
	 * @return this
	 */
	public PC_GresWidget setSize(int width, int height) {
		return setSize(width, height, true);
	}

	/**
	 * Set size, recalculate position
	 * 
	 * @param gswidth  width
	 * @param gsheight height
	 * @return this
	 */
	public PC_GresWidget setSize(PC_VecI size) {
		this.size.setTo(size);
		return this;
	}

	/**
	 * Get position
	 * 
	 * @return {x, y}
	 */
	public PC_VecI getPosition() {
		return pos;
	}

	/**
	 * Set position of the widget
	 * 
	 * @param x x
	 * @param y y
	 * @return this
	 */
	public PC_GresWidget setPosition(int x, int y) {
		this.pos.setTo(x, y, 0);
		return this;
	}

	/**
	 * @return is enabled?
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * set "enabled" flag
	 * 
	 * @param enabled state
	 * @return this
	 */
	public PC_GresWidget enable(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	/**
	 * Refresh calculated children positions
	 */
	public abstract void calcChildPositions();

	/**
	 * Default implementation of child position calculation
	 */
	public void calcChildPositionsDefault() {
		int maxh = 0, xx = 0, yy = 0;
		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				childs.get(i).calcChildPositions();
				PC_VecI childSize = childs.get(i).calcSize();
				if (!childs.get(i).isVisible())
					childSize = new PC_VecI(0, 0);
				if (childSize.y > maxh) {
					maxh = childSize.y;
				}
				if (childSize.x > size.x || childSize.y > size.y) {
					if (childSize.x > size.x) {
						size.x = childSize.x;
					}
					if (childSize.y > size.y) {
						size.y = childSize.y;
					}
					if (parent != null) {
						parent.calcChildPositions();
					}
					calcChildPositions();
					return;
				}
				if (xx + childSize.x > size.x) {
					xx = 0;
					yy += maxh + widgetMargin;
				}
				childs.get(i).setPosition(xx, yy);
				xx += size.x + widgetMargin;
			}
		}
	}

	/**
	 * @return parent widget
	 */
	public PC_GresWidget getParent() {
		return parent;
	}

	/**
	 * Add child widget
	 * 
	 * @param newwidget new widget
	 * @return this
	 */
	public PC_GresWidget add(PC_GresWidget newwidget) {
		if (newwidget instanceof PC_GresWidgetTab) {
			newwidget.parent = this;
			newwidget.setFontRenderer(fontRenderer);
			newwidget.setGui(gui);
			tabs.add((PC_GresWidgetTab) newwidget);
			newwidget.callAddedToWidget();
			return this;
		}
		if (!canAddWidget) {
			return null;
		}
		newwidget.parent = this;
		newwidget.setFontRenderer(fontRenderer);
		newwidget.setGui(gui);
		childs.add(newwidget);
		newwidget.callAddedToWidget();
		calcChildPositions();
		return this;
	}

	/**
	 * Remove child widget, even from children's lists
	 * 
	 * @param removewidget widget to remove from child list
	 * @return this
	 */
	public PC_GresWidget remove(PC_GresWidget removewidget) {
		if (!childs.remove(removewidget)) {
			for (int i = 0; i < childs.size(); i++) {
				childs.get(i).remove(removewidget);
			}
		}
		calcChildPositions();
		return this;
	}

	/**
	 * Remove all children
	 * 
	 * @return this
	 */
	public PC_GresWidget removeAll() {
		childs.removeAll(childs);
		if (parent != null) {
			parent.calcChildPositions();
		}
		return this;
	}

	/**
	 * Set color to index
	 * 
	 * @param colorIndex color index (constant)
	 * @param color      the color, eg. 0xFFFFFF.
	 * @return this
	 */
	public PC_GresWidget setColor(int colorIndex, int color) {
		if (colorIndex < 0 || colorIndex > 5) {
			return this;
		}
		this.color[colorIndex] = color;
		return this;
	}

	/**
	 * Get color for index
	 * 
	 * @param colorIndex color index (constant)
	 * @return color number, eg. 0xFFFFFF
	 */
	public int getColor(int colorIndex) {
		if (colorIndex < 0 || colorIndex > 5) {
			return 0;
		}
		return color[colorIndex];
	}

	/**
	 * Get string length from font renderer
	 * 
	 * @param text the string
	 * @return length in pixels
	 */
	public int getStringWidth(String text) {
		FontRenderer fr = getFontRenderer();
		return fr.getStringWidth(text);
	}

	/**
	 * Get char height
	 * 
	 * @return height in pixels
	 */
	protected int getLineHeight() {
		return getFontRenderer().FONT_HEIGHT;
	}

	/**
	 * Draw string, using colors from the color array.
	 * 
	 * @param text text to draw (usually the label)
	 * @param x    pos x
	 * @param y    pos y
	 */
	protected void drawString(String text, int x, int y) {
		FontRenderer fr = getFontRenderer();
		if (color[enabled ? textColorShadowEnabled : textColorShadowDisabled] != 0) {
			fr.drawString(text, x + 1, y + 1, color[enabled ? textColorShadowEnabled : textColorShadowDisabled]);
		}
		fr.drawString(text, x, y,
				color[enabled ? (isMouseOver ? textColorHover : textColorEnabled) : textColorDisabled]);
	}

	/**
	 * Draw string, using overide color
	 * 
	 * @param text          text to draw (usually the label)
	 * @param x             pos x
	 * @param y             pos y
	 * @param colorOverride custom color
	 */
	protected void drawStringColor(String text, int x, int y, int colorOverride) {
		FontRenderer fr = getFontRenderer();
		if (color[enabled ? textColorShadowEnabled : textColorShadowDisabled] != 0) {
			fr.drawString(text, x + 1, y + 1, color[enabled ? textColorShadowEnabled : textColorShadowDisabled]);
		}
		fr.drawString(text, x, y, colorOverride);
	}

	public static PC_RectI setDrawRect(PC_RectI old, PC_RectI _new, double scale) {
		PC_RectI rect;
		if (old == null) {
			rect = _new.copy();
		} else {
			rect = old.averageQuantity(_new);
		}
		if (rect.width <= 0 || rect.height <= 0)
			return null;
		int h = mc.displayHeight;
		GL11.glScissor((int) (rect.x * scale), h - (int) ((rect.y + rect.height) * scale), (int) (rect.width * scale),
				(int) (rect.height * scale));
		return rect;
	}

	/**
	 * Render this and all children at correct positions
	 * 
	 * @param posOffset offset from top left
	 */
	public void updateRenderer(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
		if (!visible)
			return;

		tabRenderer(posOffset.offset(pos), null, scale);

		PC_RectI scissorNew = setDrawRect(scissorOld,
				new PC_RectI(posOffset.x + pos.x, posOffset.y + pos.y, size.x, size.y), scale);
		if (scissorNew == null)
			return;

		PC_RectI rect = render(posOffset, scissorNew, scale);
		if (rect != null)
			scissorNew = setDrawRect(scissorNew, rect, scale);

		childRenderer(posOffset.offset(pos), scissorNew, scale);
	}

	public void childRenderer(PC_VecI posOffset, PC_RectI scissorNew, double scale) {
		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				if (childs.get(i).visible)
					childs.get(i).updateRenderer(posOffset, scissorNew, scale);
			}
		}
	}

	public void tabRenderer(PC_VecI posOffset, PC_RectI scissorNew, double scale) {
		if (tabs != null) {
			int y = 5;
			for (int i = 0; i < tabs.size(); i++) {
				if (tabs.get(i).visible) {
					tabs.get(i).setPosition(size.x - 2, y);
					tabs.get(i).updateRenderer(posOffset, scissorNew, scale);
					y += tabs.get(i).getSize().y + 2;
				}
			}
		}
	}

	/**
	 * Do render this widget
	 * 
	 * @param posOffset offset from top left
	 */
	protected abstract PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale);

	/**
	 * Get the widget under mouse cursor. First tries children, then self, null at
	 * last.
	 * 
	 * @param mousePos mouse absolute x
	 * @return the widget under mouse
	 */
	public PC_GresWidget getWidgetUnderMouse(PC_VecI mousePos) {
		if (!visible)
			return null;
		PC_GresWidget widget;
		PC_VecI mpos = mousePos.copy().add(-pos.x, -pos.y, 0);

		MouseOver mo = MouseOver.NON;

		// mouse not over this widget
		if (mpos.x < 0 || mpos.x >= getSize().x || mpos.y < 0 || mpos.y >= getSize().y
				|| (mo = mouseOver(mpos)) != MouseOver.CHILD) {
			this.isMouseOver = false;

			if (childs != null) {
				for (int i = 0; i < childs.size(); i++) {
					childs.get(i).getWidgetUnderMouse(new PC_VecI(-1, -1));
				}
				for (int i = 0; i < childs.size(); i++) {
					childs.get(i).getTabUnderMouse(new PC_VecI(-1, -1));
				}
			}

			if (mo == MouseOver.THIS) {
				this.isMouseOver = true;
				return this;
			}

			return getTabUnderMouse(mousePos);
		}

		this.isMouseOver = true;

		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {

				widget = childs.get(i).getWidgetUnderMouse(mpos);
				if (widget != null) {
					for (i++; i < childs.size(); i++) {
						childs.get(i).getWidgetUnderMouse(new PC_VecI(-1, -1));
					}
					for (i = 0; i < childs.size(); i++) {
						childs.get(i).getTabUnderMouse(new PC_VecI(-1, -1));
					}
					return widget;
				}

			}
		}

		return this;
	}

	public PC_GresWidget getTabUnderMouse(PC_VecI mousePos) {
		if (!visible)
			return null;
		PC_GresWidget widget;
		PC_VecI mpos = mousePos.copy().add(-pos.x, -pos.y, 0);

		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				widget = childs.get(i).getTabUnderMouse(mpos);
				if (widget != null) {
					for (i++; i < childs.size(); i++) {
						childs.get(i).getTabUnderMouse(new PC_VecI(-1, -1));
					}
					return widget;
				}

			}
		}

		if (tabs != null) {
			for (int i = 0; i < tabs.size(); i++) {
				widget = tabs.get(i).getWidgetUnderMouse(mpos);
				if (widget != null) {
					for (i++; i < tabs.size(); i++) {
						tabs.get(i).getWidgetUnderMouse(new PC_VecI(-1, -1));
					}
					return widget;
				}
			}
		}

		return null;
	}

	/**
	 * Get absolute position on screen
	 * 
	 * @return coord of top left.
	 */
	public PC_VecI getPositionOnScreen() {
		PC_VecI position;
		if (parent != null) {
			position = parent.getPositionOnScreen().copy().add(pos);

		} else {
			position = pos.copy();
		}
		return position;
	}

	/**
	 * Render textured rect with Alpha support at given position.
	 * 
	 * @param offset    offset relative to root top left
	 * @param texture   texture to render (filename)
	 * @param rectSize  size of the rendered texture
	 * @param imgOffset offset within the texture image (from top left)
	 */
	protected void renderImage(PC_VecI offset, String texture, PC_VecI rectSize, PC_VecI imgOffset) {
		renderImage_static(this, texture, offset.offset(pos), rectSize, imgOffset);
	}

	/**
	 * Render textured rect with Alpha support at given position.
	 * 
	 * @param gui       the gui being drawed on
	 * @param texture   texture to render (filename)
	 * @param startPos  left top corner absolute position
	 * @param rectSize  size of the rendered texture
	 * @param imgOffset offset within the texture image (from top left)
	 */
	protected static void renderImage_static(Gui gui, String texture, PC_VecI startPos, PC_VecI rectSize,
			PC_VecI imgOffset) {

		mc.renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, texture));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		gui.drawTexturedModalRect(startPos.x, startPos.y, imgOffset.x, imgOffset.y, rectSize.x, rectSize.y);

		GL11.glDisable(GL11.GL_BLEND);

	}

	/**
	 * Render texture using 9patch-like scaling method.<br>
	 * 
	 * @param offset    offset relative to root top left
	 * @param texture   texture to render (filename)
	 * @param rectSize  rectangle size
	 * @param imgOffset offset within the texture image (from top left)
	 * @param imgSize   size of the whole "scalable" region in texture file (eg. the
	 *                  whole huge "button" field)
	 */
	protected void renderTextureSliced(PC_VecI offset, String texture, PC_VecI rectSize, PC_VecI imgOffset,
			PC_VecI imgSize) {
		renderTextureSliced_static(this, offset.offset(pos), texture, rectSize, imgOffset, imgSize,
				new PC_RectI(0, 0, 0, 0));
	}

	protected void renderTextureSliced(PC_VecI offset, String texture, PC_VecI rectSize, PC_VecI imgOffset,
			PC_VecI imgSize, PC_RectI frame) {
		renderTextureSliced_static(this, offset.offset(pos), texture, rectSize, imgOffset, imgSize, frame);
	}

	protected void renderTextureSlicedColored(PC_VecI offset, String texture, PC_VecI rectSize, PC_VecI imgOffset,
			PC_VecI imgSize) {
		renderTextureSliced_static(this, offset.offset(pos), texture, rectSize, imgOffset, imgSize,
				new PC_RectI(0, 0, 0, 0),
				color[enabled ? (isMouseOver ? textColorHover : textColorEnabled) : textColorDisabled]);
	}

	protected void renderTextureSlicedColored(PC_VecI offset, String texture, PC_VecI rectSize, PC_VecI imgOffset,
			PC_VecI imgSize, PC_RectI frame) {
		renderTextureSliced_static(this, offset.offset(pos), texture, rectSize, imgOffset, imgSize, frame,
				color[enabled ? (isMouseOver ? textColorHover : textColorEnabled) : textColorDisabled]);
	}

	/**
	 * Render texture using 9patch-like scaling method.<br>
	 * 
	 * @param gui       the gui being drawed on
	 * @param startPos  offset relative to parent top left
	 * @param texture   texture to render (filename)
	 * @param rectSize  rectangle size
	 * @param imgOffset offset within the texture image (from top left)
	 * @param imgSize   size of the whole "scalable" region in texture file (eg. the
	 *                  whole huge "button" field)
	 */

	protected static void renderTextureSliced_static(Gui gui, PC_VecI startPos, String texture, PC_VecI rectSize,
			PC_VecI imgOffset, PC_VecI imgSize) {
		renderTextureSliced_static(gui, startPos, texture, rectSize, imgOffset, imgSize, new PC_RectI(0, 0, 0, 0));
	}

	private static void renderTextureSliced_static(Gui gui, PC_VecI startPos, PC_VecI rectSize, PC_VecI imgOffset,
			PC_VecI imgSize) {
		for (int x = 0; x < rectSize.x; x += imgSize.x) {
			for (int y = 0; y < rectSize.y; y += imgSize.y) {
				int sx = imgSize.x;
				int sy = imgSize.y;
				if (x + sx > rectSize.x) {
					sx = rectSize.x - x;
				}
				if (y + sy > rectSize.y) {
					sy = rectSize.y - y;
				}
				gui.drawTexturedModalRect(startPos.x + x, startPos.y + y, imgOffset.x, imgOffset.y, sx, sy);
			}
		}
	}

	protected static void renderTextureSliced_static(Gui gui, PC_VecI startPos, String texture, PC_VecI rectSize,
			PC_VecI imgOffset, PC_VecI imgSize, PC_RectI frame) {
		renderTextureSliced_static(gui, startPos, texture, rectSize, imgOffset, imgSize, frame, 0xFFFFFFFF);
	}

	protected static void renderTextureSliced_static(Gui gui, PC_VecI startPos, String texture, PC_VecI rectSize,
			PC_VecI imgOffset, PC_VecI imgSize, PC_RectI frame, int color) {

		GL11.glColor4f(PC_Color.red(color), PC_Color.green(color), PC_Color.blue(color), 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		mc.getTextureManager().bindTexture(new ResourceLocation(mod_PowerCraft.MODID, texture));
		if (frame.y > 0) {
			if (frame.x > 0) {
				gui.drawTexturedModalRect(startPos.x, startPos.y, imgOffset.x, imgOffset.y, frame.x, frame.y);
			}
			renderTextureSliced_static(gui, new PC_VecI(startPos.x + frame.x, startPos.y),
					new PC_VecI(rectSize.x - frame.x - frame.width, frame.y),
					new PC_VecI(imgOffset.x + frame.x, imgOffset.y),
					new PC_VecI(imgSize.x - frame.x - frame.width, imgSize.y));
			if (frame.width > 0) {
				gui.drawTexturedModalRect(startPos.x + rectSize.x - frame.width, startPos.y, imgSize.x - frame.width,
						imgOffset.y, frame.width, frame.y);
			}
		}
		if (frame.x > 0) {
			renderTextureSliced_static(gui, new PC_VecI(startPos.x, startPos.y + frame.y),
					new PC_VecI(frame.x, rectSize.y - frame.y - frame.height),
					new PC_VecI(imgOffset.x, imgOffset.y + frame.y),
					new PC_VecI(imgSize.x, imgSize.y - frame.y - frame.height));
		}

		renderTextureSliced_static(gui, new PC_VecI(startPos.x + frame.x, startPos.y + frame.y),
				new PC_VecI(rectSize.x - frame.x - frame.width, rectSize.y - frame.y - frame.height),
				new PC_VecI(imgOffset.x + frame.x, imgOffset.y + frame.y),
				new PC_VecI(imgSize.x - frame.x - frame.width, imgSize.y - frame.y - frame.height));

		if (frame.width > 0) {
			renderTextureSliced_static(gui, new PC_VecI(startPos.x + rectSize.x - frame.width, startPos.y + frame.y),
					new PC_VecI(frame.width, rectSize.y - frame.y - frame.height),
					new PC_VecI(imgOffset.x + imgSize.x - frame.width, imgOffset.y + frame.y),
					new PC_VecI(frame.width, imgSize.y - frame.y - frame.height));
		}

		if (frame.height > 0) {
			if (frame.x > 0) {
				gui.drawTexturedModalRect(startPos.x, startPos.y + rectSize.y - frame.height, imgOffset.x,
						imgOffset.y + imgSize.y - frame.height, frame.x, frame.height);
			}
			renderTextureSliced_static(gui, new PC_VecI(startPos.x + frame.x, startPos.y + rectSize.y - frame.height),
					new PC_VecI(rectSize.x - frame.x - frame.width, frame.height),
					new PC_VecI(imgOffset.x + frame.x, imgOffset.y + imgSize.y - frame.height),
					new PC_VecI(imgSize.x - frame.x - frame.width, frame.height));
			if (frame.width > 0) {
				gui.drawTexturedModalRect(startPos.x + rectSize.x - frame.width, startPos.y + rectSize.y - frame.height,
						imgOffset.x + imgSize.x - frame.width, imgOffset.y + imgSize.y - frame.height, frame.width,
						frame.height);
			}
		}

		GL11.glDisable(GL11.GL_BLEND);

	}

	protected static void drawTexturedModalRectWithIcon(int x, int y, int sizeX, int sizeY, IIcon icon,
			String texture) {

		Tessellator tessellator = Tessellator.instance;
		float f3 = icon.getMinU();
		float f4 = icon.getMaxU();
		float f5 = icon.getMinV();
		float f6 = icon.getMaxV();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(x, y + sizeY, 0.0D, (double) f3, (double) f6);
		tessellator.addVertexWithUV(x + sizeX, y + sizeY, 0.0D, (double) f4, (double) f6);
		tessellator.addVertexWithUV(x + sizeX, y, 0.0D, (double) f4, (double) f5);
		tessellator.addVertexWithUV(x, y, 0.0D, (double) f3, (double) f5);
		tessellator.draw();
	}

	/**
	 * Check if mouse is over widget.<br>
	 * The given coordinate is relative to widget's top left corner.
	 * 
	 * @param mousePos mouse position
	 * @return is over
	 */
	public abstract MouseOver mouseOver(PC_VecI mousePos);

	/**
	 * Mouse clicked on widget.
	 * 
	 * @param mousePos mouse position
	 * @param key      mouse button index, -1 = mouse up.
	 * @return event accepted
	 */
	public abstract boolean mouseClick(PC_VecI mousePos, int key);

	/**
	 * On mouse moved. Last focused widget gets mouse move events.
	 * 
	 * @param mousePos current mouse position.
	 */
	public abstract void mouseMove(PC_VecI mousePos);

	/**
	 * On mouse wheel moved. Last focused widget gets wheel move events.
	 * 
	 * @param i wheelmoved direction
	 */
	public abstract void mouseWheel(int i);

	/**
	 * On key pressed.
	 * 
	 * @param c   character of the key
	 * @param key key index
	 * @return true if key was valid and was used.
	 */
	public abstract boolean keyTyped(char c, int key);

	/**
	 * Called when Widget added to another widget
	 */
	public void callAddedToWidget() {
		addedToWidget();
		for (PC_GresWidget w : childs) {
			w.callAddedToWidget();
		}
	}

	/**
	 * Called when Widget added to another widget
	 */
	public abstract void addedToWidget();

	protected static void drawButton(PC_GresWidget widget, PC_VecI pos, PC_VecI size, String text, int state) {

		int txC = 0xe0e0e0;

		if (state == 0) {
			txC = 0xa0a0a0; // dark
		}
		if (state == 1) {
			txC = 0xe0e0e0; // light
		}
		if (state > 1) {
			txC = 0xffffa0; // yellow
		}

		renderTextureSliced_static(widget, pos, imgdir + "button.png", size, new PC_VecI(0, state * 50),
				new PC_VecI(256, 50), new PC_RectI(2, 2, 2, 3));

		widget.drawCenteredString(widget.getFontRenderer(), text, pos.x + size.x / 2,
				pos.y + (size.y - widget.getFontRenderer().FONT_HEIGHT) / 2, txC);
	}

	public Slot getSlotUnderMouse(PC_VecI mousePos) {
		return null;
	}

	public List<String> getTooltip(PC_VecI mousePos) {
		if (tooltip == null || tooltip.equals("")) {
			return null;
		}
		List<String> l = new ArrayList<String>();
		l.add(tooltip);
		return l;
	}

	public void keyChange(String key, Object value) {
		if (key.equals(tileEnityObjectKey)) {
			onObjectChange(value);
		}
		for (PC_GresWidget w : childs) {
			w.keyChange(key, value);
		}
	}

	protected void onObjectChange(Object value) {
	}

	public void tick() {
		if (childs != null) {
			for (int i = 0; i < childs.size(); i++) {
				childs.get(i).tick();
			}
		}
		if (tabs != null) {
			for (int i = 0; i < tabs.size(); i++) {
				tabs.get(i).tick();
			}
		}
		onTick();
	}

	protected void onTick() {
	}

}
