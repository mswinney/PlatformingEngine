package leveldata;

import java.awt.Graphics;

public class Entity {
	private short x;
	private short y;
	private EntityType type;
	private byte[] options;
	
	public int getTypeIndex() { return type.ordinal(); }
	public short getX() { return x; }
	public short getY() { return y; }
	
	private static enum EntityType {
		ZERO,
		ONE,
		TWO,
		THREE;
	}
	
	public Entity(int typeIndex, short x, short y, byte[] options) {
		type = EntityType.values()[typeIndex];
		this.x = x;
		this.y = y;
		this.options = options;
	}
	public void paint(Graphics g, int i, int j) {
		// TODO Auto-generated method stub
		ZettaUtil.drawTile(2, Block.getTileset(0), g,
				this.getX() + i,
				this.getY() + j);
	}
}
