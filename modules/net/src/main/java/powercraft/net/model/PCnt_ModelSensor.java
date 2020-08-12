package powercraft.net.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCnt_ModelSensor extends ModelBase {
	private ModelRenderer parts[];

	/**
	 * Radio block model.
	 */
	public PCnt_ModelSensor() {

		textureWidth = 90;
		textureHeight = 62;

		parts = new ModelRenderer[8];

		// bulb on
		parts[0] = new ModelRenderer(this, 66, 0);
		parts[0].addBox(-3F, -15F, -3F, 6, 6, 6, 0.0F);

		// bulb off
		parts[1] = new ModelRenderer(this, 66, 13);
		parts[1].addBox(-3F, -15F, -3F, 6, 6, 6, 0.0F);

		// wood

		// base
		parts[2] = new ModelRenderer(this, 0, 0);
		parts[2].addBox(-8F, -4F, -8F, 16, 4, 16, 0.0F);

		// stick
		parts[3] = new ModelRenderer(this, 49, 0);
		parts[3].addBox(-2F, -12F, -2F, 4, 8, 4, 0.0F);

		// stone

		// base
		parts[4] = new ModelRenderer(this, 0, 42);
		parts[4].addBox(-8F, -4F, -8F, 16, 4, 16, 0.0F);

		// stick
		parts[5] = new ModelRenderer(this, 49, 42);
		parts[5].addBox(-2F, -12F, -2F, 4, 8, 4, 0.0F);

		// obsidian

		// base
		parts[6] = new ModelRenderer(this, 0, 21);
		parts[6].addBox(-8F, -4F, -8F, 16, 4, 16, 0.0F);

		// stick
		parts[7] = new ModelRenderer(this, 49, 21);
		parts[7].addBox(-2F, -12F, -2F, 4, 8, 4, 0.0F);
	}

	/**
	 * Set rendered device state and type
	 * 
	 * @param type transmitter [TRUE] or receiver [FALSE]
	 * @param on   active [TRUE] or passive[FALSE]
	 */
	public void setType(int type, boolean on) {
		parts[2].showModel = parts[3].showModel = (type == 0);
		parts[4].showModel = parts[5].showModel = (type == 1);
		parts[6].showModel = parts[7].showModel = (type == 2);

		parts[0].showModel = on;
		parts[1].showModel = !on;
	}

	/**
	 * Do render.
	 */
	public void render() {
		for (ModelRenderer part : parts) {
			if (part == null) {
				break;
			}

			part.render(0.0625F); // length of one size and position unit

		}
	}
}
