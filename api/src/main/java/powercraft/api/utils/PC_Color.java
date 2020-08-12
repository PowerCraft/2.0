package powercraft.api.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import powercraft.api.renderer.PC_Renderer;

public class PC_Color extends PC_VecF {

	public static final long serialVersionUID = 1745800961264333415L;

	private static HashMap<String, Integer> namedColors = new HashMap<String, Integer>();
	public static final int crystal_colors[] = { 0xff9900, 0xff1111, 0x39ff11, 0x5555ff, 0xCCCCFF, 0xff33ff, 0x33ffff,
			0xffff00 };

	static {
		namedColors.put("white", 0xffffff);
		namedColors.put("silver", 0xc0c0c0);
		namedColors.put("gray", 0x808080);
		namedColors.put("black", 0x000000);
		namedColors.put("red", 0xff0000);
		namedColors.put("maroon", 0x800000);
		namedColors.put("yellow", 0xffff00);
		namedColors.put("olive", 0x808000);
		namedColors.put("lime", 0x00ff00);
		namedColors.put("green", 0x008000);
		namedColors.put("aqua", 0x00ffff);
		namedColors.put("teal", 0x008080);
		namedColors.put("blue", 0x0000ff);
		namedColors.put("navy", 0x000080);
		namedColors.put("fuchsia", 0xff00ff);
		namedColors.put("purple", 0x800080);
		namedColors.put("brick", 0xB22222);
		namedColors.put("darkred", 0x8B0000);
		namedColors.put("salmon", 0xFA8072);
		namedColors.put("pink", 0xdb5b9d);
		namedColors.put("orange", 0xff4500);
		namedColors.put("gold", 0xffd700);
		namedColors.put("magenta", 0xff00ff);
		namedColors.put("violet", 0x9400d3);
		namedColors.put("indigo", 0x483D8B);
		namedColors.put("limegreen", 0x32cd32);
		namedColors.put("darkgreen", 0x006400);
		namedColors.put("cyan", 0x00ffff);
		namedColors.put("steel", 0x4682b4);
		namedColors.put("lightblue", 0x78a2dd); // 0094FF BAE2FF
		namedColors.put("darkblue", 0x00008b);
		namedColors.put("brown", 0x8b4513);
		namedColors.put("lightgray", 0xd3d3d3);
		namedColors.put("darkgray", 0xa9a9a9);
	}

	public PC_Color() {
		this(0.0f, 0.0f, 0.0f);
	}

	public PC_Color(float x) {
		this(x, 0.0f, 0.0f);
	}

	public PC_Color(float x, float y) {
		this(x, y, 0.0f);
	}

