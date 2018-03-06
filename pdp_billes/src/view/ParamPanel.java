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
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;

public class ParamPanel extends JPanel {

	private Controller _controller;
	int _paramZoneWidth, _paramZoneHeight, _maxCreationZoneWidth, _maxCreationZoneHeight;

	private JPanel _conteneur = new JPanel();
	private JPanel _conteneurLongueur = new JPanel();
	private JPanel _conteneurLargeur = new JPanel();
	private JPanel _conteneurInclinaison = new JPanel();
	private JPanel _conteneurRadius = new JPanel();
	private JPanel _conteneurMass = new JPanel();

	private JLabel _labelLongueur = new JLabel("Longueur   ");
	private JLabel _labelLargeur = new JLabel("Largeur      ");
	private JLabel _labelInclinaison = new JLabel("Inclinaison  ");
	private JLabel _labelRadius = new JLabel("Rayon des balles ");
	private JLabel _labelMass = new JLabel("Masse des balles ");

	private JTextField _txtMass;
	private JTextField _txtLongueur;
	private JTextField _txtLargeur;
	private JTextField _txtInclinaison;
	private JTextField _txtRadius;
	private JButton _changeButton = new JButton("Changer");
	private JButton _runButton = new JButton("Lancer");
	private JButton _resetButton = new JButton("Reinitialiser");
	private JButton _exportButton = new JButton("Exporter");
	private JButton _importButton = new JButton("Importer");

	private DrawingPanel _dp;

	public ParamPanel(Dimension frameSize, Controller c, DrawingPanel creationZone) {
		_controller = c;
		_dp = creationZone;
		initialize(frameSize, creationZone);
	}

	private void initialize(Dimension frameSize, DrawingPanel creationZone) {
		int panelWidth = initializeComponents(frameSize, creationZone);
		setBounds(panelWidth + 20, 10, _paramZoneWidth, _paramZoneHeight);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		initializeContainers();

		// Les boutons agissent sur la zone de dessin, on a donc besoin de la
		// connaitre dans la gestion des listneners, c'est pour ca qu'elle est
		// passee en parametre
		addListneners(creationZone);
	}

	private void addListneners(DrawingPanel creationZone) {
		_changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkInt(_txtLongueur.getText()) && checkInt(_txtLargeur.getText())) {
					int newCreationZoneWidth = Integer.parseInt(_txtLongueur.getText());
					if (newCreationZoneWidth > _maxCreationZoneWidth) {
						newCreationZoneWidth = _maxCreationZoneWidth;
						_txtLongueur.setText(Integer.toString(newCreationZoneWidth));
					}

					int newCreationZoneHeight = Integer.parseInt(_txtLargeur.getText());
					if (newCreationZoneHeight > _maxCreationZoneHeight) {
						newCreationZoneHeight = _maxCreationZoneHeight;
						_txtLargeur.setText(Integer.toString(newCreationZoneHeight));
					}
					creationZone.deleteObjectsOutOfBounds(creationZone.getX(),
							creationZone.getX() + newCreationZoneWidth, creationZone.getY(),
							creationZone.getY() + newCreationZoneHeight);
					_controller.setDimensionsPlan(creationZone, newCreationZoneWidth, newCreationZoneHeight);
				}
				if (checkInt(_txtRadius.getText()))
					_controller.set_defaultBallRadius(Integer.parseInt(_txtRadius.getText()));

				if (checkDouble(_txtMass.getText()))
					_controller.set_defaultBallMass(Double.parseDouble(_txtMass.getText()));

