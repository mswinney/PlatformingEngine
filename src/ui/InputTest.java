package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import platform.ZettaUtil;

public class InputTest extends Game {
	
	private static boolean[] states;
	private static BufferedImage tileset;
	private static BufferedImage background;
	private static final String TILESET_LOCATION = "resources/inputtest.png";
	private static final int WINDOW_X = 200;//Game.TILE_X * Input.values().length;
	private static final int WINDOW_Y = 350;//Game.TILE_Y;

	public InputTest() {
		tileset = ZettaUtil.loadImage(TILESET_LOCATION);
		background = ZettaUtil.loadImage("resources/bgtest.png");
		states = new boolean[Controls.Input.values().length];
		for (int i = 0; i < states.length; i++) {
			states[i] = false;
		}
	}
	
	
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, 200, 350, 0, 0, 200, 350, null);
		for (int i = 0; i < Controls.Input.values().length; i++) {
			int index = i + (states[i] ? 6 : 0);
			ZettaUtil.drawTile(index, tileset, g, i, 0);
		}
	}

	@Override
	public void advanceAnimations() {
		// TODO Auto-generated method stub

	}

	@Override
	public void input(Controls.Input type, boolean pressed) {
		if (type == Controls.Input.DEBUG && pressed && states[type.ordinal()] == false) {
			// switching debug from false to true, show info
			for (int i = 0; i < states.length; i++) {
				System.out.print(states[i] + " ");
			}
			System.out.println();
		}
		states[type.ordinal()] = pressed;
	}

	@Override
	public Dimension getWindowSize() {
		// TODO Auto-generated method stub
		return new Dimension(WINDOW_X, WINDOW_Y);
	}
	@Override
	public String getWindowTitle() { return "Input Test"; }

}
