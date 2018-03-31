package model;

import java.awt.Point;

public class ObstacleLine {
	private double _COR; // Coefficient of Restitution
	private Point _begin;
	private Point _end;

	public ObstacleLine(Point begin, Point end, double COR) {
		_begin = begin;
		_end = end;
		_COR = COR;
	}

	/**
	 * Returns True if Point is on Obstacle. Else, returns False.
	 * 
	 * Tests if point C is on straight line AB. Uses triangle inequality.
	 * If AC + CB = AB, then C is on AB.
	 */
	public Boolean contains(Point p) {
		double distFromBeginToPoint = p.distance(_begin);
		double distFromPointToEnd = _end.distance(p);
		double distFromBeginToEnd = _end.distance(_begin);
		if (distFromBeginToEnd == distFromBeginToPoint + distFromPointToEnd)
			return true;
		return false;
	}

	/** 
	 * Returns True if Point is near Obstacle. Else, returns False.
	 * 
	 * This function is used to click more easily on Obstacle in DrawingPanel.
	 * If right click event was checked with contains() method, then the user
	 * would have to click perfectly on Obstacle, and it can be difficult to do so.
	 */
	public Boolean isNearPoint(Point p) {
		double distFromBeginToPoint = p.distance(_begin);
		double distFromPointToEnd = _end.distance(p);
		double distFromBeginToEnd = _end.distance(_begin);
		if (Math.abs(distFromBeginToEnd - (distFromBeginToPoint + distFromPointToEnd)) <= 0.1)
			return true;
		return false;
	}

	public Point get_begin() {
		return _begin;
	}

	public void set_begin(Point begin) {
		this._begin = begin;
	}

	public Point get_end() {
		return _end;
	}

	public void set_end(Point end) {
		this._end = end;
	}

	public void setPositions(Point begin, Point end) {
		_begin = begin;
		_end = end;
	}

	public double getCOR() {
		return _COR;
	}

	public void setCOR(double COR) {
		_COR = COR;
	}

}
