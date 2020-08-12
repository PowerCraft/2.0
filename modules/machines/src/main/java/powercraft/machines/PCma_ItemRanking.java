package powercraft.machines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.utils.PC_Struct3;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Property;

public class PCma_ItemRanking {

	private static List<PC_Struct3<ItemStack, Float, Integer>> ranking = new ArrayList<PC_Struct3<ItemStack, Float, Integer>>();
	private static List<ItemStack> alreadyDone = new ArrayList<ItemStack>();
	private static boolean hasInit = false;

	private static void setRank(ItemStack itemstack, int rank) {
		PC_Struct3<ItemStack, Float, Integer> s = get(itemstack);
		if (s == null) {
			ranking.add(s = new PC_Struct3<ItemStack, Float, Integer>(itemstack, 0.0f, 0));
		}
		s.b *= s.c;
		s.b += rank;
		s.c++;
		s.b /= s.c;
	}

	private static PC_Struct3<ItemStack, Float, Integer> get(ItemStack pcisi) {
		for (PC_Struct3<ItemStack, Float, Integer> s : ranking) {
			if (s.a.equals(pcisi)) {
				return s;
			}
		}
		return null;
	}

	public static float getRank(ItemStack itemstack) {
		PC_Struct3<ItemStack, Float, Integer> s = get(itemstack);
		if (s != null) {
			return s.b;
		}
		return 0;
	}

	private static boolean makeTree() {
		boolean anyDone = false;
		/*
		 * for(Item item:
		 * ((FMLControlledNamespacedRegistry)GameData.getItemRegistry())){
		 * if(item!=null){ List<ItemStack> l = null; if(item instanceof PC_IItemInfo){ l
		 * = ((PC_IItemInfo)item).getItemStacks(new ArrayList<ItemStack>()); }else if
		 * (item instanceof ItemBlock) { Block b =
		 * Block.getBlockFromItem((ItemBlock)item); if (b != null){ if (b instanceof
		 * PC_IItemInfo){ l = ((PC_IItemInfo)b).getItemStacks(new
		 * ArrayList<ItemStack>()); }else{ if (item.getHasSubtypes()){ l = new
		 * ArrayList<ItemStack>(); boolean bo = false; for (int j = 0; true; j++){
		 * ItemStack is = new ItemStack(b, 1, j); if
		 * (PC_RecipeRegistry.getRecipesForProduct(is).size() > 0 ||
		 * PC_RecipeRegistry.getFeedstock(is).size() > 0) { l.add(is); bo = false;
		 * }else{ if(bo) break; bo = true; } } }else{ l = new ArrayList<ItemStack>();
		 * l.add(new ItemStack(b)); } } } }else if (item.getHasSubtypes()){ l = new
		 * ArrayList<ItemStack>(); boolean b = false; for (int j = 0; true; j++){
		 * ItemStack is = new ItemStack(item, 1, j); if
		 * (PC_RecipeRegistry.getRecipesForProduct(is).size() > 0 ||
		 * PC_RecipeRegistry.getFeedstock(is).size() > 0) { l.add(is); b = false; }else{
		 * if(b) break; b = true; } } }else{ l = new ArrayList<ItemStack>(); l.add(new
		 * ItemStack(item)); } if(l!=null){ for(ItemStack is:l){ ItemStack pcis = is;
		 * if(alreadyDone.contains(pcis)) continue; List<IRecipe> recipes =
		 * PC_RecipeRegistry.getRecipesForProduct(is); List<ItemStack> feedstocks =
		 * PC_RecipeRegistry.getFeedstock(is);
		 * if(recipes.size()==0&&feedstocks.size()==0){ if(get(pcis)==null){
		 * setRank(pcis, 10000); anyDone = true; } if(!alreadyDone.contains(pcis))
		 * alreadyDone.add(pcis); } for(ItemStack itemstack:feedstocks){ ItemStack pcisi
		 * = itemstack; PC_Struct3<ItemStack, Float, Integer> s = get(pcisi); if(s !=
		 * null){ setRank(pcis, (int)(Math.min(s.b+100, s.b*2))); anyDone = true;
		 * if(!alreadyDone.contains(pcis)) alreadyDone.add(pcis); } } for(IRecipe
		 * recipe:recipes){ List<ItemStack>[][] input =
		 * PC_RecipeRegistry.getExpectedInput(recipe, -1, -1); if(input==null){
		 * continue; } float rank=0; for(int x=0; x<input.length; x++){ for(int y=0;
		 * y<input[x].length; y++){ List<ItemStack> list = input[x][y]; if(list==null ||
		 * list.size()<=0) continue; float bestRank = -1; for(ItemStack pcisi:list){
		 * PC_Struct3<ItemStack, Float, Integer>s = get(pcisi); if(s!=null) {
		 * if(bestRank == -1 || bestRank>s.b) bestRank = s.b; } } if(bestRank == -1){
		 * rank = -1; break; }else{ rank += bestRank; } } } if(rank>0){ rank /=
		 * recipe.getRecipeOutput().stackSize; setRank(pcis, (int)(rank+2.5)); anyDone =
		 * true; if(!alreadyDone.contains(pcis)) alreadyDone.add(pcis); } } } } } }
		 */
		return anyDone;
	}

