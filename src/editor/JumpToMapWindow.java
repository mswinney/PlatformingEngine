package editor;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import platform.Zone;

public class JumpToMapWindow extends JDialog implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -5214951792743399452L;
	private static final int DEFAULT_WIDTH = 9;
	private static final int DEFAULT_HEIGHT = 9;
	
	private LevelEditor parent;
	private int lastX, lastY;
	private int offsetX, offsetY;
	private int mouseTileX = Integer.MIN_VALUE, mouseTileY = Integer.MIN_VALUE;
	
	public JumpToMapWindow(LevelEditor edit) {
		super(LevelEditor.editor, "Jump from Zone Map", Dialog.ModalityType.DOCUMENT_MODAL);
		this.parent = edit;
		
		offsetX = (DEFAULT_WIDTH / 2 - parent.getWorkingX()) * (Zone.MAP_TILE_X +1);
		offsetY = (DEFAULT_HEIGHT / 2 - parent.getWorkingY()) * (Zone.MAP_TILE_Y +1);
		
		JPanel j = new JPanel() {
			private static final long serialVersionUID = 3756500092107416931L;
			@Override
			public void paint(Graphics g) {
				parent.getZone().paintFullMapWithGrid(g, offsetX, offsetY,
						parent.getWorkingX(), parent.getWorkingY(),
						mouseTileX, mouseTileY, this.getWidth(), this.getHeight());
			}
		};
		this.add(j);
		j.setPreferredSize( new Dimension(DEFAULT_WIDTH * (Zone.MAP_TILE_X+1),
				DEFAULT_HEIGHT * (Zone.MAP_TILE_Y+1)));
		j.addMouseListener(this);
		j.addMouseMotionListener(this);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (mouseTileX >= 0 && mouseTileX < parent.getZone().getWidth()
				&& mouseTileY >= 0 && mouseTileY < parent.getZone().getHeight()) {
			// jump, close window
			parent.setWorkingCoords(mouseTileX, mouseTileY);
			this.dispose();
		}
	}
	private void updateMouseTile(MouseEvent e) {
		int absoluteX = (e.getX() - offsetX), absoluteY = (e.getY() - offsetY);
		mouseTileX = absoluteX / (Zone.MAP_TILE_X + 1);
		mouseTileY = absoluteY / (Zone.MAP_TILE_Y + 1);
		if (absoluteX < 0)
			mouseTileX--;
		if (absoluteY < 0)
			mouseTileY--;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();
	}
	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - lastX;
		int dy = e.getY() - lastY;
		lastX = e.getX();
		lastY = e.getY();
		offsetX += dx;
		offsetY += dy;
		this.updateMouseTile(e);
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.updateMouseTile(e);
		this.repaint();
	}
	
}
