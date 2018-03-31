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

@SuppressWarnings("serial")
public class ParamLine extends AParamObject {
	private ObstacleLine _line;

	private JPanel _beginContainer = new JPanel();
	private JLabel _beginLabel = new JLabel("Depart  ");
	private JLabel _beginXLabel = new JLabel("X ");
	private JLabel _beginYLabel = new JLabel("Y ");

	private JTextField _beginXText;
	private JTextField _beginYText;

	private JPanel _endContainer = new JPanel();
	private JLabel _endLabel = new JLabel("Arrivee  ");
	private JLabel _endXLabel = new JLabel("X ");
	private JLabel _endYLabel = new JLabel("Y ");
	private JTextField _endXText;
	private JTextField _endYText;

	private JPanel _CORContainer = new JPanel();
	private JLabel _CORLabel = new JLabel("Coef. de restitution ");
	private JTextField _CORText;

	public ParamLine(ObstacleLine ol, Controller c, DrawingPanel dp) {
		super(c, dp);
		_line = ol;

		initialize();

		this.setVisible(true);
	}

	public ParamLine(Controller c, DrawingPanel dp) {
		super(c, dp);
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
		_changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String beginX = _beginXText.getText();
				String beginY = _beginYText.getText();
				String endX = _endXText.getText();
				String endY = _endYText.getText();
				String COR = _CORText.getText();

				int ibeginX, ibeginY, iendX, iendY;
				double dCOR;

				/*
				 * If at least a field isn't valid, we cancel the whole update
				 */
				if (checkInt(beginX) && checkInt(beginY) && checkInt(endX) && checkInt(endY) && checkDouble(COR)) {
					ibeginX = Integer.parseInt(beginX);
					ibeginY = Integer.parseInt(beginY);
					iendX = Integer.parseInt(endX);
					iendY = Integer.parseInt(endY);
					dCOR = Double.parseDouble(COR);

					/*
					 * If updates succeeds, refresh the drawing panel
					 */
					if (_controller.updateLine(_line, ibeginX, ibeginY, iendX, iendY, dCOR)) {
						_drawingPan.repaintBufferedImage(_controller.get_lines(), _controller.get_balls());
						_drawingPan.repaint();
					}
				}
				closeFrame();
			}
		});

	}

	private void initializeContainers() {
		_container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
		_buttonContainer.setLayout(new BoxLayout(_buttonContainer, BoxLayout.LINE_AXIS));
		_beginContainer.setLayout(new BoxLayout(_beginContainer, BoxLayout.LINE_AXIS));
		_endContainer.setLayout(new BoxLayout(_endContainer, BoxLayout.LINE_AXIS));
		_CORContainer.setLayout(new BoxLayout(_CORContainer, BoxLayout.LINE_AXIS));

		_buttonContainer.add(_changeButton);

		_beginContainer.add(_beginLabel);
		_beginContainer.add(_beginXLabel);
		_beginContainer.add(_beginXText);
		_beginContainer.add(_beginYLabel);
		_beginContainer.add(_beginYText);

		_endContainer.add(_endLabel);
		_endContainer.add(_endXLabel);
		_endContainer.add(_endXText);
		_endContainer.add(_endYLabel);
		_endContainer.add(_endYText);

		_CORContainer.add(_CORLabel);
		_CORContainer.add(_CORText);

		_container.add(_beginContainer);
		_container.add(_endContainer);
		_container.add(_CORContainer);
		_container.add(_buttonContainer);

		_buttonContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_beginContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_endContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		_CORContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		_container.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

		this.add(_container);
	}

	private void initializeComponents() {
		/*
		 * Initialize TextFields content with the corresponding actual value in
		 * the model
		 */
		_beginXText = new JTextField(Integer.toString((int) _line.get_begin().getX()));
		_beginYText = new JTextField(Integer.toString((int) _line.get_begin().getY()));
		_endXText = new JTextField(Integer.toString((int) _line.get_end().getX()));
		_endYText = new JTextField(Integer.toString((int) _line.get_end().getY()));
		_CORText = new JTextField(Double.toString((double) _line.getCOR()));

	}
}
