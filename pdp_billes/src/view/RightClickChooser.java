package view;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

public class RightClickChooser {

	public static IRightClickPopUpMenu createRightClickPopUp(Ball b, Controller c, DrawingPanel dp) {
		return new RightClickPopUpBall(b, c, dp);
	}

	public static IRightClickPopUpMenu createRightClickPopUp(ObstacleLine o, Controller c, DrawingPanel dp) {
		return new RightClickPopUpLine(o, c, dp);
	}
}
