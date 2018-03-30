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
	/*
	 * Echelle de vitesse de notre application. Permet d eviter que nos objets
	 * bougent trop vite
	 */
	private double _scale;
	private double _defaultInclinaison;
	private Vector _gravityAcceleration;

	public Circuit(int width, int height) {
		_width = width;
		_height = height;
		_defaultBallMass = 1;
		_defaultBallRadius = 10;
		_defaultInclinaison = 45;
		_defaultCOR = 0.5;
		_scale = 800;
		double ax = 0;
		double ay = _gravitation * Math.sin(Math.toRadians(_defaultInclinaison));
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

	/*
	 * Importe les donnees du circuit au format XML du fichier f passe en
	 * parametre. Lors de l import, on ne verifie pas la validite de notre
	 * fichier pour l application. Un fichier importe a forcement une extension
	 * .pdp. On part du principe qu un fichier ayant cette extension a forcement
	 * la structure adequate pour l import.
	 */
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
		NodeList COR = document.getElementsByTagName("COR");
		NodeList xDepart = document.getElementsByTagName("XOBSDEPART");
		NodeList yDepart = document.getElementsByTagName("YOBSDEPART");
		NodeList xArrivee = document.getElementsByTagName("XOBSARRIVEE");
		NodeList yArrivee = document.getElementsByTagName("YOBSARRIVEE");
		for (int i = 0; i < listeObstacles.getLength(); i++) {
			double xdep, ydep, xarr, yarr, cor;
			cor = Double.parseDouble(COR.item(i).getTextContent());
			xdep = Double.parseDouble(xDepart.item(i).getTextContent());
			ydep = Double.parseDouble(yDepart.item(i).getTextContent());
			xarr = Double.parseDouble(xArrivee.item(i).getTextContent());
			yarr = Double.parseDouble(yArrivee.item(i).getTextContent());

			_lines.add(new ObstacleLine(new Point((int) xdep, (int) ydep), new Point((int) xarr, (int) yarr), cor));
		}
	}

	private void importCircuitXML(Document document) {
		/*
		 * On recupere la taille de l ecran de l utilisateur. Si la taille de
		 * celui ci ne permet pas d afficher le circuit qu on esaye d importer,
		 * on rogne le dit circuit (a droite et en bas)
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

		_defaultInclinaison = Double.parseDouble(document.getElementsByTagName("INCLINAISON").item(0).getTextContent());
		_defaultBallRadius = Integer
				.parseInt(document.getElementsByTagName("DEFAULTBALLRADIUS").item(0).getTextContent());
		_defaultBallMass = Double
				.parseDouble(document.getElementsByTagName("DEFAULTBALLMASS").item(0).getTextContent());
		_defaultCOR = Double.parseDouble(document.getElementsByTagName("DEFAULTCOR").item(0).getTextContent());
		_scale = Double.parseDouble(document.getElementsByTagName("SCALE").item(0).getTextContent());
	}

	private void importBallsXML(Document document) {
		_balls.clear();
		NodeList listeBilles = document.getElementsByTagName("BILLE");
		NodeList xBilles = document.getElementsByTagName("BX");
		NodeList yBilles = document.getElementsByTagName("BY");
		NodeList radiusBilles = document.getElementsByTagName("RADIUS");
		NodeList massBilles = document.getElementsByTagName("MASS");
		for (int i = 0; i < listeBilles.getLength(); i++) {
			double x, y, mass;
			int radius;
			x = Double.parseDouble(xBilles.item(i).getTextContent());
			y = Double.parseDouble(yBilles.item(i).getTextContent());
			mass = Double.parseDouble(massBilles.item(i).getTextContent());
			radius = Integer.parseInt(radiusBilles.item(i).getTextContent());
			_balls.add(new Ball(x, y, radius, mass));
		}
	}

	/*
	 * Exporte les donnees de notre circuit au format XML dans un fichier f
	 * passe en parametre
	 */
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

			/* Mise en page du fichier */
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

		Element defCOR = doc.createElement("DEFAULTCOR");
		defCOR.setTextContent(String.valueOf(_defaultCOR));
		rootElement.appendChild(defCOR);

		Element incl = doc.createElement("INCLINAISON");
		incl.setTextContent(String.valueOf(_defaultInclinaison));
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
		Element billes = doc.createElement("BILLES");
		rootElement.appendChild(billes);

		for (Ball b : _balls) {
			Element bille = doc.createElement("BILLE");
			billes.appendChild(bille);

			Element xBille = doc.createElement("BX");
			xBille.setTextContent(String.valueOf(b.get_x()));
			bille.appendChild(xBille);

			Element yBille = doc.createElement("BY");
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
				Element pointTrace = doc.createElement("P");
				trace.appendChild(pointTrace);

				Element xTrace = doc.createElement("X");
				xTrace.setTextContent(String.valueOf(p.getX()));
				pointTrace.appendChild(xTrace);

				Element yTrace = doc.createElement("Y");
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

	/*
	 * Mutateur de l inclinaison. Lorsqu on modifie l inclinaison, on doit
	 * recalculer la valeur de l acceleration verticale de la bille qui depend
	 * de la gravite et de l inclinaison du plan
	 */
	public void set_inclinaison(double inclinaison) {
		_defaultInclinaison = inclinaison;
		computeGravitation();
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

	public void set_scale(double scale) {
		for (Ball b : _balls) {
			b.set_speed(b.get_velocity().getX() * _scale / scale, b.get_velocity().getY() * _scale / scale);
		}
		_scale = scale;
		computeGravitation();
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

	private void computeGravitation() {
		double ax = 0;
		double ay = _gravitation * Math.sin(Math.toRadians(_defaultInclinaison));
		_gravityAcceleration.setCartesian(ax / _scale, ay / _scale);
	}

}
