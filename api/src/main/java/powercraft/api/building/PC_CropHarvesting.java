package powercraft.api.building;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Logger;

public class PC_CropHarvesting implements PC_ISpecialHarvesting {

	private static final File folder = new File(PC_Utils.getPowerCraftFile(), "/crops");
	private static final PC_Direction sideList[] = { PC_Direction.TOP, PC_Direction.FRONT, PC_Direction.BACK,
			PC_Direction.LEFT, PC_Direction.RIGHT, PC_Direction.BOTTOM };
	private static List<Crop> crops;
	private static Random rand = new Random();

	public PC_CropHarvesting() {
		loadCrops();
	}

	@Override
	public boolean useFor(World world, int x, int y, int z, Block block, int meta, int priority) {
		if (priority < 2) {
			return false;
		}
		if (getCropFor(block, meta) != null) {
			return true;
		}
		return block instanceof BlockCrops;
	}

	@Override
	public List<PC_Struct2<PC_VecI, ItemStack>> harvest(World world, int x, int y, int z, Block block, int meta,
			int fortune) {
		Crop crop = getCropFor(block, meta);
		if (crop != null) {
			return harvestSpecialCrop(world, x, y, z, block, meta, crop);
		}
		return harvestNormalCrop(world, x, y, z, block, meta, fortune);
	}

	private Crop getCropFor(Block block, int meta) {
		for (Crop crop : crops) {
			if (crop.isCrop(block, meta)) {
				return crop;
			}
		}
		return null;
	}

	private List<PC_Struct2<PC_VecI, ItemStack>> harvestNormalCrop(World world, int x, int y, int z, Block block,
			int meta, int fortune) {
		if (meta < 7) {
			return null;
		}
		List<ItemStack> drops = PC_BuildingManager.harvestEasy(world, x, y, z, fortune);
		List<PC_Struct2<PC_VecI, ItemStack>> dropsWithPlace = new ArrayList<PC_Struct2<PC_VecI, ItemStack>>();
		for (ItemStack drop : drops) {
			if (drop.stackSize > 0) {
				dropsWithPlace.add(new PC_Struct2<PC_VecI, ItemStack>(new PC_VecI(x, y, z), drop));
			}
		}
		if (!world.isRemote) {
			for (int s = 0; s < 6; s++) {
				PC_VecI offset = sideList[s].getOffset();
				Iterator<PC_Struct2<PC_VecI, ItemStack>> i = dropsWithPlace.iterator();
				while (i.hasNext()) {
					PC_Struct2<PC_VecI, ItemStack> drop = i.next();
					if (PC_BuildingManager.tryUseItem(world, x - offset.x, y - offset.y, z - offset.z, sideList[s],
							drop.b)) {
						if (drop.b.stackSize <= 0) {
							i.remove();
						}
						return dropsWithPlace;
					}
				}
			}
		}
		return dropsWithPlace;
	}

	private List<PC_Struct2<PC_VecI, ItemStack>> harvestSpecialCrop(World world, int x, int y, int z, Block block,
			int meta, Crop crop) {
		if (crop.isFinished(block, meta)) {
			// CropReplant replant = crop.getReplant(block.blockID, meta);
			// if(!world.isRemote)
			// PC_Utils.setBID(world, x, y, z, replant.replant.blockID,
			// replant.replant.metadata);
			List<PC_Struct2<PC_VecI, ItemStack>> drops = new ArrayList<PC_Struct2<PC_VecI, ItemStack>>();
			// for(ItemStack drop:replant.getDrops(meta)){
			// drops.add(new PC_Struct2<PC_VecI, ItemStack>(new PC_VecI(x, y, z), drop));
			// }
			return drops;
		}
		return null;
	}

