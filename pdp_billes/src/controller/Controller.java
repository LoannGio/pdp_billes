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

/*Pattern singleton : il n y a qu une seule instance de cette classe et fournit un point d acces de type global a cette classe */
public class Controller {
	private Circuit _circuit;
	private static Controller instance = new Controller();
	/* isRunning est un boolean indiquant si une execution est en cours */
	private boolean _isRunning;
	private PhysicalEngine _pe;

	private Controller() {
		_circuit = new Circuit(500, 500);
		_isRunning = false;
		_pe = null;
	}

	public static Controller getInstance() {
		return instance;
	}

	/*
	 * Checks if the Point parameter is inside a ball. If so, returns the ball.
	 * If not, returns null.
	 */
	public Ball checkIfPointIsInBall(Point p) {
		Ball ballContains = null;
		for (Ball b : _circuit.get_balls()) {
			if (b.contains(p))
				ballContains = b;
		}
		return ballContains;
	}

	/*
	 * Checks if the Point parameter is on an obstacle. If so, returns the
	 * obstacle. If not, returns null.
	 */
	public ObstacleLine checkIfPointIsNearLine(Point p) {
		ObstacleLine lineContains = null;

		for (ObstacleLine o : _circuit.get_lines()) {
			if (o.isNearPoint(p))
				lineContains = o;
		}
		return lineContains;
	}

	/*
	 * Remove obstacles that are outside the circuit. Used on circuit
	 * redimensionning.
	 */
	public void removeLinesOutOfBounds(int xMin, int xMax, int yMin, int yMax) {
		Point begin, end;
		Iterator<ObstacleLine> iterObstacleLine = get_lines().iterator();
		while (iterObstacleLine.hasNext()) {
			ObstacleLine o = iterObstacleLine.next();
			begin = o.get_begin();
			end = o.get_end();
			if (begin.getX() > xMax || begin.getX() < xMin || begin.getY() > yMax || begin.getY() < yMin
					|| end.getX() > xMax || end.getX() < xMin || end.getY() > yMax
					|| end.getY() < yMin) {
				iterObstacleLine.remove();
			}
		}
		iterObstacleLine = null;
	}

