package editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import editor.LevelEditor.Change.ChangeType;

import leveldata.Block;
import leveldata.layer.Layer;
import leveldata.layer.TileTypeLayer;

public class LayerPanel extends EditorPanel implements ListSelectionListener {
	private static final long serialVersionUID = -1058797019735080913L;
	private JPanel closedPanel;
	private JPanel openPanel;
	private boolean isOpen;
	private JList<Layer> layerList;
	private TileTypeLayer lastTileLayer;
	private Layer lastLayer;
	private JButton depthUp;
	private JButton depthDown;
	private LayerPropertiesButton editButton;
	private Block block;

	protected LayerPanel(final LevelEditor editor) {
		super(editor);
		isOpen = false;
		closedPanel = new JPanel();
		openPanel = new JPanel();
		
		closedPanel.setVisible(!isOpen);
		openPanel.setVisible(isOpen);
		
		// set up open panel
		openPanel.add(new ToggleButton("Close Layers"));
		layerList = new JList<Layer>();
		layerList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.getSelectionModel().addListSelectionListener(this);
		JScrollPane layers = new JScrollPane(layerList);
		layers.setFocusable(false);
		
		openPanel.add(layers);
		
		// first button row: depth up, edit layer, depth down
		JPanel movePanel = new JPanel();
		depthUp = new JButton("^");
		depthUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layer orig = LayerPanel.this.getCurrentLayer();
				Layer l = (Layer) orig.clone();
				l.setDepth(l.getDepth() - 1);
				editor.change(ChangeType.LAYER_CHANGE, orig, l);
			}
		});
		
		editButton = new LayerPropertiesButton();
		
		depthDown = new JButton("v");
		depthDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layer orig = LayerPanel.this.getCurrentLayer();
				Layer l = (Layer) orig.clone();
				l.setDepth(l.getDepth() + 1);
				editor.change(ChangeType.LAYER_CHANGE, orig, l);
			}
		});

		movePanel.add(depthUp);
		movePanel.add(editButton);
		movePanel.add(depthDown);
		openPanel.add(movePanel);
		
		// second button row: add layer, remove layer
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.add(new JButton("+"));
		addRemovePanel.add(new JButton("-"));
		openPanel.add(addRemovePanel);
		
		// set up closed panel
		closedPanel.add(new ToggleButton("Layers"));
		
		openPanel.setPreferredSize(new Dimension(128, 1024));
		closedPanel.setPreferredSize(new Dimension(40, 1024));
		this.add(closedPanel);
		this.add(openPanel);
	}
	
	public void updateLayers(Block block) {
		this.block = block;
		if (block != null)
			this.setLastLayer(block.getCollisionLayer());
		this.updateLayers();
	}
	public void updateLayers() {
		layerList.setPreferredSize(new Dimension(133, layerList.getHeight()));
		if (block == null) {
			layerList.setListData(new Layer[0]);
			return;
		}
		layerList.setListData(block.getLayerArray());
		layerList.setSelectedValue(this.lastLayer, true);
	}
	private void setLastLayer(Layer l) {
		if (l instanceof TileTypeLayer) {
			this.lastTileLayer = (TileTypeLayer)l;
		}
		this.lastLayer = l;
		// enable/disable edit layer button if we moved onto or off of a collision layer
		depthUp.setEnabled(l.hasVariableDepth());
		editButton.setEnabled(l.hasEditableProperties());
		depthDown.setEnabled(l.hasVariableDepth());
	}
	
	public Layer getCurrentLayer() {
		return lastLayer;
	}
	public TileTypeLayer getCurrentTileLayer() {
		return lastTileLayer;
	}
	public void paint(Graphics g) {
		super.paint(g);
		this.updateLayers();
	}
	
	public void updateLayerListSelection(Layer l) {
		this.updateLayers();
		// set selection
		layerList.setSelectedValue(l, true);
		/*for (Layer a : block.getLayerArray()) {
			if (l == a) {
				System.out.println("Found: " + a);
				return;
			}
		}
		System.out.println("Not found");*/
	}
	
	@Override
	protected Dimension getEditorPanelSize() {
		return new Dimension(isOpen ? openPanel.getPreferredSize().width :
			closedPanel.getPreferredSize().width,
				Integer.MAX_VALUE);
	}
	public Dimension getMaximumSize() {
		return new Dimension(this.getEditorPanelSize().width, Integer.MAX_VALUE);
	}
	
	private class ToggleButton extends JButton implements ActionListener {
		private static final long serialVersionUID = -1266011530358064620L;

		private ToggleButton(String title) {
			super();
			this.setText(title);
			this.addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			LayerPanel p = LayerPanel.this;
			isOpen = !isOpen;
			p.closedPanel.setVisible(!isOpen);
			p.openPanel.setVisible(isOpen);
			p.repaint();
		}
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			//System.out.println(layerList.getSelectedValuesList());
			List<Layer> selection = layerList.getSelectedValuesList();
			if (selection.size() != 1) {
				// something has gone terribly wrong (or we're just changing blocks)
				// it's probably nothing to worry about
				// keep walking, citizen
				return;
			}
			this.setLastLayer(selection.get(0));
			this.getEditor().focus();
		}
	}
	
	private class LayerPropertiesButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 3176678147486412006L;
		public LayerPropertiesButton() {
			super("Edit");
			this.addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			LayerPanel p = LayerPanel.this;
			if (!p.getCurrentLayer().hasEditableProperties()) {
				return;
			}
			LayerEditWindow.generateLayerEditDialog(p.getCurrentLayer(), p);
		}
	}
}
