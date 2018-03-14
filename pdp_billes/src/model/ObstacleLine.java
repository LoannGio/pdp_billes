package model;

import java.awt.Point;

public class ObstacleLine {
	public static double COR = 0.5; // Coefficient of Restitution
	private Point _depart;
	private Point _arrivee;

	public ObstacleLine(Point depart, Point arrivee) {
		_depart = depart;
		_arrivee = arrivee;
	}

	public Boolean contains(Point p) {
		double distDepartPoint = p.distance(_depart);
		double distPointArrivee = _arrivee.distance(p);
		double distDepartArrivee = _arrivee.distance(_depart);
		if (distDepartArrivee == distDepartPoint + distPointArrivee)
			return true;
		return false;
	}

	public Boolean isNearPoint(Point p) {
		double distDepartPoint = p.distance(_depart);
		double distPointArrivee = _arrivee.distance(p);
		double distDepartArrivee = _arrivee.distance(_depart);
		if (Math.abs(distDepartArrivee - (distDepartPoint + distPointArrivee)) <= 0.1)
			return true;
		return false;
	}

	public Point get_depart() {
		return _depart;
	}

	public void set_depart(Point _depart) {
		this._depart = _depart;
	}

	public Point get_arrivee() {
		return _arrivee;
	}

	public void set_arrivee(Point _arrivee) {
		this._arrivee = _arrivee;
	}

	public void setAll(Point depart, Point arrivee) {
		_depart = depart;
		_arrivee = arrivee;
	}
}
