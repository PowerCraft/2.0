package powercraft.api.gres;

import net.minecraft.util.ResourceLocation;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

/**
 * Resizable GUI user-editable colorable progress bar widget
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresProgressBar extends PC_GresWidget {

	/** Bar fraction (0-1F) */
	protected float fraction = 0.2F;
	/** Color of the bar */
	protected int colorHex = 0xffffff;
	/** Type - 0=striped, 1=plain 3D, 2=plain */
	protected int type = 0;
	/** Label enabled */
	protected boolean showLabel = false;
	/** label multiplier (fraction *= this) */
	protected int labelMultiplier = 100;
	/** label ofset (added to the output label) */
	protected int labelOffset = 0;
	/** the bar width */
	private int barWidth;
	/** string appended after label number */
	protected String labelAppend = "";
	/** max width of label number */
	protected int labelWidth = 0;

	/** Can user change value? */
	private boolean acceptsInput = false;

	/**
	 * Image from a texture file
	 * 
	 * @param color hex color of the bar
	 * @param width width of the widget
	 */
	public PC_GresProgressBar(int color, int width) {
		super(width, 11);
		barWidth = width;
		canAddWidget = false;
		this.colorHex = color;
	}

	/**
	 * Image from a texture file
	 * 
	 * @param color hex color of the bar
	 */
	public PC_GresProgressBar(int color) {
		super(100, 11);
		canAddWidget = false;
		this.colorHex = color;
	}

	/**
	 * @return the fraction (0-1F)
	 */
	public float getFraction() {
		return fraction;
	}

	/**
	 * Get the number shown as label.
	 * 
	 * @return number
	 */
	public int getNumber() {
		return (labelOffset + Math.round(fraction * labelMultiplier));
	}

	/**
	 * @param fraction the fraction to set (0-1F)
	 * @return this
	 */
	public PC_GresProgressBar setFraction(float fraction) {
		this.fraction = fraction;
		return this;
	}

	/**
	 * @return the hex color
	 */
	public int getColor() {
		return colorHex;
	}

	/**
	 * @param colorHex the hex color to set
	 * @return this
	 */
	public PC_GresProgressBar setColor(int colorHex) {
		this.colorHex = colorHex;
		return this;
	}

	/**
	 * @return bar type (0=striped outset, 1=outset, 2=plain)
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set bar type
	 * 
	 * @param type the bar type (0=striped outset, 1=outset, 2=plain)
	 * @return this
	 */
	public PC_GresProgressBar setType(int type) {
		this.type = type;
		return this;
	}

	/**
	 * Set label offset (added to the output number)
	 * 
	 * @param offset the offset
	 * @return this
	 */
	public PC_GresProgressBar setLabelOffset(int offset) {
		this.labelOffset = offset;
		return this;
	}

	/**
	 * @return does show label
	 */
	public boolean getShowLabel() {
		return showLabel;
	}

	/**
	 * @param showLabel do show label
	 * @return this
	 */
	public PC_GresProgressBar setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
		return this;
	}

	/**
	 * @return the label multiplier (if set to 100, percent sign will be shown)
	 */
	public int getLabelMultiplier() {
		return labelMultiplier;
	}

	/**
	 * @param labelMultiplier the label multiplier (if set to 100, percent sign will
	 *                        be shown)
	 * @return this
	 */
	public PC_GresProgressBar setLabelMultiplier(int labelMultiplier) {
		this.labelMultiplier = labelMultiplier;
		return this;
	}

	/**
	 * @return the labelWidth
	 */
	public int getLabelWidth() {
		return labelWidth;
	}

	/**
	 * Set label width (if label shown)
	 * 
	 * @param labelWidth the labelWidth to set
	 * @return this
	 */
	public PC_GresProgressBar setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
		return this;
	}

	/**
	 * @return the minimal bar width
	 */
	public int getBarWidth() {
		return barWidth;
	}

	/**
	 * Set minimal bar width
	 * 
	 * @param barWidth the bar width to set
	 * @return this
	 */
	public PC_GresProgressBar setBarWidth(int barWidth) {
		this.barWidth = barWidth;
		return this;
	}

	/**
	 * @return the labelAppend
	 */
	public String getLabelAppend() {
		return labelAppend;
	}

	/**
	 * @param labelAppend the labelAppend to set
	 * @return this
	 */
	public PC_GresProgressBar setLabelAppend(String labelAppend) {
		this.labelAppend = labelAppend;
		return this;
	}

	/**
	 * @return the acceptsInput
	 */
	public boolean getEditable() {
		return acceptsInput;
	}

	/**
	 * @param acceptsInput the acceptsInput to set
	 * @return this
	 */
	public PC_GresProgressBar setEditable(boolean acceptsInput) {
		this.acceptsInput = acceptsInput;
		return this;
	}

	/**
	 * Configure label on right
	 * 
	 * @param append     label unit (eg. %, km, t)
	 * @param longest    longest expected text shown on label (eg. 100% or 999km)
	 * @param multiplier fraction multiplier. The fraction is multiplied by this and
	 *                   rounded before showing as label.
	 * @return this
	 */
	public PC_GresProgressBar configureLabel(String append, String longest, int multiplier) {
		setShowLabel(true);
		setLabelAppend(append);
		setLabelWidth(getStringWidth(longest));
		setLabelMultiplier(multiplier);
		return this;
	}

	@Override
	public PC_VecI calcSize() {

		if (showLabel) {
			size.setTo(barWidth + labelWidth + 3, 11, 0);
		}

		return size.copy();
	}

	@Override
	public void calcChildPositions() {

	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		String texture = imgdir + "widgets.png";

		renderTextureSliced(offsetPos, texture, size.copy().add(showLabel ? -(labelWidth + 3) : 0, 0, 0),
				new PC_VecI(0, 11 * 2), new PC_VecI(256, 11), new PC_RectI(1, 1, 1, 1));

		mc.renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, texture));

		PC_Color colorRGB = new PC_Color(colorHex);

		colorRGB.syncGL();

		int inner_x = pos.x + offsetPos.x + 1;
		int inner_y = pos.y + offsetPos.y;
		int texture_x = 0;
		int texture_y = 11 * (3 + type);
		int inner_width = Math.round((size.x - 2 - (showLabel ? labelWidth + 3 : 0)) * fraction);
		int inner_height = 11;

		drawTexturedModalRect(inner_x, inner_y, texture_x, texture_y, inner_width, inner_height);

		if (showLabel) {
			String lbl = getNumber() + labelAppend;
			drawString(lbl, pos.x + offsetPos.x + size.copy().add(-labelWidth, 0, 0).x, pos.y + offsetPos.y + 2);
		}

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.THIS;
	}

	private boolean dragging = false;

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {

		dragging = (key != -1);

		int inner_width = Math.round((size.x - 2 - (showLabel ? labelWidth + 3 : 0)));

		if (!acceptsInput) {
			return false;
		}

		if (mpos.x >= 0) {
			fraction = (mpos.x) / (float) inner_width;
			if (fraction > 1) {
				fraction = 1;
			}
			if (fraction < 0) {
				fraction = 0;
			}
		}

		return true;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mpos) {
		if (dragging) {
			mouseClick(mpos, 0);
			gui.registerAction(this);
		}
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
