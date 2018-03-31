package test.controller;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import view.DrawingPanel;

public class ControllerTest {
	Controller c;
	Circuit circuit;

	@Before
	public void setUp() throws Exception {
		c = Controller.getInstance();
		circuit = getControllerCircuit();
	}

	public Circuit getControllerCircuit() {
		try {
			Field fcircuit = Controller.class.getDeclaredField("_circuit");
			fcircuit.setAccessible(true);
			return (Circuit) fcircuit.get(c);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@After
	public void tearDown() throws Exception {
		c.clearCircuit();
		c = null;
		circuit = null;
	}

	@Test
	public void test_checkIfPointIsInBall() {
		Point p = new Point(10, 10);
		Ball b = new Ball(10, 10, 3, 1);
		c.addBall(b);

		// The point is on the ball's center
		assertEquals(b, c.checkIfPointIsInBall(p));

		// The point is on the ball, not on the center
		p.setLocation(11, 10);
		assertEquals(b, c.checkIfPointIsInBall(p));

		// The point isn't on the ball
		p.setLocation(50, 50);
		assertEquals(null, c.checkIfPointIsInBall(p));
	}

	@Test
	public void test_removeLinesOutOfBounds() {
		Point depart = new Point(150, 150);
		Point arrivee = new Point(400, 150);
		ObstacleLine o = new ObstacleLine(depart, arrivee, 0.5);
		c.addLine(o);

		// The obstacle is still in the circuit
		circuit.set_width(450);
		circuit.set_height(500);
		c.removeLinesOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_lines().size(), 1, 0);

		// The obstacle is out of the circuit
		circuit.set_width(300);
		c.removeLinesOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_lines().size(), 0, 0);
	}

