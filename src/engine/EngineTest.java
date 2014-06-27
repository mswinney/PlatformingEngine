package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

import leveldata.Block;
import leveldata.ZettaUtil;
import leveldata.Zone;
import ui.Controls.Input;
import ui.Game;

public class EngineTest extends ui.Game {

    private static final String DATA_LOCATION = "resources/data/";
    // player global coordinates
    private int playerX;
    private int playerY;
    // screen global coordinates
    // TODO
    private int screenX;
    private int screenY;
    // amount of change remaining to bleed into screen coordinates
    // TODO implement
    private int playerDeltaX;
    private int playerDeltaY;
    private int screenDeltaX;
    private int screenDeltaY;
    private Zone zone;
    private int showPlayerDot;
    private static final int MAP_TILE_WIDTH = 16;
    private static final int MAP_TILE_HEIGHT = 16;
    private static final int STATUS_BAR_MAP_HEIGHT = 5;
    private static final int STATUS_BAR_MAP_WIDTH = 3;
    private static final int STATUS_BAR_MAP_X_OFFSET = 0;
    private static final int STATUS_BAR_MAP_Y_OFFSET = 0;
    private static final int SIDEBAR_WIDTH = STATUS_BAR_MAP_WIDTH * MAP_TILE_WIDTH;
    private static final Color PLAYER_DOT_COLOR = new Color(128, 128, 128, 200);
    private static final Color MAP_BACKGROUND_COLOR = Color.BLUE;
    private static final Color SIDEBAR_BACKGROUND_COLOR = Color.BLACK;
    private static final Color GAME_BACKGROUND_COLOR = Color.GRAY;
    int delta = 16;
    private boolean[] scrollLocks = new boolean[4]; // up, down, left, right

