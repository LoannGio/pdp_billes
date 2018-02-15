package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;

public class ControllerTest {
	Controller c;
	Circuit circuit;

	@Before
	public void setUp() throws Exception {
		circuit = new Circuit(500, 500);
		c = new Controller(circuit);
	}

	@After
	public void tearDown() throws Exception {
		circuit = null;
		c = null;
	}

	@Test
	public void test_checkIfPointIsInBall() {
		Point p = new Point(10, 10);
		Ball b = new Ball(10, 10, 3, 1, 0);
		c.addBall(b);

		// Le point est sur le centre de la bille
		assertEquals(b, c.checkIfPointIsInBall(p));

		// Le point est dans la bille, mais pas sur son centre
		p.setLocation(11, 10);
		assertEquals(b, c.checkIfPointIsInBall(p));

		// Le point n'est pas dans la bille
		p.setLocation(50, 50);
		assertEquals(null, c.checkIfPointIsInBall(p));
	}

	@Test
	public void test_checkIfPointIsInLine() {
		Point p = new Point(10, 10);
		ObstacleLine o = new ObstacleLine(new Point(10, 0), new Point(10, 20), 10);
		c.addLine(o);

		// Le point est pile sur la ligne
		assertEquals(o, c.checkIfPointIsInLine(p));

		// Le point est sur la ligne en tenant compte de l'epaisseur de celle-ci
		p.setLocation(11, 10);
		assertEquals(o, c.checkIfPointIsInLine(p));

		// Le point est en dehors de la ligne
		p.setLocation(50, 50);
		assertEquals(null, c.checkIfPointIsInLine(p));
	}

	@Test
	public void test_removeLinesOutOfBounds() {

	}

	@Test
	public void test_removeBallsOutOfBounds() {

	}

	@Test
	public void test_checkIfBallIsOnExistingLine() {

	}

	@Test
	public void test_checkIfBallIsOnExistingBall() {

	}

	@Test
	public void test_checkIfLineIsOnExistingBall() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 2);
		Ball b = new Ball(10, 10, 2, 1, 0);
		c.addLine(o);

	}

	@Test
	public void test_updateBall() {
		Ball b = new Ball(10, 10, 0, 1, 0);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(10, 10));

		c.addBall(b);
		assertEquals(1, b.get_points().size());
		assertEquals(1, b.get_mass(), 0.001);
		assertEquals(0, b.get_radius());
		assertEquals(true, b.get_points().equals(points));

		points = new ArrayList<Point>();
		points.add(new Point(19, 20));
		points.add(new Point(20, 19));
		points.add(new Point(20, 20));
		points.add(new Point(20, 21));
		points.add(new Point(21, 20));

		// *** Test update valide
		c.updateBall(b, 1, 2, 20, 20);
		testBallPosition(b, points);

		// *** Test update invalide
		// La modification ne peut pas se faire, la bille doit revenir dans
		// l'etat dans lequel elle etait avant l update

		// ** Test en updatant avec des coordonnees hors de l ecran
		c.updateBall(b, 1, 2, circuit.get_width() + 1, 20);
		testBallPosition(b, points);

		c.updateBall(b, 1, 2, 20, circuit.get_height() + 1);
		testBallPosition(b, points);

		// ** Test en updatant sur un autre objet (ball ou line)
		// * Conflit avec une bille
		Ball b2 = new Ball(30, 30, 2, 1, 0);
		c.addBall(b2);
		// Centres identiques
		c.updateBall(b, 1, 2, 30, 30);

		testBallPosition(b, points);

		// Centres differents mais b et b2 se chevauchent
		c.updateBall(b, 1, 2, 29, 30);
		testBallPosition(b, points);

		// * Conflit avec un obstacle
		ObstacleLine o = new ObstacleLine(new Point(0, 30), new Point(60, 30), 10);
		c.addLine(o);

		// Centre de la bille sur la droite
		c.updateBall(b, 1, 2, 30, 30);
		testBallPosition(b, points);

		// Centre de la bille sur l'obstacle en prenant en compte son epaisseur
		c.updateBall(b, 1, 2, 35, 30);
		testBallPosition(b, points);
	}

	private void testBallPosition(Ball b, ArrayList<Point> points) {
		assertEquals(5, b.get_points().size());
		assertEquals(2, b.get_mass(), 0.001);
		assertEquals(1, b.get_radius());
		assertEquals(true, b.get_points().equals(points));
	}

	@Test
	public void test_updateLine() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 2);
		c.addLine(o);

		// ** Update valide
		c.updateLine(o, 3, 0, 20, 20, 20);
		testLinePosition(o);

		// ** Update invalide
		// La modification ne peut pas se faire, l obstacle doit revenir dans
		// l'etat dans lequel elle etait avant l update

		// * Modification des positions hors du circuit
		c.updateLine(o, 3, circuit.get_width() + 1, 20, 20, 20);
		testLinePosition(o);

		c.updateLine(o, 3, 0, circuit.get_height() + 1, 20, 20);
		testLinePosition(o);

		c.updateLine(o, 3, 0, 20, circuit.get_width() + 1, 20);
		testLinePosition(o);

		c.updateLine(o, 3, 0, 20, 20, circuit.get_height() + 1);
		testLinePosition(o);

		// * Conflit avec une bille
		Ball b = new Ball(30, 20, 2, 1, 0);
		c.addBall(b);

		c.updateLine(o, 3, 0, 20, 60, 20);
		testLinePosition(o);
	}

	private void testLinePosition(ObstacleLine o) {
		assertEquals(3, o.get_thickness());
		assertEquals(new Point(0, 20), o.get_depart());
		assertEquals(new Point(20, 20), o.get_arrivee());
	}

	@Test
	public void testCollisionSegment() {
		Circuit circuit = new Circuit(600, 800);
		Controller controller = new Controller(circuit);
		Ball ball1 = new Ball(120, 189, 10, 1, 0);
		Ball ball2 = new Ball(300, 400, 10, 1, 0);
		Point p1 = new Point(80, 200);
		Point p2 = new Point(200, 200);
		ObstacleLine obstacle = new ObstacleLine(p1, p2, 1);
		boolean b1 = controller.collisionSegment(ball1, obstacle);
		boolean b2 = controller.collisionSegment(ball2, obstacle);
		assertEquals(true, b1);
		assertEquals(false, b2);
	}

	@Test
	public void testCollisionPointCerle() {
		Ball ball1 = new Ball(29, 26, 15, 1, 0);
		Ball ball2 = new Ball(26, 13, 5, 1, 0);
		Point2D.Double p1 = new Point2D.Double(33.0, 10);
		Point2D.Double p2 = new Point2D.Double(22.0, 10);
		boolean b1 = c.collisionPointCerle(p1, p2, ball1);
		boolean b2 = c.collisionPointCerle(p1, p2, ball2);
		assertEquals(true, b1);
		assertEquals(false, b2);
	}

	@Test
	public void testDistance() {
		Point2D.Double p1 = new Point2D.Double(33.0, 5);
		Point2D.Double p2 = new Point2D.Double(22.0, 5);
		double dist = c.distance(p1, p2);
		assertEquals(11, dist, 0);
	}

	@Test
	public void testCollisionBall() {
		Ball ball1 = new Ball(120, 180, 10, 1, 0);
		Ball ball2 = new Ball(110, 185, 6, 1, 0);
		Ball ball3 = new Ball(200, 300, 8, 1, 0);
		boolean b1 = c.collisionBall(ball1, ball2);
		boolean b2 = c.collisionBall(ball1, ball3);
		assertEquals(true, b1);
		assertEquals(false, b2);
	}

}
