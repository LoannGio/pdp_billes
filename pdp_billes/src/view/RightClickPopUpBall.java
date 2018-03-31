package view;

import controller.Controller;
import model.Ball;

@SuppressWarnings("serial")
public class RightClickPopUpBall extends ARightClickPopUp {
	private Ball _ball;
	

	public RightClickPopUpBall(Ball b, Controller c, DrawingPanel dp) {
		super(c, dp);
		_ball = b;
		initialize();
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
