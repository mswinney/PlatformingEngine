package editor;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import platform.Zone;

import editor.MapIconConstructWindow.CloseOnOkayListener;

public class NewZoneWindow extends JDialog {
	private static final long serialVersionUID = 1895789087897469290L;
	public int width = 32;
	public int height = 32;
	public int blockSizeX = 16;
	public int blockSizeY = 16;

	LevelEditor parent;

	public NewZoneWindow(LevelEditor parent, boolean isCloseable) {
		super(parent, "New Zone", Dialog.ModalityType.DOCUMENT_MODAL);
		this.parent = parent;
		this.setLayout(new FlowLayout());
		JButton okay = new JButton("OK");
		okay.addActionListener(new CloseOnOkayListener(this));
		SingleOptionPanel widthPanel = null;
		SingleOptionPanel heightPanel = null;
		SingleOptionPanel blockSizeXPanel = null;
		SingleOptionPanel blockSizeYPanel = null;
		try {
			widthPanel = new SingleOptionPanel("Map Width", this.getRootPane(),
					new FieldChangeListener(this, "width"));
			widthPanel.setValue(width);
			heightPanel = new SingleOptionPanel("Map Height", this.getRootPane(),
					new FieldChangeListener(this, "height"));
			heightPanel.setValue(height);
			blockSizeXPanel = new SingleOptionPanel("Block Size X (tiles)", this.getRootPane(),
					new FieldChangeListener(this, "blockSizeX"));
			blockSizeXPanel.setValue(blockSizeX);
			blockSizeYPanel = new SingleOptionPanel("Block Size Y (tiles)", this.getRootPane(),
					new FieldChangeListener(this, "blockSizeY"));
			blockSizeYPanel.setValue(blockSizeY);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		this.add(widthPanel);
		this.add(heightPanel);
		this.add(blockSizeXPanel);
		this.add(blockSizeYPanel);
		this.add(okay);
		
		if (!isCloseable)
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.setSize(200, 400);
		this.setResizable(false);
	}

	public void dispose() {
		parent.setZone(this.generateZone());
		parent.cleanUpAfterNewZone();
		super.dispose();
	}

	private Zone generateZone() {
		return new Zone(width, height, blockSizeX, blockSizeY);
	}

	private static class FieldChangeListener implements ChangeListener {
		private Field f;
		private NewZoneWindow w;
		private FieldChangeListener(NewZoneWindow w, String fieldName)
				throws NoSuchFieldException, SecurityException {
			this.w = w;
			this.f = w.getClass().getField(fieldName);
		}
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				f.set(w, (int)((JSpinner)e.getSource()).getValue());
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
	}
}
