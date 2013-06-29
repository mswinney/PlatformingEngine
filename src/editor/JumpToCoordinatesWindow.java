package editor;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import editor.MapIconConstructWindow.CloseOnOkayListener;

public class JumpToCoordinatesWindow extends JDialog implements PropertyChangeListener {
	private static final long serialVersionUID = 8423016853234204973L;
	private LevelEditor parent;
	private JFormattedTextField workingXField;
	private JFormattedTextField workingYField;
	private int workingX = 0;
	private int workingY = 0;
	
	public JumpToCoordinatesWindow(LevelEditor edit) {
		super(edit, "Jump to Coordinates", Dialog.ModalityType.DOCUMENT_MODAL);
		parent = edit;
		this.setLayout(new FlowLayout());
		
		this.add(new JLabel("X"));
		workingXField = new JFormattedTextField(NumberFormat.getNumberInstance());
        workingXField.setValue(parent.getWorkingX());
        workingXField.setColumns(5);
        workingXField.addPropertyChangeListener("value", this);
        this.add(workingXField);
        this.add(new JLabel("Y"));
        workingYField = new JFormattedTextField(NumberFormat.getNumberInstance());
        workingYField.setValue(parent.getWorkingX());
        workingYField.setColumns(5);
        workingYField.addPropertyChangeListener("value", this);
        this.add(workingYField);
		
		JButton okay = new JButton("OK");
		okay.addActionListener(new CloseOnOkayListener(this));
		this.add(okay);
		
		this.setSize(200, 90);
		this.setResizable(false);
	}
	
	 public void propertyChange(PropertyChangeEvent e) {
	        Object source = e.getSource();
	        if (source == workingXField) {
	            workingX = ((Number)workingXField.getValue()).intValue();
	        }
	        else if (source == workingYField) {
	            workingY = ((Number)workingYField.getValue()).intValue();
	        }
	    }
	
	public void dispose() {
		parent.setWorkingCoords(workingX, workingY);
		super.dispose();
	}
}
