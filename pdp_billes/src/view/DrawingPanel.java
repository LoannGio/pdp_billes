package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {
	private Controller _controller;
	private Point _pressedLocation;
	private Boolean _creatingLine;

	/*
	 * tmpDraw est un petit rectangle qui sera affiche au point d origine
	 * potentiel d un obstacle pendant le drag de la souris lors de la creation
	 * d un obstacle
	 */
	private Shape _tmpDraw;
	private double _zoomFactor;
	private int _panelWidth, _panelHeight;
	private IRightClickPopUpMenu _rightClickPopUp;

	/*
	 * Image memoire contenant les obstacle et les traces des billes a tout
	 * temps de l execution. Cette image permet d eviter d avoir a reparcourir
	 * 1)la liste des traces de chaque bile et de tous les repeindre a chaque
	 * fois 2) la liste des obstacles qui, de toute facon, ne changera jamais
	 * pendant une execution
	 */
	private BufferedImage _buffer;

	public DrawingPanel(Dimension frameSize, JFrame parent) {
		initialize(frameSize, parent);
		addListneners();
	}

	private void initialize(Dimension frameSize, JFrame parent) {
		_controller = Controller.getInstance();
		_pressedLocation = null;
		_creatingLine = false;
		_tmpDraw = null;
		_zoomFactor = 1;

		/*
		 * Proportions du panneau de dessin dans la fenetre
		 */
		double widthProportion = 0.8;
		double heightProportion = 0.92;

		_panelWidth = (int) Math.round(widthProportion * frameSize.width);
		_panelHeight = (int) Math.round(heightProportion * frameSize.height);
		_controller.setDimensionsPlan(this, _panelWidth, _panelHeight);
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
	}

	private void addListneners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!_controller.isRunningApp()) {
					/*
					 * Si on fait un clic gauche, on commence a creer un
					 * obstacle
					 */
					if (e.getButton() == MouseEvent.BUTTON1) {
						_pressedLocation = new Point((int) (e.getX() / _zoomFactor), (int) (e.getY() / _zoomFactor));
						_creatingLine = true;
						_tmpDraw = new Rectangle((int) (e.getX() / _zoomFactor), (int) (e.getY() / _zoomFactor) - 5, 10,
								10);
						repaint();
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						/*
						 * Clic droit, on verifie si on a clique sur un objet.
						 * Si oui, on ouvre le popUp correspondant a l objet
						 * sujet
						 */
						Object o = null;
						if ((o = _controller.checkIfPointIsInBall(e.getPoint())) != null) {
							_rightClickPopUp = RightClickChooser.createRightClickPopUp((Ball) o, _controller,
									getMyself());
							_rightClickPopUp.show(e.getComponent(), e.getX(), e.getY());
						} else if ((o = _controller.checkIfPointIsNearLine(e.getPoint())) != null) {
							_rightClickPopUp = RightClickChooser.createRightClickPopUp((ObstacleLine) o, _controller,
									getMyself());
							_rightClickPopUp.show(e.getComponent(), e.getX(), e.getY());

						} else {
							/*
							 * On n a pas clique sur un objet existant, on va
							 * donc creer une bille
							 */
							Ball b = new Ball(e.getX(), e.getY(), _controller.get_defaultBallRadius(),
									_controller.get_defaultBallMass());
							if (!(_controller.checkIfBallIsOnExistingObject(b))) {
								b.set_location(b.get_x() / _zoomFactor, b.get_y() / _zoomFactor);
								_controller.addBall(b);
							}
							repaint();

						}
					}

				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!_controller.isRunningApp()) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						/*
						 * Si on relache le clique gauche (donc il a forcement
						 * deja ete presse) et on essaie de creer un obstacle
						 * entre le point ou la souris a ete enfoncee et celui
						 * ou elle a ete relachee La creation echoue si 1) le
						 * point d origine est le meme que le point d arrivee 2)
						 * le point d arrivee est en dehors du panneau de dessin
						 * 3) l obstacle traverse une bille
						 */
						if (e.getX() <= _panelWidth && e.getX() >= 0 && e.getY() <= _panelHeight && e.getY() >= 0
								&& (e.getX() != _pressedLocation.getX() || e.getY() != _pressedLocation.getY())) {
							Point arrivee = new Point();
							arrivee.setLocation(e.getX(), e.getY());
							ObstacleLine o = new ObstacleLine(_pressedLocation, arrivee, _controller.get_defaultCOR());
							if (!(_controller.checkIfLineIsOnExistingBall(o))) {
								arrivee.setLocation(arrivee.getX() / _zoomFactor, arrivee.getY() / _zoomFactor);
								o.set_arrivee(arrivee);
								_controller.addLine(o);
								drawLineBuffer(o);
							}
						}
						_pressedLocation = null;
						_creatingLine = false;
						_tmpDraw = null;
						repaint();
					}
				}
			}

		});
	}

	public void drawLineBuffer(ObstacleLine o) {
		/* Peindre un obstacle sur l image bufferisee */
		Line2D line = new Line2D.Double(o.get_begin(), o.get_end());
		Graphics2D gbuff = _buffer.createGraphics();
		gbuff.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gbuff.setColor(Color.blue);
		gbuff.draw(line);
		gbuff.dispose();
	}

	public void drawTraceBuffer(Point p) {
		/* Peindre un point (une trace de bille) sur l image bufferisee */
		Graphics2D gbuff = _buffer.createGraphics();
		gbuff.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gbuff.setColor(Color.green);
		gbuff.fillOval(p.x, p.y, 1, 1);
		gbuff.dispose();
	}

	/*
	 * Efface ce qui est present sur le panneau de dessin et repeint par dessus.
	 * Dans un premier temps, on affiche l image bufferisse ; puis, on peint les
	 * billes par dessus
	 * 
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.red);

		g2.drawImage(_buffer, 0, 0, this);

		for (Ball b : _controller.get_balls()) {
			g2.fillOval((int) (b.get_x() - b.get_radius()), (int) (b.get_y() - b.get_radius()), b.get_radius() * 2,
					b.get_radius() * 2);
		}

		/*
		 * Si on est en train de creer une ligne (clique gauche enfonce), on
		 * affiche un petit carre a titre indicatif
		 */
		if (_creatingLine) {
			g2.setColor(Color.BLACK);
			g2.draw(_tmpDraw);
		}
		// g2.dispose();
	}

	public int getPanelWidth() {
		return _panelWidth;
	}

	public int getPanelHeight() {
		return _panelHeight;
	}

	public void setPanelWidth(int panelWidth) {
		_panelWidth = panelWidth;
		_buffer = resizeBufferedImage(_buffer, panelWidth, getPanelHeight());

	}

	public void setPanelHeight(int panelHeight) {
		_panelHeight = panelHeight;
		_buffer = resizeBufferedImage(_buffer, getPanelWidth(), panelHeight);

	}

	public static BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) {
		BufferedImage tmp = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = tmp.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		return tmp;
	}

	public DrawingPanel getMyself() {
		/*
		 * On a besoin de cette fonction pour recuperer l'instance du
		 * DrawingPannel dans les classes internes utilisees par les Listeners
		 */
		return this;
	}

	public void deleteObjectsOutOfBounds(int xMin, int xMax, int yMin, int yMax) {
		_controller.removeBallsOutOfBounds(xMin, xMax, yMin, yMax);
		_controller.removeLinesOutOfBounds(xMin, xMax, yMin, yMax);
	}

	public void mySetBounds(int x, int y, int newCreationZoneWidth, int newCreationZoneHeight) {
		setBounds(x, y, newCreationZoneWidth, newCreationZoneHeight);
		if (_buffer != null)
			_buffer = resizeBufferedImage(_buffer, newCreationZoneWidth, newCreationZoneHeight);
	}

	public void clearBufferedImage() {
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
	}

	public void repaintBufferedImage(ArrayList<ObstacleLine> lines, ArrayList<Ball> balls) {
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
		for (ObstacleLine o : lines)
			drawLineBuffer(o);
		for (Ball b : balls)
			for (Point p : b.get_track())
				drawTraceBuffer(p);
	}
}