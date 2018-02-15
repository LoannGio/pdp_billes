package test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.ObstacleLine;

public class ObstacleLineTest {

	private ObstacleLine o;
	private Point depart, arrivee, p;
	private int thickness;
	private ArrayList<Point> points; // Il est admis que la colonne de points du
										// point d'arrivee n'est pas incluse
										// dans la liste

	@Before
	public void setUp() throws Exception {
		thickness = 3;
		points = new ArrayList<Point>();
	}

	@After
	public void tearDown() throws Exception {
		o = null;
		depart = null;
		arrivee = null;
		points = null;
		p = null;

	}

	@Test
	public void test_calcPoints() {
		depart = new Point(50, 50);
		arrivee = new Point(53, 50);
		o = new ObstacleLine(depart, arrivee, thickness);
		for (int j = 49; j <= 51; j++) {
			for (int i = 50; i <= 52; i++) {
				p = new Point(i, j);
				points.add(p);
			}
		}
		assertEquals(o.get_points().equals(points), true);

	}

	@Test
	public void test_contains() {
		depart = new Point(50, 50);
		arrivee = new Point(50, 53);
		thickness = 50;
		o = new ObstacleLine(depart, arrivee, thickness);
		assertEquals(o.contains(depart), true);
		depart.setLocation(0, 20);
		o.setAll(depart, arrivee, thickness);
		p = new Point(-1, 25);
		assertEquals(o.contains(p), true);
	}

}
