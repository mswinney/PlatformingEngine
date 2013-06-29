package editor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class EditorPanel extends JPanel {
	private static final long serialVersionUID = -1322948386836422021L;
	public static final Color SELECTED_COLOR = Color.ORANGE;
	public static final Color HOVER_COLOR = Color.yellow;
	private LevelEditor editor;
	protected EditorPanel(LevelEditor editor) {
		this.editor = editor;
	}
	protected EditorPanel(LevelEditor editor, String label, Color bg) {
		this(editor);
		this.add(new JLabel(label));
		this.setBackground(bg);
	}
	protected LevelEditor getEditor() {
		return editor;
	}
	protected Dimension getEditorPanelSize() {
		return new Dimension(112, 112);
	}
	public Dimension getMinimumSize() {
		return getEditorPanelSize();
	}
	public Dimension getPreferredSize() {
		return getEditorPanelSize();
	}
	public Dimension getMaximumSize() {
		return getEditorPanelSize();
	}
}
