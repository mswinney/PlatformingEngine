package leveldata;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import editor.EditorPanel;
import leveldata.layer.LayerID;
import leveldata.layer.CollisionLayer;
import leveldata.layer.ColorLayer;
import leveldata.layer.TileLayer.Tile;
import ui.Game;

public class Zone {
	private final static int VERSION_MAGIC = 1380275199; // RES-FF
	private final static int END_ENTITY_ID = 0xFFFF;
	private final static Color ROOM_OUTLINE_COLOR = new Color(0, 181, 181, 255);
	private final static Color NULL_ROOM_COLOR = new Color(64, 64, 64, 127);
	private final static Color GRID_COLOR = Color.gray;

	private Block[][] map;
	private int blockSizeX;
	private int blockSizeY;
	private short startingX; // unsigned
	private short startingY; // unsigned
	private static int MAP_COLOR_ALPHA = 255;
	public static final int MAP_TILE_X = 16;
	public static final int MAP_TILE_Y = 16;
	private Color[] doorColors;

	private Zone() {
		doorColors = new Color[15];
		doorColors[0] = null; // no door
		doorColors[1] = new Color(127, 127, 127, 255); // standard door
		doorColors[2] = Color.RED;
		doorColors[3] = Color.GREEN;
		doorColors[4] = Color.BLUE;
	}

	// New file constructor
	public Zone(int width, int height, int blockSizeX, int blockSizeY) {
		this();
		map = new Block[height][width];
		this.blockSizeX = blockSizeX;
		this.blockSizeY = blockSizeY;
		startingX = 0;
		startingY = 0;
	}

	// Loading file constructor
	public Zone(String stageLoc) throws IOException {
		this();
		RandomAccessFile data = ZettaUtil.loadResource(stageLoc);

		// verify magic version number
		if(data.readInt() != VERSION_MAGIC) {
			ZettaUtil.error("File " + stageLoc + " is not valid");
			throw new IllegalArgumentException("File " + stageLoc + " is not valid");
		}

		// read in header
		this.blockSizeX = data.readUnsignedShort();
		this.blockSizeY = data.readUnsignedShort();
		int mapWidth = data.readUnsignedShort();
		int mapHeight = data.readUnsignedShort();
		startingX = data.readShort();
		startingY = data.readShort();
		map = new Block[mapHeight][mapWidth];

		// read in room data
		this.readBlocks(data);

		// TODO: read optional flags
		data.close();
	}

	private void readBlocks(RandomAccessFile data) throws IOException {
		boolean readNext = true;
		while (readNext) {
			int mapX = data.readUnsignedShort();
			int mapY = data.readUnsignedShort();
			short musicIndex = data.readShort();
			short ambienceIndex = data.readShort();

			short mapIconIndex = data.readShort();
			short doorBytes = data.readShort();
			byte scrollByte = data.readByte();

			// read map color
			int mapColorRed = data.readUnsignedByte();
			int mapColorGreen = data.readUnsignedByte();
			int mapColorBlue = data.readUnsignedByte();
			Color mapColor = new Color(mapColorRed, mapColorGreen, mapColorBlue, MAP_COLOR_ALPHA);

			short mapMarkingBytes = data.readShort();
			Block newBlock = new Block(this.blockSizeX, this.blockSizeY, musicIndex,
					ambienceIndex, mapIconIndex, doorBytes, scrollByte, mapColor,
					mapMarkingBytes);
			this.addBlock(newBlock, mapX, mapY);

			boolean readLayers = true;
			while (readLayers) {
				// read layer data
				byte layerID = data.readByte();
				switch (LayerID.getLayerID(layerID & 0xFF)) {
				case TILE_COLLISION:
					data.readByte(); // advance past fake index FF present for alignment
					newBlock.setCollisionLayer(new CollisionLayer(newBlock, data, this));
					break;
				case COLOR_DECORATION:
					newBlock.addLayer(new ColorLayer(newBlock, data));
					break;
				case TILE_DECORATION:
				case IMAGE_DECORATION:
				case ENTITY_DATA:
					ZettaUtil.error("Layer type %s not implemented yet",
							LayerID.getLayerID(layerID).name());
					break;
				case END_OF_FILE:
					data.readByte();
					return;
				case END_BLOCK:
					readLayers = false;
					data.readByte(); // advance
					break;
				default: ZettaUtil.error("Unknown layer ID %d", layerID); break;
				}
			}
		}
	}

	public static ArrayList<Entity> readEntities(RandomAccessFile data) throws IOException {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		while (true) {
			int entityID = data.readUnsignedShort();
			if (entityID != END_ENTITY_ID) {
				short x = data.readShort();
				short y = data.readShort();
				byte[] options = new byte[10];
				data.read(options);
				entities.add(new Entity(entityID, x, y, options));
			}
			else {
				return entities;
			}
		}
	}

