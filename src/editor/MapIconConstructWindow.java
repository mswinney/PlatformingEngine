package editor;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import editor.LevelEditor.Change.ChangeType;
import editor.MapIconPanel.MapIcon.DoorPosition;
import editor.MapIconPanel.MapIcon.ScrollLock;

import leveldata.Block;

public class MapIconConstructWindow extends JDialog {
	private static final long serialVersionUID = 6172096767912903453L;

	//private Block block;
	private MapIconPanel preview;
	private LockButton[] lockButtons;

	public MapIconConstructWindow(Block block) {
		super(LevelEditor.editor, "Edit Map Icon", Dialog.ModalityType.DOCUMENT_MODAL);
		//this.block = block;
		this.setLayout(new FlowLayout());

		preview = new MapIconPanel();
		preview.updateData();

		JButton mapColor = new JButton("Map Color");
		JColorChooser colorSelect = new JColorChooser();

		mapColor.addActionListener(new ColorfulListener(this, colorSelect));

		JButton okay = new JButton("OK");
		okay.addActionListener(new CloseOnOkayListener(this));

		RoomIconPalette iconPalette = new RoomIconPalette(block, this);
		iconPalette.setSelection(preview.getMapIcon().getMapIconIndex());

		JPanel lockButtonPanel = new JPanel();
		lockButtonPanel.setLayout(new BoxLayout(lockButtonPanel, BoxLayout.Y_AXIS));
		lockButtons = new LockButton[ScrollLock.values().length];
		for (int i = 0; i < ScrollLock.values().length; i++) {
			lockButtons[i] = new LockButton(preview, ScrollLock.values()[i]);
			lockButtonPanel.add(lockButtons[i]);
		}

		JPanel doorSpinners = new JPanel();
		doorSpinners.setLayout(new BoxLayout(doorSpinners, BoxLayout.Y_AXIS));
		doorSpinners.add(generateDoorSpinner("Top Door", DoorPosition.UP));
		doorSpinners.add(generateDoorSpinner("Left Door", DoorPosition.LEFT));
		doorSpinners.add(generateDoorSpinner("Bottom Door", DoorPosition.DOWN));
		doorSpinners.add(generateDoorSpinner("Right Door", DoorPosition.RIGHT));

		this.add(iconPalette);
		this.add(preview);
		this.add(lockButtonPanel);
		this.add(doorSpinners);
		this.add(mapColor);
		this.add(okay);

		this.setSize(400, 400);
		this.setResizable(false);
	}
	private SingleOptionPanel generateDoorSpinner(String title, final DoorPosition pos) {
		SingleOptionPanel spin = new SingleOptionPanel(title, this.getRootPane(),
				new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview.getMapIcon().setDoor(pos,
						(int) ((JSpinner)e.getSource()).getValue());
				preview.repaint();
			}
		});
		spin.setValue(preview.getMapIcon().getDoor(pos));
		spin.setMax(14);
		return spin;
	}

	private void saveAndClose() {
		LevelEditor.editor.change(ChangeType.MAPICON,
				LevelEditor.editor.getCurrentBlock().getMapIcon(), preview.getMapIcon());
	}
	public void dispose() {
		this.saveAndClose();
		super.dispose();
	}
	public void repaint() {
		for (LockButton l : lockButtons) {
			l.repaint();
		}
		super.repaint();
	}
	public MapIconPanel getPreviewPanel() {
		return preview;
	}

	public static class CloseOnOkayListener implements ActionListener {
		private JDialog w;
		public CloseOnOkayListener(JDialog w) {
			this.w = w;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			w.dispose();
		}
	}
	private static class ColorfulListener implements ActionListener {
		private MapIconConstructWindow w;
		private JColorChooser color;
		private ColorfulListener(MapIconConstructWindow w, JColorChooser color) {
			this.w = w;
			this.color = color;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			color.setColor(w.preview.getMapIcon().getMapColor());
			JColorChooser.createDialog(w, "Select Map Color", true, color, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.preview.getMapIcon().setMapColor(color.getColor());
				}
			}, null).setVisible(true);
		}
	}
	private static class LockButton extends JButton {
		private static final long serialVersionUID = -6714423773183918137L;
		private MapIconPanel p;
		private ScrollLock l;
		public LockButton(MapIconPanel preview, ScrollLock lock) {
			this.p = preview;
			this.l = lock;
			this.refreshText();
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					flipLock();
				}
			});
		}
		protected void flipLock() {
			p.getMapIcon().setScrollLock(l, !getLocked());
			this.refreshText();
			p.repaint();
		}
		public void repaint() {
			this.refreshText();
			super.repaint();
		}
		private boolean getLocked() { return p.getMapIcon().getScrollLock(l); }
		private void refreshText() {
			if (l != null)
				this.setText(l.name() + " " + (this.getLocked()?"Locked":"Unlocked"));
		}

	}
}
