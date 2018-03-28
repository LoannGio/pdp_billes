package test.model;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.ObstacleLine;

public class ObstacleLineTest {

	private ObstacleLine o;
	private Point depart, arrivee, p;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		o = null;
		depart = null;
		arrivee = null;
		p = null;

	}

	@Test
	public void test_contains() {
		depart = new Point(50, 50);
		arrivee = new Point(50, 53);
		o = new ObstacleLine(depart, arrivee, 0.5);
		assertEquals(o.contains(depart), true);
		depart.setLocation(0, 20);
		o.setPositions(depart, arrivee);
		p = new Point(-1, 25);
		assertEquals(o.contains(p), false);
	}
	
	@Test
	public void testIsNearPoint(){
		depart = new Point(50, 50);
		arrivee = new Point(600, 50);
		o = new ObstacleLine(depart, arrivee, 0.5);
		Point p = new Point(300, 52);
		assertEquals(o.isNearPoint(p), true);
	}

}
