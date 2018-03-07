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
			double collision_angle = Math.atan2((ball1.get_y() - ball2.get_y()), (ball1.get_x() - ball2.get_x()));
			double speed1 = ball1.get_speed();
			double speed2 = ball2.get_speed();

			double direction_1 = Math.atan2(ball1.get_velocity().getY(), ball1.get_velocity().getX());
			double direction_2 = Math.atan2(ball2.get_velocity().getY(), ball2.get_velocity().getX());
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
			double vx0 = cosAngle * final_xspeed_1 - sinAngle * final_yspeed_1;
			double vy0 = sinAngle * final_xspeed_1 + cosAngle * final_yspeed_1;
			ball1.set_init_speed(vx0, vy0);

			vx0 = cosAngle * final_xspeed_2 - sinAngle * final_yspeed_2;
			vy0 = sinAngle * final_xspeed_2 + cosAngle * final_yspeed_2;
			ball2.set_init_speed(vx0, vy0);

			// get the mtd
			Point2D.Double posDiff = new Point2D.Double((ball1.get_x() - ball2.get_x()),
					(ball1.get_y() - ball2.get_y()));
			double d = Math.sqrt(posDiff.x * posDiff.x + posDiff.y * posDiff.y);

			// minimum translation distance to push balls apart after
			// intersecting
			double mtdx = posDiff.x * (((ball1.get_radius() + ball2.get_radius()) - d) / d);
			double mtdy = posDiff.y * (((ball1.get_radius() + ball2.get_radius()) - d) / d);
			// resolve intersection --
			// computing inverse mass quantities
			double im1 = 1 / ball1.get_mass();
			double im2 = 1 / ball2.get_mass();

			// push-pull them apart based off their mass
			double x0 = ball1.get_x() + mtdx * (im1 / (im1 + im2));
			double y0 = ball1.get_y() + mtdy * (im1 / (im1 + im2));
			ball1.set_init_location(x0, y0);

			ball1.set_time(0.01);
			x0 = ball2.get_x() - mtdx * (im2 / (im1 + im2));
			y0 = ball2.get_y() - mtdy * (im2 / (im1 + im2));
			ball2.set_init_location(x0, y0);
			ball2.set_time(0.01);

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
			double coefficientRestitution = 0.6;
			double mag = coefficientRestitution * ball.get_speed();

			double _vx0 = Math.cos(Math.toRadians(angle)) * mag;
			double _vy0 = Math.sin(Math.toRadians(angle)) * mag;
			ball.set_init_speed(_vx0, _vy0);
			ball.set_init_location(ball.get_location().getX(), ball.get_location().getY());
			ball.set_time(0.01);
		}
	}
}
