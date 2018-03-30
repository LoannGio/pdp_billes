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

	private JPanel _conteneur = new JPanel();
	private JPanel _conteneurLongueur = new JPanel();
	private JPanel _conteneurLargeur = new JPanel();
	private JPanel _conteneurRadius = new JPanel();
	private JPanel _conteneurMass = new JPanel();
	private JPanel _conteneurInclinaison = new JPanel();
	private JPanel _conteneurCOR = new JPanel();
	private JPanel _conteneurScale = new JPanel();

	private JLabel _labelLongueur = new JLabel("Longueur   ");
	private JLabel _labelLargeur = new JLabel("Largeur      ");
	private JLabel _labelRadius = new JLabel("Rayon des balles ");
	private JLabel _labelMass = new JLabel("Masse des balles ");
	private JLabel _labelInclinaison = new JLabel("Inclinaison ");
	private JLabel _labelCOR = new JLabel("Coef. restitution d'obstacles ");
	private JLabel _labelScale = new JLabel("Précision   ");

	private JTextField _txtMass;
	private JTextField _txtLongueur;
	private JTextField _txtLargeur;
	private JTextField _txtRadius;
	private JTextField _txtCOR;
	private JTextField _txtScale;

	private JButton _changeButton = new JButton("Changer");
	private JButton _runButton = new JButton("Lancer");
	private JButton _stopButton = new JButton("Arreter");
	private JButton _resetButton = new JButton("Reinitialiser");
	private JButton _clearBallsButton = new JButton("Suppr. billes");
	private JButton _clearLinesButton = new JButton("Suppr. obstacles");
	private JButton _exportButton = new JButton("Exporter");
	private JButton _importButton = new JButton("Importer");

	private JSlider _inclinaisonSlider = new JSlider(0, 90, 45);

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

		/*
		 * Les boutons agissent sur la zone de dessin, on a donc besoin de la
		 * connaitre dans la gestion des listneners, c'est pour ca qu'elle est
		 * passee en parametre
		 */
		addListneners(creationZone);
	}

	private void addListneners(DrawingPanel creationZone) {
		_changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					/*
					 * Si les dimension entrees du panneau sont correctes, faire
					 * les modifications necessaires
					 */
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

						_controller.setDimensionsPlan(creationZone, newCreationZoneWidth, newCreationZoneHeight);
					}

					/*
					 * Pour les tests suivant, on verifie si la valeur
					 * renseignee est correcte et, le cas echeant, on modifie la
					 * variable correspondante
					 */
					if (checkInt(_txtRadius.getText()))
						_controller.set_defaultBallRadius(Integer.parseInt(_txtRadius.getText()));

					if (checkDouble(_txtMass.getText()))
						_controller.set_defaultBallMass(Double.parseDouble(_txtMass.getText()));

					if (checkDouble(_txtCOR.getText()))
						_controller.set_defaultCOR(Double.parseDouble(_txtCOR.getText()));

					if (checkDouble(_txtScale.getText()))
						_controller.set_defaultScale(Double.parseDouble(_txtScale.getText()));
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
					/* On ouvre une fenetre de selection de fichier */
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

					chooser.setFileFilter(filter);

					int retValue = chooser.showOpenDialog(null);
					if (retValue == JFileChooser.APPROVE_OPTION) {
						/*
						 * On lance la fonction d import en lui passant en
						 * parametre le fichier selectionne
						 */
						_controller.importerCircuit(creationZone, chooser.getSelectedFile());
					}

					updateLabels();
				}
			}
		});

		_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!_controller.isRunningApp()) {
					/* On ouvre une fenetre de selection de fichier */
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("PDP files (*.pdp)", "pdp");

					chooser.setFileFilter(filter);

					int retValue = chooser.showSaveDialog(null);
					/*
					 * Si on a selectionne un fichier et que celui ci est
					 * valide, on lance la fonction exporter avec le fichier
					 * selectionne
					 */
					if (retValue == JFileChooser.APPROVE_OPTION) {
						File f = null;
						if (!chooser.getSelectedFile().getName().endsWith(".pdp"))
							f = new File(chooser.getSelectedFile().getAbsolutePath() + ".pdp");
						else
							f = chooser.getSelectedFile();
						_controller.exporterCircuit(f);

					}
				}
			}
		});

		_inclinaisonSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				_controller.set_defaultInclinaison(_inclinaisonSlider.getValue());
			}

		});
	}

	/*
	 * Prend un string en entree. Par une expression reguliere, retourne si oui
	 * ou non ce string est un entier positif ou nul
	 */
	private Boolean checkInt(String s) {
		return s.matches("[0-9]+");
	}

	/*
	 * Prend un string en entree. Par une expression reguliere, retourne si oui
	 * ou non ce string est un double positif ou nul
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

		_conteneur.setLayout(new BoxLayout(_conteneur, BoxLayout.PAGE_AXIS));
		_conteneurLongueur.setLayout(new BoxLayout(_conteneurLongueur, BoxLayout.LINE_AXIS));
		_conteneurLargeur.setLayout(new BoxLayout(_conteneurLargeur, BoxLayout.LINE_AXIS));
		_conteneurRadius.setLayout(new BoxLayout(_conteneurRadius, BoxLayout.LINE_AXIS));
		_conteneurMass.setLayout(new BoxLayout(_conteneurMass, BoxLayout.LINE_AXIS));
		_conteneurInclinaison.setLayout(new BoxLayout(_conteneurInclinaison, BoxLayout.LINE_AXIS));
		_conteneurCOR.setLayout(new BoxLayout(_conteneurCOR, BoxLayout.LINE_AXIS));
		_conteneurScale.setLayout(new BoxLayout(_conteneurScale, BoxLayout.LINE_AXIS));

		_conteneurLongueur.add(_labelLongueur);
		_conteneurLongueur.add(_txtLongueur);
		_conteneurLargeur.add(_labelLargeur);
		_conteneurLargeur.add(_txtLargeur);
		_conteneurRadius.add(_labelRadius);
		_conteneurRadius.add(_txtRadius);
		_conteneurMass.add(_labelMass);
		_conteneurMass.add(_txtMass);
		_conteneurCOR.add(_labelCOR);
		_conteneurCOR.add(_txtCOR);
		_conteneurScale.add(_labelScale);
		_conteneurScale.add(_txtScale);

		_conteneur.add(_conteneurLongueur);
		_conteneur.add(_conteneurLargeur);
		_conteneur.add(_conteneurCOR);
		_conteneur.add(_conteneurRadius);
		_conteneur.add(_conteneurMass);
		_conteneur.add(_conteneurScale);

		_conteneurInclinaison.add(_labelInclinaison);
		_inclinaisonSlider.setMajorTickSpacing(10);
		_inclinaisonSlider.setMinorTickSpacing(5);
		_inclinaisonSlider.setPaintLabels(true);
		_inclinaisonSlider.setPaintTicks(true);
		_conteneurInclinaison.add(_inclinaisonSlider);
		_conteneur.add(_conteneurInclinaison);

		_conteneur.add(_changeButton);
		_conteneur.add(_runButton);
		_conteneur.add(_stopButton);
		_conteneur.add(_resetButton);
		_conteneur.add(_clearBallsButton);
		_conteneur.add(_clearLinesButton);
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

	/*
	 * Initialise le contenu des zones de texte a la valeur actuelle du champ
	 * qui leur correspond
	 */
	private void initializeLabels(int panelWidth, int panelHeight) {
		_txtLongueur = new JTextField(Integer.toString(panelWidth));
		_txtLargeur = new JTextField(Integer.toString(panelHeight));
		_txtRadius = new JTextField(Integer.toString(_controller.get_defaultBallRadius()));
		_txtMass = new JTextField(Double.toString(_controller.get_defaultBallMass()));
		_txtCOR = new JTextField(Double.toString(_controller.get_defaultCOR()));
		_txtScale = new JTextField(Double.toString(_controller.getdefaultScale()));
	}

	/*
	 * Fonction appelee lors d un import de circuit afin d actualiser les
	 * valeurs des champs aux nouvelles valeurs presentes en memoire
	 */
	private void updateLabels() {
		Dimension creationZoneDim = _controller.getDimensionsPlan();
		_txtLongueur.setText(Integer.toString(creationZoneDim.width));
		_txtLargeur.setText(Integer.toString(creationZoneDim.height));
		_txtRadius.setText(Integer.toString(_controller.get_defaultBallRadius()));
		_txtMass.setText(Double.toString(_controller.get_defaultBallMass()));
		_inclinaisonSlider.setValue((int) _controller.get_defaultInclinaison());
		_txtCOR.setText(Double.toString(_controller.get_defaultCOR()));
		_txtScale.setText(Double.toString(_controller.getdefaultScale()));
	}

}
