package view;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import controller.Controller;

public abstract class AParamObject extends JDialog {
	protected Controller _controller;
	protected DrawingPanel _drawingPan;
	protected JPanel _conteneur = new JPanel();
	protected JButton _buttonChange = new JButton("Modifier");
	protected JPanel _buttonConteneur = new JPanel();

	public AParamObject(Controller c, DrawingPanel dp) {
		_controller = c;
		_drawingPan = dp;
	}

	protected void initialize() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode dm = ge.getScreenDevices()[ge.getScreenDevices().length - 1].getDisplayMode();
		Dimension screenSize = new Dimension(dm.getWidth(), dm.getHeight());
		int width = 350;
		int height = 250;
		this.setBounds(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2, width, height);
		this.setModalityType(ModalityType.APPLICATION_MODAL);

	}

	protected Boolean checkInt(String s) {
		return s.matches("[0-9]+");
	}

	protected Boolean checkDouble(String s) {
		Boolean isDouble = false;
		if (checkInt(s))
			isDouble = true;

		if (s.matches("[0-9]+.[0-9]+"))
			isDouble = true;

		return isDouble;
	}

	protected void closeFrame() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

}
