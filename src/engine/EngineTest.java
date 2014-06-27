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
    // player room coordinates
    private int mapX;
    private int mapY;
    // player coordinates within room
    private int roomX;
    private int roomY;
    // coordinates of screen
    private int screenX;
    private int screenY;
    private int screenDeltaX;
    private int screenDeltaY;
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
        roomX = 32;
        roomY = 32;
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

                // draw background
                g.setColor(Color.GREEN);
                g.fillRect(0, 0, windowSize.width, windowSize.height);

                // draw current block
                drawCurrentBlock(g);

                drawMap(g);

                // draw sidebar
                g.setColor(SIDEBAR_BACKGROUND_COLOR);
                g.fillRect(0, STATUS_BAR_MAP_HEIGHT * TILE_Y,
                        STATUS_BAR_MAP_WIDTH * TILE_X, windowSize.height - STATUS_BAR_MAP_HEIGHT);

                // draw "player"
                g.setColor(Color.CYAN);
                //g.fillRect(sidebarWidth + roomX + 6, roomY + 6, 4, 4);
                g.fillRect(zone.getBlockSizeX()/2 * TILE_X + sidebarWidth + 6,
                        zone.getBlockSizeY()/2 * TILE_Y + 6, 4, 4);

                break;
        }
    }

    private void drawCurrentBlock(Graphics g) {
        int sidebarWidth = STATUS_BAR_MAP_WIDTH * MAP_TILE_WIDTH;
        Dimension windowSize = this.getWindowSize();
        // draw background
        g.setColor(GAME_BACKGROUND_COLOR);
        g.fillRect(sidebarWidth, 0, windowSize.width - sidebarWidth, windowSize.height);

        zone.drawBlockOffset(g, mapX, mapY, zone.getBlockSizeX()/2, zone.getBlockSizeY()/2,
                sidebarWidth-roomX, -roomY);
        // draw neighboring blocks

    }

    private void drawMap(Graphics g) {
        int sidebarWidth = STATUS_BAR_MAP_WIDTH * MAP_TILE_WIDTH;
        g.setColor(MAP_BACKGROUND_COLOR);
        g.fillRect(STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET, sidebarWidth,
                STATUS_BAR_MAP_HEIGHT * MAP_TILE_HEIGHT);
        zone.paintFullMap(g, mapX, mapY, STATUS_BAR_MAP_WIDTH, STATUS_BAR_MAP_HEIGHT,
                STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET);
        g.setColor(PLAYER_DOT_COLOR);
        g.fillRect(STATUS_BAR_MAP_WIDTH/2 * MAP_TILE_WIDTH + 6,
                STATUS_BAR_MAP_HEIGHT/2 * MAP_TILE_HEIGHT + 6, 4, 4);
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
                case LEFT: movePixels(-16, 0); break;
                case RIGHT: movePixels(16, 0); break;
                case UP: movePixels(0, -16); break;
                case DOWN: movePixels(0, 16); break;
                case DEBUG:
                case JUMP: mode = MapMode.values()[(mode.ordinal()+1) % MapMode.values().length];
                    break;
            }
        }
    }

    private void movePixels(int x, int y) {
        // TODO: check scroll lock, update accordingly
        roomX += x;
        roomY += y;
        int w = getBlockPixelWidth();
        int h = getBlockPixelHeight();
        if (roomX < 0) {
            // moving off left edge
            if (mapX > 0) {
                roomX += w;
                mapX -= 1;
            }
            else {
                roomX = 0;
            }
        }
        else if (roomX > w-1) {
            // moving off right edge
            if (mapX < zone.getWidth()-1) {
                roomX -= w;
                mapX += 1;
            }
            else {
                roomX = w - TILE_X;
            }
        }

        if (roomY < 0) {
            // moving off top edge
            if (mapY > 0) {
                roomY += h;
                mapY -= 1;
            }
            else {
                roomY = 0;
            }
        }
        else if (roomY > h-1) {
            // moving off bottom edge
            if (mapY < zone.getHeight()-1) {
                roomY -= h;
                mapY += 1;
            }
            else {
                roomY = h - TILE_Y;
            }
        }
    }

    @Override
    public Dimension getWindowSize() {
        return new Dimension((STATUS_BAR_MAP_WIDTH + zone.getBlockSizeX())*TILE_X, zone.getBlockSizeY() *TILE_Y);
    }
    @Override
    public String getWindowTitle() { return "Engine Test"; }
    private int getBlockPixelWidth() {
        return zone.getBlockSizeX() * TILE_X;
    }
    private int getBlockPixelHeight() {
        return zone.getBlockSizeY() * TILE_Y;
    }
}