	public PC_Color(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PC_Color(int hex) {
		x = red(hex);
		y = green(hex);
		z = blue(hex);
	}

	public PC_Color(PC_Vec vec) {
		x = vec.getX().floatValue();
		y = vec.getY().floatValue();
		z = vec.getZ().floatValue();
	}

	public float getRed() {
		return x;
	}

	public float getGreen() {
		return y;
	}

	public float getBlue() {
		return z;
	}

	public PC_Color syncGL() {
		PC_Renderer.glColor3f(x, y, z);
		return this;
	}

	public PC_Color syncGL(float alpha) {
		PC_Renderer.glColor4f(x, y, z, alpha);
		return this;
	}

	public int getHex() {
		int r255 = (int) Math.round(x * 255) & 0xff;
		int g255 = (int) Math.round(y * 255) & 0xff;
		int b255 = (int) Math.round(z * 255) & 0xff;
		return r255 << 16 | g255 << 8 | b255;
	}

	public PC_Color setTo(int hex) {
		x = red(hex);
		y = green(hex);
		z = blue(hex);
		return this;
	}

	public PC_Color copy() {
		return new PC_Color(this);
	}

	public static float red(int hex) {
		return ((hex & 0xff0000) >> 16) / 255.0f;
	}

	public static float green(int hex) {
		return ((hex & 0x00ff00) >> 8) / 255.0f;
	}

	public static float blue(int hex) {
		return ((hex & 0x0000ff)) / 255.0f;
	}

	public static PC_Color fromHex(int hex) {
		return new PC_Color().setTo(hex);
	}

	public static Integer getHexColorForName(String text) {
		if (namedColors.containsKey(text.toLowerCase())) {
			return namedColors.get(text.toLowerCase());
		}

		return null;
	}

	private static Map<String, String> magicColors = new HashMap<String, String>();
	static {
		magicColors.put("[black]", "§0");
		magicColors.put("[navy]", "§1");
		magicColors.put("[blue]", "§1");
		magicColors.put("[dblue]", "§1");
		magicColors.put("[darkblue]", "§1");
		magicColors.put("[green]", "§2");
		magicColors.put("[dgreen]", "§2");
		magicColors.put("[darkgreen]", "§2");
		magicColors.put("[dcyan]", "§3");
		magicColors.put("[darkcyan]", "§3");
		magicColors.put("[daqua]", "§3");
		magicColors.put("[darkaqua]", "§3");
		magicColors.put("[darkred]", "§4");
		magicColors.put("[red]", "§4");
		magicColors.put("[dred]", "§4");
		magicColors.put("[purple]", "§5");
		magicColors.put("[orange]", "§6");
		magicColors.put("[grey]", "§7");
		magicColors.put("[gray]", "§7");
		magicColors.put("[dgrey]", "§8");
		magicColors.put("[darkgrey]", "§8");
		magicColors.put("[dgray]", "§8");
		magicColors.put("[darkgray]", "§8");
		magicColors.put("[indigo]", "§9");
		magicColors.put("[lblue]", "§9");
		magicColors.put("[lightblue]", "§9");
		magicColors.put("[lime]", "§a");
		magicColors.put("[limegreen]", "§a");
		magicColors.put("[aqua]", "§b");
		magicColors.put("[cyan]", "§b");
		magicColors.put("[lred]", "§c");
		magicColors.put("[lightred]", "§c");
		magicColors.put("[pink]", "§d");
		magicColors.put("[yellow]", "§e");
		magicColors.put("[white]", "§f");
		magicColors.put("[random]", "§k");
		magicColors.put("[bold]", "§l");
		magicColors.put("[b]", "§l");
		magicColors.put("[s]", "§m");
		magicColors.put("[strike]", "§m");
		magicColors.put("[u]", "§n");
		magicColors.put("[underline]", "§n");
		magicColors.put("[italics]", "§o");
		magicColors.put("[i]", "§o");
		magicColors.put("[reset]", "§r");
		magicColors.put("[r]", "§r");
		magicColors.put("[#0]", "§0");
		magicColors.put("[#1]", "§1");
		magicColors.put("[#2]", "§2");
		magicColors.put("[#3]", "§3");
		magicColors.put("[#4]", "§4");
		magicColors.put("[#5]", "§5");
		magicColors.put("[#6]", "§6");
		magicColors.put("[#7]", "§7");
		magicColors.put("[#8]", "§8");
		magicColors.put("[#9]", "§9");
		magicColors.put("[#a]", "§a");
		magicColors.put("[#b]", "§b");
		magicColors.put("[#c]", "§c");
		magicColors.put("[#d]", "§d");
		magicColors.put("[#e]", "§e");
		magicColors.put("[#f]", "§f");
	}

	/**
	 * Convert color and formatting tags in input string to chat color codes.
	 * 
	 * @param input string to convert
	 * @return converted input
	 */
	public static String convertMagicColors(String input) {
		input = input.replaceAll("\\[/.*?\\]", "§r");
		input = input.replaceAll("</.*?>", "§r");
		for (Entry<String, String> entry : magicColors.entrySet()) {
			input = input.replace(entry.getKey(), entry.getValue());
			input = input.replace(entry.getKey().replace('[', '<').replace(']', '>'), entry.getValue());
		}
		return input;
	}

	/**
	 * @return new random color
	 */
	public static PC_Color randomColor() {
		return new PC_Color().randomize();
	}

	/**
	 * Set all 3 channels to random color, tries to make bright or at least not dark
	 * colors
	 * 
	 * @return this
	 */
	public PC_Color randomize() {
		Random rand = new Random();
		do {
			x = rand.nextFloat();
			y = rand.nextFloat();
			z = rand.nextFloat();
		} while (x + y + z < 0.8F);

		return this;
	}

}
