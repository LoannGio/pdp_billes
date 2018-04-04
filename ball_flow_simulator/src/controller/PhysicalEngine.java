
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
	private AnimationTimer _timer;
	private Quadtree _quad;

	public PhysicalEngine(Circuit circuit) {
		_circuit = circuit;
		_controller = Controller.getInstance();
		_quad = new Quadtree(0, new Rectangle(0, 0, (int) _controller.getDimensionsPlan().getWidth(),
				(int) _controller.getDimensionsPlan().getHeight()));
	}

	/**
	 * This method moves balls of the circuit. Checks and resolves collisions
	 * between balls by using a Quadtree for space partitioning. Checks and
	 * resolves collisions between balls and obstacles. Updates program's wiew.
	 */
	public void run(DrawingPanel dp) {
		_timer = new AnimationTimer(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_quad.clear();
				ArrayList<Ball> returnObjects = new ArrayList<Ball>();
				for (Ball ball : _circuit.get_balls()) {
					if (!ballIsOutOfCircuit(ball)) {
						_quad.insert(ball);
						returnObjects.clear();
						_quad.retrieve(returnObjects, ball);
						for (Ball ball2 : returnObjects) {
							if (ball2 != ball) {
								if (_controller.checkCollisionBallBall(ball, ball2)) {
									resolveCollisionBallBall(ball, ball2);
								}
							}
						}
						for (ObstacleLine obstacle : _circuit.get_lines()) {
							if (_controller.checkCollisionBallObstacle(ball, obstacle)) {
								resolveCollisionBallObstacle(ball, obstacle);
							}
						}
						ball.step(_circuit.get_acceleration());
						dp.drawTraceBuffer(ball.get_track().get(ball.get_track().size() - 1));
					}
				}
				dp.repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		});
		_timer.start();
	}

	public void stop() {
		_timer.stop();
	}

	/**
	 * This method determines if a ball is completely out of the circuit, by
	 * comparing the position of the ball (taking its radius into account) with
	 * dimensions of the circuit.
	 */
	private boolean ballIsOutOfCircuit(Ball b) {
		double bx = b.get_x();
		double by = b.get_y();
		double br = b.get_radius();
		int dpwidth = _circuit.get_width();
		int dpheight = _circuit.get_height();
		if (bx - br > dpwidth || bx + br < 0 || by - br > dpheight || by + br < 0)
			return true;
		return false;

	}

	/**
	 * This method calculates the speed of both balls after their collision, by
	 * using formulas of elastic impact. Balls are repositioned by taking a
	 * minimum distance and their mass into account. The new speed vectors are
	 * calculated from direction and collision angle of both balls.
	 */

	private void resolveCollisionBallBall(Ball ball1, Ball ball2) {
		double collision_angle = Math.atan2((ball2.get_y() - ball1.get_y()), (ball2.get_x() - ball1.get_x()));
		double direction_1 = Math.atan2(ball1.get_velocity().getY(), ball1.get_velocity().getX());
		double direction_2 = Math.atan2(ball2.get_velocity().getY(), ball2.get_velocity().getX());
		Vector pos1 = ball1.get_location();
		Vector pos2 = ball2.get_location();
		double v1 = ball1.get_speed();
		double v2 = ball2.get_speed();
		double m1 = ball1.get_mass();
		double m2 = ball2.get_mass();
		double r1 = ball1.get_radius();
		double r2 = ball2.get_radius();

		/*
		 * Speeds are calculated after impact, by using direction and collision
		 * angle from both balls.
		 */
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

		// Minimum distance for balls repositioning
		Vector posDiff = Vector.vectorSubtract(pos1, pos2);
		double d = Math.sqrt(Math.pow(posDiff.getX(), 2) + Math.pow(posDiff.getY(), 2));
		Vector mtd = new Vector(posDiff.getX() * (((r1 + r2) - d) / d), posDiff.getY() * (((r1 + r2) - d) / d));
		double im1 = 1 / m1;
		double im2 = 1 / m2;
		ball1.set_location(pos1.getX() + mtd.getX() * (im1 / (im1 + im2)),
				pos1.getY() + mtd.getY() * (im1 / (im1 + im2)));
		ball2.set_location(pos2.getX() - mtd.getX() * (im2 / (im1 + im2)),
				pos2.getY() - mtd.getY() * (im2 / (im1 + im2)));
	}

	/**
	 * This method resolves the collision between a ball and an obstacle. The
	 * collision is detected often when ball is overlapping on the obstacle, so
	 * the ball is correctly repositioned. Based on the incident angle and the
	 * perpendicular angle, the bouncing angle is calculated. This angle allows
	 * to calculate the new speed vector.
	 */
	private void resolveCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		Point2D.Double c = new Point2D.Double(ball.get_x(), ball.get_y());
		double angle = Math.toDegrees(Math.atan2(ball.get_velocity().getY(), ball.get_velocity().getX()));
		Vector N = new Vector();
		N = GetNormale(obstacle.get_begin(), obstacle.get_end(), c);
		Point2D.Double P = ProjectionI(obstacle.get_begin(),obstacle.get_end(), c);
		if((P.getX() > Math.min(obstacle.get_begin().getX(),obstacle.get_end().getX())
				 && P.getX() < Math.max(obstacle.get_begin().getX(),obstacle.get_end().getX())) ) {
			ReplaceBall(obstacle, ball);
			// projection is on obstacle
			double normalAngle = Math.toDegrees(Math.atan2(N.getY(), N.getX()));
			angle = 2 * normalAngle - 180 - angle;
			double vx = Math.cos(Math.toRadians(angle)) * ball.get_speed();
			double vy = Math.sin(Math.toRadians(angle)) * ball.get_speed();
			ball.set_speed(vx, vy * obstacle.getCOR());

		} else { // projection is not on obstacle
			int point = _controller.whereCollisionSegment(ball, obstacle);
			Point2D.Double A = new Point2D.Double(obstacle.get_begin().getX(), obstacle.get_begin().getY());
			Point2D.Double B = new Point2D.Double(obstacle.get_end().getX(), obstacle.get_end().getY());

			if (point == 2) { // ball collides with the begin point of the
								// obstacle
				Vector v1 = new Vector(A.getX() - c.getX(), A.getY() - c.getY());
				Point2D.Double haut = new Point2D.Double(A.getX() + (v1.getY() * 15), A.getY() - (v1.getX() * 15));
				Point2D.Double bas = new Point2D.Double(A.getX() - (v1.getY() * 15), A.getY() + (v1.getX() * 15));
				Point ob_begin = new Point((int) haut.getX(), (int) haut.getY());
				Point ob_end = new Point((int) bas.getX(), (int) bas.getY());
				ObstacleLine obtmp = new ObstacleLine(ob_begin, ob_end, 1);
				resolveCollisionBallObstacle(ball, obtmp);
			}

			if (point == 3) { // ball collides with the end point of the
								// obstacle
				Vector v1 = new Vector(B.getX() - c.getX(), B.getY() - c.getY());
				Point2D.Double haut = new Point2D.Double(B.getX() + v1.getY(), B.getY() - v1.getX());
				Point2D.Double bas = new Point2D.Double(B.getX() - v1.getY(), B.getY() + v1.getX());
				Point ob_begin = new Point((int) haut.getX(), (int) haut.getY());
				Point ob_end = new Point((int) bas.getX(), (int) bas.getY());
				ObstacleLine obtmp = new ObstacleLine(ob_begin, ob_end, 1);
				resolveCollisionBallObstacle(ball, obtmp);
			}

		}
	}

	/*
	 * The orthogonal vector of the tangent of a point to project is calculated.
	 * Returns the normal vector of segment AB going through point C.
	 */
	private Vector GetNormale(Point A, Point B, Point2D.Double C) {
		Vector AB, AC, N;
		AB = new Vector(B.x - A.x, B.y - A.y);
		AC = new Vector(C.x - A.x, C.y - A.y);
		float parenthesis = (float) (AB.getX() * AC.getY() - AB.getY() * AC.getX());
		N = new Vector(-AB.getY() * (parenthesis), AB.getX() * (parenthesis));
		float norm = (float) Math.sqrt(Math.pow(N.getX(), 2) + Math.pow(N.getY(), 2));
		N.setCartesian(N.getX() / norm, N.getY() / norm);
		if (Double.isNaN(N.getX()) || Double.isNaN(N.getY()))
			N.setCartesian(0, 0);
		return N;
	}

	/*
	 * This method returns the perpendicular projection of a point in relation
	 * to a straight line.
	 */
	private Point2D.Double ProjectionI(Point A, Point B, Point2D.Double C) {
		Vector u = new Vector(B.x - A.x, B.y - A.y);
		Vector AC = new Vector(C.x - A.x, C.y - A.y);
		double ti = (u.getX() * AC.getX() + u.getY() * AC.getY()) / (u.getX() * u.getX() + u.getY() * u.getY());
		Point2D.Double I = new Point2D.Double(A.x + ti * u.getX(), A.y + ti * u.getY());
		return I;
	}

	/**
	 * The ball is repositioned based on its distance with the obstacle. The
	 * events of vertical, horizontal or diagonal obstacle are handled. The ball
	 * is then repositioned based on its center position in relation to its
	 * perpendicular projection on the obstacle. A contact point is obtained,
	 * where the distance between the ball and the obstacle is equal to the
	 * ball's radius.
	 */
	private void ReplaceBall(ObstacleLine obstacle, Ball ball) {
		
		Point2D.Double c = new Point2D.Double(ball.get_x(), ball.get_y());
		Point2D.Double p = ProjectionI(obstacle.get_begin(), obstacle.get_end(), c);
		double dist = _controller.distance(c, p);
		if (dist < ball.get_radius()) {
				if (p.getY() > c.getY())
					if (p.getX() > c.getX())
						ball.set_location(ball.get_x() - (ball.get_radius() - dist),
								ball.get_y() - (ball.get_radius() - dist));
					else
						ball.set_location(ball.get_x() + (ball.get_radius() - dist),
								ball.get_y() - (ball.get_radius() - dist));

				else 
				if (p.getX() > c.getX())
					ball.set_location(ball.get_x() - (ball.get_radius() - dist),
							ball.get_y() + (ball.get_radius() - dist));
				else
					ball.set_location(ball.get_x() + (ball.get_radius() - dist),
							ball.get_y() + (ball.get_radius() - dist));

		}
	}
}
