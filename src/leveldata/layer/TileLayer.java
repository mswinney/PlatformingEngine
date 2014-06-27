package leveldata.layer;

import java.awt.Graphics;
import java.io.IOException;
import java.io.RandomAccessFile;

import leveldata.Block;
import leveldata.ZettaUtil;

public class TileLayer extends Layer implements TileTypeLayer {
	private Tile[][] tiles;
	protected TileLayer(Block parentBlock, byte depth, int blockSizeX, int blockSizeY) {
		super(parentBlock, depth);
		tiles = new Tile[blockSizeX][blockSizeY];
	}
	protected TileLayer(TileLayer t) {
		super(t.getParentBlock(), t.getDepth());
		this.tiles = t.tiles.clone();
	}
	public void setTile(Tile t, int x, int y) {
		tiles[y][x] = t;
	}
	@Override
	public void paint(Graphics g, int i, int j, int xOffset, int yOffset) {
		for (int y = 0; y < tiles[0].length; y++) {
			for (int x = 0; x < tiles.length; x++) {
				Tile t = this.getTile(x, y);
				ZettaUtil.drawTile(t.getIndex(), Block.getTileset(t.getTileset()), g, x+i, y+j, xOffset, yOffset);
			}
		}
	}
	public Tile getTile(int x, int y) {
		return tiles[y][x];
	}
	public TileLayer clone() {
		return new TileLayer(this);
	}
	
	public static class Tile {
		private byte index;
		private byte tileset;
		public int getIndex() { return index & 0xFF; }
		public int getTileset() { return tileset & 0xFF; }
		public Tile(int tileset, int index) {
			this.index = (byte) (index & 0xFF);
			this.tileset = (byte) (tileset & 0xFF);
		}
		
		public String toString() {
			return String.format("[Tile: Tileset %d, Index %d]", tileset, index);
		}
		public boolean equals(Object o) {
			if (o.getClass() == Tile.class) {
				Tile t = (Tile) o;
				if (this.index == t.index && this.tileset == t.tileset) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void saveData(RandomAccessFile file) throws IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public TileTypeLayer duplicate() {
		return this.clone();
	}
	@Override
	public boolean hasEditableProperties() {
		return true;
	}
	@Override
	public boolean hasVariableDepth() {
		return true;
	}
}
