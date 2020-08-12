package powercraft.api.building;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Logger;

public class PC_TreeHarvesting implements PC_ISpecialHarvesting {

	private static final File folder = new File(PC_Utils.getPowerCraftFile(), "/trees");
	private static int MAXRECURSION = 1000;
	private static List<Tree> trees;

	public PC_TreeHarvesting() {
		loadTrees();
	}

	@Override
	public boolean useFor(World world, int x, int y, int z, Block block, int meta, int priority) {
		if (priority < 2) {
			return false;
		}
		return getTreeFor(block, meta) != null;
	}

	@Override
	public List<PC_Struct2<PC_VecI, ItemStack>> harvest(World world, int x, int y, int z, Block block, int meta,
			int fortune) {
		Tree tree = getTreeFor(block, meta);
		List<PC_Struct2<PC_VecI, ItemStack>> drops = new ArrayList<PC_Struct2<PC_VecI, ItemStack>>();
		if (!world.isRemote)
			harvestWood(world, x, y, z, block, meta, fortune, tree, drops, 0);
		return drops;
	}

	public void harvestWood(World world, int x, int y, int z, Block block, int meta, int fortune, Tree tree,
			List<PC_Struct2<PC_VecI, ItemStack>> drops, int recursion) {
		List<ItemStack> blockDrops = PC_BuildingManager.harvestEasy(world, x, y, z, fortune);
		if (blockDrops != null) {
			for (ItemStack blockDrop : blockDrops) {
				drops.add(new PC_Struct2<PC_VecI, ItemStack>(new PC_VecI(x, y, z), blockDrop));
			}
		}
		if (recursion < MAXRECURSION) {
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						block = PC_Utils.getBlock(world, nx, ny, nz);
						meta = PC_Utils.getMD(world, nx, ny, nz);
						if (block != null) {
							if (tree.woods.contains(new TreeState(block, meta))) {
								harvestWood(world, nx, ny, nz, block, meta, fortune, tree, drops, recursion + 1);
							}
						}
					}
				}
			}
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						block = PC_Utils.getBlock(world, nx, ny, nz);
						meta = PC_Utils.getMD(world, nx, ny, nz);
						if (block != null) {
							if (tree.leaves.contains(new TreeState(block, meta))) {
								harvestLeaves(world, nx, ny, nz, block, meta, fortune, tree, drops, recursion + 1);
							}
						}
					}
				}
			}
			block = PC_Utils.getBlock(world, x, y - 1, z);
			if (block != null) {
				Iterator<PC_Struct2<PC_VecI, ItemStack>> i = drops.iterator();
				while (i.hasNext()) {
					PC_Struct2<PC_VecI, ItemStack> drop = i.next();
					for (TreeState sampling : tree.saplings) {
						// if(sampling == drop.b && (sampling.metadata == drop.b.getItemDamage() ||
						// sampling.metadata==-1)){
						if (PC_BuildingManager.tryUseItem(world, x, y - 1, z, PC_Direction.TOP, drop.b)) {
							if (drop.b.stackSize <= 0) {
								i.remove();
							}
							return;
						}
					}
				}
			}
		}
	}
	// }

	public void harvestLeaves(World world, int x, int y, int z, Block block, int meta, int fortune, Tree tree,
			List<PC_Struct2<PC_VecI, ItemStack>> drops, int recursion) {
		List<ItemStack> blockDrops = PC_BuildingManager.harvestEasy(world, x, y, z, fortune);
		if (blockDrops != null) {
			for (ItemStack blockDrop : blockDrops) {
				drops.add(new PC_Struct2<PC_VecI, ItemStack>(new PC_VecI(x, y, z), blockDrop));
			}
		}
		if (recursion < MAXRECURSION) {
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						block = PC_Utils.getBlock(world, nx, ny, nz);
						meta = PC_Utils.getMD(world, nx, ny, nz);
						if (block != null) {
							if (tree.leaves.contains(new TreeState(block, meta))) {
								harvestLeaves(world, nx, ny, nz, block, meta, fortune, tree, drops, recursion + 1);
							}
						}
					}
				}
			}
		}
	}

	private Tree getTreeFor(Block block, int meta) {
		for (Tree tree : trees) {
			if (tree.woods.contains(new TreeState(block, meta))) {
				return tree;
			}
		}
		return null;
	}

	/**
	 * Load trees data from file.
	 */
	public static void loadTrees() {
		if (trees != null) {
			return;
		}

		trees = new ArrayList<Tree>();

		for (int i = 0; i < 4; i++) {
			Tree tree = new Tree();
			tree.woods.add(new TreeState(Blocks.log, i, 3));
			tree.leaves.add(new TreeState(Blocks.leaves, i, 3));
			tree.saplings.add(new TreeState(Blocks.sapling, i));
			trees.add(tree);
		}

		Tree tree = new Tree();
		// tree.woods.add(new TreeState(Block.mushroomCapBrown.blockID, -1));
		// tree.saplings.add(new TreeState(Block.mushroomBrown.blockID, 0));
		trees.add(tree);

		tree = new Tree();
		// tree.woods.add(new TreeState(Block.mushroomCapRed.blockID, -1));
		// tree.saplings.add(new TreeState(Block.mushroomRed.blockID, 0));
		trees.add(tree);

		PC_Logger.finer("Loading XML configuration for trees.");

		if (!folder.exists()) {
			folder.mkdir();
		}

		if (!(new File(folder + "/" + "default.xml")).exists()) {

			try {
				PC_Logger.finest("Generating default trees configuration file in " + folder + "/trees.xml");

				FileWriter out;

				out = new FileWriter(new File(folder + "/" + "default.xml"));

				// @formatter:off
				// write the default crops
				try {
					out.write("<?xml version='1.1' encoding='UTF-8' ?>\n" + "<!-- \n"
							+ " This file defines trees harvestable automatically (eg. by harvester machine)\n"
							+ " The purpose of this system is to make PowerCraft compatible with new trees from mods.\n"
							+ " All files in 'trees' directory will be parsed, so please DO NOT EDIT THIS FILE but make your own.\n"
							+ "-->\n\n" + "<trees>\n" + "\n" + "\t<tree name='Oak'>\n"
							+ "\t\t<wood id='17' meta='0' />\n" + "\t\t<leaves id='18' meta='0' />\n"
							+ "\t\t<sapling id='6' meta='0' />\n" + "\t</tree>\n" + "\n" + "\t<tree name='Pine'>\n"
							+ "\t\t<wood id='17' meta='1' />\n" + "\t\t<leaves id='18' meta='1' />\n"
							+ "\t\t<sapling id='6' meta='1' />\n" + "\t</tree>\n" + "\n" + "\t<tree name='Birch'>\n"
							+ "\t\t<wood id='17' meta='2' />\n" + "\t\t<leaves id='18' meta='2' />\n"
							+ "\t\t<sapling id='6' meta='2' />\n" + "\t</tree>\n" + "\n" + "\t<tree name='Jungle'>\n"
							+ "\t\t<wood id='17' meta='3' />\n" + "\t\t<leaves id='18' meta='3' />\n"
							+ "\t\t<sapling id='6' meta='3' />\n" + "\t</tree>\n" + "\n"
							+ "\t<tree name='Huge Brown Mushroom'>\n" + "\t\t<wood id='99' meta='-1' />\n"
							+ "\t\t<sapling id='39' meta='0' />\n" + "\t</tree>\n" + "\n"
							+ "\t<tree name='Huge Red Mushroom'>\n" + "\t\t<wood id='100' meta='-1' />\n"
							+ "\t\t<sapling id='40' meta='0' />\n" + "\t</tree>\n" + "\n" + "</trees>");
				} catch (IOException e) {
					e.printStackTrace();
				}
				// @formatter:on

				out.close();
			} catch (IOException e) {
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

		PC_Logger.finer("Trees configuration loaded.");

	}

	/**
	 * Load and parse XML file with tree specs
	 * 
	 * @param file the file to load
	 */
	private static void parseFile(File file) {

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList treesList = doc.getElementsByTagName("tree");

			treeloop: for (int i = 0; i < treesList.getLength(); i++) {

				Node treeNode = treesList.item(i);
				if (treeNode.getNodeType() == Node.ELEMENT_NODE) {

					// process one crop entry

					Element tree = (Element) treeNode;

					// <wood>
					NodeList woodlist = tree.getElementsByTagName("wood");
					if (woodlist.getLength() != 1) {
						PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file
								+ " - invalid no. of <wood> tags in <tree>");
						continue treeloop;
					}

					Element wood = (Element) woodlist.item(0);

					// <leaves>
					NodeList leaveslist = tree.getElementsByTagName("leaves");
					Element leaves = null;
					if (leaveslist.getLength() == 1) {
						leaves = (Element) leaveslist.item(0);
					}

					// <sapling>
					NodeList saplinglist = tree.getElementsByTagName("sapling");
					Element sapling = null;
					if (saplinglist.getLength() == 1) {
						sapling = (Element) saplinglist.item(0);
					}

					// parse wood.

					TreeState woodStruct;
					TreeState leavesStruct = null;
					TreeState saplingStruct = null;

					String woodId_s = wood.getAttribute("id");

					if (woodId_s.equals("") || !woodId_s.matches("[0-9]+")) {
						PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad wood ID");
						continue treeloop;
					}

					int wood_id = Integer.parseInt(woodId_s);

					String woodMeta_s = wood.getAttribute("meta");

					if (woodMeta_s.equals("") || !woodMeta_s.matches("-?[0-9]+")) {
						PC_Logger
								.warning("Tree manager - parseFile - Error while parsing " + file + " - bad wood meta");
						continue treeloop;
					}

					int wood_meta = Integer.parseInt(woodMeta_s);

					// woodStruct = new TreeState(wood_id, wood_meta);

					if (leaves != null) {

						String leavesId_s = leaves.getAttribute("id");

						if (leavesId_s.equals("") || !leavesId_s.matches("[0-9]+")) {
							PC_Logger.warning(
									"Tree manager - parseFile - Error while parsing " + file + " - bad leaves ID");
							continue treeloop;
						}

						int leaves_id = Integer.parseInt(leavesId_s);

						String leavesMeta_s = leaves.getAttribute("meta");

						if (leavesMeta_s.equals("") || !leavesMeta_s.matches("-?[0-9]+")) {
							PC_Logger.warning(
									"Tree manager - parseFile - Error while parsing " + file + " - bad leaves meta");
							continue treeloop;
						}

						int leaves_meta = Integer.parseInt(leavesMeta_s);

						// leavesStruct = new TreeState(leaves_id, leaves_meta);

					}

					if (sapling != null) {

						String saplingId_s = sapling.getAttribute("id");

						if (saplingId_s.equals("") || !saplingId_s.matches("[0-9]+")) {
							PC_Logger.warning(
									"Tree manager - parseFile - Error while parsing " + file + " - bad sapling ID");
							continue treeloop;
						}

						int sapling_id = Integer.parseInt(saplingId_s);

						String saplingMeta_s = sapling.getAttribute("meta");

						if (saplingMeta_s.equals("") || !saplingMeta_s.matches("[0-9]+")) {
							PC_Logger.warning(
									"Tree manager - parseFile - Error while parsing " + file + " - bad sapling meta");
							continue treeloop;
						}

						int sapling_meta = Integer.parseInt(saplingMeta_s);

						// saplingStruct = new TreeState(sapling_id, sapling_meta);

					}

					Tree ttree = new Tree();

					// ttree.woods.add(woodStruct);
					// if(leavesStruct!=null){
					// ttree.leaves.add(leavesStruct);
					// }
					// if(saplingStruct!=null){
					// ttree.saplings.add(saplingStruct);
					// }
					trees.add(ttree);

					PC_Logger.finest("   - Tree \"" + tree.getAttribute("name") + "\" loaded. -> " + ttree);

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

	private static class TreeState {

		public Block block;
		public int metadata;
		public int bitMask;

		public TreeState(Block block, int metadata) {
			this.block = block;
			this.metadata = metadata;
			this.bitMask = -1;
		}

		public TreeState(Block block, int metadata, int bitMask) {
			this.block = block;
			this.metadata = metadata;
			this.bitMask = bitMask;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TreeState)) {
				return false;
			}
			TreeState other = (TreeState) obj;
			return other.block == block && ((other.metadata & bitMask) == (metadata & other.bitMask) || metadata == -1);
		}

	}

	private static class Tree {

		public List<TreeState> woods = new ArrayList<TreeState>();
		public List<TreeState> leaves = new ArrayList<TreeState>();
		public List<TreeState> saplings = new ArrayList<TreeState>();

	}

}
