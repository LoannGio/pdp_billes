package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;

@SuppressWarnings("serial")
public class ParamPanel extends JPanel {

	private Controller _controller;
	int _paramZoneWidth, _paramZoneHeight, _maxCreationZoneWidth, _maxCreationZoneHeight;

	private JPanel _container = new JPanel();
	private JPanel _widthContainer = new JPanel();
	private JPanel _heightContainer = new JPanel();
	private JPanel _radiusContainer = new JPanel();
	private JPanel _massContainer = new JPanel();
	private JPanel _inclinationContainer = new JPanel();
	private JPanel _CORContainer = new JPanel();
	private JPanel _scaleContainer = new JPanel();

	private JLabel _widthLabel = new JLabel("Longueur   ");
	private JLabel _heightLabel = new JLabel("Largeur      ");
	private JLabel _radiusLabel = new JLabel("Rayon des balles ");
	private JLabel _massLabel = new JLabel("Masse des balles ");
	private JLabel _inclinationLabel = new JLabel("Inclinaison ");
	private JLabel _CORLabel = new JLabel("Coef. restitution d'obstacles ");
	private JLabel _scaleLabel = new JLabel("Précision   ");

	private JTextField _massTxt;
	private JTextField _widthTxt;
	private JTextField _heightTxt;
	private JTextField _radiusTxt;
	private JTextField _CORTxt;
	private JTextField _scaleTxt;

	private JButton _changeButton = new JButton("Changer");
	private JButton _runButton = new JButton("Lancer");
	private JButton _stopButton = new JButton("Arreter");
	private JButton _resetButton = new JButton("Reinitialiser");
	private JButton _clearBallsButton = new JButton("Suppr. billes");
	private JButton _clearLinesButton = new JButton("Suppr. obstacles");
	private JButton _exportButton = new JButton("Exporter");
	private JButton _importButton = new JButton("Importer");

	private JSlider _inclinationSlider = new JSlider(0, 90, 45);

	private DrawingPanel _dp;

	public ParamPanel(Dimension frameSize, DrawingPanel creationZone) {
		_controller = Controller.getInstance();
		_dp = creationZone;
		initialize(frameSize, creationZone);
	}

	private void initialize(Dimension frameSize, DrawingPanel creationZone) {
		int panelWidth = initializeComponents(frameSize, creationZone);
		setBounds(panelWidth + 20, 10, _paramZoneWidth, _paramZoneHeight);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		initializeContainers();

		/**
		 * Buttons have an effect on drawing panel, so we need to know it in the
		 * listnener's management. That's why it is passed as parameter
		 */
		addListneners(creationZone);
	}

