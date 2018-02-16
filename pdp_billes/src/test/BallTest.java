package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;
import model.ObstacleLine;

public class BallTest {
	Ball ball;

	@Before
	public void setUp() throws Exception {
		ball = new Ball(0, 0, 0, 1, 10);
	}

	@After
	public void tearDown() throws Exception {
		ball = null;
	}

	@Test
	public void test_listePoints() {
		assertEquals(1, ball.get_points().size());
		ball.setAll(10, 10, 1, 1, 10);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(9, 10));
		points.add(new Point(10, 9));
		points.add(new Point(10, 10));
		points.add(new Point(10, 11));
		points.add(new Point(11, 10));

		assertEquals(5, ball.get_points().size());
		assertEquals(true, ball.get_points().equals(points));

	}

	@Test
	public void test_ballLocation() {
		assertEquals(new Point(0, 0), new Point((int) ball.get_x(), (int) ball.get_y()));
		ball.set_x(10);
		ball.set_y(10);
		assertEquals(new Point(10, 10), new Point((int) ball.get_x(), (int) ball.get_y()));
	}

	@Test
	public void testStep() {
		double x_init = 100;
		double y_init = 100;
		ball = new Ball(x_init, y_init, 3, 1, 40);
		ball.step();
		ball.step();
		boolean b1 = (x_init == ball.get_x());
		boolean b2 = (y_init < ball.get_y());
		assertEquals(true, b1);
		assertEquals(true, b2);
	}

	@Test
	public void testResolveCollisionObstacle() {

		ball.set_x(100);
		ball.set_y(100);
		ball.set_radius(3);
		Point p1 = new Point(80, 140);
		Point p2 = new Point(300, 200);
		ObstacleLine obstacle = new ObstacleLine(p1, p2, 1);
		ball.resolveCollisionObstacle(obstacle);
		assertEquals(0, ball.get_ax(), 0);
		assertEquals(0, ball.get_ay(), 0);

	}

	@Test
	public void testResolveCollisionBall() {

		ball.set_x(100); ball.set_y(100);
		ball.set_radius(10);
		Ball ball2 = new Ball(120, 100, 10, 1, 10);
		ball.resolveCollisionBall(ball2);
		boolean ball_ay_is_null = (ball.get_ay() == 0);
		boolean ball2_ay_is_null = (ball2.get_ay() == 0);
		assertEquals(0, ball.get_ax(), 0);
		assertEquals(ball_ay_is_null, false);
		assertEquals(0, ball2.get_ax(), 0);
		assertEquals(ball2_ay_is_null, false);
		ball.step();
		ball2.step();
		ball.set_vx(1);
		ball2.set_vx(-1);
		ball.set_vx0(1);
		ball2.set_vx0(-1);
		ball.resolveCollisionBall(ball2);
		boolean ball_vy0_is_positive = (ball.get_vy0() > 0);
		boolean ball2_vy0_is_positive = (ball2.get_vy0() > 0);
		boolean ball_vx0_is_negative = (ball.get_vx0() < 0);
		boolean ball2_vx0_is_positive = (ball2.get_vx0() > 0);
		assertEquals(ball_vx0_is_negative, true);
		assertEquals(ball_vy0_is_positive, true);
		assertEquals(ball2_vx0_is_positive, true);
		assertEquals(ball2_vy0_is_positive, true);
	}

}
