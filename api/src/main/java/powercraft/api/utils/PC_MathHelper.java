package powercraft.api.utils;

import java.util.Random;

public class PC_MathHelper {
	private static float[] SIN_TABLE = new float[65536];

	public static final float sin(float par0) {
		return SIN_TABLE[(int) (par0 * 10430.378F) & 65535];
	}

	public static final float cos(float par0) {
		return SIN_TABLE[(int) (par0 * 10430.378F + 16384.0F) & 65535];
	}

	public static final float sqrt_float(float par0) {
		return (float) Math.sqrt((double) par0);
	}

	public static final float sqrt_double(double par0) {
		return (float) Math.sqrt(par0);
	}

	public static int floor_float(float par0) {
		int var1 = (int) par0;
		return par0 < (float) var1 ? var1 - 1 : var1;
	}

	public static int truncateDoubleToInt(double par0) {
		return (int) (par0 + 1024.0D) - 1024;
	}

	public static int floor_double(double par0) {
		int var2 = (int) par0;
		return par0 < (double) var2 ? var2 - 1 : var2;
	}

	public static long floor_double_long(double par0) {
		long var2 = (long) par0;
		return par0 < (double) var2 ? var2 - 1L : var2;
	}

	public static float abs(float par0) {
		return par0 >= 0.0F ? par0 : -par0;
	}

	public static int abs_int(int par0) {
		return par0 >= 0 ? par0 : -par0;
	}

	public static int ceiling_float_int(float par0) {
		int var1 = (int) par0;
		return par0 > (float) var1 ? var1 + 1 : var1;
	}

	public static int ceiling_double_int(double par0) {
		int var2 = (int) par0;
		return par0 > (double) var2 ? var2 + 1 : var2;
	}

	public static int clamp_int(int par0, int par1, int par2) {
		return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
	}

	public static float clamp_float(float par0, float par1, float par2) {
		return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
	}

	public static double abs_max(double par0, double par2) {
		if (par0 < 0.0D) {
			par0 = -par0;
		}

		if (par2 < 0.0D) {
			par2 = -par2;
		}

		return par0 > par2 ? par0 : par2;
	}

	public static int bucketInt(int par0, int par1) {
		return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
	}

	public static boolean stringNullOrLengthZero(String par0Str) {
		return par0Str == null || par0Str.length() == 0;
	}

	public static int getRandomIntegerInRange(Random par0Random, int par1, int par2) {
		return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
	}

	public static double func_82716_a(Random par0Random, double par1, double par3) {
		return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
	}

	public static double average(long[] par0ArrayOfLong) {
		long var1 = 0L;
		long[] var3 = par0ArrayOfLong;
		int var4 = par0ArrayOfLong.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			long var6 = var3[var5];
			var1 += var6;
		}

		return (double) var1 / (double) par0ArrayOfLong.length;
	}

	public static float wrapAngleTo180_float(float par0) {
		par0 %= 360.0F;

		if (par0 >= 180.0F) {
			par0 -= 360.0F;
		}

		if (par0 < -180.0F) {
			par0 += 360.0F;
		}

		return par0;
	}

	public static double wrapAngleTo180_double(double par0) {
		par0 %= 360.0D;

		if (par0 >= 180.0D) {
			par0 -= 360.0D;
		}

		if (par0 < -180.0D) {
			par0 += 360.0D;
		}

		return par0;
	}

	public static int func_82715_a(String par0Str, int par1) {
		int var2 = par1;

		try {
			var2 = Integer.parseInt(par0Str);
		} catch (Throwable var4) {
			;
		}

		return var2;
	}

	public static int func_82714_a(String par0Str, int par1, int par2) {
		int var3 = par1;

		try {
			var3 = Integer.parseInt(par0Str);
		} catch (Throwable var5) {
			;
		}

		if (var3 < par2) {
			var3 = par2;
		}

		return var3;
	}

	public static double func_82712_a(String par0Str, double par1) {
		double var3 = par1;

		try {
			var3 = Double.parseDouble(par0Str);
		} catch (Throwable var6) {
			;
		}

		return var3;
	}

	public static double func_82713_a(String par0Str, double par1, double par3) {
		double var5 = par1;

		try {
			var5 = Double.parseDouble(par0Str);
		} catch (Throwable var8) {
			;
		}

		if (var5 < par3) {
			var5 = par3;
		}

		return var5;
	}

	static {
		for (int var0 = 0; var0 < 65536; ++var0) {
			SIN_TABLE[var0] = (float) Math.sin((double) var0 * Math.PI * 2.0D / 65536.0D);
		}
	}
}
