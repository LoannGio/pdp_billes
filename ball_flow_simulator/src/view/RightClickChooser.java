package view;

import java.awt.Point;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

public class RightClickChooser {
	/*
	 * This class implements the Visitor class corresponding to the design
	 * pattern of same name
	 */

	public static IRightClickPopUpMenu createRightCLickPopUp(Point p, DrawingPanel dp) {
		Controller c = Controller.getInstance();
		Ball b;
		ObstacleLine o;
		if ((b = c.checkIfPointIsInBall(p)) != null) {
			return new RightClickPopUpBall(b, c, dp);
		} else if ((o = c.checkIfPointIsNearLine(p)) != null) {
			return new RightClickPopUpLine(o, c, dp);
		}
		return null;
	}
}
