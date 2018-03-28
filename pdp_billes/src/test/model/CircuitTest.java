package test.model;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;
import model.Circuit;
import model.ObstacleLine;

public class CircuitTest {
	Circuit c;
	Ball b;
	ObstacleLine o;

	@Before
	public void setUp() throws Exception {
		c = new Circuit(500, 500);
		b = new Ball(0, 0, 1, 1);
		o = new ObstacleLine(new Point(10, 10), new Point(20, 20));
	}

	@After
	public void tearDown() throws Exception {
		c = null;
		b = null;
		o = null;
	}

	@Test
	public void test_AddRemoveBall() {
		assertEquals(0, c.get_balls().size());
		int nbBalls = 10;
		for (int i = 0; i < nbBalls; i++)
			c.addBall(b);
		assertEquals(nbBalls, c.get_balls().size());
		c.removeBall(b);
		assertEquals(nbBalls - 1, c.get_balls().size());
	}

	@Test
	public void test_AddRemoveLine() {
		assertEquals(0, c.get_lines().size());
		int nbLines = 20;
		for (int i = 0; i < nbLines; i++)
			c.addLine(o);
		assertEquals(nbLines, c.get_lines().size());
		c.removeLine(o);
		assertEquals(nbLines - 1, c.get_lines().size());
	}

	@Test
	public void test_clearAll() {
		assertEquals(0, c.get_balls().size());
		assertEquals(0, c.get_lines().size());
		int nbBalls = 10;
		int nbLines = 20;

		for (int i = 0; i < nbBalls; i++)
			c.addBall(b);
		for (int i = 0; i < nbLines; i++)
			c.addLine(o);

		assertEquals(nbBalls, c.get_balls().size());
		assertEquals(nbLines, c.get_lines().size());
		c.clearAll();
		assertEquals(0, c.get_balls().size());
		assertEquals(0, c.get_lines().size());
	}

}
