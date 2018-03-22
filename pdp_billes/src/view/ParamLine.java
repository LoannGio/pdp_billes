package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import model.ObstacleLine;

public class ParamLine extends AParamObject {
	private ObstacleLine _line;

	private JPanel _departConteneur = new JPanel();
	private JLabel _departLabel = new JLabel("Depart  ");
	private JLabel _departXLabel = new JLabel("X ");
	private JLabel _departYLabel = new JLabel("Y ");
	private JTextField _departXText;
	private JTextField _departYText;

	private JPanel _arriveeConteneur = new JPanel();
	private JLabel _arriveeLabel = new JLabel("Arrivee  ");
	private JLabel _arriveeXLabel = new JLabel("X ");
	private JLabel _arriveeYLabel = new JLabel("Y ");
	private JTextField _arriveeXText;
	private JTextField _arriveeYText;

	public ParamLine(ObstacleLine ol, Controller c, DrawingPanel dp) {
		super(c, dp);
		_line = ol;

		initialize();

		this.setVisible(true);
	}

	@Override
	protected void initialize() {
		super.initialize();

		this.setTitle("param line");

		initializeComponents();
		initializeContainers();

		addListneners();

		this.repaint();
	}

	private void addListneners() {
		_buttonChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String departX = _departXText.getText();
				String departY = _departYText.getText();
				String arriveeX = _arriveeXText.getText();
				String arriveeY = _arriveeYText.getText();

				int idepartX, idepartY, iarriveeX, iarriveeY;

				// Si il y a une erreur de typo, on ne fait pas l'operation
				if (checkInt(departX) && checkInt(departY) && checkInt(arriveeX) && checkInt(arriveeY)) {
					idepartX = Integer.parseInt(departX);
					idepartY = Integer.parseInt(departY);
					iarriveeX = Integer.parseInt(arriveeX);
					iarriveeY = Integer.parseInt(arriveeY);

					if (_controller.updateLine(_line, idepartX, idepartY, iarriveeX, iarriveeY)) {
						_drawingPan.repaintBufferedImageObstacles(_controller.get_lines());
						_drawingPan.repaint();
					}
				}
				closeFrame();
			}
		});

	}

	private void initializeContainers() {
		_conteneur.setLayout(new BoxLayout(_conteneur, BoxLayout.PAGE_AXIS));
		_buttonConteneur.setLayout(new BoxLayout(_buttonConteneur, BoxLayout.LINE_AXIS));
		_departConteneur.setLayout(new BoxLayout(_departConteneur, BoxLayout.LINE_AXIS));
		_arriveeConteneur.setLayout(new BoxLayout(_arriveeConteneur, BoxLayout.LINE_AXIS));

		_buttonConteneur.add(_buttonChange);

		_departConteneur.add(_departLabel);
		_departConteneur.add(_departXLabel);
		_departConteneur.add(_departXText);
		_departConteneur.add(_departYLabel);
		_departConteneur.add(_departYText);

		_arriveeConteneur.add(_arriveeLabel);
		_arriveeConteneur.add(_arriveeXLabel);
		_arriveeConteneur.add(_arriveeXText);
		_arriveeConteneur.add(_arriveeYLabel);
		_arriveeConteneur.add(_arriveeYText);

		_conteneur.add(_departConteneur);
		_conteneur.add(_arriveeConteneur);
		_conteneur.add(_buttonConteneur);

		_buttonConteneur.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_departConteneur.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_arriveeConteneur.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		_conteneur.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

		this.add(_conteneur);
	}

	private void initializeComponents() {
		_departXText = new JTextField(Integer.toString((int) _line.get_depart().getX()));
		_departYText = new JTextField(Integer.toString((int) _line.get_depart().getY()));
		_arriveeXText = new JTextField(Integer.toString((int) _line.get_arrivee().getX()));
		_arriveeYText = new JTextField(Integer.toString((int) _line.get_arrivee().getY()));
	}
}
