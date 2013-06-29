package editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import ui.Game;

public abstract class MouseHandler implements MouseListener, MouseMotionListener {
	private EditorPanel parent;
	private int tileX;
	private int tileY;
	public MouseHandler(EditorPanel p){
		this(p, Game.TILE_X, Game.TILE_Y);
	}
	public MouseHandler(EditorPanel p, int tileX, int tileY) {
		this.parent = p;
		this.tileX = tileX;
		this.tileY = tileY;
	}
	protected EditorPanel getParent() { return parent; }

	private void setCursorInternal(int i, int j) {
		this.setCursor(i, j);
		this.getParent().repaint();
	}
	protected abstract void setCursor(int i, int j);
	protected abstract void click(int i, int j, int k);
	protected void click(MouseEvent e) {
		click(e.getX() / tileX -1, e.getY() / tileY -1, e.getButton());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.setCursorInternal(e.getX() / tileX, e.getY() / tileY);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		click(e);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		this.setCursorInternal(e.getX() / tileX, e.getY() / tileY);
		click(e);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		this.setCursorInternal(-2, -2);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		this.getParent().getEditor().setLastClickType(e.getButton());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

}
