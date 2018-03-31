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

	private JPanel _radiusContainer = new JPanel();
	private JLabel _radiusLabel = new JLabel("Rayon  ");
	private JTextField _radiusText;

	private JPanel _massContainer = new JPanel();
	private JLabel _massLabel = new JLabel("Masse  ");
	private JTextField _massText;

	private JPanel _centerContainer = new JPanel();
	private JLabel _centerLabel = new JLabel("Centre  ");
	private JLabel _centerXLabel = new JLabel("X ");
	private JLabel _centerYLabel = new JLabel("Y ");
	private JTextField _centerXText;
	private JTextField _centerYText;

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

	/*
	 * Initialize TextFields content with the corresponding actual value in the
	 * model
	 */
	private void initializeComponents() {
		_radiusText = new JTextField(Integer.toString(_ball.get_radius()));
		_massText = new JTextField(Double.toString(_ball.get_mass()));
		_centerXText = new JTextField(Integer.toString((int) _ball.get_x()));
		_centerYText = new JTextField(Integer.toString((int) _ball.get_y()));
	}

	private void addListneners() {
		_changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String radius = _radiusText.getText();
				String mass = _massText.getText();
				String centerX = _centerXText.getText();
				String centerY = _centerYText.getText();
				int iradius, icenterX, icenterY;
				double imass;

				/*
				 * If at least a field isn't valid, we cancel the whole update
				 */
				if (checkInt(radius) && checkDouble(mass) && checkInt(centerX) && checkInt(centerY)) {
					iradius = Integer.parseInt(radius);
					imass = Double.parseDouble(mass);
					icenterX = Integer.parseInt(centerX);
					icenterY = Integer.parseInt(centerY);

					/*
					 * If updates succeeds, refresh the drawing panel
					 */
					if (_controller.updateBall(_ball, iradius, imass, icenterX, icenterY)) {
						_drawingPan.repaint();
					}
				}
				closeFrame();
			}
		});
	}

	private void initializeContainers() {
		_container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
		_radiusContainer.setLayout(new BoxLayout(_radiusContainer, BoxLayout.LINE_AXIS));
		_massContainer.setLayout(new BoxLayout(_massContainer, BoxLayout.LINE_AXIS));
		_buttonContainer.setLayout(new BoxLayout(_buttonContainer, BoxLayout.LINE_AXIS));
		_centerContainer.setLayout(new BoxLayout(_centerContainer, BoxLayout.LINE_AXIS));

		_radiusContainer.add(_radiusLabel);
		_radiusContainer.add(_radiusText);
		_massContainer.add(_massLabel);
		_massContainer.add(_massText);
		_buttonContainer.add(_changeButton);

		_centerContainer.add(_centerLabel);
		_centerContainer.add(_centerXLabel);
		_centerContainer.add(_centerXText);
		_centerContainer.add(_centerYLabel);
		_centerContainer.add(_centerYText);

		_container.add(_centerContainer);
		_container.add(_radiusContainer);
		_container.add(_massContainer);
		_container.add(_buttonContainer);

		_buttonContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_radiusContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_container.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

		this.add(_container);
	}
}
