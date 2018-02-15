package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;

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
		ball.setAll(10, 10, 1, 1);
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
	public void test_step() {

	}

}
