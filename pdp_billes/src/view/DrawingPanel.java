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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;
import model.Ball;
import model.ObstacleLine;

public class DrawingPanel extends JPanel {
	private Controller _controller;
	private JFrame _parent;
	private Point _pressedLocation;
	private Boolean _creatingLine;
	private Shape _tmpDraw;
	private double _zoomFactor;
	private int _panelWidth, _panelHeight;
	private IRightClickPopUpMenu _rightClickPopUp;

	public DrawingPanel(Dimension frameSize, JFrame parent) {
		initialize(frameSize, parent);
		addListneners();
	}

	private void initialize(Dimension frameSize, JFrame parent) {
		_controller = Controller.getInstance();
		_pressedLocation = null;
		_creatingLine = false;
		_tmpDraw = null;
		_parent = parent;
		_zoomFactor = 1;

		double widthProportion = 0.8;
		double heightProportion = 0.92;
		_panelWidth = (int) Math.round(widthProportion * frameSize.width);
		_panelHeight = (int) Math.round(heightProportion * frameSize.height);
		_controller.setDimensionsPlan(this, _panelWidth, _panelHeight);
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
							ObstacleLine o = new ObstacleLine(_pressedLocation, arrivee);
							if (!(_controller.checkIfLineIsOnExistingBall(o))) {
								arrivee.setLocation(arrivee.getX() / _zoomFactor, arrivee.getY() / _zoomFactor);
								o.set_arrivee(arrivee);
								_controller.addLine(o);
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

		MouseWheelListener mwl = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// Zoom in
				if (e.getWheelRotation() < 0) {
					_zoomFactor *= 1.1;
					repaint();
				}
				// Zoom out
				if (e.getWheelRotation() > 0) {
					_zoomFactor /= 1.1;
					repaint();
				}
				// _zoomer = true;
			}
		};

		addMouseWheelListener(mwl);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.red);

		if ((int) _zoomFactor != 1) {
			AffineTransform at = new AffineTransform();
			at.scale(_zoomFactor, _zoomFactor);
			Rectangle size = getBounds();
			double tx = ((size.getWidth() - getWidth() * _zoomFactor) / 2) / _zoomFactor;
			double ty = ((size.getHeight() - getHeight() * _zoomFactor) / 2) / _zoomFactor;
			at.translate(tx, ty);
			g2.transform(at);
		}

		for (Ball b : _controller.get_balls()) {
			g2.fillOval((int) (b.get_x() - b.get_radius()), (int) (b.get_y() - b.get_radius()), b.get_radius() * 2,
					b.get_radius() * 2);
			/*
			 * for (Point p : b.get_trace()) { g2.fillOval(p.x, p.y, 1, 1); }
			 */

			/*
			 * ArrayList<Point> trace = b.get_trace(); if(trace.size() > 200)
			 * for(int i = 200 ; i > 0 ; i--)
			 * g2.fillOval(trace.get(trace.size()-i).x,
			 * trace.get(trace.size()-i).y, 1, 1);
			 */
		}

		for (ObstacleLine o : _controller.get_lines()) {
			Line2D line = new Line2D.Double(o.get_depart(), o.get_arrivee());
			g2.draw(line);
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
	}

	public void setPanelHeight(int panelHeight) {
		_panelHeight = panelHeight;
	}

	public JFrame get_parent() {
		return _parent;
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
}
