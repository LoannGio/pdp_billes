package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import controller.Controller;
import model.Ball;

@SuppressWarnings("serial")
public class RightClickPopUpBall extends ARightClickPopUp {
	private Ball _ball;
	private JMenuItem _suppr;
	private JMenuItem _param;

	public RightClickPopUpBall(Ball b, Controller c, DrawingPanel dp) {
		super(c, dp);
		_ball = b;
		initialize();
	}

	@Override
	public void initialize() {
		_param = new JMenuItem("Parametres");
		_suppr = new JMenuItem("Supprimer");
		add(_param);
		add(_suppr);

		_param.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				parameter();
			}
		});

		_suppr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				remove();
			}
		});

	}

	@Override
	public void parameter() {
		new ParamBall(_ball, _controller, _drawingPan);

	}

	@Override
	public void remove() {
		_controller.removeBall(_ball);
		_drawingPan.repaintBufferedImage(_controller.get_lines(), _controller.get_balls());
		_drawingPan.repaint();
	}
}