	/**
	 * Call this method to explicitly init static fields -> list of crops from XML
	 * files
	 */
	private static void loadCrops() {
		if (crops != null) {
			return;
		}

		crops = new ArrayList<Crop>();

		PC_Logger.finer("Loading XML configuration for crops.");

		if (!folder.exists()) {
			folder.mkdirs();
		}

		if (!(new File(folder + "/default.xml")).exists()) {

			try {
				PC_Logger.finest("Generating default crops config in " + folder + "/default.xml");

				FileWriter out = new FileWriter(new File(folder + "/default.xml"));

				// @formatter:off
				// write the default crops
				out.write("<?xml version='1.1' encoding='UTF-8' ?>\n" + "<!-- \n" + "  BLOCK HARVESTER CONFIG FILE\n"
						+ "  You can add your own crops into this file.\n"
						+ "  Any other xml files in this folder will be parsed too.\n\n"
						+ "  If you make a setup file for some mod, please post it on forums.\n\n"
						+ "  Special values:\n" + "    metaMature  = -1  ...  any metadata\n"
						+ "    metaReplant = -1  ...  do not replant\n\n"
						+ "    Item meta   <  0  ...  get item with meta = blockMeta & abs(THIS_NUMBER) - useful for leaves\n\n"
						+ "  Item meta can be ranged - use 4-7 for random meta in range 4 to 7 (inclusive).\n"
						+ "  You can also use range for item count (eg. 0-5). \n\n"
						+ "  Higher rarity number means more rare. Use 1 for regular drops. \n" + "-->\n\n"
						+ "<crops>\n" + "\n" + "</crops>");
				// @formatter:on

				out.close();

			} catch (IOException e) {
				PC_Logger.severe("Generating default crops config file failed due to an IOException.");
				e.printStackTrace();
			}

		}

		String[] files = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.matches("[^.]+[.]xml");
			}
		});

		for (String filename : files) {

			PC_Logger.finest("* loading file " + filename + "...");
			File file = new File(folder + "/" + filename);
			parseFile(file);

		}

		PC_Logger.finer("Crops configuration loaded.");

	}

	/*
	 * <?xml version="1.0" encoding="UTF-8" ?> <crops> <crop name="My Crop"> <block
	 * id="79" metaReplant="0" metaMature="7"> <item id="318" meta="0" count="1-2"
	 * rarity="1"> <item id="319" meta="1-5" count="1-2" rarity="4"> </crop>
	 * </crops>
	 */

	/**
	 * Load and parse XML file with crops specs
	 * 
	 * @param file the file to load
	 */
	private static void parseFile(File file) {

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList cropsList = doc.getElementsByTagName("crop");

			croploop: for (int i = 0; i < cropsList.getLength(); i++) {

				Node cropNode = cropsList.item(i);
				if (cropNode.getNodeType() == Node.ELEMENT_NODE) {

					// process one crop entry

					Element crop = (Element) cropNode;

					// <block>
					NodeList blocks = crop.getElementsByTagName("block");

					// <item>
					NodeList items = crop.getElementsByTagName("item");
					if (blocks.getLength() < 1) {
						PC_Logger.warning(
								"Crop manager - parseFile - Error while parsing " + file + " - no <item>s in <crop>");
						continue croploop;
					}

					int itemCount = items.getLength();

					Crop c = new Crop();

					for (int j = 0; j < blocks.getLength(); j++) {

						Element block = (Element) blocks.item(j);

						// <block attrs>
						String block_id_s = block.getAttribute("id");

						if (block_id_s.equals("") || !block_id_s.matches("[0-9]+")) {
							PC_Logger.warning(
									"Crop manager - parseFile - Error while parsing " + file + " - bad block ID");
							continue croploop;
						}

						int id = Integer.parseInt(block_id_s);

						String block_meta_replant_s = block.getAttribute("metaReplant");

						if (block_meta_replant_s.equals("") || !block_meta_replant_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning(
									"Crop manager - parseFile - Error while parsing " + file + " - bad replant meta");
							continue croploop;
						}

						int meta_replant = Integer.parseInt(block_meta_replant_s);

						String block_meta_mature_s = block.getAttribute("metaMature");

						if (block_meta_mature_s.equals("") || !block_meta_mature_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning(
									"Crop manager - parseFile - Error while parsing " + file + " - bad mature meta");
							continue croploop;
						}

						int meta = Integer.parseInt(block_meta_mature_s);

						// c.replant.put(new CropState(block, meta), new CropReplant(block,
						// meta_replant));

					}

					itemloop: for (int j = 0; j < itemCount; j++) {

						try {
							int itemMetaA, itemMetaB, itemCountA, itemCountB, itemRarityA, itemRarityB, itemPriority,
									itemId;

							Element item = (Element) items.item(j);

							// id
							String item_id_s = item.getAttribute("id");

							if (item_id_s.equals("") || !item_id_s.matches("[0-9]+")) {
								PC_Logger.warning(
										"Crop manager - parseFile - Error while parsing " + file + " - bad item ID");
								continue croploop;
							}

							itemId = Integer.parseInt(item_id_s);

							// priority
							String item_priority_s = item.getAttribute("priority");

							if (item_id_s.equals("")) {

								item_priority_s = "1";

							} else if (!item_id_s.matches("[0-9]+")) {
								PC_Logger.warning(
										"Crop manager - parseFile - Error while parsing " + file + " - bad item ID");
								continue croploop;
							}

							itemPriority = Integer.parseInt(item_priority_s);

							// rarity 1/200
							String item_rarity_s = item.getAttribute("rarity");

							if (item_rarity_s.equals("")) {

								item_rarity_s = "1";

							}
							if (!item_rarity_s.matches("[0-9]+([/][0-9]+)?")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file
										+ " - bad item rarity");
								continue croploop;
							}

							String[] item_rarity_parts = item_rarity_s.split("/");

							if (item_rarity_parts.length == 1) {
								itemRarityA = 1;
								itemRarityB = Integer.parseInt(item_rarity_parts[0]);
							} else {
								itemRarityA = Integer.parseInt(item_rarity_parts[0]);
								itemRarityB = Integer.parseInt(item_rarity_parts[1]);

								if (itemRarityA > itemRarityB) {
									itemRarityA = itemRarityB = 1;
								}
							}

							// meta start-stop
							String item_meta_s = item.getAttribute("meta");

							if (item_meta_s.equals("")) {
								item_meta_s = "0";
							} else if (!item_meta_s.matches("[-]?[0-9]+") && !item_meta_s.matches("[0-9]+[-][0-9]+")) {
								PC_Logger.warning(
										"Crop manager - parseFile - Error while parsing " + file + " - bad item meta");
								continue croploop;
							}

							String[] item_meta_parts;

							if (item_meta_s.matches("[-]?[0-9]+")) {
								item_meta_parts = new String[1];
								item_meta_parts[0] = item_meta_s;

							} else {
								item_meta_parts = item_meta_s.split("-");
							}

							if (item_meta_parts.length == 1) {
								itemMetaA = itemMetaB = Integer.parseInt(item_meta_parts[0]);
							} else {
								itemMetaA = Integer.parseInt(item_meta_parts[0]);
								itemMetaB = Integer.parseInt(item_meta_parts[1]);

								if (itemMetaB < itemMetaA) {
									itemMetaB = itemMetaA;
								}
							}

							// cout start-stop
							String item_count_s = item.getAttribute("count");

							if (item_count_s.equals("")) {

								item_count_s = "1";

							} else if (!item_count_s.matches("[0-9]+(-[0-9]+)?")) {
								PC_Logger.warning(
										"Crop manager - parseFile - Error while parsing " + file + " - bad item count");
								continue croploop;
							}

							String[] item_count_parts = item_count_s.split("-");

							if (item_count_parts.length == 1) {
								itemCountA = itemCountB = Integer.parseInt(item_count_parts[0]);
							} else {
								itemCountA = Integer.parseInt(item_count_parts[0]);
								itemCountB = Integer.parseInt(item_count_parts[1]);

								if (itemCountB < itemCountA) {
									itemCountB = itemCountA;
								}
							}

							for (CropReplant replant : c.replant.values()) {
								replant.drops.add(new CropDrops(itemId, itemMetaA, itemMetaB, itemCountA, itemCountB,
										itemRarityA, itemRarityB, itemPriority));
							}

						} catch (NumberFormatException e) {
							continue itemloop;
						}

					}

					crops.add(c);

					HashMap<Block, Integer> mainMeta = new HashMap<Block, Integer>();
					for (CropReplant replant : c.replant.values()) {
						Integer meta;
						if ((meta = mainMeta.get(replant.replant.blockID)) == null) {
							mainMeta.put(replant.replant.blockID, replant.replant.metadata);
						} else {
							if ((meta < replant.replant.metadata || replant.replant.metadata == -1) && meta != -1) {
								mainMeta.put(replant.replant.blockID, replant.replant.metadata);
							}
						}
					}

					for (Entry<Block, Integer> growing : mainMeta.entrySet()) {
						if (growing.getValue() > 0) {
							for (int j = 0; j < growing.getValue(); j++) {
								c.replant.put(new CropState(growing.getKey(), j), null);
							}
						}
					}

					PC_Logger.finest("   - Loaded crop \"" + crop.getAttribute("name") + "\".");

				}

			}

		} catch (SAXParseException err) {
			PC_Logger.severe("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			PC_Logger.severe(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static class CropState {

		public Block blockID;
		public int metadata;

		public CropState(Block blockID, int metadata) {
			this.blockID = blockID;
			this.metadata = metadata;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CropState)) {
				return false;
			}
			CropState other = (CropState) obj;
			return other.blockID == blockID && (other.metadata == metadata || metadata == -1);
		}

	}

	private static class CropDrops {

		public int itemId;
		public int itemMetaA;
		public int itemMetaB;
		public int itemCountA;
		public int itemCountB;
		public int itemRarityA;
		public int itemRarityB;
		public int itemPriority;

		public CropDrops(int itemId, int itemMetaA, int itemMetaB, int itemCountA, int itemCountB, int itemRarityA,
				int itemRarityB, int itemPriority) {
			this.itemId = itemId;
			this.itemMetaA = itemMetaA;
			this.itemMetaB = itemMetaB;
			this.itemCountA = itemCountA;
			this.itemCountB = itemCountB;
			this.itemRarityA = itemRarityA;
			this.itemRarityB = itemRarityB;
			this.itemPriority = itemPriority;
		}

	}

	private static class CropReplant {

		public CropState replant;
		public List<CropDrops> drops = new ArrayList<CropDrops>();

		public CropReplant(Block block, int metadata) {
			replant = new CropState(block, metadata);
		}

		public List<ItemStack> getDrops(int meta) {
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();

			for (int priorityTurn = 1; priorityTurn < 20; priorityTurn++) {

				int itemsOfPriority = 0;
				int itemsDropped = 0;

				for (CropDrops drop : drops) {
					if (drop.itemPriority == priorityTurn) {

						itemsOfPriority++;

						if (drop.itemRarityB > 0 && rand.nextInt(drop.itemRarityB) < drop.itemRarityA) {

							int stackMeta;

							if (drop.itemMetaA < 0) {
								stackMeta = meta & (-drop.itemMetaA);
							} else {
								stackMeta = drop.itemMetaA + rand.nextInt(drop.itemMetaB - drop.itemMetaA + 1);
							}

							int stackCount = drop.itemCountA + rand.nextInt(drop.itemCountB - drop.itemCountA + 1);

							// if (stackMeta >= 0 && stackMeta < 32000 && stackCount > 0 &&
							// Items.itemsList[drop.itemId] != null) {

							// stacks.add(new ItemStack(drop.item, stackCount, stackMeta));
							// itemsDropped++;

							// }
						}

					}

				}

				if (itemsOfPriority == 0) {
					break;
				}

				if (itemsOfPriority > 0 && itemsDropped > 0) {
					break;
				}

			}

			if (stacks.size() == 0) {
				return null;
			}

			return stacks;
		}

	}

	private static class Crop {

		public HashMap<CropState, CropReplant> replant = new HashMap<CropState, CropReplant>();

		public boolean isCrop(Block blockID, int metadata) {
			if (isGrowing(blockID, metadata)) {
				return true;
			}
			return isFinished(blockID, metadata);
		}

		public boolean isGrowing(Block blockID, int metadata) {
			CropState cs = new CropState(blockID, metadata);
			return replant.containsKey(cs) && replant.get(cs) == null;
		}

		public boolean isFinished(Block blockID, int metadata) {
			CropState cs = new CropState(blockID, metadata);
			return replant.containsKey(cs) && replant.get(cs) != null;
		}

		public CropReplant getReplant(Block blockID, int metadata) {
			return replant.get(new CropState(blockID, metadata));
		}

	}

}
