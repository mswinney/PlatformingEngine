package leveldata.layer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;

import leveldata.Block;
import leveldata.ZettaUtil;
import leveldata.Zone;
import leveldata.layer.TileLayer.Tile;

public class CollisionLayer extends Layer implements TileTypeLayer {
	public short[][] tiles;
	public CollisionLayer(Block parentBlock, RandomAccessFile data, Zone parent)
			throws IOException {
		super(parentBlock, (byte) 0);
		tiles = new short[parent.getBlockSizeY()][parent.getBlockSizeX()];
		for (int y = 0; y < parent.getBlockSizeY(); y++) {
			for (int x = 0; x < parent.getBlockSizeX(); x++) {
				tiles[y][x] = data.readByte();
			}
		}
	}
	public CollisionLayer(Block parentBlock, int blockSizeX, int blockSizeY) {
		super(parentBlock, (byte) 0);
		tiles = new short[blockSizeX][blockSizeY];
	}
	public CollisionLayer(CollisionLayer c) {
		super(c.getParentBlock(), c.getDepth());
		short[][] newTiles = new short[c.tiles.length][c.tiles[0].length];
		for (int i = 0; i < c.tiles.length; i++) {
			newTiles[i] = c.tiles[i].clone();
		}
		this.tiles = newTiles;
	}
	
	@Override
	public void setDepth(int i) {
		// Collision Layers are always Depth 0
		return;
	}
	
	public String toString() {
		return super.toString() + " " + this.tiles + " " + this.tiles[0][0];
	}

	@Override
	public void paint(Graphics g, int i, int j, int xOffset, int yOffset) {
		BufferedImage collisionTileset = Block.getTileset(0);
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				int t = tiles[y][x] & 0xFF;
				ZettaUtil.drawTile(t, collisionTileset, g, x+i, y+j, xOffset, yOffset);
			}
		}
	}

	@Override
	public void saveData(RandomAccessFile file) throws IOException {
		file.writeByte(LayerID.TILE_COLLISION.getIndex());
		file.writeByte(0xFF);
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				file.writeByte(tiles[y][x]);
			}
		}
	}

	@Override
	public Tile getTile(int x, int y) {
		return new Tile(0, tiles[y][x] & 0xFFFF);
	}

	@Override
	public void setTile(Tile t, int x, int y) {
		this.tiles[y][x] = (short) t.getIndex();
	}

	@Override
	public CollisionLayer clone() {
		return new CollisionLayer(this);
	}
	@Override
	public boolean hasEditableProperties() {
		return false;
	}
	public boolean hasVariableDepth() {
		return false;
	}
	@Override
	public TileTypeLayer duplicate() {
		return this.clone();
	}
}
