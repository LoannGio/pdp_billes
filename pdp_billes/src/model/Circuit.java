package model;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
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
	private double _defaultCOR;
	private static double _gravitation = 9.80665;
	/**
	 * Velocity's scale of our application. It handles time step of the simulation.
	 * Increasing this attribute improves the precision of the simulation but it
	 * also increases its duration.
	 */
	private double _scale;
	private double _defaultInclination;
	private Vector _gravityAcceleration;

	public Circuit(int width, int height) {
		_width = width;
		_height = height;
		_defaultBallMass = 1;
		_defaultBallRadius = 10;
		_defaultInclination = 45;
		_defaultCOR = 0.5;
		_scale = 800;
		double ax = 0;
		double ay = _gravitation * Math.sin(Math.toRadians(_defaultInclination));
		_gravityAcceleration = new Vector(ax / _scale, ay / _scale);
	}

	public void addBall(Ball b) {
		_balls.add(b);
	}

	public Boolean removeBall(Ball b) {
		return _balls.remove(b);
	}

	public void clearAll() {
		clearBalls();
		clearLines();

	}

	public void clearBalls() {
		_balls = new ArrayList<Ball>();
	}

	public void clearLines() {
		_lines = new ArrayList<ObstacleLine>();
	}

	public Boolean removeLine(ObstacleLine o) {
		return _lines.remove(o);
	}

	public void addLine(ObstacleLine ol) {
		_lines.add(ol);
	}

	/** Import data of the circuit in XML format of the f file passed in parameter.
	 * During the import, we do not verify the validity of our file. An imported file
	 * must have a .pdp extension. It is assumed that a file with this extension always
	 * have a validate structure. 
	 */
	public void toImport(File f) {
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
		NodeList obstaclesList = document.getElementsByTagName("OBSTACLE");
		NodeList COR = document.getElementsByTagName("COR");
		NodeList xBegin = document.getElementsByTagName("XOBSDEPART");
		NodeList yBegin = document.getElementsByTagName("YOBSDEPART");
		NodeList xEnd = document.getElementsByTagName("XOBSARRIVEE");
		NodeList yEnd = document.getElementsByTagName("YOBSARRIVEE");
		for (int i = 0; i < obstaclesList.getLength(); i++) {
			double xbeg, ybeg, xe, ye, cor;
			cor = Double.parseDouble(COR.item(i).getTextContent());
			xbeg = Double.parseDouble(xBegin.item(i).getTextContent());
			ybeg = Double.parseDouble(yBegin.item(i).getTextContent());
			xe = Double.parseDouble(xEnd.item(i).getTextContent());
			ye = Double.parseDouble(yEnd.item(i).getTextContent());

			_lines.add(new ObstacleLine(new Point((int) xbeg, (int) ybeg), new Point((int) xe, (int) ye), cor));
		}
	}

	private void importCircuitXML(Document document) {
		/** 
		 * After getting user's screen size, we check if it's large enough to display circuit.
		 * If it's not, then the circuit is cropped (on right and on bottom).
		 */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode dm = ge.getScreenDevices()[ge.getScreenDevices().length - 1].getDisplayMode();
		Dimension screenSize = new Dimension(dm.getWidth(), dm.getHeight());
		double widthProportion = 0.8;
		double heightProportion = 0.92;
		double panelMaxWidth = (int) Math.round(widthProportion * screenSize.width);
		double panelMaxHeight = (int) Math.round(heightProportion * screenSize.height);

		_width = Integer.parseInt(document.getElementsByTagName("LONGUEUR").item(0).getTextContent());
		if (_width > panelMaxWidth)
			_width = (int) panelMaxWidth;
		_height = Integer.parseInt(document.getElementsByTagName("HAUTEUR").item(0).getTextContent());
		if (_height > panelMaxHeight)
			_height = (int) panelMaxHeight;

		_defaultInclination = Double.parseDouble(document.getElementsByTagName("INCLINAISON").item(0).getTextContent());
		_defaultBallRadius = Integer
				.parseInt(document.getElementsByTagName("DEFAULTBALLRADIUS").item(0).getTextContent());
		_defaultBallMass = Double
				.parseDouble(document.getElementsByTagName("DEFAULTBALLMASS").item(0).getTextContent());
		_defaultCOR = Double.parseDouble(document.getElementsByTagName("DEFAULTCOR").item(0).getTextContent());
		_scale = Double.parseDouble(document.getElementsByTagName("SCALE").item(0).getTextContent());
	}

	private void importBallsXML(Document document) {
		_balls.clear();
		NodeList ballsList = document.getElementsByTagName("BILLE");
		NodeList xBalls = document.getElementsByTagName("BX");
		NodeList yBalls = document.getElementsByTagName("BY");
		NodeList radiusBalls = document.getElementsByTagName("RADIUS");
		NodeList massBalls = document.getElementsByTagName("MASS");
		for (int i = 0; i < ballsList.getLength(); i++) {
			double x, y, mass;
			int radius;
			x = Double.parseDouble(xBalls.item(i).getTextContent());
			y = Double.parseDouble(yBalls.item(i).getTextContent());
			mass = Double.parseDouble(massBalls.item(i).getTextContent());
			radius = Integer.parseInt(radiusBalls.item(i).getTextContent());
			_balls.add(new Ball(x, y, radius, mass));
		}
	}

	/**
	 * Exports circuit data in XML format in a file F passed as parameter.
	 */
	public void toExport(File f) {
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

			/* File layout */
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

			Element COR = doc.createElement("COR");
			COR.setTextContent(String.valueOf(o.getCOR()));
			obstacle.appendChild(COR);

			Element xObsBegin = doc.createElement("XOBSDEPART");
			xObsBegin.setTextContent(String.valueOf(o.get_begin().getX()));
			obstacle.appendChild(xObsBegin);

			Element yObsBegin = doc.createElement("YOBSDEPART");
			yObsBegin.setTextContent(String.valueOf(o.get_begin().getY()));
			obstacle.appendChild(yObsBegin);

			Element xObsEnd = doc.createElement("XOBSARRIVEE");
			xObsEnd.setTextContent(String.valueOf(o.get_end().getX()));
			obstacle.appendChild(xObsEnd);

			Element yObsEnd = doc.createElement("YOBSARRIVEE");
			yObsEnd.setTextContent(String.valueOf(o.get_end().getY()));
			obstacle.appendChild(yObsEnd);
		}
	}

	private void saveCircuitXML(Document doc, Element rootElement) {
		Element width = doc.createElement("LONGUEUR");
		width.setTextContent(String.valueOf(_width));
		rootElement.appendChild(width);

		Element height = doc.createElement("HAUTEUR");
		height.setTextContent(String.valueOf(_height));
		rootElement.appendChild(height);

		Element defCOR = doc.createElement("DEFAULTCOR");
		defCOR.setTextContent(String.valueOf(_defaultCOR));
		rootElement.appendChild(defCOR);

		Element incl = doc.createElement("INCLINAISON");
		incl.setTextContent(String.valueOf(_defaultInclination));
		rootElement.appendChild(incl);

		Element defBallRadius = doc.createElement("DEFAULTBALLRADIUS");
		defBallRadius.setTextContent(String.valueOf(_defaultBallRadius));
		rootElement.appendChild(defBallRadius);

		Element defBallMass = doc.createElement("DEFAULTBALLMASS");
		defBallMass.setTextContent(String.valueOf(_defaultBallMass));
		rootElement.appendChild(defBallMass);

		Element defScale = doc.createElement("SCALE");
		defScale.setTextContent(String.valueOf(_scale));
		rootElement.appendChild(defScale);
	}

	private void saveBallsXML(Document doc, Element rootElement) {
		Element balls = doc.createElement("BILLES");
		rootElement.appendChild(balls);

		for (Ball b : _balls) {
			Element ball = doc.createElement("BILLE");
			balls.appendChild(ball);

			Element xBall = doc.createElement("BX");
			xBall.setTextContent(String.valueOf(b.get_x()));
			ball.appendChild(xBall);

			Element yBall = doc.createElement("BY");
			yBall.setTextContent(String.valueOf(b.get_y()));
			ball.appendChild(yBall);

			Element radius = doc.createElement("RADIUS");
			radius.setTextContent(String.valueOf(b.get_radius()));
			ball.appendChild(radius);

			Element mass = doc.createElement("MASS");
			mass.setTextContent(String.valueOf(b.get_mass()));
			ball.appendChild(mass);

			Element track = doc.createElement("TRACE");
			ball.appendChild(track);

			for (Point p : b.get_track()) {
				Element pointTrace = doc.createElement("P");
				track.appendChild(pointTrace);

				Element xTrace = doc.createElement("X");
				xTrace.setTextContent(String.valueOf(p.getX()));
				pointTrace.appendChild(xTrace);

				Element yTrace = doc.createElement("Y");
				yTrace.setTextContent(String.valueOf(p.getY()));
				pointTrace.appendChild(yTrace);
			}

		}
	}
	
	private void computeGravitation() {
		double ax = 0;
		double ay = _gravitation * Math.sin(Math.toRadians(_defaultInclination));
		_gravityAcceleration.setCartesian(ax / _scale, ay / _scale);
	}
	
	/**
	 * Inclination setter. When inclination is modified, balls' vertical acceleration
	 * is calculated again, because it depends of gravity and circuit inclination.
	 */
	public void set_inclination(double inclinaison) {
		_defaultInclination = inclinaison;
		computeGravitation();
	}
	
	public void set_scale(double scale) {
		for (Ball b : _balls) {
			b.set_speed(b.get_velocity().getX() * _scale / scale, b.get_velocity().getY() * _scale / scale);
		}
		_scale = scale;
		computeGravitation();
	}

	public static double get_gravitation() {
		return _gravitation;
	}

	public static void set_gravitation(double gravitation) {
		_gravitation = gravitation;
	}

	public double get_inclination() {
		return _defaultInclination;
	}

	public Vector get_acceleration() {
		return _gravityAcceleration;
	}

	public int get_width() {
		return _width;
	}

	public void set_width(int width) {
		this._width = width;
	}

	public int get_height() {
		return _height;
	}

	public void set_height(int height) {
		this._height = height;
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

	public void set_defaultBallRadius(int ballRadius) {
		this._defaultBallRadius = ballRadius;
	}

	public double get_defaultBallMass() {
		return _defaultBallMass;
	}

	public void set_defaultBallMass(double ballMass) {
		this._defaultBallMass = ballMass;
	}

	public double get_scale() {
		return _scale;
	}

	public Vector get_gravityAcceleration() {
		return _gravityAcceleration;
	}

	public void set_gravityAcceleration(Vector _gravityAcceleration) {
		this._gravityAcceleration = _gravityAcceleration;
	}

	public double get_defaultCOR() {
		return _defaultCOR;
	}

	public void set_defaultCOR(double defaultCOR) {
		this._defaultCOR = defaultCOR;
	}

}
