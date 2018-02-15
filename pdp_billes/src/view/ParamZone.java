package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;

public class ParamZone extends JPanel {

	private Controller _controller;
	int _paramZoneWidth, _paramZoneHeight, _maxCreationZoneWidth, _maxCreationZoneHeight;

	private JPanel _conteneur = new JPanel();
	private JPanel _conteneurLongueur = new JPanel();
	private JPanel _conteneurLargeur = new JPanel();
	private JPanel _conteneurInclinaison = new JPanel();
	private JPanel _conteneurThickness = new JPanel();
	private JPanel _conteneurRadius = new JPanel();
	private JPanel _conteneurMass = new JPanel();

	private JLabel _labelLongueur = new JLabel("Longueur   ");
	private JLabel _labelLargeur = new JLabel("Largeur      ");
	private JLabel _labelInclinaison = new JLabel("Inclinaison  ");
	private JLabel _labelThickness = new JLabel("Epaisseur lignes ");
	private JLabel _labelRadius = new JLabel("Rayon des balles ");
	private JLabel _labelMass = new JLabel("Masse des balles ");

	private JTextField _txtMass;
	private JTextField _txtLongueur;
	private JTextField _txtLargeur;
	private JTextField _txtInclinaison;
	private JTextField _txtThickness;
	private JTextField _txtRadius;
	private JButton _changeButton = new JButton("Changer");
	private JButton _runButton = new JButton("Lancer");
	private JButton _resetButton = new JButton("Reinitialiser");
	private DrawingPanel _dp;

	public ParamZone(Dimension frameSize, Controller c, DrawingPanel creationZone) {
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

				if (checkInt(_txtThickness.getText()))
					_controller.set_defaultLineThickness(Integer.parseInt(_txtThickness.getText()));

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
		_conteneurThickness.setLayout(new BoxLayout(_conteneurThickness, BoxLayout.LINE_AXIS));
		_conteneurRadius.setLayout(new BoxLayout(_conteneurRadius, BoxLayout.LINE_AXIS));
		_conteneurMass.setLayout(new BoxLayout(_conteneurMass, BoxLayout.LINE_AXIS));

		_conteneurLongueur.add(_labelLongueur);
		_conteneurLongueur.add(_txtLongueur);
		_conteneurLargeur.add(_labelLargeur);
		_conteneurLargeur.add(_txtLargeur);
		_conteneurInclinaison.add(_labelInclinaison);
		_conteneurInclinaison.add(_txtInclinaison);
		_conteneurThickness.add(_labelThickness);
		_conteneurThickness.add(_txtThickness);
		_conteneurRadius.add(_labelRadius);
		_conteneurRadius.add(_txtRadius);
		_conteneurMass.add(_labelMass);
		_conteneurMass.add(_txtMass);

		_conteneur.add(_conteneurLongueur);
		_conteneur.add(_conteneurLargeur);
		_conteneur.add(_conteneurInclinaison);
		_conteneur.add(_conteneurThickness);
		_conteneur.add(_conteneurRadius);
		_conteneur.add(_conteneurMass);
		_conteneur.add(_changeButton);
		_conteneur.add(_runButton);
		_conteneur.add(_resetButton);
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
		_txtLongueur = new JTextField(Integer.toString(panelWidth));
		_txtLargeur = new JTextField(Integer.toString(panelHeight));
		_txtInclinaison = new JTextField(Double.toString(_controller.get_defaultInclinaison()));
		_txtThickness = new JTextField(Integer.toString(_controller.get_defaultLineThickness()));
		_txtRadius = new JTextField(Integer.toString(_controller.get_defaultBallRadius()));
		_txtMass = new JTextField(Double.toString(_controller.get_defaultBallMass()));
		return panelWidth;
	}

}