	private void addListneners(DrawingPanel creationZone) {
		_changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					/*
					 * If entered dimensions of panel are correct, proceed to
					 * needed updates
					 */
					if (checkInt(_widthTxt.getText()) && checkInt(_heightTxt.getText())) {
						int newCreationZoneWidth = Integer.parseInt(_widthTxt.getText());
						if (newCreationZoneWidth > _maxCreationZoneWidth) {
							newCreationZoneWidth = _maxCreationZoneWidth;
							_widthTxt.setText(Integer.toString(newCreationZoneWidth));
						}

						int newCreationZoneHeight = Integer.parseInt(_heightTxt.getText());
						if (newCreationZoneHeight > _maxCreationZoneHeight) {
							newCreationZoneHeight = _maxCreationZoneHeight;
							_heightTxt.setText(Integer.toString(newCreationZoneHeight));
						}

						_controller.setDimensionsPlan(creationZone, newCreationZoneWidth, newCreationZoneHeight);
					}

					/*
					 * For the following tests, we check if the given value is
					 * correct and, if so, we update the corresponding variable
					 */
					if (checkInt(_radiusTxt.getText()))
						_controller.set_defaultBallRadius(Integer.parseInt(_radiusTxt.getText()));

					if (checkDouble(_massTxt.getText()))
						_controller.set_defaultBallMass(Double.parseDouble(_massTxt.getText()));

					if (checkDouble(_CORTxt.getText()))
						_controller.set_defaultCOR(Double.parseDouble(_CORTxt.getText()));

					if (checkDouble(_scaleTxt.getText()))
						_controller.set_defaultScale(Double.parseDouble(_scaleTxt.getText()));
				}
			}
		});

		_runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					_runButton.setBackground(Color.green);
					_stopButton.setBackground(null);
					_controller.runSimulation(_dp);
				}
			}
		});

		_stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_controller.isRunningApp()) {
					_runButton.setBackground(null);
					_stopButton.setBackground(Color.red);
					_controller.stopSimulation();
				}

			}
		});

		_resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					_controller.clearCircuit();
					creationZone.clearBufferedImage();
					creationZone.repaint();
				}
			}
		});

		_clearBallsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					_controller.clearBalls();
					creationZone.repaintBufferedImage(_controller.get_lines(), _controller.get_balls());
					creationZone.repaint();
				}
			}
		});

		_clearLinesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					_controller.clearLines();
					creationZone.repaintBufferedImage(_controller.get_lines(), _controller.get_balls());
					creationZone.repaint();
				}
			}
		});

		_importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					/* Opening file selection window */
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

					chooser.setFileFilter(filter);

					int retValue = chooser.showOpenDialog(null);
					if (retValue == JFileChooser.APPROVE_OPTION) {
						/*
						 * Calling import method with the file as parameter
						 */
						_controller.importCircuit(creationZone, chooser.getSelectedFile());
					}

					updateLabels();
				}
			}
		});

		_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					/* Opening file selection window */
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

					chooser.setFileFilter(filter);

					int retValue = chooser.showSaveDialog(null);
					/*
					 * If a valid file is selected, call export method this file
					 * as parameter
					 */
					if (retValue == JFileChooser.APPROVE_OPTION) {
						File f = null;
						if (!chooser.getSelectedFile().getName().endsWith(".pdp"))
							f = new File(chooser.getSelectedFile().getAbsolutePath() + ".pdp");
						else
							f = chooser.getSelectedFile();
						_controller.exportCircuit(f);

					}
				}
			}
		});

		_inclinationSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				_controller.set_defaultInclinaison(_inclinationSlider.getValue());
			}

		});
	}

	/*
	 * Takes a string entry. Using a regelur expression, returns either the
	 * string contains a positive integer or not
	 */
	private Boolean checkInt(String s) {
		return s.matches("[0-9]+");
	}

	/*
	 * Takes a string entry. Using a regelur expression, returns either the
	 * string contains a positive double or not
	 */
	private Boolean checkDouble(String s) {
		Boolean isDouble = false;
		if (checkInt(s))
			isDouble = true;

		if (s.matches("[0-9]+.[0-9]+"))
			isDouble = true;

		return isDouble;
	}

	private void initializeContainers() {

		_container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
		_widthContainer.setLayout(new BoxLayout(_widthContainer, BoxLayout.LINE_AXIS));
		_heightContainer.setLayout(new BoxLayout(_heightContainer, BoxLayout.LINE_AXIS));
		_radiusContainer.setLayout(new BoxLayout(_radiusContainer, BoxLayout.LINE_AXIS));
		_massContainer.setLayout(new BoxLayout(_massContainer, BoxLayout.LINE_AXIS));
		_inclinationContainer.setLayout(new BoxLayout(_inclinationContainer, BoxLayout.LINE_AXIS));
		_CORContainer.setLayout(new BoxLayout(_CORContainer, BoxLayout.LINE_AXIS));
		_scaleContainer.setLayout(new BoxLayout(_scaleContainer, BoxLayout.LINE_AXIS));

		_widthContainer.add(_widthLabel);
		_widthContainer.add(_widthTxt);
		_heightContainer.add(_heightLabel);
		_heightContainer.add(_heightTxt);
		_radiusContainer.add(_radiusLabel);
		_radiusContainer.add(_radiusTxt);
		_massContainer.add(_massLabel);
		_massContainer.add(_massTxt);
		_CORContainer.add(_CORLabel);
		_CORContainer.add(_CORTxt);
		_scaleContainer.add(_scaleLabel);
		_scaleContainer.add(_scaleTxt);

		_container.add(_widthContainer);
		_container.add(_heightContainer);
		_container.add(_CORContainer);
		_container.add(_radiusContainer);
		_container.add(_massContainer);
		_container.add(_scaleContainer);

		_inclinationContainer.add(_inclinationLabel);
		_inclinationSlider.setMajorTickSpacing(10);
		_inclinationSlider.setMinorTickSpacing(5);
		_inclinationSlider.setPaintLabels(true);
		_inclinationSlider.setPaintTicks(true);
		_inclinationContainer.add(_inclinationSlider);
		_container.add(_inclinationContainer);

		_container.add(_changeButton);
		_container.add(_runButton);
		_container.add(_stopButton);
		_container.add(_resetButton);
		_container.add(_clearBallsButton);
		_container.add(_clearLinesButton);
		_container.add(_importButton);
		_container.add(_exportButton);

		add(_container);
	}

	private int initializeComponents(Dimension frameSize, DrawingPanel creationZone) {
		_maxCreationZoneWidth = creationZone.getWidth();
		_maxCreationZoneHeight = creationZone.getHeight();
		int panelWidth = creationZone.getWidth();
		int panelHeight = creationZone.getHeight();
		double widthProportion = 0.17;
		double heightProportion = 0.92;
		_paramZoneWidth = (int) Math.round(widthProportion * frameSize.width);
		_paramZoneHeight = (int) Math.round(heightProportion * frameSize.height);
		initializeLabels(panelWidth, panelHeight);
		return panelWidth;
	}

	/*
	 * Initialize TextFields content with the corresponding actual value in the
	 * model
	 */
	private void initializeLabels(int panelWidth, int panelHeight) {
		_widthTxt = new JTextField(Integer.toString(panelWidth));
		_heightTxt = new JTextField(Integer.toString(panelHeight));
		_radiusTxt = new JTextField(Integer.toString(_controller.get_defaultBallRadius()));
		_massTxt = new JTextField(Double.toString(_controller.get_defaultBallMass()));
		_CORTxt = new JTextField(Double.toString(_controller.get_defaultCOR()));
		_scaleTxt = new JTextField(Double.toString(_controller.getdefaultScale()));
	}

	/*
	 * This method is called when a circuit is imported. It's purpose is to
	 * update textfields values to the new corresponding circuit values
	 */
	private void updateLabels() {
		Dimension creationZoneDim = _controller.getDimensionsPlan();
		_widthTxt.setText(Integer.toString(creationZoneDim.width));
		_heightTxt.setText(Integer.toString(creationZoneDim.height));
		_radiusTxt.setText(Integer.toString(_controller.get_defaultBallRadius()));
		_massTxt.setText(Double.toString(_controller.get_defaultBallMass()));
		_inclinationSlider.setValue((int) _controller.get_defaultInclinaison());
		_CORTxt.setText(Double.toString(_controller.get_defaultCOR()));
		_scaleTxt.setText(Double.toString(_controller.getdefaultScale()));
	}

}
