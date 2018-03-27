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

public class DrawingPanel extends JPanel {
	private Controller _controller;
	private Point _pressedLocation;
	private Boolean _creatingLine;
	private Shape _tmpDraw;
	private double _zoomFactor;
	private int _panelWidth, _panelHeight;
	private IRightClickPopUpMenu _rightClickPopUp;
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
					if (e.getButton() == MouseEvent.BUTTON1) {
						_pressedLocation = new Point((int) (e.getX() / _zoomFactor), (int) (e.getY() / _zoomFactor));
						_creatingLine = true;
						_tmpDraw = new Rectangle((int) (e.getX() / _zoomFactor), (int) (e.getY() / _zoomFactor) - 5, 10,
								10);
						repaint();
					} else if (e.getButton() == MouseEvent.BUTTON3) {
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
		Line2D line = new Line2D.Double(o.get_depart(), o.get_arrivee());
		Graphics2D gbuff = _buffer.createGraphics();
		gbuff.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gbuff.setColor(Color.blue);
		gbuff.draw(line);
		gbuff.dispose();
	}

	public void drawTraceBuffer(Point p) {
		Graphics2D gbuff = _buffer.createGraphics();
		gbuff.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gbuff.setColor(Color.green);
		gbuff.fillOval(p.x, p.y, 1, 1);
		gbuff.dispose();
	}

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

	public void repaintBufferedImageObstacles(ArrayList<ObstacleLine> lines) {
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
		for (ObstacleLine o : lines)
			drawLineBuffer(o);

	}

	public void repaintBufferedImage(ArrayList<ObstacleLine> lines, ArrayList<Ball> balls) {
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
		for (ObstacleLine o : lines)
			drawLineBuffer(o);
		for (Ball b : balls)
			for (Point p : b.get_trace())
				drawTraceBuffer(p);
	}

	public void repaintBufferedImageTraces(ArrayList<Ball> balls) {
		_buffer = new BufferedImage(_panelWidth, _panelHeight, BufferedImage.TYPE_INT_ARGB);
		for (Ball b : balls)
			for (Point p : b.get_trace())
				drawTraceBuffer(p);

	}
}