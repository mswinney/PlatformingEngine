package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import editor.LevelEditor.Change.ChangeType;
import editor.MapIconPanel.MapIcon;

import platform.Block;
import platform.ZettaUtil;
import platform.Zone;
import platform.layer.CollisionLayer;
import platform.layer.Layer;
import platform.layer.TileLayer.Tile;
import platform.layer.TileTypeLayer;
import ui.Controls;
import ui.Controls.Input;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = 5715694716891041363L;
	private static final String DEFAULT_TITLE = "Platforming Engine Level Editor";
	private static final String WORKING_TITLE = "Platforming Engine Level Editor [%s]";
	private static final Color TOOLBAR_COLOR = new Color(136, 136, 255, 255);
	private static final Color DEFAULT_MAP_COLOR = new Color(171, 205, 239, 255);
	private static final String EDITOR_DATA_FILE = "editordata.dat";
	private static final String STAGE_DATA_FILE_EXTENSION = "dat";
	public static LevelEditor editor;
	private static boolean[] controlPressed;
	private final Controls controls = new Controls();
	private Zone zone;
	private int workingX;
	private int workingY;
	private Deque<Change> undo;
	private Deque<Change> redo;
	private String currentFilepath;
	private boolean isUnsavedNewFile = false;
	private boolean unsavedChanges = false;
	private TilePalettePanel tilePalettePanel;
	private RoomOptionsPanel roomOptionsPanel;
	private LayerPanel layerPanel;
	private TileTypeLayer currentLayer;
	private int lastClickType = MouseEvent.BUTTON1;

	public static void main(String[] args) {
		editor = new LevelEditor();
	}

	public LevelEditor() {
		super(DEFAULT_TITLE);
		ZettaUtil.init();
		this.setupKeypresses();

		undo = new ArrayDeque<Change>();
		redo = new ArrayDeque<Change>();
		controlPressed = new boolean[Input.values().length];
		for (int i = 0; i < controlPressed.length; i++) {
			controlPressed[i] = false;
		}

		this.loadEditorData(EDITOR_DATA_FILE);

		this.setJMenuBar(createMenuBar());
		this.setContentPane(createContentPane());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showExitDialog();
			}
		});
		
		this.setSize(640, 480);
		this.setMinimumSize(new Dimension(640, 480));
		this.setVisible(true); 
	}

	private void setupKeypresses() {
		for (int i = 0; i < Controls.Input.values().length; i++) {
			this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("pressed " +
					controls.controlKeys[i]),"prs "+i);
			this.getRootPane().getActionMap().put("prs "+i, new
					InputAction(Input.values()[i], true));
			this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("released " +
					controls.controlKeys[i]),"rel "+i);
			this.getRootPane().getActionMap().put("rel "+i, new
					InputAction(Input.values()[i], false));
		}
	}

	private static JMenuBar createMenuBar() {
		// http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
		JMenuBar menuBar;
		JMenu menu;

		//Create the menu bar.
		menuBar = new JMenuBar();

		// File menu
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		addMenuItem(menu, "New...", KeyEvent.VK_N,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						LevelEditor.editor.newFile(true);
					}
				});

		addMenuItem(menu, "Open...", KeyEvent.VK_O,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						LevelEditor.editor.showOpenDialog();
					}
				});
		addMenuItem(menu, "Save", KeyEvent.VK_S,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						LevelEditor.editor.saveFile();
					}
				});
		addMenuItem(menu, "Save As...")
		.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				LevelEditor.editor.showSaveDialog();
			}
		});
		menu.addSeparator();
		addMenuItem(menu, "Exit", KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK))
		.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				LevelEditor.editor.showExitDialog();
			}
		});

		// Edit menu
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		addMenuItem(menu, "Undo", KeyEvent.VK_U,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						LevelEditor.editor.undo();
					}
				});
		addMenuItem(menu, "Redo", KeyEvent.VK_R,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						LevelEditor.editor.redo();
					}
				});
		addMenuItem(menu, "Delete This Room...")
		.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				LevelEditor e = LevelEditor.editor;
				int i = JOptionPane.showConfirmDialog(e, "Deleting rooms cannot be undone.\n" +
						"Are you sure you want to\n" + "delete this room?", "Delete Room",
						JOptionPane.WARNING_MESSAGE);
				if (i == JOptionPane.OK_OPTION) {
					e.getZone().deleteBlock(e.getWorkingX(), e.getWorkingY());
					e.undo = new ArrayDeque<Change>();
					e.redo = new ArrayDeque<Change>();
					e.repaint();
				}
			}
		});
		menu.addSeparator();
		addMenuItem(menu, "Properties...").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// TODO: add properties menu to change starting position, name of stage,
				//   global door colors
				ZettaUtil.log("Properties hit");
			}
		});

		menu = new JMenu("Jump");
		menu.setMnemonic(KeyEvent.VK_J);
		menuBar.add(menu);
		addMenuItem(menu, "To Coordinates...",
				KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						new JumpToCoordinatesWindow(LevelEditor.editor).setVisible(true);
					}
				});
		addMenuItem(menu, "From Zone Map...",
				KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK))
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								new JumpToMapWindow(LevelEditor.editor);
							}
						});

		menu = new JMenu("Play");
		menu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(menu);
		addMenuItem(menu, "Play from Start", KeyEvent.VK_S,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK))
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						ZettaUtil.log("Play from Start");
						// TODO
					}
				});
		addMenuItem(menu, "Play from Here", KeyEvent.VK_H,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK))
						.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								ZettaUtil.log("Play from Here");
								// TODO
							}
						});

		return menuBar;
	}

	private static JMenuItem addMenuItem(JMenu menu, String name, int mnemonic,
			KeyStroke accelerator) {
		JMenuItem menuItem = new JMenuItem(name, mnemonic);
		menuItem.setAccelerator(accelerator);
		menu.add(menuItem);
		return menuItem;
	}
	private static JMenuItem addMenuItem(JMenu menu, String name, KeyStroke accelerator) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setAccelerator(accelerator);
		menu.add(menuItem);
		return menuItem;
	}
	private static JMenuItem addMenuItem(JMenu menu, String name) {
		JMenuItem menuItem = new JMenuItem(name);
		menu.add(menuItem);
		return menuItem;
	}

	private Container createContentPane() {
		//Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		// Set up bottom toolbar
		JPanel bottomToolbar = new JPanel();
		bottomToolbar.setLayout(new BoxLayout(bottomToolbar, BoxLayout.X_AXIS));
		bottomToolbar.setBackground(TOOLBAR_COLOR);
		contentPane.add(bottomToolbar, BorderLayout.PAGE_END);

		// Bottom Toolbar: Tile Palette
		TilePalettePanel tilePalette = new TilePalettePanel(this);
		bottomToolbar.add(tilePalette);
		// Bottom Toolbar: Entity Palette
		EntityPalettePanel entityPalette = new EntityPalettePanel(this);
		bottomToolbar.add(entityPalette);
		// Bottom Toolbar: Add Space
		Component glue = Box.createHorizontalGlue();
		bottomToolbar.add(glue);
		// Bottom Toolbar: Entity Options
		EntityOptionsPanel entityOptions = new EntityOptionsPanel(this);
		bottomToolbar.add(entityOptions);

		// Set up right toolbar
		JPanel rightToolbar = new JPanel();
		rightToolbar.setLayout(new BoxLayout(rightToolbar, BoxLayout.X_AXIS));
		contentPane.add(rightToolbar, BorderLayout.LINE_END);
		// Left side
		layerPanel = new LayerPanel(this);
		layerPanel.updateLayers(this.getCurrentBlock());
		rightToolbar.add(layerPanel);
		
		// Right side
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
		rightToolbar.add(rightSide);
		
		// Right Toolbar: Map Panel
		MapPanel mapPanel = new MapPanel(this);
		rightSide.add(mapPanel);

		// Right Toolbar: Room Options
		roomOptionsPanel = new RoomOptionsPanel(this);
		rightSide.add(roomOptionsPanel);


		// Main Editor Window
		TilePanel tile = new TilePanel(this);
		contentPane.add(tile, BorderLayout.CENTER);


		return contentPane;
	}
	public Zone getZone() { return zone; }

	public int getWorkingX() { return workingX; }
	public int getWorkingY() { return workingY; }
	public void setWorkingCoords(int wx, int wy) {
		workingX = ZettaUtil.clamp(wx, 0, zone.getWidth()-1);
		workingY = ZettaUtil.clamp(wy, 0, zone.getHeight()-1);
		if (layerPanel != null) {
			layerPanel.updateLayers(this.getCurrentBlock());
		}
		this.repaint();
	}
	public Block getCurrentBlock() { return zone.getBlock(workingX, workingY); }

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
				editor.input(this.type, this.pressed);
				controlPressed[this.type.ordinal()] = this.pressed;
			}
		}
	}

	private void input(Input type, boolean pressed) {
		if (pressed) {
			switch (type) {
			case LEFT: this.setWorkingCoords(workingX-1, workingY); break;
			case RIGHT: this.setWorkingCoords(workingX+1, workingY); break;
			case UP: this.setWorkingCoords(workingX, workingY-1); break;
			case DOWN: this.setWorkingCoords(workingX, workingY+1); break;
			case JUMP: ZettaUtil.log("Undo: " + undo + "\nRedo: " + redo +
					"\nCurrent Tile Layer: " + currentLayer); break;
			case DEBUG: ZettaUtil.log("You hit the DEBUG key.");
			break;
			}
		}
	}
	
	public void focus() {
		this.getRootPane().requestFocus();
	}

	/*
	 * File I/O functions
	 */
	private void updateTitle(String f) {
		if (f != null) {
			currentFilepath = f;
			super.setTitle(String.format(WORKING_TITLE,
					ZettaUtil.fileComponent(currentFilepath)));
		}
		else {
			super.setTitle(String.format(WORKING_TITLE, "New Zone"));
		}
	}
	private void updateTitle(boolean unsavedChanges) {
		if (this.unsavedChanges == unsavedChanges) {
			return;
		}
		if (unsavedChanges) {
			super.setTitle(this.getTitle() + " *");
		}
		else {
			super.setTitle(this.getTitle().substring(0, this.getTitle().lastIndexOf("*")-1 ));
		}
		this.unsavedChanges = unsavedChanges;
	}
	private void loadEditorData(String editorDataFile) {
		int wx, wy;
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(new File(editorDataFile), "r");
		} catch (FileNotFoundException e1) {
			ZettaUtil.log("Couldn't find editor data; probably first boot");
			this.newFile(false);
			return;
		}
		String[] line = null;
		String filename;

		try {
			line = f.readLine().split(";");
		} catch (IOException e) {
			ZettaUtil.log("Invalid editor data-- reading first line failed");
			this.newFile(false);
			try {
				f.close();
			} catch (IOException e1) {
				ZettaUtil.log("Closing the editor data file failed...? " +
						"Something's gone terribly wrong...");
			}
			return;
		}
		wx = Integer.parseInt(line[0]);
		wy = Integer.parseInt(line[1]);
		filename = line[2];
		this.openFile(new File(filename));
		this.setWorkingCoords(wx, wy);
		try {
			f.close();
		} catch (IOException e) {
			ZettaUtil.log("Closing the editor data file failed... " +
					"but we loaded from it, so we should be okay?");
		}
	}

	private void resetEditor() {
		this.setWorkingCoords(this.zone.getStartingX(), this.zone.getStartingY());
		this.undo = new ArrayDeque<Change>();
		this.redo = new ArrayDeque<Change>();
		this.repaint();
	}
	private void newFile(boolean isCloseable) {
		if (unsavedChanges) {
			int i = JOptionPane.showConfirmDialog(this,
					"If you create a new file now, all changes\n" + 
							"since your last save will be lost.\n" +
					"Save this file first?");
			switch (i) {
			case JOptionPane.YES_OPTION: this.saveFile(); break;
			case JOptionPane.NO_OPTION: break;
			case JOptionPane.CANCEL_OPTION: return;
			}
		}
		new NewZoneWindow(this, isCloseable).setVisible(true);
	}
	public void cleanUpAfterNewZone() {
		updateTitle(null);
		isUnsavedNewFile = true;
		this.resetEditor();
	}
	private void showOpenDialog() {
		String openFilepath = ZettaUtil.pathComponent(LevelEditor.editor.currentFilepath);
		JFileChooser fc = new JFileChooser(openFilepath);
		int returnVal = fc.showOpenDialog(LevelEditor.editor);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.openFile(fc.getSelectedFile());
		}
	}
	private void showSaveDialog() {
		String saveFilepath = ZettaUtil.pathComponent(LevelEditor.editor.currentFilepath);
		JFileChooser fc = new JFileChooser(saveFilepath);
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String extension = ZettaUtil.getExtension(f);
				return extension != null && extension.equals(STAGE_DATA_FILE_EXTENSION);      
			}

			@Override
			public String getDescription() {
				return "Stage Data Files";
			}

		});
		int returnVal = fc.showSaveDialog(LevelEditor.editor);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File f = fc.getSelectedFile();

				// tack on ".dat" if they didn't
				if (!ZettaUtil.getExtension(f).equals(STAGE_DATA_FILE_EXTENSION)) {
					f = new File(f.getAbsolutePath() + "." + STAGE_DATA_FILE_EXTENSION);
				}

				// check to see if we're overwriting
				if (f.exists()) {
					int i = JOptionPane.showConfirmDialog(this,
							"Are you sure you want to overwrite?", "Save As",
							JOptionPane.YES_NO_OPTION);
					if (i != JOptionPane.OK_OPTION) {
						return;
					}
				}

				// save
				this.saveData(f);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void showExitDialog() {
		if (unsavedChanges) {
			int i = JOptionPane.showConfirmDialog(this,
					"If you quit now, all changes since your\n" + 
							"last save will be lost.\n" +
							"Save this file?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (i) {
			case JOptionPane.YES_OPTION: this.saveFile(); this.quit();
			case JOptionPane.NO_OPTION: this.quit();
			case JOptionPane.CANCEL_OPTION: break;
			}
		}
		else {
			this.quit();
		}
	}
	private void quit() {
		System.exit(0);
	}
	private void openFile(File file) {
		isUnsavedNewFile = false;
		currentFilepath = file.getAbsolutePath();
		this.updateTitle(currentFilepath);
		try {
			this.zone = new Zone(currentFilepath);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error opening " + currentFilepath);
			this.newFile(false);
		}
		this.resetEditor();
	}
	private void saveFile() {
		if (isUnsavedNewFile) {
			// no existing file to save over
			showSaveDialog();
			return;
		}
		try {
			this.saveData(new File(currentFilepath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void saveData(File f) throws FileNotFoundException, IOException {
		File f1 = f.getAbsoluteFile();
		if (f.exists()) {
			File newFile = new File(f.getAbsolutePath() + ".bak");
			newFile.delete();
			f.renameTo(newFile);
		}
		this.zone.saveData(new RandomAccessFile(f1, "rwd"));
		this.updateTitle(false);
		this.currentFilepath = f1.getAbsolutePath();
		this.saveEditorData(EDITOR_DATA_FILE);
	}
	private void saveEditorData(String editorDataFile) throws IOException {
		RandomAccessFile f = new RandomAccessFile(new File(editorDataFile), "rwd");
		f.writeBytes(workingX + ";" + workingY + ";" + this.currentFilepath);
		f.setLength(f.getFilePointer());
		f.close();
	}

	public void setZone(Zone z) {
		this.zone = z;
	}

	public static class Change {
		public enum ChangeType {
			AMBIENCE, MUSIC, MAPICON, COLLISION_TILE, LAYER_CHANGE;
		}
		private ChangeType type;
		private Object before;
		private Object after;
		private Block block;
		Change(ChangeType type, Object before, Object after) {
			this.type = type;
			this.before = before;
			this.after = after;
			this.block = LevelEditor.editor.getCurrentBlock();
		}
		public String toString() {
			return type + " " + before + " -> " + after + " (" + block + ")";
		}
		private boolean isMoot() {
			return this.before.equals(this.after);
		}
		private static void execute(Change c) {
			switch (c.type) {
			case AMBIENCE: c.block.setAmbienceIndex((int)c.after);
			break;
			case MUSIC: c.block.setMusicIndex((int)c.after);
			break;
			case MAPICON: c.block.setMapIcon((MapIcon) c.after);
			break;
			case COLLISION_TILE: c.block.setCollisionLayer((CollisionLayer) c.after);
			break;
			case LAYER_CHANGE:
				c.block.replaceLayer((Layer)c.before, (Layer)c.after);
				editor.layerPanel.updateLayerListSelection((Layer)c.after);
			break;
			default: ZettaUtil.log("Unknown Change "+c); return;
			}
		}
		private static void undo(Change c) {
			switch (c.type) {
			case AMBIENCE: c.block.setAmbienceIndex((int)c.before);
			break;
			case MUSIC: c.block.setMusicIndex((int)c.before);
			break;
			case MAPICON: c.block.setMapIcon((MapIcon) c.before);
			break;
			case COLLISION_TILE: c.block.setCollisionLayer((CollisionLayer) c.before);
			break;
			case LAYER_CHANGE:
				c.block.replaceLayer((Layer)c.after, (Layer)c.before);
				editor.layerPanel.updateLayerListSelection((Layer)c.before);
				break;
			default: ZettaUtil.log("Unknown Change "+c); return;
			}
		}
	}

	public void change(ChangeType type, Object initialValue, Object finalValue) {
		if (this.getCurrentBlock() != null) {
			Change next = new Change(type, initialValue, finalValue);
			if (next.isMoot()) {
				System.out.println("Moot");
				return;
			}
			Change last = undo.peek();
			// intentional reference comparison below
			if (last != null && last.type == next.type && last.block == next.block &&
					last.after == next.before) {
				last.after = next.after;
			}
			else {
				undo.push(next);
			}
			if (redo.size() > 0) {
				redo.clear();
			}
			Change.execute(next);
			this.updateTitle(true);
			this.repaint();
		}
	}

	public void undo() {
		if (undo.size() > 0) {
			Change next = undo.pop();
			Change.undo(next);
			redo.push(next);
			this.repaint();
		}
	}
	public void redo() {
		if (redo.size() > 0) {
			Change next = redo.pop();
			Change.execute(next);
			undo.push(next);
			this.repaint();
		}
	}

	public void setTilePalettePanel(TilePalettePanel p) {
		this.tilePalettePanel = p;
	}

	public void executeTileClick(int i, int j, int k) {
		if (i < 0 || j < 0 ||
				i >= this.getZone().getBlockSizeX() || j >= this.getZone().getBlockSizeY()) {
			return;
		}
		if (this.isLastClick(k, MouseEvent.BUTTON1)) {
			// left click; write current tile to location
			if (0 <= i && i < this.getZone().getBlockSizeX() &&
					0 <= j && j < this.getZone().getBlockSizeY()) {
				if (this.getCurrentBlock() == null) {
					this.getZone().initBlock(this.getWorkingX(), this.getWorkingY(),
							roomOptionsPanel.getMusicIndex(),
							roomOptionsPanel.getAmbienceIndex(),
							LevelEditor.getDefaultMapIconIndex(),
							LevelEditor.getDefaultDoorBytes(),
							LevelEditor.getDefaultScrollByte(),
							this.getDefaultMapColor(),
							LevelEditor.getDefaultMapMarkingBytes());
					currentLayer = this.getCurrentBlock().getCollisionLayer();
				}
				// guaranteed existing block; update tile
				else if (currentLayer.getClass().equals(CollisionLayer.class)) {
					// updating collision layer
					this.updateCollisionTile(i, j, this.tilePalettePanel.getCurrentTile());
				}
				else {
					this.updateLayerTile(i, j, this.tilePalettePanel.getCurrentTile());
				}
				this.repaint();
			}
		}
		else if (this.isLastClick(k, MouseEvent.BUTTON3)) {
			// right click; read current tile from location
			if (this.getCurrentBlock() != null) {
				this.tilePalettePanel.setCurrentTile(currentLayer.getTile(i, j));
			}
		}
		else {
			ZettaUtil.log("Unknown clicktype " + k);
		}
		this.setLastClickType(k);
	}

	private boolean isLastClick(int k, int index) {
		return k == index || (k == 0 && lastClickType == index);
	}

	public void setLastClickType(int k) {
		if (k != 0) {
			this.lastClickType = k;
		}
	}

	private TileTypeLayer getCurrentTileLayer() {
		return layerPanel.getCurrentTileLayer();
	}

	private void updateCollisionTile(int i, int j, Tile currentTile) {
		CollisionLayer newLayer = (CollisionLayer)
				this.getCurrentBlock().getCollisionLayer().duplicate();
		if (currentTile.equals(newLayer.getTile(i, j))) {
			return;
		}
		newLayer.setTile(tilePalettePanel.getCurrentTile(), i, j);
		this.change(ChangeType.COLLISION_TILE, this.getCurrentBlock().getCollisionLayer(),
				newLayer);
	}

	private void updateLayerTile(int i, int j, Tile currentTile) {
		// TODO
		TileTypeLayer newLayer = this.getCurrentTileLayer().duplicate();
		if (currentTile.equals(newLayer.getTile(i, j))) {
			return;
		}
		newLayer.setTile(tilePalettePanel.getCurrentTile(), i, j);
		this.change(ChangeType.LAYER_CHANGE, this.getCurrentBlock().getCollisionLayer(),
				newLayer);
	}
	
	

	private static short getDefaultMapIconIndex() { return 0; }
	private static short getDefaultDoorBytes() { return 0; }
	private static byte getDefaultScrollByte() { return (byte) 0xFF; }
	private Color getDefaultMapColor() {
		Zone z = this.getZone();
		Block start = z.getBlock(z.getStartingX(), z.getStartingY());
		return start != null ? start.getMapColor() : DEFAULT_MAP_COLOR;
	}
	private static short getDefaultMapMarkingBytes() { return 0; }
}
