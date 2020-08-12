package powercraft.light.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCli_ModelLight extends ModelBase {

	private ModelRenderer parts[];

	/**
	 * Radio block model.
	 */
	public PCli_ModelLight() {

		textureWidth = 70;
		textureHeight = 20;

		parts = new ModelRenderer[2];

		// small
		parts[0] = new ModelRenderer(this, 0, 0);
		parts[0].addBox(-3F, 6F, -3F, 6, 2, 6, 0.0F);

		// huge
		parts[1] = new ModelRenderer(this, 11, 0);
		parts[1].addBox(-7F, 5F, -7F, 14, 3, 14, 0.0F);

	}

	/**
	 * Do render.
	 */
	public void renderNormal() {
		parts[0].render(0.0625F); // length of one size and position unit
	}

	/**
	 * Do render.
	 */
	public void renderHuge() {
		parts[1].render(0.0625F); // length of one size and position unit
	}

}
