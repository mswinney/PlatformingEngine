package leveldata;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import leveldata.layer.CollisionLayer;
import leveldata.layer.Layer;
import leveldata.layer.LayerID;
import editor.MapIconPanel.MapIcon;

public class Block {
	private ArrayList<Layer> layers;
	private CollisionLayer collisionLayer;
	private ArrayList<Entity> entities;
	private Color mapColor;
	private static BufferedImage mapTileset;
	private static final String MAP_TILESET_FILEPATH = "resources/sprites/maptiles.png";
	private static BufferedImage roommarkTileset;
	private static final String ROOMMARK_TILESET_FILEPATH = "resources/sprites/roommarks.png";
	private static ArrayList<BufferedImage> tilesets;
	private static final String TILESET_FILEPATH = "resources/sprites/tileset%d.png";
	private static final int MAX_TILESETS = 256;
	private short musicIndex;
	private short ambienceIndex;
	private short mapIconIndex;
	private short doorBytes;
	private short mapMarkingBytes;
	private short backgroundIndex;
	private byte scrollByte;

	// up, left, down, right
	private static final int[] doorXCoords = {6, 0, 6, 14};
	private static final int[] doorYCoords = {0, 6, 14, 6};
	private static final int[] doorWidths  = {4, 2, 4, 2};
	private static final int[] doorHeights = {2, 4, 2, 4 };

	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Music ");
		output.append(musicIndex);
		output.append(", Ambience ");
		output.append(ambienceIndex);
		output.append(", Map Icon ");
		output.append(mapIconIndex);
		output.append(", Door Bytes ");
		output.append(doorBytes);
		output.append(", Map Marking Bytes ");
		output.append(mapMarkingBytes);
		output.append(", Scroll Byte ");
		output.append(scrollByte);
		output.append(", Map Color ");
		output.append(mapColor);
		return output.toString();
	}

	public Block(int blockSizeX, int blockSizeY, short musicIndex, short ambienceIndex,
			short mapIconIndex, short doorBytes, byte scrollByte, Color mapColor,
			short mapMarkingBytes) {
		layers = new ArrayList<Layer>();
		this.musicIndex = musicIndex;
		this.ambienceIndex = ambienceIndex;
		this.mapIconIndex = mapIconIndex;
		this.doorBytes = doorBytes;
		this.scrollByte = scrollByte;
		this.mapColor = mapColor;
		this.mapMarkingBytes = mapMarkingBytes;
		this.collisionLayer = new CollisionLayer(this, blockSizeX, blockSizeY);
	}
	public static void loadTilesets() {
		tilesets = new ArrayList<BufferedImage>();
		try {
			// load map tileset, room mark tileset
			mapTileset = ZettaUtil.loadImage(MAP_TILESET_FILEPATH);
			roommarkTileset = ZettaUtil.loadImage(ROOMMARK_TILESET_FILEPATH);
			// load room tilesets
			for (int i = 0; i < MAX_TILESETS; i++) {
				BufferedImage next = ZettaUtil.loadImage(String.format(TILESET_FILEPATH, i));
				if (next != null) {
					tilesets.add(next);
				}
				else {
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void addLayer(Layer l) {
		layers.add(l);
	}
	public void paintEditor(Graphics g) {
		drawBlock(g, 1, 1, 0, 0);
	}
	public void drawBlock(Graphics g, int i, int j, int xOffset, int yOffset) {
        // TODO: update for layers
        // draw tiles
        for (Layer l : layers) {
            l.paint(g, i, j, xOffset, yOffset);
        }
        collisionLayer.paint(g, i, j, xOffset, yOffset);
        // draw entities
        if (entities != null) {
            for (int k = 0; k < entities.size(); k++) {
                Entity next = entities.get(k);
                next.paint(g, i, j);
            }
        }
	}
    public void paintBlockOffset(Graphics g, int pixelOffsetX, int pixelOffsetY) {

    }

	public void paintMapIcon(Graphics g, Color[] doorColors, int x, int y, int width,
			int height) {
		Block.paintMapIcon(g, doorColors, mapIconIndex, mapColor, mapMarkingBytes, doorBytes,
				x, y, width, height);
	}
	public static void paintMapIcon(Graphics g, Color[] doorColors, int mapIconIndex,
			Color mapColor, short mapMarkingBytes, short doorBytes,
			int x, int y, int width, int height) {
		// draw base tile color
		g.setColor(mapColor);
		g.fillRect(x, y, width, height);
		// draw walls
		ZettaUtil.drawTileAtLocation(mapIconIndex, mapTileset, g, x, y, width, height);
		// draw doors
		Block.drawDoors(g, doorColors, doorBytes, x, y, width, height);
		// draw room marks
		ZettaUtil.drawTileAtLocation(mapMarkingBytes & 0xFFFF, roommarkTileset, g, x, y);
	}
	private static void drawDoors(Graphics g, Color[] doorColors, short doorBytes,
			int x, int y, int width, int height) {
		byte[] doors = splitDoorByte(doorBytes);
		// up, left, down, right
		for (int i = 0; i < doors.length; i++) {
			if (doors[i] > 0) {
				g.setColor(doorColors[doors[i]]);
				g.fillRect(x + doorXCoords[i] * width / 16,
						y + doorYCoords[i] * height / 16,
						doorWidths[i] * width / 16, doorHeights[i] * height / 16);
			}
		}
	}
	private static byte[] splitDoorByte(short doorBytes) {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (doorBytes & 0x000F);
		bytes[2] = (byte) ((doorBytes & 0x00F0) / 16);
		bytes[1] = (byte) ((doorBytes & 0x0F00) / 256);
		bytes[0] = (byte) ((doorBytes & 0xF000) / 4096);
		return bytes;
	}
	// Up, Left, Down, Right
	public static boolean[] getScrollLocking(byte scrollByte) {
		boolean[] b = new boolean[4];
		b[0] = (scrollByte & 0x8) > 0;
		b[1] = (scrollByte & 0x4) > 0;
		b[2] = (scrollByte & 0x2) > 0;
		b[3] = (scrollByte & 0x1) > 0;
		return b;
	}

	public void saveData(RandomAccessFile file) throws IOException {
		// write header
		file.writeShort(musicIndex);
		file.writeShort(ambienceIndex);
		file.writeShort(mapIconIndex);
		
		// write map icon
		file.writeShort(doorBytes);
		file.writeByte(scrollByte);
		file.writeByte(mapColor.getRed());
		file.writeByte(mapColor.getGreen());
		file.writeByte(mapColor.getBlue());
		file.writeShort(mapMarkingBytes);

		collisionLayer.saveData(file);
		for (Layer l : layers) {
			l.saveData(file);
		}
		if (entities != null) {
			file.writeByte(LayerID.ENTITY_DATA.getIndex());
			file.writeByte(0x00);
			// write out entities
		}
		file.writeByte(LayerID.END_BLOCK.getIndex());
		file.writeByte(0x00);
	}

	public int getMusicIndex() { return musicIndex & 0xFFFF; }
	public int getAmbienceIndex() { return ambienceIndex & 0xFFFF; }
	public int getMapIconIndex() { return mapIconIndex & 0xFFFF; }
	public short getMapMarkingBytes() { return mapMarkingBytes; }
	public short getDoorBytes() { return doorBytes; }
	public Color getMapColor() { return mapColor; }
	public byte getScrollByte() { return scrollByte; }
	public static int getNumTilesets() { return tilesets.size(); }
	public static BufferedImage getMapTileset() { return mapTileset; }
	public short getBackgroundIndex() { return backgroundIndex; }

	public void setAmbienceIndex(int after) { ambienceIndex = (short)after; }
	public void setMusicIndex(int after) { musicIndex = (short)after; }
	public void setMapIconIndex(int after) { mapIconIndex = (short)after; }
	public void setBackgroundIndex(int after) { backgroundIndex = (short)after; }

	public void setMapIcon(MapIcon i) {
		this.mapIconIndex = (short) i.getMapIconIndex();
		this.doorBytes = i.getDoorBytes();
		this.mapColor = i.getMapColor();
		this.mapMarkingBytes = i.getMapMarkingBytes();
		this.scrollByte = i.getScrollByte();
	}

	public MapIcon getMapIcon() {
		return new MapIcon(this);
	}
	public static BufferedImage getTileset(int index) {
		return tilesets.get(index);
	}

	public void setCollisionLayer(CollisionLayer collisionLayer) {
		this.collisionLayer = collisionLayer;
	}

	public CollisionLayer getCollisionLayer() {
		return this.collisionLayer;
	}

	public Layer getLayer(int currentLayerIndex) {
		return layers.get(currentLayerIndex);
	}
	public Layer[] getLayerArray() {
		Layer[] l = new Layer[layers.size() + 1];
		l[0] = this.collisionLayer;
		for (int i = 0; i < layers.size(); i++) {
			l[i + 1] = layers.get(i);
		}
		Arrays.sort(l);
		return l;
	}

	public void replaceLayer(Layer before, Layer after) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i) == before) {
				layers.set(i, after);
			}
		}
	}

}
