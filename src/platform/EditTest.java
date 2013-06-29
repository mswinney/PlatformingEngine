package platform;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import ui.Controls.Input;

public class EditTest extends ui.Game {

	private static final String DATA_LOCATION = "resources/data/";
	private String filepath;
	private int mapX;
	private int mapY;
	private Zone zone;
	private MapMode mode;
	private int showPlayerDot;
	private static final Color PLAYER_DOT_COLOR = new Color(128, 128, 128, 200);

	private static enum MapMode {
		FULL_MAP,
		ROOM_MAPS;
	}

	public EditTest(String stageName) {
		filepath = DATA_LOCATION + stageName;
		try {
			loadZone(filepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Block.loadTilesets();
		mapX = zone.getStartingX();
		mapY = zone.getStartingY();
		mode = MapMode.FULL_MAP;
	}
	private void loadZone(String filepath) throws IOException { zone = new Zone(filepath); }
	

	@Override
	public void paint(Graphics g) {
		switch (mode) {
		case FULL_MAP: 
			zone.paintFullMap(g);
			if (showPlayerDot<30) {
				g.setColor(PLAYER_DOT_COLOR);
				g.fillRect( mapX * Zone.MAP_TILE_X + 6, mapY * Zone.MAP_TILE_Y + 6, 4, 4);
			}
			break;
		case ROOM_MAPS: zone.paintRoomEditor(g, mapX, mapY); break;
		}
	}

	@Override
	public void advanceAnimations() {
		// TODO Auto-generated method stub
		showPlayerDot = showPlayerDot>60 ? 0: showPlayerDot+1;
	}

	@Override
	public void input(Input type, boolean pressed) {
		if (pressed) {
			switch (type) {
			case LEFT: if (mapX > 0) mapX--; break;
			case RIGHT: if (mapX < zone.getWidth() - 1) mapX++; break;
			case UP: if (mapY > 0) mapY--; break;
			case DOWN: if (mapY < zone.getHeight() - 1) mapY++; break;
			case DEBUG: mode = MapMode.values()[(mode.ordinal()+1) % MapMode.values().length];
			break;
			case JUMP: try {
				ZettaUtil.log("Outputting file...");
				this.saveData("output.dat");
				ZettaUtil.log("Success!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			}
		}
	}

	// maybe make this faster with some sort of "keep track of differences" algorithm?
	public void saveData(String filepath) throws IOException {
		File file = new File(filepath);
		file.createNewFile();
		RandomAccessFile file2 = new RandomAccessFile(file, "rwd");
		zone.saveData(file2);
	}

	@Override
	public Dimension getWindowSize() { return new Dimension(200,200); }
	@Override
	public String getWindowTitle() { return "Map Test"; }
}
