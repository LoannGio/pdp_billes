package controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.AnimationTimer;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import model.Quadtree;
import model.Vector;
import view.DrawingPanel;

public class PhysicalEngine {
	private Circuit _circuit;
	private Controller _controller;
	private AnimationTimer timer;
	private Quadtree _quad;

	public PhysicalEngine(Circuit circuit) {
		_circuit = circuit;
		_controller = Controller.getInstance();
		_quad = new Quadtree(0, new Rectangle(0, 0, (int) _controller.getDimensionsPlan().getWidth(),
				(int) _controller.getDimensionsPlan().getHeight()));
	}

	/************************************
	 * Ball - Ball
	 ************************************/

	public void run(DrawingPanel dp) {
		timer = new AnimationTimer(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle square;
				// on vide le quadtree
				_quad.clear();

				ArrayList<Ball> returnObjects = new ArrayList<Ball>();

				for (Ball ball : _circuit.get_balls()) {
					if (!ballIsOutOfPanel(ball, dp)) {
						_quad.insert(ball);

						returnObjects.clear();
						_quad.retrieve(returnObjects, ball);
						for (ObstacleLine obstacle : _circuit.get_lines()) {
							if (_controller.checkCollisionBallObstacle(ball, obstacle)) {
								resolveCollisionBallObstacle(ball, obstacle);
							}
						}
						for (Ball ball2 : returnObjects) {
							if (ball2 != ball) {
								if (_controller.checkCollisionBallBall(ball, ball2)) {
									resolveCollisionBallBall(ball, ball2);
								}
							}
						}
						dp.repaint();
						ball.step(_circuit.get_acceleration());
						dp.drawTraceBuffer(ball.get_trace().get(ball.get_trace().size() - 1));

					}
				}
				Toolkit.getDefaultToolkit().sync();
			}
		});
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public boolean ballIsOutOfPanel(Ball b, DrawingPanel dp) {
		double bx = b.get_x();
		double by = b.get_y();
		double br = b.get_radius();
		int dpx = dp.getX();
		int dpy = dp.getY();
		int dpwidth = dp.getWidth();
		int dpheight = dp.getHeight();
		if (bx - br > dpx + dpwidth || bx + br < dpx || by - br > dpy + dpheight || by + br < dpy)
			return true;
		return false;

	}

	/**************** Version Ball - Ball *************************/

	public double angleCollisionBall(Ball ball1, Ball ball2) {
		Vector v;
		double d1 = Math.sqrt(Math.pow(ball1.get_x(), 2) + Math.pow(ball1.get_y(), 2));
		double d2 = Math.sqrt(Math.pow(ball2.get_x(), 2) + Math.pow(ball2.get_y(), 2));
		Vector loc1 = ball1.get_location();
		Vector loc2 = ball2.get_location();

		if (d1 > d2)
			v = Vector.vectorSubtract(loc1, loc2);
		else
			v = Vector.vectorSubtract(loc2, loc1);

		double angle = Math.atan2(v.getY(), v.getX());
		return angle;
	}

	private void resolveCollisionBallBall(Ball ball1, Ball ball2) {

		double collision_angle = angleCollisionBall(ball1, ball2);
		double direction_1 = Math.atan2(ball1.get_velocity().getY(), ball1.get_velocity().getX()); // direction
																									// of
																									// ball
																									// 1
		double direction_2 = Math.atan2(ball2.get_velocity().getY(), ball2.get_velocity().getX()); // direction
																									// of
																									// ball
																									// 2
		Vector pos1 = ball1.get_location(); // position of ball 1
		Vector pos2 = ball2.get_location(); // position of ball 2
		double v1 = ball1.get_speed(); // speed of ball 1
		double v2 = ball2.get_speed(); // speed of ball 2
		double m1 = ball1.get_mass(); // mass of ball 1
		double m2 = ball2.get_mass(); // mass of ball 2
		double r1 = ball1.get_radius(); // raduis of ball 2
		double r2 = ball2.get_radius(); // raduis of ball 1

		/***************
		 * new Velocity of two ball according to their directions
		 **************/
		Vector new_v1 = new Vector(v1 * Math.cos(direction_1 - collision_angle),
				v1 * Math.sin(direction_1 - collision_angle));
		Vector new_v2 = new Vector(v2 * Math.cos(direction_2 - collision_angle),
				v2 * Math.sin(direction_2 - collision_angle));
		Vector final_v1 = new Vector(((m1 - m2) * new_v1.getX() + (m2 * 2) * new_v2.getX()) / (m1 + m2), new_v1.getY());
		Vector final_v2 = new Vector(((m1 * 2) * new_v1.getX() + (m2 - m1) * new_v2.getX()) / (m1 + m2), new_v2.getY());
		double cosAngle = Math.cos(collision_angle);
		double sinAngle = Math.sin(collision_angle);
		ball1.set_speed(cosAngle * final_v1.getX() - sinAngle * final_v1.getY(),
				sinAngle * final_v1.getX() + cosAngle * final_v1.getY());
		ball2.set_speed(cosAngle * final_v2.getX() - sinAngle * final_v2.getY(),
				sinAngle * final_v2.getX() + cosAngle * final_v2.getY());

		/**************
		 * minimum translation distance to push balls apart after
		 *************/

		Vector posDiff = Vector.vectorSubtract(pos1, pos2);
		double d = Math.sqrt(Math.pow(posDiff.getX(), 2) + Math.pow(posDiff.getY(), 2));
		Vector mtd = new Vector(posDiff.getX() * (((r1 + r2) - d) / d), posDiff.getY() * (((r1 + r2) - d) / d));
		// computing inverse mass quantities
		double im1 = 1 / m1;
		double im2 = 1 / m2;
		// push-pull them apart based off their mass
		ball1.set_location(pos1.getX() + mtd.getX() * (im1 / (im1 + im2)),
				pos1.getY() + mtd.getY() * (im1 / (im1 + im2)));
		ball2.set_location(pos2.getX() - mtd.getX() * (im2 / (im1 + im2)),
				pos2.getY() - mtd.getY() * (im2 / (im1 + im2)));

	}

	/**************************************
	 * * Ball - Obstacle
	 ****************************/

	private void resolveCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {

		/*********
		 * new Velocity of two ball according to its direction
		 *********/
		Point2D.Double c = new Point2D.Double(ball.get_x(), ball.get_y());
		double angle = Math.toDegrees(Math.atan2(ball.get_velocity().getY(), ball.get_velocity().getX()));
		Vector N = new Vector();
		N = GetNormale(obstacle.get_depart(), obstacle.get_arrivee(), c);
		double normalAngle = Math.toDegrees(Math.atan2(N.getY(), N.getX()));
		angle = 2 * normalAngle - 180 - angle;
		double vx = Math.cos(Math.toRadians(angle)) * ball.get_speed();
		double vy = Math.sin(Math.toRadians(angle)) * ball.get_speed() * ObstacleLine.COR;
		ball.set_speed(vx, vy);

		/***
		 * move the ball in the direction of its respective velocity until they
		 * have only one point of intersection
		 ***/
		Point2D.Double p = ProjectionI(obstacle.get_depart(), obstacle.get_arrivee(), c);
		double dist = _controller.distance(c, p);
		if (dist < ball.get_radius()) {
			ball.set_location(ball.get_x(), ball.get_y() - (ball.get_radius() - dist));
		}

	}

	private Vector GetNormale(Point A, Point B, Point2D.Double C) {

		Vector u, AC, N;
		u = new Vector(B.x - A.x, B.y - A.y);
		AC = new Vector(C.x - A.x, C.y - A.y);
		float parenthesis = (float) (u.getX() * AC.getY() - u.getY() * AC.getX());
		N = new Vector(-u.getY() * (parenthesis), u.getX() * (parenthesis));
		float norme = (float) Math.sqrt(Math.pow(N.getX(), 2) + Math.pow(N.getY(), 2));
		N.setCartesian(N.getX() / norme, N.getY() / norme);
		return N;
	}

	Point2D.Double ProjectionI(Point A, Point B, Point2D.Double C) {

		Vector u = new Vector(B.x - A.x, B.y - A.y);
		Vector AC = new Vector(C.x - A.x, C.y - A.y);
		double ti = (u.getX() * AC.getX() + u.getY() * AC.getY()) / (u.getX() * u.getX() + u.getY() * u.getY());
		Point2D.Double I = new Point2D.Double(A.x + ti * u.getX(), A.y + ti * u.getY());
		return I;
	}

}