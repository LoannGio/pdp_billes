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

		}

	}

	@Test
	public void test_importExport() {
		/* On cree une balle avec une trace */
		Ball ball = new Ball(10, 10, 7, 4);
		ArrayList<Point> exportedTrace = new ArrayList<Point>();
		exportedTrace.add(new Point(2, 2));
		try {
			Field f = ball.getClass().getDeclaredField("_trace");
			f.setAccessible(true);
			f.set(ball, exportedTrace);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * On ajoute une balle et un obstacle au circuit afin qu'il y ait
		 * quelque chose a exporter
		 */
		c.addBall(ball);
		c.addLine(new ObstacleLine(new Point(10, 10), new Point(20, 10), 0.8));
		c.addLine(new ObstacleLine(new Point(20, 10), new Point(20, 10), 0.8));

		/*
		 * On modifie toutes les valeurs modifiables par defaut afin de tester
		 * si l export et l import en tiendront bien compte. On les sauvegardes
		 * dans des variables pour les comparer plus tard
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

		/* On cree un fichier et on exporte notre circuit dedans */
		f = new File("TUexport.pdp");
		c.toExport(f);

		/*
		 * On re initialise le circuit. Toutes ses valeurs sont desormais celles
		 * par defaut. Ensuite, on importe un circuit a partir du fichier que l
		 * on vient d exporter. Si l import et l export ont correctement
		 * fonctionnes, on devrait avoir les memes valeurs avant et apres l
		 * import
		 */
		c = new Circuit(666, 666);
		c.toImport(f);

		/* On test les valeurs par defaut */
		assertEquals(exporteddefaultMass, c.get_defaultBallMass(), 1E-10);
		assertEquals(exporteddefaultRadius, c.get_defaultBallRadius());
		assertEquals(exporteddefaultCOR, c.get_defaultCOR(), 1E-10);
		assertEquals(exportedWidth, c.get_width());
		assertEquals(exportedHeight, c.get_height());
		assertEquals(exportedInclinaison, c.get_inclination(), 1E-10);

		/* On test la presence, la position et les attributs des balles */
		assertEquals(exportedBalls.size(), c.get_balls().size());
		for (int i = 0; i < c.get_balls().size(); i++) {
			Ball b = c.get_balls().get(i);
			Ball b2 = exportedBalls.get(i);

			assertEquals(b.get_location().getX(), b2.get_location().getX(), 1E-10);
			assertEquals(b.get_location().getY(), b2.get_location().getY(), 1E-10);
			assertEquals(b.get_mass(), b2.get_mass(), 1E-10);
			assertEquals(b.get_radius(), b2.get_radius());
		}

		/* On test la presence, la position et les attributs des obstacles */
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
		 * On n importe jamais les traces. Cette donnee est juste vouee a etre
		 * exportee. Ici, on teste que les traces presentes dans le fichiers
		 * sont bien celles qu on avait dans le circuit exporte
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
		 * On ajoute environ 500*100 billes au circuit pour faire un gros
		 * fichier a importer
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