				if (checkDouble(_txtInclinaison.getText()))
					_controller.set_defaultInclinaison(Double.parseDouble(_txtInclinaison.getText()));

			}
		});

		_runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_controller.run(_dp);
			}
		});

		_resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_controller.clearCircuit();
				creationZone.repaint();
			}
		});

		_importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

				chooser.setFileFilter(filter);

				int retValue = chooser.showOpenDialog(null);
				if (retValue == JFileChooser.APPROVE_OPTION) {
					_controller.importerCircuit(creationZone, chooser.getSelectedFile());
				}

				Dimension creationZoneDim = _controller.getDimensionsPlan();
				updateLabels(creationZoneDim.width, creationZoneDim.height);
			}
		});

		_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

				chooser.setFileFilter(filter);

				int retValue = chooser.showSaveDialog(null);
				if (retValue == JFileChooser.APPROVE_OPTION) {
					File f = null;
					if (!chooser.getSelectedFile().getName().endsWith(".pdp"))
						f = new File(chooser.getSelectedFile().getAbsolutePath() + ".pdp");
					else
						f = chooser.getSelectedFile();
					_controller.exporterCircuit(f);

				}
			}
		});
	}

	private Boolean checkInt(String s) {
		return s.matches("[0-9]+");
	}

	private Boolean checkDouble(String s) {
		Boolean isDouble = false;
		if (checkInt(s))
			isDouble = true;

		if (s.matches("[0-9]+.[0-9]+"))
			isDouble = true;

		return isDouble;
	}

	private void initializeContainers() {
		_conteneur.setLayout(new BoxLayout(_conteneur, BoxLayout.PAGE_AXIS));
		_conteneurLongueur.setLayout(new BoxLayout(_conteneurLongueur, BoxLayout.LINE_AXIS));
		_conteneurLargeur.setLayout(new BoxLayout(_conteneurLargeur, BoxLayout.LINE_AXIS));
		_conteneurInclinaison.setLayout(new BoxLayout(_conteneurInclinaison, BoxLayout.LINE_AXIS));
		_conteneurRadius.setLayout(new BoxLayout(_conteneurRadius, BoxLayout.LINE_AXIS));
		_conteneurMass.setLayout(new BoxLayout(_conteneurMass, BoxLayout.LINE_AXIS));

		_conteneurLongueur.add(_labelLongueur);
		_conteneurLongueur.add(_txtLongueur);
		_conteneurLargeur.add(_labelLargeur);
		_conteneurLargeur.add(_txtLargeur);
		_conteneurInclinaison.add(_labelInclinaison);
		_conteneurInclinaison.add(_txtInclinaison);
		_conteneurRadius.add(_labelRadius);
		_conteneurRadius.add(_txtRadius);
		_conteneurMass.add(_labelMass);
		_conteneurMass.add(_txtMass);

		_conteneur.add(_conteneurLongueur);
		_conteneur.add(_conteneurLargeur);
		_conteneur.add(_conteneurInclinaison);
		_conteneur.add(_conteneurRadius);
		_conteneur.add(_conteneurMass);
		_conteneur.add(_changeButton);
		_conteneur.add(_runButton);
		_conteneur.add(_resetButton);
		_conteneur.add(_importButton);
		_conteneur.add(_exportButton);

		add(_conteneur);
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

	private void initializeLabels(int panelWidth, int panelHeight) {
		_txtLongueur = new JTextField(Integer.toString(panelWidth));
		_txtLargeur = new JTextField(Integer.toString(panelHeight));
		_txtInclinaison = new JTextField(Double.toString(_controller.get_defaultInclinaison()));
		_txtRadius = new JTextField(Integer.toString(_controller.get_defaultBallRadius()));
		_txtMass = new JTextField(Double.toString(_controller.get_defaultBallMass()));
	}

	private void updateLabels(int panelWidth, int panelHeight) {
		_txtLongueur.setText(Integer.toString(panelWidth));
		_txtLargeur.setText(Integer.toString(panelHeight));
		_txtInclinaison.setText(Double.toString(_controller.get_defaultInclinaison()));
		_txtThickness.setText(Integer.toString(_controller.get_defaultLineThickness()));
		_txtRadius.setText(Integer.toString(_controller.get_defaultBallRadius()));
		_txtMass.setText(Double.toString(_controller.get_defaultBallMass()));
	}

}
