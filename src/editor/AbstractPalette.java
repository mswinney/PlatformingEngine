package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import leveldata.ZettaUtil;

public abstract class AbstractPalette extends JPanel {
	private static final long serialVersionUID = 6307052543710474271L;
	private Dimension SIZE;
	private int selected; // index of selected item
	private int hiddenRows; // number of rows scrolled past
	private int columns;
	private int rows;
	private int tileSizeX, tileSizeY;
	private static final int HGAP = 1, VGAP = 1;
	private static final int SCROLL_WIDTH = 12;
	private BufferedImage tileset;
	public AbstractPalette(BufferedImage tileset, int tilesizeX, int tilesizeY,
			int columns, int rows) {
		this.tileset = tileset;
		this.tileSizeX = tilesizeX;
		this.tileSizeY = tilesizeY;
		this.columns = columns;
		this.rows = rows;
		SIZE = new Dimension(columns * (tileSizeX+HGAP) + 1 + SCROLL_WIDTH,
				rows * (tileSizeY+VGAP) + 1);
		this.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				((AbstractPalette)e.getSource()).resolveClick(e.getX(), e.getY());
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
		});
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(this.getFillColor());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		int selectedX = -1, selectedY = -1;
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < columns; i++) {
				int tileIndex = (hiddenRows+j) * columns + i;
				g.setColor(getFrameColor());
				g.drawRect(i * (tileSizeX + HGAP), j * (tileSizeY + VGAP),
						tileSizeX+HGAP, tileSizeY+VGAP);
				ZettaUtil.drawTileAtLocation(tileIndex, tileset, g,
						1 + i * (tileSizeX + HGAP), 1 + j * (tileSizeY + VGAP),
						tileSizeX, tileSizeY);
				if (tileIndex == this.selected) {
					selectedX = i;
					selectedY = j;
				}
			}
		}
		if (selectedX != -1) {
			g.setColor(getSelectionIconColor());
			g.drawRect(selectedX * (tileSizeX + HGAP), selectedY * (tileSizeY + VGAP),
					tileSizeX+HGAP, tileSizeY+VGAP);
		}
	}
	
	public void resolveClick(int x, int y) {
		if (x > columns * (tileSizeX+HGAP) -1) {
			// scroll bar click
			ZettaUtil.log("Scroll bar click!");
			if (y < rows * tileSizeY / 2) { hiddenRows = Math.max(hiddenRows-1, 0); }
			else { hiddenRows++; }
		}
		else {
			// palette click
			int tileIndex = x / (tileSizeX+HGAP) + ( y / (tileSizeY+VGAP) + hiddenRows) * columns;
			this.selected = tileIndex;
			this.selectionChanged();
		}
		this.repaint();
	}

	@Override
	public Dimension getMinimumSize() { return SIZE; }
	public Dimension getMaximumSize() { return SIZE; }
	public Dimension getPreferredSize() { return SIZE; }
	public int getSelection() { return selected; }
	protected Color getFillColor() { return Color.CYAN; }
	protected static Color getFrameColor() { return Color.GRAY; }
	protected static Color getSelectionIconColor() { return EditorPanel.SELECTED_COLOR; }
	protected abstract void selectionChanged();
	public void setSelection(int index) { selected = index; }
	protected void setTileset(BufferedImage image) {
		this.tileset = image;
	}
}
