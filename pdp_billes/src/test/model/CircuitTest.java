package test.model;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import model.Ball;
import model.Circuit;
import model.ObstacleLine;

public class CircuitTest {
	Circuit c;
	Ball b;
	ObstacleLine o;
	File f;

	@Before
	public void setUp() throws Exception {
		c = new Circuit(500, 500);
		b = new Ball(0, 0, 1, 1);
		o = new ObstacleLine(new Point(10, 10), new Point(20, 20), 0.5);
	}

	@After
	public void tearDown() throws Exception {
		c = null;
		b = null;
		o = null;
		try {
			if (f.exists())
				f.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test_importExport() {
		/* Creting a ball with track */
		Ball ball = new Ball(10, 10, 7, 4);
		ArrayList<Point> exportedTrace = new ArrayList<Point>();
		exportedTrace.add(new Point(2, 2));
		try {
			Field f = ball.getClass().getDeclaredField("_track");
			f.setAccessible(true);
			f.set(ball, exportedTrace);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Adding a ball and two obstacles to the circuit to export something
		 */
		c.addBall(ball);
		c.addLine(new ObstacleLine(new Point(10, 10), new Point(20, 10), 0.8));
		c.addLine(new ObstacleLine(new Point(20, 10), new Point(20, 10), 0.8));

		/*
		 * Updating all updatable default values in order to test if the export
		 * and the import will save and load these values. We save these new
		 * values in variables in rder to test the imported circuit later
		 */
		c.set_inclination(90);
		c.set_defaultBallMass(12);
		c.set_defaultBallRadius(20);
		c.set_defaultCOR(0.8);
		c.set_width(100);
		c.set_height(100);
		double exporteddefaultMass = c.get_defaultBallMass();
		int exporteddefaultRadius = c.get_defaultBallRadius();
		double exporteddefaultCOR = c.get_defaultCOR();
		int exportedWidth = c.get_width();
		int exportedHeight = c.get_height();
		double exportedInclinaison = c.get_inclination();
		ArrayList<Ball> exportedBalls = c.get_balls();
		ArrayList<ObstacleLine> exportedLines = c.get_lines();

		f = new File("TUexport.pdp");
		c.toExport(f);

		/*
		 * Reseting circuit and importing the file. If export and import both
		 * succeeded, the imported circuit attributes shall be the same as the
		 * one we created before export
		 */
		c = new Circuit(666, 666);
		c.toImport(f);

		/* Testing default attributes */
		assertEquals(exporteddefaultMass, c.get_defaultBallMass(), 1E-10);
		assertEquals(exporteddefaultRadius, c.get_defaultBallRadius());
		assertEquals(exporteddefaultCOR, c.get_defaultCOR(), 1E-10);
		assertEquals(exportedWidth, c.get_width());
		assertEquals(exportedHeight, c.get_height());
		assertEquals(exportedInclinaison, c.get_inclination(), 1E-10);

		/* Testing ball presence and attributes */
		assertEquals(exportedBalls.size(), c.get_balls().size());
		for (int i = 0; i < c.get_balls().size(); i++) {
			Ball b = c.get_balls().get(i);
			Ball b2 = exportedBalls.get(i);

			assertEquals(b.get_location().getX(), b2.get_location().getX(), 1E-10);
			assertEquals(b.get_location().getY(), b2.get_location().getY(), 1E-10);
			assertEquals(b.get_mass(), b2.get_mass(), 1E-10);
			assertEquals(b.get_radius(), b2.get_radius());
		}

		/* Testing line presence and attributes */
		assertEquals(exportedLines.size(), c.get_lines().size());
		for (int i = 0; i < c.get_lines().size(); i++) {
			ObstacleLine o = c.get_lines().get(i);
			ObstacleLine o2 = exportedLines.get(i);
			assertEquals(o.getCOR(), o2.getCOR(), 1E-10);
			assertEquals(o.get_begin().getX(), o2.get_begin().getX(), 1E-10);
			assertEquals(o.get_begin().getY(), o2.get_begin().getY(), 1E-10);
			assertEquals(o.get_end().getX(), o2.get_end().getX(), 1E-10);
			assertEquals(o.get_end().getY(), o2.get_end().getY(), 1E-10);

		}

		/*
		 * We never import tracks. This data is only meant to be exported. Here
		 * we test their presence in the exported file to control if they have
		 * been successfully exported
		 */
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(f);

			NodeList pointTrace = document.getElementsByTagName("P");
			NodeList listeXTrace = document.getElementsByTagName("X");
			NodeList listeYTrace = document.getElementsByTagName("Y");

			for (int i = 0; i < pointTrace.getLength(); i++) {
				double x, y, x2, y2;
				x = Double.parseDouble(listeXTrace.item(i).getTextContent());
				y = Double.parseDouble(listeYTrace.item(i).getTextContent());

				x2 = exportedTrace.get(i).getX();
				y2 = exportedTrace.get(i).getY();

				assertEquals(x, x2, 1E-10);
				assertEquals(y, y2, 1E-10);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Test
	public void tes_import_runtime() {
		c.set_width(1000);
		c.set_height(1000);
		Ball b;
		/*
		 * Adding around 500*100 balls to the circuit to create a "heavy" file
		 * to import
		 */
		for (int i = 10; i < 5000; i = i + 10) {
			for (int j = 10; j < 1000; j = j + 10) {
				b = new Ball(i, j, 3, 4);
				c.addBall(b);
			}
		}
		f = new File("TUimport.pdp");
		c.toExport(f);

		long debutImport = System.currentTimeMillis();
		c.toImport(f);
		long finImport = System.currentTimeMillis();

		long tempsImport = finImport - debutImport;

		assertEquals(true, tempsImport < 2000);

	}

}