	/*
	 * Remove balls that are outside the circuit. Used on circuit
	 * redimensionning.
	 */
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
		_isRunning = true;
		if (_pe == null)
			_pe = new PhysicalEngine(_circuit);
		_pe.run(creationZone);
	}

	public void stopSimulation() {
		_isRunning = false;
		_pe.stop();
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

	public Boolean updateBall(Ball b, int new_radius, double new_mass, int new_centerX, int new_centerY) {
		int oldRadius = b.get_radius();
		double oldMass = b.get_mass();
		double oldX = b.get_x();
		double oldY = b.get_y();
		b.setAll(new_centerX, new_centerY, new_radius, new_mass);
		if (!checkIfBallIsOnExistingObject(b) && !(new_centerX > _circuit.get_width())
				&& !(new_centerY > _circuit.get_height())) {
			return true;
		}

		b.setAll(oldX, oldY, oldRadius, oldMass);
		return false;
	}

	public Boolean updateLine(ObstacleLine line, int new_beginX, int new_beginY, int new_endX, int new_endY,
			double newCOR) {
		Point oldBegin = line.get_begin();
		Point oldEnd = line.get_end();
		Point newBegin = new Point(new_beginX, new_beginY);
		Point newEnd = new Point(new_endX, new_endY);

		line.setPositions(newBegin, newEnd);
		line.setCOR(newCOR);

		if (!checkIfLineIsOnExistingBall(line) && !(new_beginX > _circuit.get_width())
				&& !(new_beginY > _circuit.get_height()) && !(new_endX > _circuit.get_width())
				&& !(new_endY > _circuit.get_height())) {
			return true;
		}

		line.setPositions(oldBegin, oldEnd);
		return false;
	}

	public void importCircuit(DrawingPanel creationZone, File f) {
		_circuit.toImport(f);
		setDimensionsPlan(creationZone, _circuit.get_width(), _circuit.get_height());
		creationZone.repaint();
	}

	public void exportCircuit(File f) {
		_circuit.toExport(f);
	}

	public boolean checkCollisionBallBall(Ball ball1, Ball ball2) {
		Point2D.Double center1 = new Point2D.Double(ball1.get_location().getX(), ball1.get_location().getY());
		Point2D.Double center2 = new Point2D.Double(ball2.get_location().getX(), ball2.get_location().getY());
		double dist = distance(center1, center2);
		if (dist > ball1.get_radius() + ball2.get_radius())
			return false;
		else
			return true;
	}

	public boolean checkCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		Vector u = new Vector(obstacle.get_begin().getX() - obstacle.get_end().getX(),
				obstacle.get_begin().getY() - obstacle.get_end().getY());
		Vector AC = new Vector(ball.get_x() - obstacle.get_end().getX(), ball.get_y() - obstacle.get_end().getY());

		/* Vector u norm */
		double numerator = Math.abs(u.getX() * AC.getY() - u.getY() * AC.getX());
		double denominator = Math.sqrt(Math.pow(u.getX(), 2) + Math.pow(u.getY(), 2));
		double CI = numerator / denominator;
		if (CI < ball.get_radius())
			return collisionSegment(ball, obstacle);
		else
			return false;
	}

	private boolean collisionSegment(Ball ball, ObstacleLine obstacle) {

		Point2D.Double A = new Point2D.Double(obstacle.get_begin().getX(), obstacle.get_begin().getY());
		Point2D.Double B = new Point2D.Double(obstacle.get_end().getX(), obstacle.get_end().getY());
		Point2D.Double C = new Point2D.Double(ball.get_x(), ball.get_y());

		Vector AB = new Vector(B.x - A.x, B.y - A.y);
		Vector AC = new Vector(C.x - A.x, C.y - A.y);
		Vector BC = new Vector(C.x - B.x, C.y - B.y);
		Vector BA = new Vector(-AB.getX(), -AB.getY());

		float pscal1 = (float) Vector.dotProduct(AB, AC);
		float pscal2 = (float) Vector.dotProduct(BA, BC);

		if (pscal1 >= 0 && pscal2 >= 0)
			return true;

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

	/*
	 * Modifies the circuit and its drawing panel dimensions. Remove all out of
	 * new bounds objects
	 */
	public void setDimensionsPlan(DrawingPanel creationZone, int newCreationZoneWidth, int newCreationZoneHeight) {
		_circuit.set_width(newCreationZoneWidth);
		_circuit.set_height(newCreationZoneHeight);
		creationZone.deleteObjectsOutOfBounds(creationZone.getX(), creationZone.getX() + newCreationZoneWidth,
				creationZone.getY(), creationZone.getY() + newCreationZoneHeight);
		creationZone.mySetBounds(10, 10, newCreationZoneWidth, newCreationZoneHeight);
		creationZone.repaintBufferedImage(_circuit.get_lines(), _circuit.get_balls());
	}

	public Dimension getDimensionsPlan() {
		return new Dimension(_circuit.get_width(), _circuit.get_height());
	}

	public void addBall(Ball b) {
		_circuit.addBall(b);
	}

	public void addBall(double x, double y) {
		_circuit.addBall(new Ball(x, y, _circuit.get_defaultBallRadius(), _circuit.get_defaultBallMass()));
	}

	public void addLine(Point depart, Point arrivee) {
		_circuit.addLine(new ObstacleLine(depart, arrivee, _circuit.get_defaultCOR()));
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

	public void clearCircuit() {
		_circuit.clearAll();
	}

	public void clearBalls() {
		_circuit.clearBalls();
	}

	public void clearLines() {
		_circuit.clearLines();
	}

	public boolean isRunningApp() {
		return _isRunning;
	}

	public double getdefaultScale() {
		return _circuit.get_scale();
	}

	public void set_defaultScale(double scale) {
		_circuit.set_scale(scale);
	}

	public double get_defaultInclinaison() {
		return _circuit.get_inclination();
	}

	public void set_defaultInclinaison(double incl) {
		_circuit.set_inclination(incl);
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

	public double get_defaultCOR() {
		return _circuit.get_defaultCOR();
	}

	public void set_defaultCOR(double COR) {
		_circuit.set_defaultCOR(COR);
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
}