    public EngineTest(String stageName) {
        String stageLoc = DATA_LOCATION + stageName;
        try {
            zone = new Zone(stageLoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Block.loadTilesets();
        playerX = zone.getStartingX() * zone.getBlockSizeX();
        playerY = zone.getStartingY() * zone.getBlockSizeY();
    }

    @Override
    public void paint(Graphics g) {
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
        drawPlayer(g);
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.CYAN);
        //g.fillRect(sidebarWidth + roomX + 6, roomY + 6, 4, 4);
        //g.fillRect(zone.getBlockSizeX()/2 * TILE_X + SIDEBAR_WIDTH + 6,
        //        zone.getBlockSizeY()/2 * TILE_Y + 6, 4, 4);
        g.fillRect(playerX - screenX + SIDEBAR_WIDTH + (TILE_X * zone.getBlockSizeX()/2),
                playerY - screenY + (TILE_Y * zone.getBlockSizeY()/2), 16, 16);
    }

    private void toggleCameraLock() {
        scrollLocks[0] = !scrollLocks[0];
        ZettaUtil.log("Screen lock: " + scrollLocks[0]);
    }

    private void drawCurrentBlock(Graphics g) {
        Dimension windowSize = this.getWindowSize();
        // draw background
        g.setColor(GAME_BACKGROUND_COLOR);
        g.fillRect(SIDEBAR_WIDTH, 0, windowSize.width - SIDEBAR_WIDTH, windowSize.height);

        drawRoom(g, 0, 0, 0, 0);
        // draw neighboring blocks
        if (shouldDrawAboveRoom()) {
            drawRoom(g, 0, -1, 0, -zone.getBlockSizeY());
        }
        else if (shouldDrawBelowRoom()) {
            drawRoom(g, 0, 1, 0, zone.getBlockSizeY());
        }
        if (shouldDrawLeftRoom()) {
            drawRoom(g, -1, 0, -zone.getBlockSizeX(), 0);
            // draw diagonals on left
            if (shouldDrawAboveRoom()) {
                drawRoom(g, -1, -1, -zone.getBlockSizeX(), -zone.getBlockSizeY());
            }
            else if (shouldDrawBelowRoom()) {
                drawRoom(g, -1, 1, -zone.getBlockSizeX(), zone.getBlockSizeY());
            }
        }
        else if (shouldDrawRightRoom()) {
            drawRoom(g, 1, 0, zone.getBlockSizeX(), 0);
            // draw diagonals on right
            if (shouldDrawAboveRoom()) {
                drawRoom(g, 1, -1, zone.getBlockSizeX(), -zone.getBlockSizeY());
            }
            else if (shouldDrawBelowRoom()) {
                drawRoom(g, 1, 1, zone.getBlockSizeX(), zone.getBlockSizeY());
            }
        }
    }

    private void drawRoom(Graphics g, int mapDeltaX, int mapDeltaY, int roomDeltaX, int roomDeltaY) {
        int mapX = this.getScreenMapX();
        int mapY = this.getScreenMapY();
        int roomX = zone.getBlockSizeX()/2;
        int roomY = zone.getBlockSizeY()/2;
        int pixelOffsetX = SIDEBAR_WIDTH-this.getScreenRoomX();
        int pixelOffsetY = -this.getScreenRoomY();
        zone.drawBlockOffset(g, mapX + mapDeltaX, mapY + mapDeltaY, roomX + roomDeltaX, roomY + roomDeltaY,
                pixelOffsetX, pixelOffsetY);
    }

    private boolean shouldDrawRightRoom() {
        return this.getPlayerRoomX() > zone.getBlockSizeX() * TILE_X / 2;
    }
    private boolean shouldDrawLeftRoom() {
        return this.getPlayerRoomX() < zone.getBlockSizeX() * TILE_X / 2;
    }
    private boolean shouldDrawBelowRoom() {
        return this.getPlayerRoomY() > zone.getBlockSizeY() * TILE_Y / 2;
    }
    private boolean shouldDrawAboveRoom() {
        return this.getPlayerRoomY() < zone.getBlockSizeY() * TILE_Y / 2;
    }

    private void drawMap(Graphics g) {
        g.setColor(MAP_BACKGROUND_COLOR);
        g.fillRect(STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET, SIDEBAR_WIDTH,
                STATUS_BAR_MAP_HEIGHT * MAP_TILE_HEIGHT);
        zone.paintFullMap(g, this.getPlayerMapX(), this.getPlayerMapY(), STATUS_BAR_MAP_WIDTH, STATUS_BAR_MAP_HEIGHT,
                STATUS_BAR_MAP_X_OFFSET, STATUS_BAR_MAP_Y_OFFSET);
        g.setColor(PLAYER_DOT_COLOR);
        g.fillRect(STATUS_BAR_MAP_WIDTH/2 * MAP_TILE_WIDTH + 6, STATUS_BAR_MAP_HEIGHT/2 * MAP_TILE_HEIGHT + 6, 4, 4);
    }

    private int getPlayerMapX() {
        return playerX / (TILE_X * zone.getBlockSizeX());
    }
    private int getPlayerMapY() {
        return playerY / (TILE_Y * zone.getBlockSizeX());
    }
    private int getPlayerRoomX() {
        return playerX % (TILE_X * zone.getBlockSizeX());
    }
    private int getPlayerRoomY() {
        return playerY % (TILE_Y * zone.getBlockSizeY());
    }
    private int getScreenMapX() {
        return screenX / (TILE_X * zone.getBlockSizeX());
    }
    private int getScreenMapY() {
        return screenY / (TILE_Y * zone.getBlockSizeX());
    }
    private int getScreenRoomX() {
        return screenX % (TILE_X * zone.getBlockSizeX());
    }
    private int getScreenRoomY() {
        return screenY % (TILE_Y * zone.getBlockSizeY());
    }

    @Override
    public void advanceAnimations() {
        showPlayerDot = showPlayerDot>60 ? 0: showPlayerDot+1;
        // update player position
        if (playerDeltaX > 0) {
            playerX++;
            playerDeltaX--;
        }
        else if (playerDeltaX < 0) {
            playerX--;
            playerDeltaX++;
        }
        if (playerDeltaY > 0) {
            playerY++;
            playerDeltaY--;
        }
        else if (playerDeltaY < 0) {
            playerY--;
            playerDeltaY++;
        }
        // update screen position
        if (screenDeltaX > 0) {
            screenX++;
            screenDeltaX--;
        }
        else if (screenDeltaX < 0) {
            screenX--;
            screenDeltaX++;
        }
        if (screenDeltaY > 0) {
            screenY++;
            screenDeltaY--;
        }
        else if (screenDeltaY < 0) {
            screenY--;
            screenDeltaY++;
        }
    }

    @Override
    public void input(Input type, boolean pressed) {
        if (pressed) {
            switch (type) {
                case LEFT: movePixels(-delta, 0); break;
                case RIGHT: movePixels(delta, 0); break;
                case UP: movePixels(0, -delta); break;
                case DOWN: movePixels(0, delta); break;
                case DEBUG:
                    //delta = Math.max( delta/2, 1);
                    toggleCameraLock();
                    break;
                case JUMP:
                    //delta *= 2;
                    System.out.println("X: " + this.getPlayerMapX() + ":" + this.getPlayerRoomX() +
                            "\tY: " + this.getPlayerMapY() + ":" + this.getPlayerRoomY() +
                            "\t\t Screen X: " + this.getScreenMapX() + ":" + this.getScreenRoomX() +
                            "\t Screen Y: " + this.getScreenMapY() + ":" + this.getScreenRoomY());
                    break;
            }
        }
    }

    private void movePixels(int x, int y) {
        // TODO: check scroll lock, update accordingly
        // set up amount of delta if moving onto a locked axis
        playerDeltaX += x;
        playerDeltaY += y;
        screenDeltaX += x;
        screenDeltaY += y;
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