	@Test
	public void test_removeBallsOutOfBounds() {
		Ball b = new Ball(400, 150, 10, 0);
		c.addBall(b);
		// The ball is still in the circuit
		circuit.set_width(450);
		circuit.set_height(500);
		c.removeBallsOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_balls().size(), 1, 0);
		// The ball is out of the circuit
		circuit.set_width(402);
		c.removeBallsOutOfBounds(0, circuit.get_width(), 0, circuit.get_height());
		assertEquals(c.get_balls().size(), 0, 0);
	}

	@Test
	public void test_checkIfBallIsOnExistingLine() {
		Point depart = new Point(150, 150);
		Point arrivee = new Point(400, 150);
		ObstacleLine o = new ObstacleLine(depart, arrivee, 0.5);
		c.addLine(o);
		// The ball is not on the obstacle
		Ball b = new Ball(50, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), false);

		// The ball with its radius is on the obstacle
		b = new Ball(200, 145, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), true);

		// The center of the ball is exactly on the obstacle
		b = new Ball(200, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingLine(b), true);
	}

	@Test
	public void test_checkIfBallIsOnExistingBall() {
		Ball b = new Ball(50, 150, 10, 0);
		c.addBall(b);
		// The ball is on an other ball
		Ball b2 = new Ball(52, 150, 10, 0);
		assertEquals(c.checkIfBallIsOnExistingBall(b2), true);

		Ball b3 = new Ball(71, 150, 10, 0);
		// The ball isn't on an other ball
		assertEquals(c.checkIfBallIsOnExistingBall(b3), false);

	}

	@Test
	public void test_checkIfLineIsOnExistingBall() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 0.5);

		// Existing ball
		Ball b = new Ball(10, 10, 2, 1);
		c.addBall(b);

		// The obstacle crosses the ball on its center
		assertEquals(true, c.checkIfLineIsOnExistingBall(o));

		// The obstacle and the ball collide on the ball radius
		b.set_location(10, 8.1);
		assertEquals(true, c.checkIfLineIsOnExistingBall(o));

		// The obstacle doesn't collide with any ball
		b.set_location(50, 50);
		assertEquals(false, c.checkIfLineIsOnExistingBall(o));
	}

	@Test
	public void test_updateBall() {
		Ball b = new Ball(10, 10, 1, 1);

		c.addBall(b);
		assertEquals(1, b.get_mass(), 1E-10);
		assertEquals(1, b.get_radius());

		// *** Testing valid update
		c.updateBall(b, 1, 2, 20, 20);
		testBallPosition(b);

		// *** Testing invalid update
		// Update failed, the ball must come back to its previous location

		// ** Updating ball position out of the circuit

		c.updateBall(b, 1, 2, circuit.get_width() + 1, 20);
		testBallPosition(b);

		c.updateBall(b, 1, 2, 20, circuit.get_height() + 1);
		testBallPosition(b);

		// ** Updating over an other existing object
		// * Conflict with a ball
		Ball b2 = new Ball(30, 30, 2, 1);
		c.addBall(b2);
		// Identical centers
		c.updateBall(b, 1, 2, 30, 30);

		testBallPosition(b);

		// Different centers but colliding on radiuses
		c.updateBall(b, 1, 2, 29, 30);
		testBallPosition(b);

		// * Conflict with obstacle
		ObstacleLine o = new ObstacleLine(new Point(0, 30), new Point(60, 30), 0.5);
		c.addLine(o);

		// Center of the ball is on the obstacle
		c.updateBall(b, 1, 2, 30, 30);
		testBallPosition(b);

		// Colliding on ball's radius
		c.updateBall(b, 1, 2, 30, 31);
		testBallPosition(b);
	}

	private void testBallPosition(Ball b) {
		assertEquals(2, b.get_mass(), 1E-10);
		assertEquals(1, b.get_radius());
		assertEquals(20, b.get_location().getX(), 1E-10);
		assertEquals(20, b.get_location().getY(), 1E-10);
	}

	@Test
	public void test_updateLine() {
		ObstacleLine o = new ObstacleLine(new Point(0, 10), new Point(20, 10), 0.5);
		c.addLine(o);

		// ** Valid update
		c.updateLine(o, 0, 20, 20, 20, 0.5);
		testLinePosition(o);

		// ** Invalid update
		// Update failed, the obstacle must come back to its previous location

		// * Update position out of the circuit
		c.updateLine(o, circuit.get_width() + 1, 20, 20, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, circuit.get_height() + 1, 20, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, 20, circuit.get_width() + 1, 20, 0.5);
		testLinePosition(o);

		c.updateLine(o, 0, 20, 20, circuit.get_height() + 1, 0.5);
		testLinePosition(o);

		// * Conflict with ball
		Ball b = new Ball(30, 20, 2, 1);
		c.addBall(b);
		c.updateLine(o, 0, 20, 60, 20, 0.5);
		testLinePosition(o);
	}

	private void testLinePosition(ObstacleLine o) {
		assertEquals(new Point(0, 20), o.get_begin());
		assertEquals(new Point(20, 20), o.get_end());
	}

	@Test
	public void test_collisionSegment() {		
		Controller controller = Controller.getInstance();
		Ball ball1 = new Ball(120, 189, 10, 1);
		Ball ball2 = new Ball(300, 400, 10, 1);
		Point p1 = new Point(80, 200);
		Point p2 = new Point(200, 200);
		ObstacleLine obstacle = new ObstacleLine(p1, p2, 0.5);
		boolean b1 = false;
		boolean b2 = true;
		try {
			Method collisionSegment = Controller.class.getDeclaredMethod("collisionSegment", Ball.class, ObstacleLine.class);
			collisionSegment.setAccessible(true);
			b1 = (Boolean) collisionSegment.invoke(controller, ball1, obstacle);
			b2 = (Boolean) collisionSegment.invoke(controller, ball2, obstacle);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Colliding ball and obstacle
		assertEquals(true, b1);
		// Not colliding
		assertEquals(false, b2);
	}

	@Test
	public void test_distance() {
		Point2D.Double p1 = new Point2D.Double(33.0, 5);
		Point2D.Double p2 = new Point2D.Double(22.0, 5);
		double dist = c.distance(p1, p2);
		// Valid distance
		assertEquals(true, Math.abs(dist - 11) < 1E-10);
		// Invalid distance
		assertEquals(false, Math.abs(dist - 12) < 1E-10);
	}

	/*
	 * Creates 2 balls in collision and a third one far from the others and
	 * check collisions. Then place 2 balls tangently and check if the collision
	 * is detected
	 */
	@Test
	public void test_checkCollisionBallBall() {
		Ball ball1 = new Ball(120, 180, 10, 1);
		Ball ball2 = new Ball(110, 185, 6, 1);
		Ball ball3 = new Ball(200, 300, 8, 1);
		boolean b1 = c.checkCollisionBallBall(ball1, ball2);
		boolean b2 = c.checkCollisionBallBall(ball1, ball3);
		// Les balles entrent en collision
		assertEquals(true, b1);
		// Les balles n'entrent pas en collision
		assertEquals(false, b2);
		ball2.setAll(140, 180, 10, 1);
		b1 = c.checkCollisionBallBall(ball1, ball2);
		assertEquals(true, b1);
	}

	@Test
	public void test_setDimensionsPlan() {
		DrawingPanel _dp = new DrawingPanel(new Dimension(500, 500), new JFrame());
		c.setDimensionsPlan(_dp, 300, 200);
		assertEquals(getControllerCircuit().get_width(), 300, 0);
		assertEquals(getControllerCircuit().get_height(), 200, 0);
		assertEquals(_dp.getWidth(), 300, 0);
		assertEquals(_dp.getHeight(), 200, 0);
	}

}
