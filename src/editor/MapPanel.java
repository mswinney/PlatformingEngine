package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import leveldata.ZettaUtil;

public class MapPanel extends EditorPanel {
	private static final long serialVersionUID = -3837809056314759879L;
	private static final Color BACKGROUND_COLOR = Color.gray;
	private static final int TILE_WIDTH = 7;
	private static final int TILE_HEIGHT = 7;
	private static final int TILE_SIZE = 16;
	private int cursorTileX = -2;
	private int cursorTileY = -2;
	
	protected MapPanel(LevelEditor editor) {
		super(editor);
		MapPanelMouseHandler m = new MapPanelMouseHandler(this, TILE_SIZE, TILE_SIZE);
		this.addMouseListener(m);
		this.addMouseMotionListener(m);
	}
	
	@Override
	public void paint(Graphics g) {
		// draw background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.getEditor().getZone().paintFullMap(g, this.getEditor().getWorkingX(),
				this.getEditor().getWorkingY(), TILE_WIDTH, TILE_HEIGHT);
		// draw selected room outline
		g.setColor(EditorPanel.SELECTED_COLOR);
		g.drawRect(this.getX()+TILE_WIDTH/2*TILE_SIZE -1, this.getY()+TILE_HEIGHT/2*TILE_SIZE -1,
				17,17);
		g.setColor(EditorPanel.HOVER_COLOR);
		g.drawRect(cursorTileX*TILE_SIZE -2, cursorTileY*TILE_SIZE -2,
				19,19);
	}
	
	private static class MapPanelMouseHandler extends MouseHandler {

		public MapPanelMouseHandler(MapPanel mapPanel, int tileX, int tileY) {
			super(mapPanel, tileX, tileY);
		}

		@Override
		protected void setCursor(int i, int j) {
			MapPanel p = (MapPanel)this.getParent();
			p.cursorTileX = i;
			p.cursorTileY = j;
		}

		@Override
		protected void click(int i, int j, int k) {
			// convert to offsets from center
			i = i - TILE_WIDTH / 2 + 1;
			j = j - TILE_HEIGHT / 2 + 1;
			if (k != MouseEvent.BUTTON1) return;
			
			// warp to new coords
			LevelEditor le = this.getParent().getEditor();
			int a = le.getWorkingX();
			int b = le.getWorkingY();
			int x = ZettaUtil.clamp(i+a, 0, le.getZone().getWidth() - 1);
			int y = ZettaUtil.clamp(j+b, 0, le.getZone().getHeight() - 1);
			le.setWorkingCoords(x, y);
			this.getParent().getEditor().repaint();
			
			this.getParent().getEditor().focus();
		}
	}
}
