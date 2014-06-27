package editor;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import editor.LevelEditor.Change.ChangeType;

import leveldata.layer.ColorLayer;
import leveldata.layer.Layer;

public class LayerEditWindow extends JDialog {
	private static final long serialVersionUID = 8744315264157837679L;

	/*public LayerEditWindow(Layer currentLayer, LevelEditor parent) {
		super(parent, "Edit Layer", Dialog.ModalityType.DOCUMENT_MODAL);
		this.add(LayerEditWindow.generateLayerEditPanel(currentLayer));
		this.pack();
	}*/

	public static void generateLayerEditDialog(final Layer l, final LayerPanel p) {
		// Color Layer
		final LevelEditor parent = p.getEditor();
		if (l instanceof ColorLayer) {
			final JColorChooser colorSelect = new JColorChooser();
			colorSelect.setColor(((ColorLayer)l).getColor());
			JColorChooser.createDialog(parent, "Edit Layer Color", true, colorSelect, 
					new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							ColorLayer newLayer = (ColorLayer) ((ColorLayer)l).clone();
							newLayer.setColor(colorSelect.getColor());
							parent.change(ChangeType.LAYER_CHANGE, l, newLayer);
						}
			}, null).setVisible(true);
		}
		// Uh... something else
		else {
			generateDefaultDialog(l, parent);
		}
		
	}
	private static void generateDefaultDialog(Layer l, LevelEditor parent) {
		JDialog layerEdit = new JDialog(parent, "Edit Layer: " + l.getClass().getSimpleName(),
				Dialog.ModalityType.DOCUMENT_MODAL);
		JPanel j = new JPanel();
		j.setPreferredSize(new Dimension(640, 480));
		j.add(new JLabel(l.toString()));
		layerEdit.add(j);
		layerEdit.pack();
		layerEdit.setVisible(true);
	}
}
