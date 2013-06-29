package platform;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

import ui.Controls.Input;

public class MapTest extends ui.Game {

	private static final String DATA_LOCATION = "resources/data/";
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
	
	public MapTest(String stageName) {
		String stageLoc = DATA_LOCATION + stageName;
		try {
			zone = new Zone(stageLoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Block.loadTilesets();
		mapX = zone.getStartingX();
		mapY = zone.getStartingY();
		mode = MapMode.FULL_MAP;
	}

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
			}
		}
	}

	@Override
	public Dimension getWindowSize() { return new Dimension(200,200); }
	@Override
	public String getWindowTitle() { return "Map Test"; }
}
