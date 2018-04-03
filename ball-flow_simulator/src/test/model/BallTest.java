package test.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;
import model.Circuit;
import model.Vector;

public class BallTest {
	Ball ball1;
	Ball ball2;
	Ball ball3;
	Circuit circuit;

	@Before
	public void setUp() throws Exception {
		ball1 = new Ball(0, 0, 10, 1);
		ball2 = new Ball(0, 0, 10, 1);
		ball3 = new Ball(0, 0, 10, 1);
		circuit = new Circuit(1000, 800);
	}

	@After
	public void tearDown() throws Exception {
		ball1 = null;
		ball2 = null;
		ball3 = null;
		circuit = null;
	}

	@Test
	public void test_ballSetLocation() {
		assert (ball1.get_x() == 0 && ball1.get_y() == 0);
		ball1.set_location(10, 10);
		assert (ball1.get_x() == 10 && ball1.get_y() == 10);
	}

	@Test
	public void test_ballSetSpeed() {
		assert (ball1.get_velocity().getX() == 0.0);
		assert (ball1.get_velocity().getY() == 0.0);
		ball1.set_speed(2.5, 4.0);
		assert (ball1.get_velocity().getX() == 2.5);
		assert (ball1.get_velocity().getY() == 4.0);
	}

	/*
	 * We check that the ball's position after a step is correct. The ball
	 * should move forward y axis.
	 */
	@Test
	public void test_step() {
		double x_init = 100;
		double y_init = 100;
		ball1 = new Ball(x_init, y_init, 3, 1);
		ball1.step(circuit.get_acceleration());
		boolean b1 = (x_init == ball1.get_x());
		boolean b2 = (y_init < ball1.get_y());
		assertEquals(true, b1);
		assertEquals(true, b2);
	}

	/*
	 * Looping on the "step" method, we test at every iteration that the ball is
	 * at a correct location
	 */
	@Test
	public void test_stepLoop() {
		double x_init = 100;
		double y_init = 100;
		double x_prec = x_init;
		double y_prec = y_init;
		ball1 = new Ball(x_init, y_init, 3, 1);
		for (int i = 0; i < 10; i++) {
			ball1.step(circuit.get_acceleration());
			boolean b1 = (x_prec == ball1.get_x());
			boolean b2 = (y_prec < ball1.get_y());
			assertEquals(true, b1);
			assertEquals(true, b2);
			x_prec = ball1.get_x();
			y_prec = ball1.get_y();
		}
	}

	/*
	 * We create two balls on two circuits with different incclination. We check
	 * that the ball on the most inclined circuit movesfaster than the other. We
	 * create an other ball on a circuit without inclination (0 degree
	 * inclination) and we check that the ball doesn't move.
	 */
	@Test
	public void test_stepInclinaison() {
		double x_init = 100;
		double y_init = 100;
		ball1 = new Ball(x_init, y_init, 3, 1);
		ball2 = new Ball(x_init, y_init, 3, 1);
		ball3 = new Ball(x_init, y_init, 3, 1);
		double angle1 = 20;
		double angle2 = 35;
		double angle3 = 0;

		circuit.set_inclination(angle1);
		Vector acceleration1 = new Vector(circuit.get_acceleration().getX(), circuit.get_acceleration().getY());
		circuit.set_inclination(angle2);
		Vector acceleration2 = new Vector(circuit.get_acceleration().getX(), circuit.get_acceleration().getY());

		circuit.set_inclination(angle3);
		Vector acceleration3 = new Vector(circuit.get_acceleration().getX(), circuit.get_acceleration().getY());
		for (int i = 0; i < 10; i++) {
			ball1.step(acceleration1);
			ball2.step(acceleration2);
			ball3.step(acceleration3);
			boolean b1 = (ball1.get_x() == ball2.get_x());
			boolean b2 = (ball1.get_y() < ball2.get_y());
			boolean b3 = (ball3.get_x() == x_init) && (ball3.get_y() == y_init);
			assertEquals(true, b1);
			assertEquals(true, b2);
			assertEquals(true, b3);
		}
	}

}
