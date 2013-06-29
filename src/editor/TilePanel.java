package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import platform.Zone;
import ui.Game;

public class TilePanel extends EditorPanel {
	private static final long serialVersionUID = -8701836053004554493L;
	private static final Color BACKGROUND_COLOR = new Color(120, 120, 120, 255);
	
	private int cursorTileX = -2;
	private int cursorTileY = -2;
	TilePanel(LevelEditor editor) {
		super(editor);
		
		TilePanelMouseHandler h = new TilePanelMouseHandler(this);
		this.addMouseListener(h);
		this.addMouseMotionListener(h);
	}
	protected Dimension getEditorPanelSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	@Override
	public void paint(Graphics g) {
		// draw background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		// draw fringes
		this.getEditor().getZone().paintFringes(g, this.getEditor().getWorkingX(),
				this.getEditor().getWorkingY());
		// draw current room
		this.getEditor().getZone().paintRoomEditor(g, this.getEditor().getWorkingX(),
				this.getEditor().getWorkingY());
		// draw border around current tile
		g.setColor(HOVER_COLOR);
		g.drawRect(cursorTileX * Game.TILE_X, cursorTileY * Game.TILE_Y,
				Game.TILE_X-1, Game.TILE_Y-1);
	}
	
	private class TilePanelMouseHandler extends MouseHandler {
		
		public TilePanelMouseHandler(TilePanel p) {
			super(p);
		}
		
		protected void setCursor(int i, int j) {
			Zone z = getParent().getEditor().getZone();
			TilePanel parent = (TilePanel) getParent();
			if (i > 0 && j > 0 && i < z.getBlockSizeX() + 1 && j < z.getBlockSizeY() +1) {
				parent.cursorTileX = i;
				parent.cursorTileY = j;
			}
			else {
				parent.cursorTileX = -2;
				parent.cursorTileY = -2;
			}
		}
		
		protected void click(int i, int j, int k) {
			getParent().getEditor().executeTileClick(i, j, k);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			getParent().getEditor().focus();
		}
		
	}
}
