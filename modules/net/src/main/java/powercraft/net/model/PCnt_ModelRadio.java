package powercraft.net.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCnt_ModelRadio extends ModelBase {
	private ModelRenderer parts[], tinyp[];
	/** flag that the rendered model should be the "tiny" version. */
	public boolean tiny;

	/**
	 * Radio block model.
	 */
	public PCnt_ModelRadio() {

		textureWidth = 64;
		textureHeight = 34;

		parts = new ModelRenderer[9];

		tinyp = new ModelRenderer[5];
		tinyp[0] = new ModelRenderer(this, 22, 28);
		tinyp[0].addBox(-2.5F, -1F, -2.5F, 5, 1, 5, 0.0F);

		tinyp[1] = new ModelRenderer(this, 22, 21);
		tinyp[1].addBox(-0.5F, -4F, -0.5F, 1, 3, 1, 0.0F);

		tinyp[2] = new ModelRenderer(this, 22, 28);
		tinyp[2].addBox(-0.5F, -4F, -0.5F, 1, 3, 1, 0.0F);

		tinyp[3] = new ModelRenderer(this, 27, 21);
		tinyp[3].addBox(-1F, -4.1F, -1F, 2, 2, 2, 0.0F);

		tinyp[4] = new ModelRenderer(this, 36, 21);
		tinyp[4].addBox(-1F, -4.1F, -1F, 2, 2, 2, 0.0F);

		// base
		parts[0] = new ModelRenderer(this, 0, 0);
		parts[0].addBox(-8F, -4F, -8F, 16, 4, 16, 0.0F);

		// bulb on
		parts[1] = new ModelRenderer(this, 27, 21);
		parts[1].addBox(-1F, -14F, -1F, 2, 2, 2, 0.0F);

		// bulb off
		parts[2] = new ModelRenderer(this, 36, 21);
		parts[2].addBox(-1F, -14F, -1F, 2, 2, 2, 0.0F);

		// transmitter
		parts[3] = new ModelRenderer(this, 0, 21);
		parts[3].addBox(-1.5F, -7F, -1.5F, 3, 3, 3, 0.0F);

		parts[4] = new ModelRenderer(this, 13, 21);
		parts[4].addBox(-1F, -10F, -1F, 2, 3, 2, 0.0F);

		parts[5] = new ModelRenderer(this, 22, 21);
		parts[5].addBox(-0.5F, -13F, -0.5F, 1, 3, 1, 0.0F);

		// receiver
		parts[6] = new ModelRenderer(this, 0, 28);
		parts[6].addBox(-1.5F, -7F, -1.5F, 3, 3, 3, 0.0F);

		parts[7] = new ModelRenderer(this, 13, 28);
		parts[7].addBox(-1F, -10F, -1F, 2, 3, 2, 0.0F);

		parts[8] = new ModelRenderer(this, 22, 28);
		parts[8].addBox(-0.5F, -13F, -0.5F, 1, 3, 1, 0.0F);

	}

	/**
	 * Set rendered device state and type
	 * 
	 * @param transmitter transmitter [TRUE] or receiver [FALSE]
	 * @param on          active [TRUE] or passive[FALSE]
	 */
	public void setType(boolean transmitter, boolean on) {
		parts[6].showModel = parts[7].showModel = parts[8].showModel = !transmitter;
		parts[3].showModel = parts[4].showModel = parts[5].showModel = transmitter;
		parts[1].showModel = on;
		parts[2].showModel = !on;

		tinyp[1].showModel = transmitter;
		tinyp[2].showModel = !transmitter;
		tinyp[3].showModel = on;
		tinyp[4].showModel = !on;
	}

	/**
	 * Do render.
	 */
	public void render() {
		if (!tiny) {
			for (ModelRenderer part : parts) {
				if (part == null) {
					break;
				}
				part.render(0.0625F); // length of one size and position unit
			}
		} else {
			for (ModelRenderer part : tinyp) {
				if (part == null) {
					break;
				}
				part.render(0.0625F); // length of one size and position unit
			}
		}
	}
}
