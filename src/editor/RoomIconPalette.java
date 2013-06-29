package editor;

import java.awt.Color;
import java.awt.image.BufferedImage;

import editor.MapIconPanel.MapIcon.ScrollLock;

import platform.Block;
import platform.Zone;
import ui.Game;

public class RoomIconPalette extends AbstractPalette {
	private static final long serialVersionUID = 4718078980279137161L;
	private Color fillColor;
	private MapIconConstructWindow parent;
	
	public RoomIconPalette(Block block, MapIconConstructWindow parent) {
		super(Block.getMapTileset(), Zone.MAP_TILE_X * 2, Zone.MAP_TILE_Y * 2, 8, 4);
		this.fillColor = block.getMapColor();
		this.parent = parent;
	}
	@Override
	protected Color getFillColor() {
		return fillColor;
	}
	protected void selectionChanged() {
		parent.getPreviewPanel().setMapIcon(this.getSelection());
		this.updateScrollLocking();
		parent.repaint();
	}
	private void updateScrollLocking() {
		BufferedImage tileset = Block.getMapTileset();
		int tileIndex = parent.getPreviewPanel().getMapIcon().getMapIconIndex();
		int tileX = Game.TILE_X;
		int tileY = Game.TILE_Y;
		int tileSheetWidth = tileset.getWidth()/tileX;
		int sheetLocationX = tileX*(tileIndex%tileSheetWidth);
		int sheetLocationY = tileY*(tileIndex/tileSheetWidth);
		
		this.updateScrollLock(tileset, ScrollLock.UP,
				sheetLocationX + Zone.MAP_TILE_X/2, sheetLocationY +1);
		this.updateScrollLock(tileset, ScrollLock.LEFT,
				sheetLocationX +1, sheetLocationY +Zone.MAP_TILE_Y/2);
		this.updateScrollLock(tileset, ScrollLock.DOWN,
				sheetLocationX + Zone.MAP_TILE_X/2, sheetLocationY +Zone.MAP_TILE_Y -1);
		this.updateScrollLock(tileset, ScrollLock.RIGHT,
				sheetLocationX +Zone.MAP_TILE_X -1, sheetLocationY +Zone.MAP_TILE_Y/2);
		
		parent.repaint();
	}
	private void updateScrollLock(BufferedImage tileset, ScrollLock s, int x, int y) {
		int upPix = tileset.getRGB(x, y);
		boolean upLock = Math.abs(upPix & 0xFF000000) > 0;
		parent.getPreviewPanel().getMapIcon().setScrollLock(s, upLock);
	}
}
