package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

import leveldata.Block;
import leveldata.Zone;
import ui.Controls.Input;

public class EngineTest extends ui.Game {

    private static final String DATA_LOCATION = "resources/data/";
    private int mapX;
    private int mapY;
    private Zone zone;
    private MapMode mode;
    private int showPlayerDot;
    private static final int MAP_TILE_WIDTH = 16;
    private static final int MAP_TILE_HEIGHT = 16;
    private static final int STATUS_BAR_MAP_HEIGHT = 5;
    private static final int STATUS_BAR_MAP_WIDTH = 3;
    private static final int STATUS_BAR_MAP_X_OFFSET = 0;
    private static final int STATUS_BAR_MAP_Y_OFFSET = 0;
    private static final Color PLAYER_DOT_COLOR = new Color(128, 128, 128, 200);
    private static final Color MAP_BACKGROUND_COLOR = Color.BLUE;
    private static final Color SIDEBAR_BACKGROUND_COLOR = Color.BLACK;
    private static final Color GAME_BACKGROUND_COLOR = Color.GRAY;

    private static enum MapMode {
        ENGINE_TEST,
        FULL_MAP,
        ROOM_MAPS
    }

    public EngineTest(String stageName) {
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
        mode = MapMode.values()[0];
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
            case ENGINE_TEST:
                int sidebarWidth = STATUS_BAR_MAP_WIDTH * MAP_TILE_WIDTH;
                Dimension windowSize = this.getWindowSize();

                // draw sidebar
                g.setColor(SIDEBAR_BACKGROUND_COLOR);
                g.fillRect(0, 0, windowSize.width, windowSize.height);

                // draw map
                g.setColor(MAP_BACKGROUND_COLOR);
                g.fillRect(STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET, sidebarWidth,
                        STATUS_BAR_MAP_HEIGHT * MAP_TILE_HEIGHT);
                zone.paintFullMap(g, mapX, mapY, STATUS_BAR_MAP_WIDTH, STATUS_BAR_MAP_HEIGHT,
                        STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET);
                g.setColor(PLAYER_DOT_COLOR);
                g.fillRect(STATUS_BAR_MAP_WIDTH/2 * MAP_TILE_WIDTH + 6,
                        STATUS_BAR_MAP_HEIGHT/2 * MAP_TILE_HEIGHT + 6, 4, 4);

                // draw current block
                g.setColor(GAME_BACKGROUND_COLOR);
                g.fillRect(sidebarWidth, 0, windowSize.width - sidebarWidth, windowSize.height);
                Block currentBlock = zone.getBlock(mapX, mapY);
                if (currentBlock != null)
                    currentBlock.drawBlock(g, STATUS_BAR_MAP_WIDTH, 0);
                break;
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
                case RIGHT: if (mapX < zone.getWidth()-1) mapX++; break;
                case UP: if (mapY > 0) mapY--; break;
                case DOWN: if (mapY < zone.getHeight()-1) mapY++; break;
                case DEBUG:
                case JUMP: mode = MapMode.values()[(mode.ordinal()+1) % MapMode.values().length];
                    break;
            }
        }
    }

    @Override
    public Dimension getWindowSize() {
        //return new Dimension(zone.getWidth()*MAP_TILE_WIDTH, zone.getHeight()*MAP_TILE_HEIGHT);
        return new Dimension((STATUS_BAR_MAP_WIDTH + zone.getBlockSizeX())*TILE_X, zone.getBlockSizeY() *TILE_Y);
    }
    @Override
    public String getWindowTitle() { return "Engine Test"; }
}
