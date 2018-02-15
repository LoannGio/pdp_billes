package model;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class ObstacleLine {
	private Point _depart;
	private Point _arrivee;
	private int _thickness;
	private ArrayList<Point> _points;

	public ObstacleLine(Point depart, Point arrivee, int thickness) {
		_depart = depart;
		_arrivee = arrivee;
		_thickness = thickness;
		calcPoints();
	}
	
	private void calcPoints() {
		_points = new ArrayList<Point>();
		double xMin = Math.min(_depart.getX(), _arrivee.getX()) - _thickness / 2;
		double xMax = Math.max(_depart.getX(), _arrivee.getX()) + _thickness / 2;
		double yMin = Math.min(_depart.getY(), _arrivee.getY()) - _thickness / 2;

		double x = xMin;
		double y = yMin;
		Point plusHaut = new Point();

		// Detection du point le plus haut du rectangle
		while (!contains(plusHaut)) {
			if (x == xMax) {
				x = xMin;
				y++;
			}
			Point p = new Point((int) x, (int) y);
			if (contains(p)) {
				plusHaut = p;
			} else {
				x++;
			}
		}
		_points.add(plusHaut);

		double xCurr = plusHaut.getX() + 1;
		double yCurr = plusHaut.getY();
		boolean lineFinished = false;
		boolean allFinished = false;

		// Detection de la premiere ligne du rectagne
		while (!lineFinished) {
			Point p = new Point((int) xCurr, (int) yCurr);
			if (contains(p)) {
				_points.add(p);
				xCurr++;
			} else {
				xCurr = plusHaut.getX();
				yCurr++;
				lineFinished = true;
			}
		}

		Point pLeft = null;
		Point pRight = null;

		// Tant que le rectangle n'a pas fini d'etre detecte
		while (!allFinished) {

			// On cree deux points "iterateurs", l'un allant vers la gauche,
			// l'autre a droite
			pLeft = new Point((int) xCurr, (int) yCurr);
			pRight = new Point((int) xCurr + 1, (int) yCurr);

			// Tant que les deux ne sont pas dans le rectangle, on continue
			while (!contains(pLeft) && !contains(pRight) && (pLeft.getX() >= xMin || pRight.getX() <= xMax)) {
				pLeft.setLocation(pLeft.getX() - 1, yCurr);
				pRight.setLocation(pRight.getX() + 1, yCurr);
			}

			// Si les deux ne sont jamais rentres dans le rectangle, alors c'est
			// fini
			if (pLeft.getX() < xMin && pRight.getX() > xMax) {
				allFinished = true;
			}

			if (!allFinished) {
				boolean modified = false;
				// Si le gauche est rentre, on prend les points a gauche jusqu'a
				// ce qu'on sorte et on descend d'une ligne
				if (pLeft.getX() >= xMin && contains(pLeft)) {
					xCurr = pLeft.getX();
					yCurr = pLeft.getY() + 1;
					modified = true;
					_points.add(pLeft);
					pLeft = new Point((int) pLeft.getX() - 1, (int) pLeft.getY());
					while (contains(pLeft)) {
						_points.add(pLeft);
						pLeft = new Point((int) pLeft.getX() - 1, (int) pLeft.getY());
					}
				}

				// Si le droit est rentre, on prend les points a droite jusqu'a
				// ce qu'on sorte et on descend d'une ligne
				if (pRight.getX() <= xMax && contains(pRight)) {
					if (!modified) {
						xCurr = pRight.getX();
						yCurr = pRight.getY() + 1;
					}
					_points.add(pRight);
					pRight = new Point((int) pRight.getX() + 1, (int) pRight.getY());
					while (contains(pRight)) {
						_points.add(pRight);
						pRight = new Point((int) pRight.getX() + 1, (int) pRight.getY());
					}
				}
			}
		}
	}

	public Boolean contains(Point p) {
		Shape s = new Line2D.Double(_depart.getX(), _depart.getY(), _arrivee.getX(), _arrivee.getY());
		BasicStroke bs = new BasicStroke(_thickness);
		s = bs.createStrokedShape(s);

		if (s.contains(p))
			return true;
		return false;
	}

	public Point get_depart() {
		return _depart;
	}

	public void set_depart(Point _depart) {
		this._depart = _depart;
		calcPoints();
	}

	public Point get_arrivee() {
		return _arrivee;
	}

	public void set_arrivee(Point _arrivee) {
		this._arrivee = _arrivee;
		calcPoints();
	}

	public void set_thickness(int t) {
		_thickness = t;
		calcPoints();
	}
	
	public int get_thickness() {
		return _thickness;
	}
	
	public ArrayList<Point> get_points() {
		return _points;
	}

	public void setAll(Point depart, Point arrivee, int thickness) {
		_depart = depart;
		_arrivee = arrivee;
		_thickness = thickness;
		calcPoints();
	}
}
