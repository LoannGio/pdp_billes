package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import model.Ball;

@SuppressWarnings("serial")
public class ParamBall extends AParamObject {
	private Ball _ball;

	private JPanel _radiusConteneur = new JPanel();
	private JLabel _radiusLabel = new JLabel("Rayon  ");
	private JTextField _radiusText;

	private JPanel _massConteneur = new JPanel();
	private JLabel _massLabel = new JLabel("Masse  ");
	private JTextField _massText;

	private JPanel _centreConteneur = new JPanel();
	private JLabel _centreLabel = new JLabel("Centre  ");
	private JLabel _centreXLabel = new JLabel("X ");
	private JLabel _centreYLabel = new JLabel("Y ");
	private JTextField _centreXText;
	private JTextField _centreYText;

	public ParamBall(Ball b, Controller c, DrawingPanel dp) {
		super(c, dp);
		_ball = b;

		initialize();
	}

	public ParamBall(Controller c, DrawingPanel dp) {
		super(c, dp);
	}

	@Override
	protected void initialize() {
		super.initialize();

		this.setTitle("param balle");

		initializeComponents();
		initializeContainers();

		addListneners();

		this.setVisible(true);
	}

	private void initializeComponents() {
		/*
		 * Initialise le contenu des zones de texte a la valeur actuelle du
		 * champ qui leur correspond
		 */
		_radiusText = new JTextField(Integer.toString(_ball.get_radius()));
		_massText = new JTextField(Double.toString(_ball.get_mass()));
		_centreXText = new JTextField(Integer.toString((int) _ball.get_x()));
		_centreYText = new JTextField(Integer.toString((int) _ball.get_y()));
	}

	private void addListneners() {
		_buttonChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String radius = _radiusText.getText();
				String mass = _massText.getText();
				String centreX = _centreXText.getText();
				String centreY = _centreYText.getText();
				int iradius, icentreX, icentreY;
				double imass;

				/*
				 * Si au moins un champ n est pas valide, on annule la
				 * modification
				 */
				if (checkInt(radius) && checkDouble(mass) && checkInt(centreX) && checkInt(centreY)) {
					iradius = Integer.parseInt(radius);
					imass = Double.parseDouble(mass);
					icentreX = Integer.parseInt(centreX);
					icentreY = Integer.parseInt(centreY);

					/*
					 * Si la modification reussit, on actualise le panneau de
					 * dessin
					 */
					if (_controller.updateBall(_ball, iradius, imass, icentreX, icentreY)) {
						_drawingPan.repaint();
					}
				}
				closeFrame();
			}
		});
	}

	private void initializeContainers() {
		_conteneur.setLayout(new BoxLayout(_conteneur, BoxLayout.PAGE_AXIS));
		_radiusConteneur.setLayout(new BoxLayout(_radiusConteneur, BoxLayout.LINE_AXIS));
		_massConteneur.setLayout(new BoxLayout(_massConteneur, BoxLayout.LINE_AXIS));
		_buttonConteneur.setLayout(new BoxLayout(_buttonConteneur, BoxLayout.LINE_AXIS));
		_centreConteneur.setLayout(new BoxLayout(_centreConteneur, BoxLayout.LINE_AXIS));

		_radiusConteneur.add(_radiusLabel);
		_radiusConteneur.add(_radiusText);
		_massConteneur.add(_massLabel);
		_massConteneur.add(_massText);
		_buttonConteneur.add(_buttonChange);

		_centreConteneur.add(_centreLabel);
		_centreConteneur.add(_centreXLabel);
		_centreConteneur.add(_centreXText);
		_centreConteneur.add(_centreYLabel);
		_centreConteneur.add(_centreYText);

		_conteneur.add(_centreConteneur);
		_conteneur.add(_radiusConteneur);
		_conteneur.add(_massConteneur);
		_conteneur.add(_buttonConteneur);

		_buttonConteneur.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_radiusConteneur.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_conteneur.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

		this.add(_conteneur);
	}
}
