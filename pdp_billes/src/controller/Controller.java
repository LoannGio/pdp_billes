package controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import model.Vector;
import view.DrawingPanel;

public class Controller {
	private Circuit _circuit;
	private static Controller instance = new Controller();
	private boolean isRunning;
	private PhysicalEngine pe;

	private Controller() {
		_circuit = new Circuit(500, 500);
		isRunning = false;
		pe = null;
	}

	public static Controller getInstance() {
		return instance;
	}

	public void addBall(Ball b) {
		_circuit.addBall(b);
	}

	public void addBall(double x, double y) {
		_circuit.addBall(new Ball(x, y, _circuit.get_defaultBallRadius(), _circuit.get_defaultBallMass()));
	}

	public void addLine(Point depart, Point arrivee) {
		_circuit.addLine(new ObstacleLine(depart, arrivee));
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

	public ObstacleLine checkIfPointIsNearLine(Point p) {
		ObstacleLine lineContains = null;

		for (ObstacleLine o : _circuit.get_lines()) {
			if (o.isNearPoint(p))
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

	public void runSimulation(DrawingPanel creationZone) {
		isRunning = true;
		// PhysicalEngine pe = new PhysicalEngine(creationZone, _circuit);
		if (pe == null)
			pe = new PhysicalEngine(_circuit);
		pe.run(creationZone);
	}

	public void stopSimulation() {
		isRunning = false;
		pe.stop();
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

	public void set_defaultBallRadius(int radius) {
		_circuit.set_defaultBallRadius(radius);
	}

	public double get_defaultBallMass() {
		return _circuit.get_defaultBallMass();
	}

	public void set_defaultBallMass(double mass) {
		_circuit.set_defaultBallMass(mass);
	}

	public Vector get_gravityAcceleration() {
		return _circuit.get_gravityAcceleration();
	}

	public boolean checkIfBallIsOnExistingObject(Ball b) {
		if (checkIfBallIsOnExistingBall(b))
			return true;
		if (checkIfBallIsOnExistingLine(b))
			return true;

		return false;
	}

	public Boolean checkIfBallIsOnExistingLine(Ball b) {
		for (ObstacleLine o : _circuit.get_lines()) {
			if (checkCollisionBallObstacle(b, o))
				return true;
		}
		return false;
	}

	public Boolean checkIfBallIsOnExistingBall(Ball b) {
		for (Ball b2 : _circuit.get_balls()) {
			if (checkCollisionBallBall(b, b2) && !b.equals(b2))
				return true;
		}
		return false;
	}

	public boolean checkIfLineIsOnExistingBall(ObstacleLine o) {
		for (Ball b : _circuit.get_balls()) {
			if (checkCollisionBallObstacle(b, o))
				return true;
		}
		return false;
	}

	public Boolean updateBall(Ball b, int new_radius, double new_mass, int new_centreX, int new_centreY) {
		int oldRadius = b.get_radius();
		double oldMass = b.get_mass();
		double oldX = b.get_x();
		double oldY = b.get_y();
		b.setAll(new_centreX, new_centreY, new_radius, new_mass);
		if (!checkIfBallIsOnExistingObject(b) && !(new_centreX > _circuit.get_width())
				&& !(new_centreY > _circuit.get_height())) {
			return true;
		}

		b.setAll(oldX, oldY, oldRadius, oldMass);
		return false;
	}

	public Boolean updateLine(ObstacleLine line, int new_departX, int new_departY, int new_arriveeX, int new_arriveeY) {
		Point oldDepart = line.get_depart();
		Point oldArrivee = line.get_arrivee();
		Point newDepart = new Point(new_departX, new_departY);
		Point newArrivee = new Point(new_arriveeX, new_arriveeY);

		line.setAll(newDepart, newArrivee);

		if (!checkIfLineIsOnExistingBall(line) && !(new_departX > _circuit.get_width())
				&& !(new_departY > _circuit.get_height()) && !(new_arriveeX > _circuit.get_width())
				&& !(new_arriveeY > _circuit.get_height()))
			return true;

		line.setAll(oldDepart, oldArrivee);
		return false;
	}

	public void setDimensionsPlan(DrawingPanel creationZone, int newCreationZoneWidth, int newCreationZoneHeight) {
		_circuit.set_width(newCreationZoneWidth);
		_circuit.set_height(newCreationZoneHeight);
		creationZone.setBounds(10, 10, newCreationZoneWidth, newCreationZoneHeight);
	}

	public Dimension getDimensionsPlan() {
		return new Dimension(_circuit.get_width(), _circuit.get_height());
	}

	public void importerCircuit(DrawingPanel creationZone, File f) {
		_circuit.importer(f);
		setDimensionsPlan(creationZone, _circuit.get_width(), _circuit.get_height());
		creationZone.repaint();
	}

	public void exporterCircuit(File f) {
		_circuit.exporter(f);
	}

	public boolean checkCollisionBallBall(Ball ball1, Ball ball2) {
		Point2D.Double centre1 = new Point2D.Double(ball1.get_location().getX(), ball1.get_location().getY());
		Point2D.Double centre2 = new Point2D.Double(ball2.get_location().getX(), ball2.get_location().getY());
		double dist = distance(centre1, centre2);
		if (dist > ball1.get_radius() + ball2.get_radius())
			return false;
		else
			return true;
	}

	public boolean checkCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		Vector u = new Vector(obstacle.get_depart().getX() - obstacle.get_arrivee().getX(),
				obstacle.get_depart().getY() - obstacle.get_arrivee().getY());
		Vector AC = new Vector(ball.get_x() - obstacle.get_arrivee().getX(),
				ball.get_y() - obstacle.get_arrivee().getY());
		double numerateur = u.getX() * AC.getY() - u.getY() * AC.getX(); // norme
																			// du
																			// vecteur
																			// v
		if (numerateur < 0)
			numerateur = -numerateur; // valeur absolue ; si c'est négatif,
										// on prend l'opposé.
		double denominateur = Math.sqrt(Math.pow(u.getX(), 2) + Math.pow(u.getY(), 2));
		double CI = numerateur / denominateur;
		if (CI < ball.get_radius()) {
			return collisionSegment(ball, obstacle);
		} else
			return false;
	}

	public boolean collisionSegment(Ball ball, ObstacleLine obstacle) {

		Point2D.Double A = new Point2D.Double(obstacle.get_depart().getX(), obstacle.get_depart().getY());
		Point2D.Double B = new Point2D.Double(obstacle.get_arrivee().getX(), obstacle.get_arrivee().getY());
		Point2D.Double C = new Point2D.Double(ball.get_x(), ball.get_y());

		Vector AB = new Vector(B.x - A.x, B.y - A.y);
		Vector AC = new Vector(C.x - A.x, C.y - A.y);
		Vector BC = new Vector(C.x - B.x, C.y - B.y);
		Vector BA = new Vector(-AB.getX(), -AB.getY());

		float pscal1 = (float) Vector.dotProduct(AB, AC); // produit scalaire
		float pscal2 = (float) Vector.dotProduct(BA, BC); // produit scalaire

		if (pscal1 >= 0 && pscal2 >= 0)
			return true; // I entre A et B, ok.

		// derniere possibilite, A ou B dans le cercle
		if (collisionPointCercle(A, C, ball))
			return true;
		if (collisionPointCercle(B, C, ball))
			return true;
		return false;
	}

	public boolean collisionPointCercle(Point2D.Double a, Point2D.Double b, Ball ball) {
		if (distance(a, b) <= ball.get_radius())
			return true;
		return false;
	}

	public double distance(Point2D.Double a, Point2D.Double b) {
		return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
	}

	public boolean isRunningApp() {
		return isRunning;
	}
}
