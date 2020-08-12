package powercraft.light.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCli_ModelPrism extends ModelBase {
	/** the central crystal */
	public ModelRenderer mainCrystal;
	/** glass panels */
	public ModelRenderer sides[];

	/**
	 * prism
	 */
	public PCli_ModelPrism() {
		mainCrystal = new ModelRenderer(this, 0, 0);
		mainCrystal.addBox(-4, -4, -4, 8, 8, 8, 0.0F);
		mainCrystal.addBox(-5, -3, -3, 10, 6, 6, 0.0F);
		mainCrystal.addBox(-3, -5, -3, 6, 10, 6, 0.0F);
		mainCrystal.addBox(-3, -3, -5, 6, 6, 10, 0.0F);

		sides = new ModelRenderer[10];

		sides[0] = new ModelRenderer(this, 32, 18);
		sides[0].addBox(-3F, 5F, -3F, 6, 2, 6, 0.0F);

		sides[1] = new ModelRenderer(this, 32, 18);
		sides[1].addBox(-3F, -7F, -3F, 6, 2, 6, 0.0F);

		for (int i = 2; i <= 9; i++) {
			sides[i] = new ModelRenderer(this, 32, 18);
			sides[i].addBox(5F, -3F + (i % 2 == 0 ? 0.01F : -0.01F), -3F, 2, 6, 6, 0.0F);
			sides[i].rotateAngleY = (float) (Math.PI * 0.25D * (i - 2));
		}

	}

	/**
	 * render prism
	 */
	public void renderPrism() {
		mainCrystal.render(0.0625F);

		for (ModelRenderer sideCrystal : sides) {
			sideCrystal.render(0.0625F);
		}
	}

}
