package editor;

import java.awt.Color;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SingleOptionPanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 6696970625622701302L;
	private JSpinner value;
	private JRootPane parent;

	public SingleOptionPanel(String name, JRootPane parent, ChangeListener chg) {
		// http://docs.oracle.com/javase/tutorial/uiswing/components/spinner.html
		this.value = new JSpinner(new SpinnerNumberModel(0, 0, 65535, 1));
		this.value.addChangeListener(chg);
		this.value.addChangeListener(this);
		this.parent = parent;

		JFormattedTextField textField = ((JSpinner.DefaultEditor)this.value.getEditor())
				.getTextField();
		textField.setColumns(4);

		this.add(new JLabel(name));
		this.add(this.value);
		this.setBackground(Color.GREEN);
	}
	public void setEditable(boolean b) {
		((JSpinner.DefaultEditor)this.value.getEditor()).getTextField().setEditable(b);
	}
	public void setFocusable(boolean b) {
		((JSpinner.DefaultEditor)this.value.getEditor()).getTextField().setFocusable(b);
	}
	public void setMax(int value) {
		((SpinnerNumberModel)this.value.getModel()).setMaximum(value);
	}
	public void setValue(int value) {
		this.value.setValue(value);
		this.setFocusToParent();
	}
	public void setFocusToParent() {
		if (parent != null) {
			parent.requestFocus();
		}
	}
	public int getValue() {
		return (int) this.value.getValue();
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		this.setFocusToParent();
	}
}
