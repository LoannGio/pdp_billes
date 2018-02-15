package controller;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import model.AnimationTimer;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import view.DrawingPanel;

public class Controller {
	private Circuit _circuit;

	public Controller(Circuit c) {
		_circuit = c;
	}

	public void addBall(Ball b) {
		_circuit.addBall(b);
	}

	public void addBall(double x, double y) {
		_circuit.addBall(new Ball(x, y, _circuit.get_defaultBallRadius(), _circuit.get_defaultBallMass(),
				_circuit.get_inclinaison()));
	}

	public void addLine(Point depart, Point arrivee) {
		_circuit.addLine(new ObstacleLine(depart, arrivee, _circuit.get_defaultLineThickness()));
	}

	public void addLine(ObstacleLine o) {
		_circuit.addLine(o);
	}

	public Boolean removeBall(Ball b) {
		return _circuit.removeBall(b);
	}

	public Boolean removeLine(ObstacleLine o) {
		return _circuit.removeLine(o);
	}

	public double get_defaultInclinaison() {
		return _circuit.get_inclinaison();
	}

	public void set_defaultInclinaison(double incl) {
		_circuit.set_inclinaison(incl);
	}

	public void clearCircuit() {
		_circuit.clearAll();
	}

	public Ball checkIfPointIsInBall(Point p) {
		Ball ballContains = null;
		for (Ball b : _circuit.get_balls()) {
			if (b.contains(p))
				ballContains = b;
		}
		return ballContains;
	}

	public ObstacleLine checkIfPointIsInLine(Point p) {
		ObstacleLine lineContains = null;

		for (ObstacleLine o : _circuit.get_lines()) {
			if (o.contains(p))
				lineContains = o;
		}
		return lineContains;
	}

	public void removeLinesOutOfBounds(int xMin, int xMax, int yMin, int yMax) {
		Point depart, arrivee;
		Iterator<ObstacleLine> iterObstacleLine = get_lines().iterator();
		while (iterObstacleLine.hasNext()) {
			ObstacleLine o = iterObstacleLine.next();
			depart = o.get_depart();
			arrivee = o.get_arrivee();
			if (depart.getX() > xMax || depart.getX() < xMin || depart.getY() > yMax || depart.getY() < yMin
					|| arrivee.getX() > xMax || arrivee.getX() < xMin || arrivee.getY() > yMax
					|| arrivee.getY() < yMin) {
				iterObstacleLine.remove();
			}
		}
		iterObstacleLine = null;
	}

	public void removeBallsOutOfBounds(int xMin, int xMax, int yMin, int yMax) {
		int x, y, rad;
		Iterator<Ball> iterBall = get_balls().iterator();
		while (iterBall.hasNext()) {
			Ball b = iterBall.next();
			x = (int) b.get_x();
			y = (int) b.get_y();
			rad = b.get_radius();
			if (x + rad > xMax || x < xMin || y + rad > yMax || y < yMin) {
				iterBall.remove();
			}
		}
		iterBall = null;
	}

	public ArrayList<Ball> get_balls() {
		return _circuit.get_balls();
	}

	public ArrayList<ObstacleLine> get_lines() {
		return _circuit.get_lines();
	}

	public int get_defaultBallRadius() {
		return _circuit.get_defaultBallRadius();
	}

	public int get_defaultLineThickness() {
		return _circuit.get_defaultLineThickness();
	}

	public void set_defaultBallRadius(int radius) {
		_circuit.set_defaultBallRadius(radius);
	}

	public void set_defaultLineThickness(int thickness) {
		_circuit.set_defaultLineThickness(thickness);
	}

	public double get_defaultBallMass() {
		return _circuit.get_defaultBallMass();
	}

	public void set_defaultBallMass(double mass) {
		_circuit.set_defaultBallMass(mass);
	}

	public boolean checkIfBallIsOnExistingObject(Ball b) {
		if (checkIfBallIsOnExistingBall(b))
			return true;
		if (checkIfBallIsOnExistingLine(b))
			return true;

		return false;
	}

	public Boolean checkIfBallIsOnExistingLine(Ball b) {
		for (ObstacleLine o : _circuit.get_lines())
			for (Point p : o.get_points())
				if (b.get_points().contains(p))
					return true;
		return false;
	}

	public Boolean checkIfBallIsOnExistingBall(Ball b) {
		for (Ball b2 : _circuit.get_balls())
			if (b2 != b)
				for (Point p : b.get_points())
					if (b2.get_points().contains(p))
						return true;
		return false;
	}

	public boolean checkIfLineIsOnExistingBall(ObstacleLine o) {

		for (Ball b : _circuit.get_balls())
			for (Point p : b.get_points())
				if (o.get_points().contains(p))
					return true;
		return false;
	}

