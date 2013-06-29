package editor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import platform.Block;
import platform.ZettaUtil;
import platform.layer.TileLayer.Tile;
import ui.Game;

public class TilePalettePanel extends EditorPanel {
	private static final long serialVersionUID = 9192853892710814134L;
	private SingleOptionPanel index;
	private TilePalette palette;
	public TilePalettePanel(LevelEditor editor) {
		super(editor, "Tile Palette", Color.MAGENTA);

		palette = new TilePalette(0, Game.TILE_X, Game.TILE_Y, 8, 4);
		index = new SingleOptionPanel("Tileset", this.getEditor().getRootPane(),
				new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int v = (int)((JSpinner)e.getSource()).getValue();
				try {
					palette.setTileset(v);
				}
				catch (IndexOutOfBoundsException ex) {
					// tried to go too far
					((JSpinner)e.getSource()).setValue(ZettaUtil.clamp(v, 0,
							Block.getNumTilesets() -1));
				}

			}
		});
		//index.setFocusable(true);
		//index.setEditable(true);

		this.add(index);
		this.add(palette);

		editor.setTilePalettePanel(this);
	}
	public Tile getCurrentTile() {
		return new Tile(index.getValue(), palette.getSelection());
	}
	public void setCurrentTile(Tile t) {
		this.index.setValue(t.getTileset());
		this.palette.setSelection(t.getIndex());
		this.repaint();
	}
	@Override
	protected Dimension getEditorPanelSize() {
		return new Dimension(2*super.getEditorPanelSize().width,
				super.getEditorPanelSize().height);
	}
	private static class TilePalette extends AbstractPalette {
		private static final long serialVersionUID = -4527074266853632640L;
		public TilePalette(int tilesetIndex, int tilesizeX, int tilesizeY,
				int columns, int rows) {
			super(Block.getTileset(tilesetIndex), tilesizeX, tilesizeY, columns, rows);

		}
		public void setTileset(int newTileset) {
			this.setTileset(Block.getTileset(newTileset));
			this.repaint();
		}

		@Override
		protected void selectionChanged() {
		}

	}
}
