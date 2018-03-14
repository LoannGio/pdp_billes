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
				// Rectangle trace = null;
				// ArrayList<Point> listTrace;

				// on vide le quadtree
				_quad.clear();

				ArrayList<Ball> returnObjects = new ArrayList<Ball>();

				for (Ball ball : _circuit.get_balls()) {
					if(!ballIsOutOfPanel(ball, dp)) {
						_quad.insert(ball);
						int xSquare = (int) ball.get_x() - ball.get_radius();
						int ySquare = (int) ball.get_y() - ball.get_radius();
						int dimSquare = 2 * ball.get_radius();

						square = new Rectangle(xSquare - 50, ySquare - 50, dimSquare + 100, dimSquare + 100);

						/*
						 * listTrace = ball.get_trace(); int xTrace = 0; int yTrace
						 * = 0; int dimTrace = 0; if(listTrace.size() > 50) { xTrace
						 * = listTrace.get(listTrace.size()-51).x; yTrace =
						 * listTrace.get(listTrace.size()-51).y; dimTrace = 1; trace
						 * = new Rectangle(xTrace, yTrace, dimTrace, dimTrace);
						 * System.out.println("1"); }
						 */

						dp.repaint(square);
						/*
						 * if(trace != null) { dp.repaint(trace);
						 * System.out.println("2"); }
						 */
						ball.step(_circuit.get_acceleration());
						returnObjects.clear();
						_quad.retrieve(returnObjects, ball);
						for (ObstacleLine obstacle : _circuit.get_lines()) {
							if (_controller.checkCollisionBallObstacle(ball, obstacle)) {
								while (_controller.checkCollisionBallObstacle(ball, obstacle)
										&& ball.get_velocity().getY() > 0) {
									ball.set_location(ball.get_location().getX(), ball.get_location().getY() - 0.1);
								}
								resolveCollisionBallObstacle(ball, obstacle);
							}
						}
						// System.out.println(cpt);
						for (Ball ball2 : returnObjects) {
							if (ball2 != ball) {
								if (_controller.checkCollisionBallBall(ball, ball2)) {
									resolveCollisionBallBall(ball, ball2);
								}
							}
						}
						square.setRect(xSquare - 50, ySquare - 50, dimSquare + 100, dimSquare + 100);
						// dp.repaint(square);

						/*
						 * if(listTrace.size() > 50) { xTrace =
						 * listTrace.get(listTrace.size()-51).x; yTrace =
						 * listTrace.get(listTrace.size()-51).y; if(trace != null) {
						 * trace.setRect(xTrace, yTrace, dimTrace, dimTrace);
						 * System.out.println("3"); dp.repaint(trace);
						 * System.out.println("4"); } }
						 */
					}

				}
				// dp.repaint();
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
		if(bx - br > dpx + dpwidth || bx + br < dpx || by - br > dpy + dpheight || by + br < dpy)
			return true;
		return false;
		
	}

	/**************** Version 1 Ball - Ball *************************/

	public double angleCollisionBall(Ball ball1, Ball ball2) {
		Vector v;
		double d1 = ball1.get_location().getR();
		double d2 = ball2.get_location().getR();
		Vector loc1 = ball1.get_location();
		Vector loc2 = ball2.get_location();

		if (d1 > d2)
			v = Vector.vectorSubtract(loc1, loc2);
		else
			v = Vector.vectorSubtract(loc2, loc1);
		return v.getTeta();
	}

	/*
	 * public void resolveCollisionBallBall(Ball ball1, Ball ball2) { double
	 * vx1, vy1, vx2, vy2; // these velocities are after the collision double
	 * v1, v2; // these are the velocities before the collision, these // are
	 * magnitudes double vxr1, vxr2; // these are just notations, however they
	 * mean vx // rotated double theta1, theta2; double phi; double m1, m2; //
	 * masses of the balls m1 = ball1.get_mass(); m2 = ball2.get_mass(); theta1
	 * = ball1.get_velocity().getTeta(); theta2 =
	 * ball2.get_velocity().getTeta(); v1 = ball1.get_speed(); v2 =
	 * ball2.get_speed();
	 * 
	 * // this is an angle when the two balls touch. angle between the line //
	 * which connects two ball and x axis phi = angleCollisionBall(ball1,
	 * ball2);
	 * 
	 * vxr1 = (m2 * v2 * Math.cos(theta2 - phi) * (1 + Ball.COR) + (m1 - m2 *
	 * Ball.COR) * v1 * Math.cos(theta1 - phi)) / (m1 + m2); vxr2 = (m1 * v1 *
	 * Math.cos(theta1 - phi) * (1 + Ball.COR) + (m2 - Ball.COR * m1) * v2 *
	 * Math.cos(theta2 - phi)) / (m1 + m2);
	 * 
	 * vx1 = vxr1 * Math.cos(phi) + v1 * Math.sin(theta1 - phi) * Math.cos(phi +
	 * Math.PI / 2); vy1 = vxr1 * Math.sin(phi) + v1 * Math.sin(theta1 - phi) *
	 * Math.sin(phi + Math.PI / 2); vx2 = vxr2 * Math.cos(phi) + v2 *
	 * Math.sin(theta2 - phi) * Math.cos(phi + Math.PI / 2); vy2 = vxr2 *
	 * Math.sin(phi) + v2 * Math.sin(theta2 - phi) * Math.sin(phi + Math.PI /
	 * 2);
	 * 
	 * ball1.set_speed(vx1, vy1); ball2.set_speed(vx2, vy2); }
	 */

	/**************** Version 2 Ball - Ball *************************/

	private void resolveCollisionBallBall(Ball ball1, Ball ball2) {

		double collision_angle = angleCollisionBall(ball1, ball2);

		double speed1 = ball1.get_speed();
		double speed2 = ball2.get_speed();
		double direction_1 = ball1.get_velocity().getTeta();
		double direction_2 = ball2.get_velocity().getTeta();

		double new_xspeed_1 = speed1 * Math.cos(direction_1 - collision_angle);
		double new_yspeed_1 = speed1 * Math.sin(direction_1 - collision_angle);
		double new_xspeed_2 = speed2 * Math.cos(direction_2 - collision_angle);
		double new_yspeed_2 = speed2 * Math.sin(direction_2 - collision_angle);
		double final_xspeed_1 = ((ball1.get_mass() - ball2.get_mass()) * new_xspeed_1
				+ (ball2.get_mass() + ball2.get_mass()) * new_xspeed_2) / (ball1.get_mass() + ball2.get_mass());
		double final_xspeed_2 = ((ball1.get_mass() * 2) * new_xspeed_1
				+ (ball2.get_mass() - ball1.get_mass()) * new_xspeed_2) / (ball1.get_mass() + ball2.get_mass());
		double final_yspeed_1 = new_yspeed_1;
		double final_yspeed_2 = new_yspeed_2;
		double cosAngle = Math.cos(collision_angle);
		double sinAngle = Math.sin(collision_angle);

		double vx = cosAngle * final_xspeed_1 - sinAngle * final_yspeed_1;
		double vy = sinAngle * final_xspeed_1 + cosAngle * final_yspeed_1;
		ball1.set_speed(vx, vy);
		vx = cosAngle * final_xspeed_2 - sinAngle * final_yspeed_2;
		vy = sinAngle * final_xspeed_2 + cosAngle * final_yspeed_2;
		ball2.set_speed(vx, vy);

		// get the mtd
		Vector posDiff = new Vector();
		posDiff = Vector.vectorSubtract(ball1.get_location(), ball2.get_location());
		double d = posDiff.getR();

		// minimum translation distance to push balls apart after

		// intersecting
		double mtdx = posDiff.getX() * (((ball1.get_radius() + ball2.get_radius()) - d) / d);
		double mtdy = posDiff.getY() * (((ball1.get_radius() + ball2.get_radius()) - d) / d);
		// resolve intersection --
		// computing inverse mass quantities
		double im1 = 1 / ball1.get_mass();
		double im2 = 1 / ball2.get_mass();
		// push-pull them apart based off their mass
		double x0 = ball1.get_x() + mtdx * (im1 / (im1 + im2));
		double y0 = ball1.get_y() + mtdy * (im1 / (im1 + im2));
		ball1.set_location(x0, y0);
		x0 = ball2.get_x() - mtdx * (im2 / (im1 + im2));
		y0 = ball2.get_y() - mtdy * (im2 / (im1 + im2));
		ball2.set_location(x0, y0);

	}

	/**************************************
	 * Ball - Obstacle
	 ****************************/

	/**************** Version 1 Obstacle - Ball *************************/

	private Vector GetNormale(Point A, Point B, Point2D.Double C) {

		Vector u, AC, N;
		u = new Vector(B.x - A.x, B.y - A.y);
		AC = new Vector(C.x - A.x, C.y - A.y);
		float parenthesis = (float) (u.getX() * AC.getY() - u.getY() * AC.getX());
		N = new Vector(-u.getY() * (parenthesis), u.getX() * (parenthesis));
		float norme = (float) N.getR();
		N.setCartesian(N.getX() / norme, N.getY() / norme);
		return N;
	}

	private void resolveCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		double angle = Math.toDegrees(ball.get_velocity().getTeta());
		Vector N = new Vector();
		N = GetNormale(obstacle.get_depart(), obstacle.get_arrivee(), new Point2D.Double(ball.get_x(), ball.get_y()));
		double normalAngle = Math.toDegrees(N.getTeta());
		angle = 2 * normalAngle - 180 - angle;
		double vx = Math.cos(Math.toRadians(angle)) * ball.get_speed() * ObstacleLine.COR;
		double vy = Math.sin(Math.toRadians(angle)) * ball.get_speed() * ObstacleLine.COR;

		ball.set_speed(vx, vy);
	}

	/*********************
	 * Version 2 Obstacle - Ball
	 *******************************/

	/*
	 * public void resolveCollisionBallObstacle(Ball ball, ObstacleLine
	 * obstacle) { Vector N = new Vector(); N =
	 * this.GetNormale(obstacle.get_depart(), obstacle.get_arrivee(), new
	 * Point2D.Double(ball.get_x(), ball.get_y())); Vector v = new Vector(); v =
	 * CalculerVecteurV2(ball.get_velocity(), N); ball.set_speed(v.getX() *
	 * ObstacleLine.COR, v.getY() * ObstacleLine.COR); }
	 * 
	 * Vector CalculerVecteurV2(Vector v, Vector N) { float pscal = (float)
	 * (Vector.dotProduct(v, N)); Vector v2 = new Vector(v.getX() - 2 * pscal *
	 * N.getX(), v.getY() - 2 * pscal * N.getY()); return v2; }
	 */

}
