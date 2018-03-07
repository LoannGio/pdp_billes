package view;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

public class RightClickChooser {
	private static IRightClickPopUpMenu _rightClick;

	public static IRightClickPopUpMenu createRightClickPopUp(Ball b, Controller c, DrawingPanel dp) {
		_rightClick = new RightClickPopUpBall(b, c, dp);
		return _rightClick;
	}

	public static IRightClickPopUpMenu createRightClickPopUp(ObstacleLine o, Controller c, DrawingPanel dp) {
		_rightClick = new RightClickPopUpLine(o, c, dp);
		return _rightClick;
	}
}
