package editor;

import java.awt.Color;
import java.awt.Dimension;

public class EntityPalettePanel extends EditorPanel {
	private static final long serialVersionUID = -7690540744971072691L;
	public EntityPalettePanel(LevelEditor editor) {
		super(editor, "Entity Palette", Color.CYAN);
	}
	@Override
	protected Dimension getEditorPanelSize() {
		return new Dimension(2*super.getEditorPanelSize().width,
				super.getEditorPanelSize().height);
	}
}