	private static Item getItem(String key) {
		// if(i!=null && key.equals(i.getUnlocalizedName()))
		// return i;
		return null;
	}

	private static void load(String key, PC_Property prop) {
		if (prop.hasChildren()) {
			if (!key.equals("")) {
				key += ".";
			}
			HashMap<String, PC_Property> porps = prop.getPropertys();
			for (Entry<String, PC_Property> e : porps.entrySet()) {
				load(key + e.getKey(), e.getValue());
			}
		} else {
			Item item;
			item = getItem(key);
			List<Integer> l = PC_Utils.parseIntList(prop.getString());
			int num = 0;
			for (Integer i : l) {
				if (i > 0)
					setRank(new ItemStack(item, 1, num), i);
				num++;
			}
		}
	}

	private static void reg(PC_Property prop, Item item, String nums) {
		// prop.getString(item, nums, item.getUnlocalizedName());
	}

	private static void reg(PC_Property prop, Block block, String nums) {
		// prop.getString(block, nums,
		// Item.getItemFromBlock(block).getUnlocalizedName());
	}

	public static void init() {
		if (hasInit)
			return;

		File file = new File(PC_Utils.getPowerCraftFile(), "/itemRanks.cfg");

		PC_Property prop;

		try {
			prop = PC_Property.loadFromFile(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			prop = new PC_Property();
		}

		reg(prop, Blocks.bedrock, "0");
		reg(prop, Blocks.grass, "1");
		reg(prop, Blocks.dirt, "1");
		reg(prop, Blocks.cobblestone, "1");
		reg(prop, Blocks.stonebrick, "1");
		reg(prop, Blocks.sand, "1");
		reg(prop, Blocks.gravel, "1");
		reg(prop, Blocks.gold_ore, "1000");
		reg(prop, Blocks.iron_ore, "500");
		reg(prop, Blocks.coal_ore, "250");
		reg(prop, Blocks.planks, "1, 1, 1, 1");
		reg(prop, Blocks.sponge, "1");
		reg(prop, Blocks.lapis_ore, "500");
		reg(prop, Blocks.web, "1");
		reg(prop, Blocks.mossy_cobblestone, "500");
		reg(prop, Blocks.obsidian, "2000");
		reg(prop, Blocks.fire, "1");
		reg(prop, Blocks.mob_spawner, "4000");
		reg(prop, Blocks.diamond_ore, "2000");
		// reg(prop, instanceof BlockCrops, "1");//TODO: add other agriculture and
		// flowers
		reg(prop, Blocks.redstone_ore, "250");
		reg(prop, Blocks.snow, "1");
		reg(prop, Blocks.ice, "1");
		reg(prop, Blocks.cactus, "20");
		reg(prop, Blocks.pumpkin, "100");
		reg(prop, Blocks.netherrack, "1");
		reg(prop, Blocks.soul_sand, "10");
		reg(prop, Blocks.vine, "1");
		reg(prop, Blocks.mycelium, "1");
		reg(prop, Blocks.waterlily, "1000");
		reg(prop, Blocks.nether_brick, "10");
		reg(prop, Blocks.nether_wart, "10");
		reg(prop, Blocks.cauldron, "1");
		reg(prop, Blocks.end_stone, "100");
		reg(prop, Blocks.dragon_egg, "100000");
		reg(prop, Blocks.emerald_ore, "4000");
		reg(prop, Blocks.tripwire, "100");
		reg(prop, Blocks.command_block, "1000");
		reg(prop, Blocks.flower_pot, "10");
		reg(prop, Blocks.skull, "1");
		reg(prop, Items.apple, "10");
		// reg(prop, Items., "10"); new name silk?
		reg(prop, Items.feather, "1");
		reg(prop, Items.gunpowder, "100");
		reg(prop, Items.wheat_seeds, "10");
		reg(prop, Items.melon_seeds, "10");
		reg(prop, Items.pumpkin_seeds, "10");
		reg(prop, Items.wheat, "10");
		reg(prop, Items.flint, "10");
		reg(prop, Items.cooked_porkchop, "1");
		reg(prop, Items.water_bucket, "1000");
		reg(prop, Items.lava_bucket, "10000");
		reg(prop, Items.saddle, "1000");
		reg(prop, Items.snowball, "10");
		reg(prop, Items.leather, "1");
		reg(prop, Items.milk_bucket, "1000");
		reg(prop, Items.clay_ball, "1");
		reg(prop, Items.reeds, "1");
		reg(prop, Items.slime_ball, "100");
		reg(prop, Items.egg, "10");
		reg(prop, Items.glowstone_dust, "100");
		reg(prop, Items.cooked_fished, "100");
		reg(prop, Items.bone, "10");
		reg(prop, Items.melon, "10");
		reg(prop, Items.cooked_beef, "10");
		reg(prop, Items.cooked_chicken, "10");
		reg(prop, Items.rotten_flesh, "1");
		reg(prop, Items.ender_pearl, "1000");
		reg(prop, Items.blaze_rod, "10000");
		reg(prop, Items.ghast_tear, "5000");
		reg(prop, Items.spider_eye, "10");
		reg(prop, Items.experience_bottle, "1000");
		reg(prop, Items.written_book, "1000");
		reg(prop, Items.carrot, "10");
		reg(prop, Items.potato, "10");
		reg(prop, Items.poisonous_potato, "1");
		reg(prop, Items.nether_star, "100000");
		reg(prop, Items.record_13, "10000");
		reg(prop, Items.record_cat, "10000");
		reg(prop, Items.record_blocks, "10000");
		reg(prop, Items.record_chirp, "10000");
		reg(prop, Items.record_far, "10000");
		reg(prop, Items.record_mall, "10000");
		reg(prop, Items.record_mellohi, "10000");
		reg(prop, Items.record_stal, "10000");
		reg(prop, Items.record_strad, "10000");
		reg(prop, Items.record_ward, "10000");
		reg(prop, Items.record_11, "10000");
		reg(prop, Items.record_wait, "10000");

		TreeMap<String, PC_Block> blocks = PC_BlockRegistry.getPCBlocks();
		TreeMap<String, PC_Item> items = PC_ItemRegistry.getPCItems();
		TreeMap<String, PC_ItemArmor> itemArmors = PC_ItemRegistry.getPCItemArmors();

		for (PC_Block block : blocks.values()) {
			/**
			 * TODO Object o = block.msg(PC_MSGRegistry.MSG_RATING); if(o instanceof List){
			 * List<Integer> rating = (List<Integer>)o; String s = ""; for(int i=0;
			 * i<rating.size(); i++){ s += rating.get(i); if(i<rating.size()-1) s+=", "; }
			 * reg(prop, block, s); }
			 */
		}

		for (PC_Item item : items.values()) {
			/**
			 * TODO Object o = item.msg(PC_MSGRegistry.MSG_RATING); if(o instanceof List){
			 * List<Integer> rating = (List<Integer>)o; String s = ""; for(int i=0;
			 * i<rating.size(); i++){ s += rating.get(i); if(i<rating.size()-1) s+=", "; }
			 * reg(prop, item, s); }
			 */
		}

		for (PC_ItemArmor itemArmor : itemArmors.values()) {
			/**
			 * TODO Object o = itemArmor.msg(PC_MSGRegistry.MSG_RATING); if(o instanceof
			 * List){ List<Integer> rating = (List<Integer>)o; String s = ""; for(int i=0;
			 * i<rating.size(); i++){ s += rating.get(i); if(i<rating.size()-1) s+=", "; }
			 * reg(prop, itemArmor, s); }
			 */
		}

		load("", prop);

		while (makeTree())
			;

		try {
			prop.save(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		hasInit = true;
	}

}
