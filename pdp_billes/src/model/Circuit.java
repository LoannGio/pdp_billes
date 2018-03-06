package model;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Circuit {
	private int _width;
	private int _height;
	private ArrayList<Ball> _balls = new ArrayList<Ball>();
	private ArrayList<ObstacleLine> _lines = new ArrayList<ObstacleLine>();
	private int _defaultBallRadius;
	private double _defaultBallMass;
	private static double _gravitation = 9.80665;
	private double _defaultInclinaison;

	public Circuit(int width, int height) {
		_width = width;
		_height = height;
		_defaultBallMass = 1;
		_defaultBallRadius = 10;
		_defaultInclinaison = 45;
	}

	public void addBall(Ball b) {
		_balls.add(b);
	}

	public Boolean removeBall(Ball b) {
		return _balls.remove(b);
	}

	public void clearAll() {
		_balls = new ArrayList<Ball>();
		_lines = new ArrayList<ObstacleLine>();
	}

	public Boolean removeLine(ObstacleLine o) {
		return _lines.remove(o);
	}

	public void addLine(ObstacleLine ol) {
		_lines.add(ol);
	}

	public void importer(File f) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			try {
				Document document = documentBuilder.parse(f);

				importCircuitXML(document);

				importBallsXML(document);

				importObstaclesXML(document);

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void importObstaclesXML(Document document) {
		_lines.clear();
		NodeList listeObstacles = document.getElementsByTagName("OBSTACLE");
		NodeList xDepart = document.getElementsByTagName("XOBSDEPART");
		NodeList yDepart = document.getElementsByTagName("YOBSDEPART");
		NodeList xArrivee = document.getElementsByTagName("XOBSARRIVEE");
		NodeList yArrivee = document.getElementsByTagName("YOBSARRIVEE");
		for (int i = 0; i < listeObstacles.getLength(); i++) {
			double xdep, ydep, xarr, yarr;
			xdep = Double.parseDouble(xDepart.item(i).getTextContent());
			ydep = Double.parseDouble(yDepart.item(i).getTextContent());
			xarr = Double.parseDouble(xArrivee.item(i).getTextContent());
			yarr = Double.parseDouble(yArrivee.item(i).getTextContent());

			_lines.add(new ObstacleLine(new Point((int) xdep, (int) ydep), new Point((int) xarr, (int) yarr)));
		}
	}

	private void importCircuitXML(Document document) {
		_width = Integer.parseInt(document.getElementsByTagName("LONGUEUR").item(0).getTextContent());
		_height = Integer.parseInt(document.getElementsByTagName("HAUTEUR").item(0).getTextContent());
		_defaultInclinaison = Double.parseDouble(document.getElementsByTagName("INCLINAISON").item(0).getTextContent());
		_defaultBallRadius = Integer
				.parseInt(document.getElementsByTagName("DEFAULTBALLRADIUS").item(0).getTextContent());
		_defaultBallMass = Double
				.parseDouble(document.getElementsByTagName("DEFAULTBALLMASS").item(0).getTextContent());
	}

	private void importBallsXML(Document document) {
		_balls.clear();
		NodeList listeBilles = document.getElementsByTagName("BILLE");
		NodeList xBilles = document.getElementsByTagName("X");
		NodeList yBilles = document.getElementsByTagName("Y");
		NodeList radiusBilles = document.getElementsByTagName("RADIUS");
		NodeList massBilles = document.getElementsByTagName("MASS");
		for (int i = 0; i < listeBilles.getLength(); i++) {
			double x, y, mass, inclinaison;
			int radius;
			x = Double.parseDouble(xBilles.item(i).getTextContent());
			y = Double.parseDouble(yBilles.item(i).getTextContent());
			mass = Double.parseDouble(massBilles.item(i).getTextContent());
			inclinaison = _defaultInclinaison;
			radius = Integer.parseInt(radiusBilles.item(i).getTextContent());
			_balls.add(new Ball(x, y, radius, mass, inclinaison));
		}
	}

	public void exporter(File f) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement("CIRCUIT");
			doc.appendChild(rootElement);

			saveCircuitXML(doc, rootElement);

			saveObstaclesXML(doc, rootElement);

			saveBallsXML(doc, rootElement);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");

				DOMSource source = new DOMSource(doc);

				StreamResult result = null;

				result = new StreamResult(f);
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}

	private void saveObstaclesXML(Document doc, Element rootElement) {
		Element obstacles = doc.createElement("OBSTACLES");
		rootElement.appendChild(obstacles);

		for (ObstacleLine o : _lines) {
			Element obstacle = doc.createElement("OBSTACLE");
			obstacles.appendChild(obstacle);

			Element xObsDepart = doc.createElement("XOBSDEPART");
			xObsDepart.setTextContent(String.valueOf(o.get_depart().getX()));
			obstacle.appendChild(xObsDepart);

			Element yObsDepart = doc.createElement("YOBSDEPART");
			yObsDepart.setTextContent(String.valueOf(o.get_depart().getY()));
			obstacle.appendChild(yObsDepart);

			Element xObsArrivee = doc.createElement("XOBSARRIVEE");
			xObsArrivee.setTextContent(String.valueOf(o.get_arrivee().getX()));
			obstacle.appendChild(xObsArrivee);

			Element yObsArrivee = doc.createElement("YOBSARRIVEE");
			yObsArrivee.setTextContent(String.valueOf(o.get_arrivee().getY()));
			obstacle.appendChild(yObsArrivee);
		}
	}

	private void saveCircuitXML(Document doc, Element rootElement) {
		Element width = doc.createElement("LONGUEUR");
		width.setTextContent(String.valueOf(_width));
		rootElement.appendChild(width);

		Element height = doc.createElement("HAUTEUR");
		height.setTextContent(String.valueOf(_height));
		rootElement.appendChild(height);

		Element incl = doc.createElement("INCLINAISON");
		incl.setTextContent(String.valueOf(_defaultInclinaison));
		rootElement.appendChild(incl);

		Element defBallRadius = doc.createElement("DEFAULTBALLRADIUS");
		defBallRadius.setTextContent(String.valueOf(_defaultBallRadius));
		rootElement.appendChild(defBallRadius);

		Element defdefBallMass = doc.createElement("DEFAULTBALLMASS");
		defdefBallMass.setTextContent(String.valueOf(_defaultBallMass));
		rootElement.appendChild(defdefBallMass);
	}

	private void saveBallsXML(Document doc, Element rootElement) {
		Element billes = doc.createElement("BILLES");
		rootElement.appendChild(billes);

		for (Ball b : _balls) {
			Element bille = doc.createElement("BILLE");
			billes.appendChild(bille);

			Element xBille = doc.createElement("X");
			xBille.setTextContent(String.valueOf(b.get_x()));
			bille.appendChild(xBille);

			Element yBille = doc.createElement("Y");
			yBille.setTextContent(String.valueOf(b.get_y()));
			bille.appendChild(yBille);

			Element radius = doc.createElement("RADIUS");
			radius.setTextContent(String.valueOf(b.get_radius()));
			bille.appendChild(radius);

			Element mass = doc.createElement("MASS");
			mass.setTextContent(String.valueOf(b.get_mass()));
			bille.appendChild(mass);

			Element trace = doc.createElement("TRACE");
			bille.appendChild(trace);

			for (Point p : b.get_trace()) {
				Element pointTrace = doc.createElement("POINTTRACE");
				trace.appendChild(pointTrace);

				Element xTrace = doc.createElement("XTRACE");
				xTrace.setTextContent(String.valueOf(p.getX()));
				pointTrace.appendChild(xTrace);

				Element yTrace = doc.createElement("YTRACE");
				yTrace.setTextContent(String.valueOf(p.getY()));
				pointTrace.appendChild(yTrace);
			}

		}
	}

	public static double get_gravitation() {
		return _gravitation;
	}

	public static void set_gravitation(double gravitation) {
		_gravitation = gravitation;
	}

	public double get_inclinaison() {
		return _defaultInclinaison;
	}

	public void set_inclinaison(double inclinaison) {
		_defaultInclinaison = inclinaison;
		for (Ball b : _balls) {
			b.setAcceleration(_defaultInclinaison);
		}
	}

	public int get_width() {
		return _width;
	}

	public void set_width(int _width) {
		this._width = _width;
	}

	public int get_height() {
		return _height;
	}

	public void set_height(int _height) {
		this._height = _height;
	}

	public ArrayList<Ball> get_balls() {
		return _balls;
	}

	public ArrayList<ObstacleLine> get_lines() {
		return _lines;
	}

	public int get_defaultBallRadius() {
		return _defaultBallRadius;
	}

	public void set_defaultBallRadius(int _ballRadius) {
		this._defaultBallRadius = _ballRadius;
	}

	public double get_defaultBallMass() {
		return _defaultBallMass;
	}

	public void set_defaultBallMass(double _ballMass) {
		this._defaultBallMass = _ballMass;
	}

}
