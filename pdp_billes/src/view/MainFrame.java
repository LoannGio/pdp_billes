package view;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import controller.Controller;

public class MainFrame extends JFrame {
	private Controller _controller;
	private DrawingPanel _panel;
	private ParamPanel _paramZone;

	public MainFrame(Controller c) {
		_controller = c;
		initialize();
	}

	public void initialize() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode dm = ge.getScreenDevices()[ge.getScreenDevices().length - 1].getDisplayMode();
		Dimension screenSize = new Dimension(dm.getWidth(), dm.getHeight());
		this.setTitle("pdp billes");
		this.setBounds(50, 50, screenSize.width, screenSize.height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		initializeComponents(this.getSize());
		this.add(_panel);
		this.add(_paramZone);
		this.setVisible(true);
		this.repaint();
	}

	private void initializeComponents(Dimension frameSize) {
		_panel = new DrawingPanel(frameSize, _controller, this);
		_paramZone = new ParamPanel(frameSize, _controller, _panel);
	}
}
