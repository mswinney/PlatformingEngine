package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import editor.LevelEditor.Change.ChangeType;

import platform.Block;

public class RoomOptionsPanel extends EditorPanel {
	private static final long serialVersionUID = -6453287591760809363L;
	private Block currentBlock = null;
	private SingleOptionPanel ambienceIndex;
	private SingleOptionPanel musicIndex;
	public RoomOptionsPanel(LevelEditor editor) {
		super(editor, "Room Options", Color.YELLOW);
		initializeFields(this);
		loadFields(editor.getCurrentBlock());
	}

	private void initializeFields(RoomOptionsPanel roomOptionsPanel) {
		musicIndex = new SingleOptionPanel("Music", this.getEditor().getRootPane(),
				new ChangeTypeListener(ChangeType.MUSIC));
		this.add(musicIndex);
		ambienceIndex = new SingleOptionPanel("Ambience", this.getEditor().getRootPane(),
				new ChangeTypeListener(ChangeType.AMBIENCE));
		this.add(ambienceIndex);
		this.add(new MapIconButton());
	}
	
	private class ChangeTypeListener implements ChangeListener {
		private ChangeType t;
		public ChangeTypeListener(ChangeType t) {
			this.t = t;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			LevelEditor edit = RoomOptionsPanel.this.getEditor();
			if (edit != null && LevelEditor.editor != null) {
				edit.change(t, this.getCurrent(t), (int)((JSpinner)e.getSource()).getValue());
			}
		}
		
		private int getCurrent(ChangeType t) {
			switch (t) {
			case MUSIC:
				return RoomOptionsPanel.this.getEditor().getCurrentBlock().getMusicIndex();
			case AMBIENCE:
				return RoomOptionsPanel.this.getEditor().getCurrentBlock().getAmbienceIndex();
			default:
				return 0;
			}
		}
	}
	protected Dimension getEditorPanelSize() {
		return new Dimension(super.getEditorPanelSize().width,
				2*super.getEditorPanelSize().height);
	}
	public Dimension getMaximumSize() {
		return new Dimension(getEditorPanelSize().width, Integer.MAX_VALUE);
	}
	public void paint(Graphics g) {
		super.paint(g);
		this.updateFields();
	}
	private void updateFields() {
		currentBlock = this.getEditor().getCurrentBlock();
		loadFields(currentBlock);
	}
	private void loadFields(Block block) {
		this.currentBlock = block;
		if (block != null) {
			ambienceIndex.setValue(block.getAmbienceIndex());
			musicIndex.setValue(block.getMusicIndex());
		}
	}
	public short getMusicIndex() { return (short) musicIndex.getValue(); }
	public short getAmbienceIndex() { return (short) ambienceIndex.getValue(); }
	private static class MapIconButton extends JButton {
		private static final long serialVersionUID = -7813260718703177731L;
		public MapIconButton() {
			super();
			this.setFocusable(false);
			this.add(new MapIconPanel(true));
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Block block = LevelEditor.editor.getCurrentBlock();
					if (block != null) {
						MapIconConstructWindow icon =
								new MapIconConstructWindow(block);
						icon.setVisible(true);
					}
				}
			});
		}
	}

	
}