	public void run(DrawingPanel dp) {
		AnimationTimer timer = new AnimationTimer(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Ball ball : _circuit.get_balls()) {
					ball.step();
					for (ObstacleLine obstacle : _circuit.get_lines()) {
						if (collisionObstacle(ball, obstacle)) {
							ball.resolveCollisionObstacle(obstacle);
						}

					}
					for (Ball ball2 : _circuit.get_balls()) {
						if (ball2.get_x() != ball.get_x() && ball2.get_y() != ball.get_y())
							if (collisionBall(ball, ball2)) {
								ball.resolveCollisionBall(ball2);
							}
					}

				}
				dp.repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		});
		timer.start();
	}

	public boolean collisionObstacle(Ball ball, ObstacleLine obstacle) {
		return collisionDroite(ball, obstacle);
	}

	public boolean collisionDroite(Ball ball, ObstacleLine obstacle) {
		Point2D.Double u = new Point2D.Double();
		u.x = (obstacle.get_depart().getX() - obstacle.get_arrivee().getX());
		u.y = (obstacle.get_depart().getY() - obstacle.get_arrivee().getY());
		Point2D.Double AC = new Point2D.Double();
		AC.x = (int) (ball.get_x() - obstacle.get_arrivee().getX());
		AC.y = (int) (ball.get_y() - obstacle.get_arrivee().getY());
		double numerateur = u.x * AC.y - u.y * AC.x; // norme du vecteur v
		if (numerateur < 0)
			numerateur = -numerateur; // valeur absolue ; si c'est nÃ©gatif,
										// on prend l'opposÃ©.
		double denominateur = Math.sqrt(u.x * u.x + u.y * u.y); // norme de u
		double CI = numerateur / denominateur;
		double xmin = Math.min(obstacle.get_depart().getX(), obstacle.get_arrivee().getX());
		double xmax = Math.max(obstacle.get_depart().getX(), obstacle.get_arrivee().getX());
		if (CI < ball.get_radius()) {
			return collisionSegment(ball, obstacle);
		} else
			return false;
	}

	public boolean collisionSegment(Ball ball, ObstacleLine obstacle) {
		Point2D.Double A = new Point2D.Double();
		Point2D.Double B = new Point2D.Double();
		Point2D.Double C = new Point2D.Double();
		A.x = obstacle.get_depart().getX();
		A.y = obstacle.get_depart().getY();
		B.x = obstacle.get_arrivee().getX();
		B.y = obstacle.get_arrivee().getY();
		C.x = ball.get_x();
		C.y = ball.get_y();

		Point2D.Double AB = new Point2D.Double();
		Point2D.Double ACC = new Point2D.Double();
		Point2D.Double BC = new Point2D.Double();
		AB.x = B.x - A.x;
		AB.y = B.y - A.y;
		ACC.x = C.x - A.x;
		ACC.y = C.y - A.y;
		BC.x = C.x - B.x;
		BC.y = C.y - B.y;
		float pscal1 = (float) (AB.x * ACC.x + AB.y * ACC.y); // produit
																// scalaire
		float pscal2 = (float) ((-AB.x) * BC.x + (-AB.y) * BC.y); // produit
																	// scalaire
		if (pscal1 >= 0 && pscal2 >= 0)
			return true; // I entre A et B, ok.
		// dernière possibilité, A ou B dans le cercle
		if (collisionPointCerle(A, C, ball))
			return true;
		if (collisionPointCerle(B, C, ball))
			return true;
		return false;
	}

	public boolean collisionPointCerle(Point2D.Double a, Point2D.Double b, Ball ball) {
		if (distance(a, b) <= ball.get_radius())
			return true;
		return false;
	}

	public double distance(Point2D.Double a, Point2D.Double b) {
		return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
	}

	public boolean collisionBall(Ball ball1, Ball ball2) {

		double dist = Math.pow(ball1.get_x() - ball2.get_x(), 2) + Math.pow((ball1.get_y() - ball2.get_y()), 2);
		if (dist > Math.pow(ball1.get_radius() + ball2.get_radius(), 2))
			return false;
		else
			return true;
		/*
		 * double dx = ball.get_x() - this._x; double dy = ball.get_y() -
		 * this._y; double dist = dx * dx + dy * dy; return dist <=
		 * 4*this.get_radius()*ball.get_radius();
		 */
	}

	public Boolean updateBall(Ball b, int new_radius, double new_mass, int new_centreX, int new_centreY) {
		int oldRadius = b.get_radius();
		double oldMass = b.get_mass();
		double oldX = b.get_x();
		double oldY = b.get_y();
		b.setAll(new_centreX, new_centreY, new_radius, new_mass, _circuit.get_inclinaison());

		if (!checkIfBallIsOnExistingObject(b) && !(new_centreX > _circuit.get_width())
				&& !(new_centreY > _circuit.get_height())) {
			return true;
		}

		b.setAll(oldX, oldY, oldRadius, oldMass, _circuit.get_inclinaison());
		return false;
	}

	public Boolean updateLine(ObstacleLine line, int new_thickness, int new_departX, int new_departY, int new_arriveeX,
			int new_arriveeY) {
		int oldThickness = line.get_thickness();
		Point oldDepart = line.get_depart();
		Point oldArrivee = line.get_arrivee();
		Point newDepart = new Point(new_departX, new_departY);
		Point newArrivee = new Point(new_arriveeX, new_arriveeY);

		line.setAll(newDepart, newArrivee, new_thickness);

		if (!checkIfLineIsOnExistingBall(line) && !(new_departX > _circuit.get_width())
				&& !(new_departY > _circuit.get_height()) && !(new_arriveeX > _circuit.get_width())
				&& !(new_arriveeY > _circuit.get_height()))
			return true;

		line.setAll(oldDepart, oldArrivee, oldThickness);
		return false;
	}

	public void setDimensionsPlan(DrawingPanel creationZone, int newCreationZoneWidth, int newCreationZoneHeight) {
		_circuit.set_width(newCreationZoneWidth);
		_circuit.set_height(newCreationZoneHeight);
		creationZone.setBounds(10, 10, newCreationZoneWidth, newCreationZoneHeight);
	}
}