	/*public void paintMap(Graphics g) {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (map[y][x] != -1)
					g.drawString(map[y][x] + "", x * 16+10, y * 16+10);
			}
		}
	}*/

	public void paintRoomEditor(Graphics g, int mapX, int mapY) {
		// draw room
		g.setColor(ROOM_OUTLINE_COLOR);
		g.drawRect(Game.TILE_X-1, Game.TILE_Y-1,
				this.blockSizeX * Game.TILE_X +1, this.blockSizeY * Game.TILE_Y+1);
		Block drawBlock = this.getBlock(mapX, mapY);
		if (drawBlock != null) {
			drawBlock.paintEditor(g);
		}
		else {
			//g.drawString("lol whoops out of bounds", 32, 32);
			g.setColor(NULL_ROOM_COLOR);
			g.fillRect(0, 0, (this.blockSizeX+2)*Game.TILE_X, (this.blockSizeY+2)*Game.TILE_Y);
		}
		// draw room index
		g.setFont(ZettaUtil.systemFont);
		g.setColor(new Color(128,128,128,127));
		g.drawString("("+mapX+", "+mapY+")", 0, 10);
	}

	public void paintFullMap(Graphics g) {
		paintFullMap(g, this.getWidth()/2, this.getHeight()/2,
				this.getWidth(), this.getHeight(), 0, 0);
	}
	public void paintFullMap(Graphics g, int i, int j, int width, int height) {
		paintFullMap(g, i, j, width, height, 0, 0);
	}
	public void paintFullMap(Graphics g, int i, int j, int width, int height,
			int xOffset, int yOffset) {
		// i and j are center around which this should be drawn
		int xStart = i - width/2;
		int yStart = j - height/2;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x + xStart >= 0 && y + yStart >= 0
						&& x + xStart < this.getWidth() && y + yStart < this.getHeight()
						&& map[y+yStart][x+xStart] != null) {
					this.getBlock(x+xStart, y+yStart).paintMapIcon(g, doorColors,
							x * MAP_TILE_X + xOffset, y * MAP_TILE_Y + yOffset,
							MAP_TILE_X, MAP_TILE_Y);
				}
			}
		}
	}
	public void paintFullMapWithGrid(Graphics g, int xOffset, int yOffset,
			int highlightX, int highlightY, int hoverX, int hoverY, int panelX, int panelY) {
		g.setColor(GRID_COLOR);
		int width = this.getWidth();
		int height = this.getHeight();
		g.fillRect(0, 0, panelX, panelY);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (map[y][x] == null) {
					g.setColor(NULL_ROOM_COLOR);
					g.fillRect(x * MAP_TILE_X + xOffset + x, y * MAP_TILE_Y + yOffset + y,
							MAP_TILE_X, MAP_TILE_Y);
				}
				else {
					this.getBlock(x, y).paintMapIcon(g, doorColors,
							x * MAP_TILE_X + xOffset + x, y * MAP_TILE_Y + yOffset + y,
							MAP_TILE_X, MAP_TILE_Y);
				}
			}
		}
		// draw highlight
		g.setColor(EditorPanel.SELECTED_COLOR);
		g.drawRect(highlightX * (MAP_TILE_X+1) + xOffset -1,
				highlightY * (MAP_TILE_Y+1) + yOffset -1,
				MAP_TILE_X+1, MAP_TILE_Y+1);
		g.setColor(EditorPanel.HOVER_COLOR);
		g.drawRect(hoverX * (MAP_TILE_X+1) + xOffset -2,
				hoverY * (MAP_TILE_Y+1) + yOffset -2,
				19,19);
	}

	public int getWidth() { return map[0].length; }
	public int getHeight() { return map.length; }
	public int getStartingX() { return startingX & 0xFFFF;}
	public int getStartingY() { return startingY & 0xFFFF;}
	public Block getBlock(int x, int y) {
		try {
			return map[y][x];
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	private void addBlock(Block block, int x, int y) {
		map[y][x] = block;
	}
	public Color[] getDoorColors() { return doorColors; }

	public void saveData(RandomAccessFile file) throws IOException {
		file.writeInt(VERSION_MAGIC);
		file.writeShort(blockSizeX);
		file.writeShort(blockSizeY);
		file.writeShort(getWidth());
		file.writeShort(getHeight());
		file.writeShort(startingX);
		file.writeShort(startingY);

		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				Block nextBlock = getBlock(x, y);
				if (nextBlock != null) {
					file.writeShort(x);
					file.writeShort(y);
					nextBlock.saveData(file);
				}
			}
		}
		file.seek(file.getFilePointer()-2);
		file.writeByte(LayerID.END_OF_FILE.getIndex());
		file.writeByte(LayerID.END_OF_FILE.getIndex());

		// TODO: write out custom options

		// clear anything remaining at the end
		file.setLength(file.getFilePointer());
		file.close();
	}

	public int getBlockSizeX() { return blockSizeX; }
	public int getBlockSizeY() { return blockSizeY; }

	public void initBlock(int x, int y, short musicIndex, short ambienceIndex, short mapIconIndex,
			short doorBytes, byte scrollByte, Color mapColor, short mapMarkingBytes) {
		if (this.getBlock(x, y) == null) {
			map[y][x] = new Block(this.blockSizeX, this.blockSizeY, musicIndex, ambienceIndex,
					mapIconIndex, doorBytes, scrollByte, mapColor, mapMarkingBytes);
		}
		else {
			ZettaUtil.error("Tried to init already-existing block at %d, %d", x, y);
		}
	}

	public void paintFringes(Graphics g, int mapX, int mapY) {
		// draw UL corner-fringe
		Block ULblock = this.getBlock(mapX-1, mapY-1);
		if (ULblock != null) {
			Tile ULtile = ULblock.getCollisionLayer().getTile(blockSizeX-1, blockSizeY-1);
			ZettaUtil.drawTile(ULtile, g, 0, 0);
		}
		// draw U fringe
		Block Ublock = this.getBlock(mapX, mapY-1);
		if (Ublock != null) {
			for (int i = 0; i < blockSizeX; i++) {
				Tile Utile = Ublock.getCollisionLayer().getTile(i, blockSizeY-1);
				ZettaUtil.drawTile(Utile, g, i+1, 0);
			}
		}
		// draw UR corner-fringe
		Block URblock = this.getBlock(mapX+1, mapY-1);
		if (URblock != null) {
			Tile URtile = URblock.getCollisionLayer().getTile(0, blockSizeY-1);
			ZettaUtil.drawTile(URtile, g, blockSizeX+1, 0);
		}
		// draw L fringe
		Block Lblock = this.getBlock(mapX-1, mapY);
		if (Lblock != null) {
			for (int j = 0; j < blockSizeY; j++) {
				Tile Ltile = Lblock.getCollisionLayer().getTile(blockSizeX-1, j);
				ZettaUtil.drawTile(Ltile, g, 0, j+1);
			}
		}
		// draw R fringe
		Block Rblock = this.getBlock(mapX+1, mapY);
		if (Rblock != null) {
			for (int j = 0; j < blockSizeY; j++) {
				Tile Rtile = Rblock.getCollisionLayer().getTile(0, j);
				ZettaUtil.drawTile(Rtile, g, blockSizeX+1, j+1);
			}
		}
		// draw DL corner-fringe
		Block DLblock = this.getBlock(mapX-1, mapY+1);
		if (DLblock != null) {
			Tile DLtile = DLblock.getCollisionLayer().getTile(blockSizeX-1, 0);
			ZettaUtil.drawTile(DLtile, g, 0, blockSizeY+1);
		}
		// draw D fringe
		Block Dblock = this.getBlock(mapX, mapY+1);
		if (Dblock != null) {
			for (int i = 0; i < blockSizeX; i++) {
				Tile Dtile = Dblock.getCollisionLayer().getTile(i, 0);
				ZettaUtil.drawTile(Dtile, g, i+1, blockSizeY+1);
			}
		}
		// draw DR corner-fringe
		Block DRblock = this.getBlock(mapX+1, mapY+1);
		if (DRblock != null) {
			Tile DRtile = DRblock.getCollisionLayer().getTile(0, 0);
			ZettaUtil.drawTile(DRtile, g, blockSizeX+1, blockSizeY+1);
		}
		// darken edges
		g.setColor(new Color(0, 0, 0, 110));
		g.fillRect(0, 0, Game.TILE_X * (blockSizeX+1), Game.TILE_Y);
		g.fillRect(Game.TILE_X * (blockSizeX+1), 0, Game.TILE_X,
				Game.TILE_Y * (blockSizeY+1));
		g.fillRect(0, Game.TILE_Y, Game.TILE_X, Game.TILE_Y * (blockSizeY+1));
		g.fillRect(Game.TILE_X, Game.TILE_Y * (blockSizeY+1),
				Game.TILE_X * (blockSizeX+1), Game.TILE_Y);
	}

	public void deleteBlock(int x, int y) {
		this.addBlock(null, x, y);
	}
}
