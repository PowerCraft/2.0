package powercraft.light;

import net.minecraft.util.DamageSource;

public class PCli_DamageSourceLaser extends DamageSource {

	private static PCli_DamageSourceLaser instance;

	public static DamageSource getDamageSource() {
		if (instance == null)
			instance = new PCli_DamageSourceLaser();
		return instance;
	}

	private PCli_DamageSourceLaser() {
		super("laser");
	}

}
