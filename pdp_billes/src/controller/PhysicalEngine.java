package controller;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import model.AnimationTimer;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import model.Vector;
import view.DrawingPanel;

public class PhysicalEngine {
	private Circuit _circuit;
	private Controller _controller;

	public PhysicalEngine(DrawingPanel dp, Circuit circuit) {
		_circuit = circuit;
		_controller = Controller.getInstance();
		run(dp);
	}

	/************************************
	 * Ball - Ball
	 ************************************/

	private void run(DrawingPanel dp) {
		AnimationTimer timer = new AnimationTimer(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Ball ball1 : _circuit.get_balls()) {
					ball1.step(_circuit.get_acceleration());
					for (ObstacleLine obstacle : _circuit.get_lines()) {
						if (_controller.checkCollisionBallObstacle(ball1, obstacle))
							resolveCollisionBallObstacle(ball1, obstacle);
					}
					for (Ball ball2 : _circuit.get_balls()) {
						if (ball2.get_x() != ball1.get_x() && ball2.get_y() != ball1.get_y())
							if (_controller.checkCollisionBallBall(ball1, ball2))
								resolveCollisionBallBall(ball1, ball2);
					}
				}
				dp.repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		});
		timer.start();
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

	public void resolveCollisionBallBall(Ball ball1, Ball ball2) {
		double vx1, vy1, vx2, vy2; // these velocities are after the collision
		double v1, v2; // these are the velocities before the collision, these
						// are magnitudes
		double vxr1, vxr2; // these are just notations, however they mean vx
							// rotated
		double theta1, theta2;
		double phi;
		double m1, m2; // masses of the balls
		m1 = ball1.get_mass();
		m2 = ball2.get_mass();
		theta1 = ball1.get_velocity().getTeta();
		theta2 = ball2.get_velocity().getTeta();
		v1 = ball1.get_speed();
		v2 = ball2.get_speed();

		// this is an angle when the two balls touch. angle between the line
		// which connects two ball and x axis
		phi = angleCollisionBall(ball1, ball2);

		vxr1 = (m2 * v2 * Math.cos(theta2 - phi) * (1 + Ball.COR) + (m1 - m2 * Ball.COR) * v1 * Math.cos(theta1 - phi))
				/ (m1 + m2);
		vxr2 = (m1 * v1 * Math.cos(theta1 - phi) * (1 + Ball.COR) + (m2 - Ball.COR * m1) * v2 * Math.cos(theta2 - phi))
				/ (m1 + m2);

		vx1 = vxr1 * Math.cos(phi) + v1 * Math.sin(theta1 - phi) * Math.cos(phi + Math.PI / 2);
		vy1 = vxr1 * Math.sin(phi) + v1 * Math.sin(theta1 - phi) * Math.sin(phi + Math.PI / 2);
		vx2 = vxr2 * Math.cos(phi) + v2 * Math.sin(theta2 - phi) * Math.cos(phi + Math.PI / 2);
		vy2 = vxr2 * Math.sin(phi) + v2 * Math.sin(theta2 - phi) * Math.sin(phi + Math.PI / 2);

		ball1.set_speed(vx1, vy1);
		ball2.set_speed(vx2, vy2);
	}

	/**************** Version 2 Ball - Ball *************************/
	/*
	 * private void resolveCollisionBallBall(Ball ball1, Ball ball2) {
	 * 
	 * double collision_angle = angleCollisionBall(ball1,ball2);
	 * 
	 * double speed1 = ball1.get_speed(); double speed2 = ball2.get_speed();
	 * 
	 * double direction_1 = ball1.get_velocity().getTeta(); double direction_2 =
	 * ball2.get_velocity().getTeta();
	 * 
	 * double new_xspeed_1 = speed1 * Math.cos(direction_1 - collision_angle);
	 * double new_yspeed_1 = speed1 * Math.sin(direction_1 - collision_angle);
	 * double new_xspeed_2 = speed2 * Math.cos(direction_2 - collision_angle);
	 * double new_yspeed_2 = speed2 * Math.sin(direction_2 - collision_angle);
	 * 
	 * double final_xspeed_1 = ((ball1.get_mass() - ball2.get_mass()) *
	 * new_xspeed_1 + (ball2.get_mass() + ball2.get_mass()) * new_xspeed_2) /
	 * (ball1.get_mass() + ball2.get_mass()); double final_xspeed_2 =
	 * ((ball1.get_mass() * 2) * new_xspeed_1 + (ball2.get_mass() -
	 * ball1.get_mass()) * new_xspeed_2) / (ball1.get_mass() +
	 * ball2.get_mass()); double final_yspeed_1 = new_yspeed_1; double
	 * final_yspeed_2 = new_yspeed_2;
	 * 
	 * double cosAngle = Math.cos(collision_angle); double sinAngle =
	 * Math.sin(collision_angle);
	 * 
	 * double vx = cosAngle * final_xspeed_1 - sinAngle * final_yspeed_1; double
	 * vy = sinAngle * final_xspeed_1 + cosAngle * final_yspeed_1;
	 * ball1.set_speed(vx, vy);
	 * 
	 * vx = cosAngle * final_xspeed_2 - sinAngle * final_yspeed_2; vy = sinAngle
	 * * final_xspeed_2 + cosAngle * final_yspeed_2; ball2.set_speed(vx, vy);
	 * 
	 * 
	 * // get the mtd Vector posDiff = new Vector(); posDiff =
	 * Vector.vectorSubtract(ball1.get_location(),ball2.get_location()); double
	 * d = posDiff.getR();
	 * 
	 * // minimum translation distance to push balls apart after
	 * 
	 * // intersecting double mtdx = posDiff.getX() * (((ball1.get_radius() +
	 * ball2.get_radius()) - d) / d); double mtdy = posDiff.getY() *
	 * (((ball1.get_radius() + ball2.get_radius()) - d) / d); // resolve
	 * intersection -- // computing inverse mass quantities double im1 = 1 /
	 * ball1.get_mass(); double im2 = 1 / ball2.get_mass();
	 * 
	 * // push-pull them apart based off their mass double x0 = ball1.get_x() +
	 * mtdx * (im1 / (im1 + im2)); double y0 = ball1.get_y() + mtdy * (im1 /
	 * (im1 + im2)); ball1.set_location(x0, y0);
	 * 
	 * x0 = ball2.get_x() - mtdx * (im2 / (im1 + im2)); y0 = ball2.get_y() -
	 * mtdy * (im2 / (im1 + im2)); ball2.set_location(x0, y0);
	 * 
	 * }
	 */

	/**************************************
	 * Ball - Obstacle
	 ****************************/

	/**************** Version 1 Obstacle - Ball *************************/

	Vector GetNormale(Point A, Point B, Point2D.Double C) {

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
		double _vx = Math.cos(Math.toRadians(angle)) * ball.get_speed() * ObstacleLine.COR;
		double _vy = Math.sin(Math.toRadians(angle)) * ball.get_speed() * ObstacleLine.COR;
		ball.set_speed(_vx, _vy);
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
