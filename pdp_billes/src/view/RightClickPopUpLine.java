package view;

import controller.Controller;
import model.ObstacleLine;

@SuppressWarnings("serial")
public class RightClickPopUpLine extends ARightClickPopUp {
	private ObstacleLine _line;

	public RightClickPopUpLine(ObstacleLine ol, Controller c, DrawingPanel dp) {
		super(c, dp);
		_line = ol;
		initialize();
	}

	@Override
	public void parameter() {
		new ParamLine(_line, _controller, _drawingPan);

	}

	@Override
	public void remove() {
		_controller.removeLine(_line);
		_drawingPan.repaintBufferedImage(_controller.get_lines(), _controller.get_balls());
		_drawingPan.repaint();
	}
}
