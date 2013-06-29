package ui;

import java.awt.Dimension;
import java.awt.Graphics;

public abstract class Game {
	public static final int TILE_X = 16;
	public static final int TILE_Y = 16;
	public abstract void paint(Graphics g);
	public abstract void advanceAnimations();
	/** Process input fed by the window.
	 * @param type Input type (see the enum Controls.Input)
	 * @param pressed True = button pressed, false = button released.
	 */
	public abstract void input(ui.Controls.Input type, boolean pressed);
	public abstract Dimension getWindowSize();
	public abstract String getWindowTitle();
	


}
