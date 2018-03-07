package controller;

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
				for (Ball ball : _circuit.get_balls()) {
					ball.step();
					for (ObstacleLine obstacle : _circuit.get_lines()) {
						resolveCollisionBallObstacle(ball, obstacle);
					}
					for (Ball ball2 : _circuit.get_balls()) {
						if (ball2.get_x() != ball.get_x() && ball2.get_y() != ball.get_y())
							resolveCollisionBallBall(ball, ball2);
					}
				}
				dp.repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		});
		timer.start();
	}

	private void resolveCollisionBallBall(Ball ball1, Ball ball2) {
		if (_controller.checkCollisionBallBall(ball1, ball2)) {
			double vx1, vy1, vx2, vy2; // these velocities are after the
										// collision
			double v1, v2; // these are the velocities before the collision,
							// these are magnitudes
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
			phi = angleWhenBallsTouch(ball1, ball2);

			vxr1 = (m2 * v2 * Math.cos(theta2 - phi) * (1 + Ball.COR)
					+ (m1 - m2 * Ball.COR) * v1 * Math.cos(theta1 - phi)) / (m1 + m2);
			vxr2 = (m1 * v1 * Math.cos(theta1 - phi) * (1 + Ball.COR)
					+ (m2 - Ball.COR * m1) * v2 * Math.cos(theta2 - phi)) / (m1 + m2);

			vx1 = vxr1 * Math.cos(phi) + v1 * Math.sin(theta1 - phi) * Math.cos(phi + Math.PI / 2);
			vy1 = vxr1 * Math.sin(phi) + v1 * Math.sin(theta1 - phi) * Math.sin(phi + Math.PI / 2);
			vx2 = vxr2 * Math.cos(phi) + v2 * Math.sin(theta2 - phi) * Math.cos(phi + Math.PI / 2);
			vy2 = vxr2 * Math.sin(phi) + v2 * Math.sin(theta2 - phi) * Math.sin(phi + Math.PI / 2);

			ball1.set_init_speed(vx1, vy1);
			ball2.set_init_speed(vx2, vy2);
			ball1.set_init_location(ball1.get_location().getX() + vx1, ball1.get_location().getY() + vy1);
			ball2.set_init_location(ball2.get_location().getX() + vx2, ball2.get_location().getY() + vy2);
			ball1.set_time(0);
			ball2.set_time(0);

		}
	}

	private double angleWhenBallsTouch(Ball ball1, Ball ball2) {
		if (_controller.checkCollisionBallBall(ball1, ball2)) {
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

		else
			return -1;
	}

	/*************************************
	 * Ball - Obstacle
	 ****************************/

	private void resolveCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		if (_controller.checkCollisionBallObstacle(ball, obstacle)) {
			Point2D.Double a, b, c;
			int dir = 1;
			double angle = Math.toDegrees(Math.atan2(ball.get_velocity().getX(), ball.get_velocity().getY()));

			if (obstacle.get_depart().getY() > obstacle.get_arrivee().getY()) {
				a = new Point2D.Double(obstacle.get_arrivee().getX() / Circuit.get_scale(),
						obstacle.get_arrivee().getY() / Circuit.get_scale());
				b = new Point2D.Double(obstacle.get_depart().getX() / Circuit.get_scale(),
						obstacle.get_depart().getY() / Circuit.get_scale());
				c = new Point2D.Double(obstacle.get_arrivee().getX() / Circuit.get_scale(),
						obstacle.get_depart().getY() / Circuit.get_scale());
			} else {
				a = new Point2D.Double(obstacle.get_depart().getX() / Circuit.get_scale(),
						obstacle.get_depart().getY() / Circuit.get_scale());
				b = new Point2D.Double(obstacle.get_arrivee().getX() / Circuit.get_scale(),
						obstacle.get_arrivee().getY() / Circuit.get_scale());
				c = new Point2D.Double(obstacle.get_depart().getX() / Circuit.get_scale(),
						obstacle.get_arrivee().getY() / Circuit.get_scale());
			}
			if (a.getX() > b.getX())
				dir = -1;
			double tetha = Math.toDegrees(Math.atan(_controller.distance(a, c) / _controller.distance(b, c)));
			int normalAngle = (int) (90 + (dir * tetha));
			angle = 2 * normalAngle - 180 - angle;
			double mag = 0.6 * ball.get_speed();

			double _vx0 = Math.cos(Math.toRadians(angle)) * mag;
			double _vy0 = Math.sin(Math.toRadians(angle)) * mag;
			ball.set_init_speed(_vx0, _vy0);
			ball.set_init_location(ball.get_location().getX(), ball.get_location().getY());
			ball.set_time(0.01);
		}
	}
}
