package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

public class BallTest {
	Ball ball;

	@Before
	public void setUp() throws Exception {
		ball = new Ball(0, 0, 0, 1);
	}

	@After
	public void tearDown() throws Exception {
		ball = null;
	}

	@Test
	public void test_ballLocation() {
		assertEquals(new Point(0, 0), new Point((int) ball.get_x(), (int) ball.get_y()));
		ball.set_init_location(10, 10);
		assertEquals(new Point(10, 10), new Point((int) ball.get_x(), (int) ball.get_y()));
	}

	@Test
	public void testStep() {
		double x_init = 100;
		double y_init = 100;
		ball = new Ball(x_init, y_init, 3, 1);
		ball.step();
		ball.step();
		boolean b1 = (x_init == ball.get_x());
		boolean b2 = (y_init < ball.get_y());
		assertEquals(true, b1);
		assertEquals(true, b2);
	}

	@Test
	public void testResolveCollisionObstacle() {

		ball.set_init_location(100, 100);
		ball.set_radius(3);
		Point p1 = new Point(80, 140);
		Point p2 = new Point(300, 200);
		ObstacleLine obstacle = new ObstacleLine(p1, p2);
		Controller.getInstance().resolveCollisionBallObstacle(ball, obstacle);
		assertEquals(0, ball.get_acceleration().getX(), 0.01);
		assertEquals(0, ball.get_acceleration().getY(), 0.01);

	}

	@Test
	public void testResolveCollisionBall() {

		ball.set_location(100, 100);
		ball.set_radius(10);
		Ball ball2 = new Ball(120, 100, 10, 1);
		Controller.getInstance().resolveCollisionBallBall(ball, ball2);
		boolean ball_ay_is_null = (ball.get_acceleration().getY() == 0);
		boolean ball2_ay_is_null = (ball2.get_acceleration().getY() == 0);
		assertEquals(0, ball.get_acceleration().getX(), 0.01);
		assertEquals(ball_ay_is_null, false);
		assertEquals(0, ball2.get_acceleration().getX(), 0.01);
		assertEquals(ball2_ay_is_null, false);
		ball.step();
		ball2.step();
		ball.set_speed(1, ball.get_velocity().getY());
		ball.set_init_speed(1, ball.get_init_velocity().getY());

		ball2.set_speed(-1, ball2.get_velocity().getY());
		ball2.set_init_speed(-1, ball2.get_init_velocity().getY());
		Controller.getInstance().resolveCollisionBallBall(ball, ball2);
		boolean ball_vy0_is_positive = (ball.get_init_velocity().getY() > 0);
		boolean ball2_vy0_is_positive = (ball2.get_init_velocity().getY() > 0);
		boolean ball_vx0_is_negative = (ball.get_init_velocity().getX() < 0);
		boolean ball2_vx0_is_positive = (ball2.get_init_velocity().getX() > 0);
		assertEquals(ball_vx0_is_negative, true);
		assertEquals(ball_vy0_is_positive, true);
		assertEquals(ball2_vx0_is_positive, true);
		assertEquals(ball2_vy0_is_positive, true);
	}

}
