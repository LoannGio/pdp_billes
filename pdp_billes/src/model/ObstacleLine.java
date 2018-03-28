package model;

import java.awt.Point;

public class ObstacleLine {
	private double _COR; // Coefficient of Restitution
	private Point _depart;
	private Point _arrivee;

	public ObstacleLine(Point depart, Point arrivee, double COR) {
		_depart = depart;
		_arrivee = arrivee;
		_COR = COR;
	}

	/*
	 * Savoir si un point C appartient a une droite AB. Utilise l inegalite
	 * triangulaire. si AC + CB = AB, alors C appartient a AB
	 */
	public Boolean contains(Point p) {
		double distDepartPoint = p.distance(_depart);
		double distPointArrivee = _arrivee.distance(p);
		double distDepartArrivee = _arrivee.distance(_depart);
		if (distDepartArrivee == distDepartPoint + distPointArrivee)
			return true;
		return false;
	}

	/*
	 * Savoir si un point est pres d une droite. Moins stricte que la fonction
	 * contains(). Cette fonction est utilisee pour detecter si le point donne
	 * lors d un click de souris est pres de cet obstacle. En utilisant la
	 * fonction contains, il faudrait cliquer exactement sur un point de la
	 * droite ce qui, au vu de la finesse de nos obstacle, serait complique.
	 */
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

	public void setPositions(Point depart, Point arrivee) {
		_depart = depart;
		_arrivee = arrivee;
	}

	public double getCOR() {
		return _COR;
	}

	public void setCOR(double cOR) {
		_COR = cOR;
	}

}
