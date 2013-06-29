package platform.layer;

import java.awt.Graphics;
import java.io.IOException;
import java.io.RandomAccessFile;

import platform.Block;
import platform.ZettaUtil;

public abstract class Layer implements Comparable<Layer>, Cloneable {
	private byte depth;
	private Block parentBlock;
	
	public abstract void paint(Graphics g, int i, int j);
	public abstract void saveData(RandomAccessFile file) throws IOException;
	public abstract boolean hasEditableProperties();
	public abstract boolean hasVariableDepth();
	public byte getDepth() {
		return depth;
	}
	public int compareTo(Layer other) {
		return this.depth - other.depth;
	}
	public void setDepth(int i) {
		this.depth = (byte) ZettaUtil.clamp(i, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}
	public abstract Object clone();
	protected Block getParentBlock() {
		return parentBlock;
	}
	
	protected Layer(Block parentBlock, byte depth) {
		this.parentBlock = parentBlock;
		this.depth = depth;
	}
}
