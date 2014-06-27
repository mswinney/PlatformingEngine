package ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import engine.EngineTest;
import leveldata.EditTest;
import leveldata.ZettaUtil;
import ui.Controls.Input;

public class GameWindow extends Component {
	private static final long serialVersionUID = -7658415226723982231L;
	private static Controls controls = new Controls();
	private static Game game;
	private static boolean[] controlPressed;

	public static void main(String[] args) {
		final GameWindow gameWindow = new GameWindow();
		final JFrame f = new JFrame(game.getWindowTitle());
		// http://stackoverflow.com/questions/5916226/java-swing-set-actual-frame-size-inside
		// http://stackoverflow.com/questions/2796775/setting-the-size-of-a-contentpane-inside-of-a-jframe

		f.getContentPane().setPreferredSize(GameWindow.game.getWindowSize());
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// figure out where to move the window to
		// http://docs.oracle.com/javase/tutorial/uiswing/components/frame.html

		// periodically redraw the screen
		int delay = 16; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				game.advanceAnimations();
				gameWindow.repaint();
			}
		};
		new Timer(delay, taskPerformer).start();

		for (int i = 0; i < Controls.Input.values().length; i++) {
			f.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("pressed " + controls.controlKeys[i]),"prs "+i);
			f.getRootPane().getActionMap().put("prs "+i, new InputAction(Input.values()[i], true));
			f.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("released " + controls.controlKeys[i]),"rel "+i);
			f.getRootPane().getActionMap().put("rel "+i, new InputAction(Input.values()[i], false));
		}

		f.add(gameWindow);
		f.pack();
		f.setVisible(true);
	}

	public GameWindow() {
		// pass command-line params here?
		//game = new InputTest();
        ZettaUtil.init();
        //game = new EditTest("bigstage.dat");
        game = new EngineTest("bigstage.dat");

		// set up control scaffolding
		controlPressed = new boolean[Controls.Input.values().length];
		for (int i = 0; i < controlPressed.length; i++) {
			controlPressed[i] = false;
		}
	}

	public void paint(Graphics g) {
		game.paint(g);
	}

	private static class InputAction extends AbstractAction {
		private static final long serialVersionUID = 2414389823288329446L;
		// http://docs.oracle.com/javase/tutorial/uiswing/misc/action.html
		private Input type;
		private boolean pressed;
		public InputAction(Input newType, boolean newPressed) {
			type = newType;
			pressed = newPressed;
		}
		public void actionPerformed(ActionEvent e) {
			/* This block only fires an event if the state's changed.
			 * Without this, Java will rapid-fire a ton of "phantom" key-down events
			 * without firing a corresponding key-up event until it's actually released.
			 * Good enough for recording keystrokes, but this shit won't fly in a game.
			 */
			if (controlPressed[this.type.ordinal()] ^ this.pressed) {
				game.input(this.type, this.pressed);
				controlPressed[this.type.ordinal()] = this.pressed;
			}
		}
	}

}