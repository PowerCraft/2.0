package powercraft.api.item;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.interfaces.PC_INBT;

public class PC_ItemStack implements Externalizable, PC_INBT<PC_ItemStack> {

	public static final long serialVersionUID = 1522073818686692550L;

	private Object o;
	private int count;
	private int meta;
	private NBTTagCompound nbtTag;

	public PC_ItemStack() {

	}

	public PC_ItemStack(Object o, int count, int meta) {
		this.o = o;
		this.count = count;
		this.meta = meta;
	}

	public PC_ItemStack(ItemStack is) {
		if (is.getItem() instanceof ItemBlock) {
			this.o = (ItemBlock) is.getItem();
		} else {
			this.o = is.getItem();
		}
		count = is.stackSize;
		meta = is.getItemDamage();
		if (meta == 32767)
			meta = -1;
		if (is.stackTagCompound != null) {
			nbtTag = (NBTTagCompound) is.stackTagCompound.copy();
		}
	}

	public PC_ItemStack(Object o) {
		this(o, 1, 0);
	}

	public PC_ItemStack(Object o, int count) {
		this(o, count, 0);
	}

	public ItemStack toItemStack() {
		ItemStack is;
		if (o instanceof Block) {
			is = new ItemStack((Block) o, count, meta);
		} else if (o instanceof Item) {
			is = new ItemStack((Item) o, count, meta);
		} else {
			return null;
		}

		if (nbtTag != null) {
			is.stackTagCompound = (NBTTagCompound) nbtTag.copy();
		}

		return is;
	}

	public void setNBTTag(NBTTagCompound nbtTag) {
		this.nbtTag = nbtTag;
	}

	public NBTTagCompound getNBTTag() {
		return nbtTag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMeta() {
		return meta;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemStack || obj instanceof PC_ItemStack) {
			int otherID;
			int otherMeta;
			NBTTagCompound otherNbtTag;

			if (obj instanceof ItemStack) {
				otherMeta = ((ItemStack) obj).getItemDamage();
				if (otherMeta == 32767)
					otherMeta = -1;
				otherNbtTag = ((ItemStack) obj).stackTagCompound;
			} else {
				otherMeta = ((PC_ItemStack) obj).getMeta();
				otherNbtTag = ((PC_ItemStack) obj).getNBTTag();
			}

			if (o instanceof PC_Item) {
				Object e = ((PC_Item) o).areItemsEqual(this, otherMeta, otherNbtTag);
				if (e instanceof Boolean)
					return (Boolean) e;
			}
			if (otherMeta == meta || otherMeta == -1 || meta == -1) {
				if (otherNbtTag == null && nbtTag == null) {
					return true;
				} else if (otherNbtTag != null && nbtTag != null) {
					return otherNbtTag.equals(nbtTag);
				}
			}
		}

		return false;
	}

	public PC_ItemStack copy() {
		return new PC_ItemStack(o, count, meta);
	}

	// @Override
	// public String toString() {
	// return "PC_ItemStack(" +
	// Item.itemsList[getID()].getLocalizedName(toItemStack()) + ", " + count + ", "
	// + meta + ")";
	// }

	@Override
	public void readExternal(ObjectInput inp) throws IOException, ClassNotFoundException {
		int id = inp.readInt();
		// o = Item.itemsList[id];
		count = inp.readInt();
		meta = inp.readInt();
		byte[] b = (byte[]) inp.readObject();
		// if (b != null) {
		// nbtTag = CompressedStreamTools.decompress(b);
		// }
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(count);
		out.writeInt(meta);
		if (nbtTag == null) {
			out.writeObject(null);
		} else {
			byte[] b = CompressedStreamTools.compress(nbtTag);
			out.writeObject(b);
		}
	}

	@Override
	public PC_ItemStack readFromNBT(NBTTagCompound nbttag) {
		int id = nbttag.getInteger("id");
		// o = Item.itemsList[id];
		count = nbttag.getInteger("count");
		meta = nbttag.getInteger("meta");
		if (nbttag.hasKey("nbtTag")) {
			nbtTag = nbttag.getCompoundTag("nbtTag");
		}
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttag) {
		nbttag.setInteger("count", count);
		nbttag.setInteger("meta", meta);
		// if (nbtTag != null) {
		// nbttag.setCompoundTag("nbtTag", nbtTag);
		// }
		return nbttag;
	}

}
