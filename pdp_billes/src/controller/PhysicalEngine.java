
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

	/*
	 * Cette fonction permet de deplacer les billes Verifier et resoudre les
	 * collisions entre deux billes en utilisant un quadTree pour le voisinage
	 * Verifier et resoudre les collisions entre bille obstacle actualiser la
	 * vue du programme.
	 */

	public void run(DrawingPanel dp) {
		timer = new AnimationTimer(new ActionListener() {
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
						dp.repaint();
						ball.step(_circuit.get_acceleration());
						dp.drawTraceBuffer(ball.get_track().get(ball.get_track().size() - 1));
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

	/*
	 * Cette fonction permet de determiner si une bille est completement en
	 * dehors du circuit. Pour cela, elle compare la position de la bille (en
	 * prenant en compte son rayon) avec la hauteur et la largeur du circuit.
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

	/*
	 * Cette fonction permet de calculer les vitesses des deux billes après une
	 * collsision en utilisant les formules du choc elastique elle replace les
	 * billes selon une distance minimum et selon leur masse A partir des
	 * directions des deux billes et l'angle de collision on trouve les nouveaux
	 * vecteurs de vitesse des billes.
	 */

	private void resolveCollisionBallBall(Ball ball1, Ball ball2) {
		// Variables pour le calcul des vitesses
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

		// On calcule les vitesses après le choc selon les directions et
		// l'angle de collision du deux billes
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

		// La distance minimum pour decaler les billes dans le bon endroit
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

	/*
	 * Cette fonction gere la collision entre une bille et un obstacle. Bien
	 * souvent on detecte la collision alors que la bille chevauche l'obstacle,
	 * on la replace donc sur l'obstacle. A partir de l'angle d'arrivee et la
	 * normale, on trouve l'angle de rebond. Ce denier nous permet de calculer
	 * le nouveau vecteur vitesse. Enfin on met a jour le vecteur vitesse de la
	 * balle avec set_speed.
	 */

	private void resolveCollisionBallObstacle(Ball ball, ObstacleLine obstacle) {
		Point2D.Double c = new Point2D.Double(ball.get_x(), ball.get_y());
		ReplaceBall(obstacle, ball, c);
		double angle = Math.toDegrees(Math.atan2(ball.get_velocity().getY(), ball.get_velocity().getX()));
		Vector N = new Vector();
		N = GetNormale(obstacle.get_begin(), obstacle.get_end(), c);
		double normalAngle = Math.toDegrees(Math.atan2(N.getY(), N.getX()));
		angle = 2 * normalAngle - 180 - angle;
		double vx = Math.cos(Math.toRadians(angle)) * ball.get_speed();
		double vy = Math.sin(Math.toRadians(angle)) * ball.get_speed();
		ball.set_speed(vx, vy * obstacle.getCOR());

	}

	/*
	 * On calcule le vecteur orthogonal a la tangente d'un point a projeter
	 */
	private Vector GetNormale(Point A, Point B, Point2D.Double C) {
		Vector u, AC, N;
		u = new Vector(B.x - A.x, B.y - A.y);
		AC = new Vector(C.x - A.x, C.y - A.y);
		float parenthesis = (float) (u.getX() * AC.getY() - u.getY() * AC.getX());
		N = new Vector(-u.getY() * (parenthesis), u.getX() * (parenthesis));
		float norme = (float) Math.sqrt(Math.pow(N.getX(), 2) + Math.pow(N.getY(), 2));
		N.setCartesian(N.getX() / norme, N.getY() / norme);
		if (Double.isNaN(N.getX()) || Double.isNaN(N.getY()))
			N.setCartesian(0, 0);
		return N;
	}

	/*
	 * Cette fonction renvoie la projection perpondiculaire d'un point par
	 * rapport a une droite
	 */
	private Point2D.Double ProjectionI(Point A, Point B, Point2D.Double C) {
		Vector u = new Vector(B.x - A.x, B.y - A.y);
		Vector AC = new Vector(C.x - A.x, C.y - A.y);
		double ti = (u.getX() * AC.getX() + u.getY() * AC.getY()) / (u.getX() * u.getX() + u.getY() * u.getY());
		Point2D.Double I = new Point2D.Double(A.x + ti * u.getX(), A.y + ti * u.getY());
		return I;
	}

	/*
	 * On replace la bille selon leur distance avec l'obstacle On gère les cas
	 * ou l'obstacle est verticale, horizontale, diagonale On decale la bille
	 * selon la position du center par rapport à sa projection perpondiculaire
	 * sur l'obstacle. On obtien un seul point de contacte ou la distance entre
	 * la bille et l'obstacle est egale rayon
	 */
	private void ReplaceBall(ObstacleLine obstacle, Ball ball, Point2D.Double c) {
		Point2D.Double p = ProjectionI(obstacle.get_begin(), obstacle.get_end(), c);
		double dist = _controller.distance(c, p);
		if (dist < ball.get_radius()) {
			if (p.getX() == c.getX() && p.getY() == c.getY()) {
				ball.set_location(ball.get_x() - (ball.get_radius() - dist), ball.get_y());
			} else {
				if (p.getY() > c.getY())
					if (p.getX() == c.getX())
						ball.set_location(ball.get_x(), ball.get_y() - (ball.get_radius() - dist));
					else if (p.getX() > c.getX())
						ball.set_location(ball.get_x() - (ball.get_radius() - dist),
								ball.get_y() - (ball.get_radius() - dist));
					else
						ball.set_location(ball.get_x() + (ball.get_radius() - dist),
								ball.get_y() - (ball.get_radius() - dist));

				else if (p.getX() == c.getX())
					ball.set_location(ball.get_x(), ball.get_y() + (ball.get_radius() - dist));
				else if (p.getX() > c.getX())
					ball.set_location(ball.get_x() - (ball.get_radius() - dist),
							ball.get_y() + (ball.get_radius() - dist));
				else
					ball.set_location(ball.get_x() + (ball.get_radius() - dist),
							ball.get_y() + (ball.get_radius() - dist));
			}

		}
	}
}
