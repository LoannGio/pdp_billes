package model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import controller.Controller;

public class Ball {
	private double _x;
	private double _y;
	private int _radius;

	// Masse de la balle
	private double _mass;
	private double _vx; // vitesse
	private double _vy;
	private double _ax, _ay; // acceleration
	private double _x0, _y0; // position initiale
	private double _vx0; // vitesse initiale
	private double _vy0;
	private double _fx, _fy; // force totale
	private double _px, _py, _rx, _ry; // force P et R
	private double _t; // temps relatif
	private ArrayList<Point> _trace;

	public Ball(double x, double y, int radius, double mass) {
		_x = x;
		_y = y;
		_radius = radius;
		_mass = mass;
		setAcceleration();
		_vx0 = _vx = 0; // vitesse initiale 0 m/s
		_vy0 = _vy = 0;
		_x0 = _x; // position initiale
		_y0 = _y; // (Panel.YMIN + Panel.YMAX)/2;
		_t = 0;
		_trace = new ArrayList<Point>();
	}

	public void setAcceleration() {
		double inclinaison = Controller.getInstance().get_defaultInclinaison();
		_px = -_mass * Circuit.get_gravitation() * Math.cos(Math.toRadians(inclinaison));
		_py = _mass * Circuit.get_gravitation() * Math.sin(Math.toRadians(inclinaison));
		_rx = -_px;
		_ry = 0;
		_fy = _py + _ry;
		_ax = _fx / _mass; // acceleration initiale
		_ay = _fy / _mass;
		_vx0 = _vx;
		_vy0 = _vy;
		_y0 = _y;
		_x0 = _x;
		_t = 0;
	}

	public ArrayList<Point> get_trace() {
		return _trace;
	}

	public void setAll(double x, double y, int radius, double mass, double inclinaison) {
		_x = x;
		_y = y;
		_radius = radius;
		_mass = mass;
		setAcceleration();
	}

	public Boolean contains(Point p) {
		double x = p.getX();
		double y = p.getY();
		double calc = Math.pow(x - _x, 2) + Math.pow(y - _y, 2);
		double radius_squared = Math.pow(_radius, 2);
		if (calc <= radius_squared)
			return true;
		return false;
	}

	public double get_x() {
		return _x;
	}

	public void set_x(int _x) {
		this._x = _x;
	}

	public double get_y() {
		return _y;
	}

	public void set_y(int _y) {
		this._y = _y;
	}

	public int get_radius() {
		return _radius;
	}

	public void set_radius(int _radius) {
		this._radius = _radius;
	}

	public double get_mass() {
		return this._mass;
	}

	public void set_mass(double _mass) {
		this._mass = _mass;
	}

	public double get_vx() {
		return _vx;
	}

	public void set_vx(double _vx) {
		this._vx = _vx;
	}

	public double get_vy() {
		return _vy;
	}

	public void set_vy(double _vy) {
		this._vy = _vy;
	}

	public double get_ax() {
		return _ax;
	}

	public void set_ax(double _ax) {
		this._ax = _ax;
	}

	public double get_ay() {
		return _ay;
	}

	public void set_ay(double _ay) {
		this._ay = _ay;
	}

	public double get_x0() {
		return _x0;
	}

	public void set_x0(double _x0) {
		this._x0 = _x0;
	}

	public double get_y0() {
		return _y0;
	}

	public void set_y0(double _y0) {
		this._y0 = _y0;
	}

	public double get_vx0() {
		return _vx0;
	}

	public void set_vx0(double _vx0) {
		this._vx0 = _vx0;
	}

	public double get_vy0() {
		return _vy0;
	}

	public void set_vy0(double _vy0) {
		this._vy0 = _vy0;
	}

	public double get_fx() {
		return _fx;
	}

	public void set_fx(double _fx) {
		this._fx = _fx;
	}

	public double get_fy() {
		return _fy;
	}

	public void set_fy(double _fy) {
		this._fy = _fy;
	}

	public double get_px() {
		return _px;
	}

	public void set_px(double _px) {
		this._px = _px;
	}

	public double get_py() {
		return _py;
	}

	public void set_py(double _py) {
		this._py = _py;
	}

	public double get_rx() {
		return _rx;
	}

	public void set_rx(double _rx) {
		this._rx = _rx;
	}

	public double get_ry() {
		return _ry;
	}

	public void set_ry(double _ry) {
		this._ry = _ry;
	}

	public double get_t() {
		return _t;
	}

	public void set_t(double _t) {
		this._t = _t;
	}

	public void set_x(double _x) {
		this._x = _x;
	}

	public void set_y(double _y) {
		this._y = _y;
	}

	public void step() {
		double scale = 40;
		int x = (int) _x;
		int y = (int) _y;
		this._vx = (this._vx0 + this._ax * this._t);
		this._vy = (this._vy0 + this._ay * this._t);
		double t2 = this._t * this._t;
		this._x = this._x0 / scale + this._vx0 * this._t + (this._ax * t2) / 2;
		this._y = this._y0 / scale + this._vy0 * this._t + (this._ay * t2) / 2;
		this._x *= scale;
		this._y *= scale;
		if (_x != x || _y != y)
			_trace.add(new Point((int) _x, (int) _y));
		this._t += AnimationTimer.MSSTEP;
	}

	public void resolveCollisionObstacle(ObstacleLine obstacle) {
		Point2D.Double a, b, c;
		int dir = 1;
		double angle = Math.toDegrees(Math.atan2(this._vy, this._vx));

		if (obstacle.get_depart().getY() > obstacle.get_arrivee().getY()) {
			a = new Point2D.Double(obstacle.get_arrivee().getX(), obstacle.get_arrivee().getY());
			b = new Point2D.Double(obstacle.get_depart().getX(), obstacle.get_depart().getY());
			c = new Point2D.Double(obstacle.get_arrivee().getX(), obstacle.get_depart().getY());
		} else {
			a = new Point2D.Double(obstacle.get_depart().getX(), obstacle.get_depart().getY());
			b = new Point2D.Double(obstacle.get_arrivee().getX(), obstacle.get_arrivee().getY());
			c = new Point2D.Double(obstacle.get_depart().getX(), obstacle.get_arrivee().getY());
		}
		if (a.getX() > b.getX())
			dir = -1;
		double tetha = Math.toDegrees(Math.atan(this.distance(a, c) / this.distance(b, c)));
		int normalAngle = (int) (90 + (dir * tetha));
		angle = 2 * normalAngle - 180 - angle;
		double coefficientRestitution = 0.9;
		double mag = coefficientRestitution * Math.hypot(this._vx, this._vy);
		this._vx0 = Math.cos(Math.toRadians(angle)) * mag;
		this._vy0 = Math.sin(Math.toRadians(angle)) * mag;
		// this._vx0 = Math.cos(Math.toRadians(tetha))*this.distance(a,b)/100;
		// this._vy0 = Math.sin(Math.toRadians(tetha))*this.distance(a,b)/100;
		this._x0 = this._x;
		this._y0 = this._y;
		this._t = 0.01;
	}

	private double distance(Point2D.Double point, Point2D.Double b) {
		return Math.sqrt(Math.pow((b.x - point.x), 2) + Math.pow((b.y - point.y), 2));
	}

	public void resolveCollisionBall(Ball ball) {

		double collision_angle = Math.atan2((ball.get_y() - this._y), (ball.get_x() - this._x));
		double speed1 = this._vy;
		double speed2 = ball.get_vy();

		double direction_1 = Math.atan2(this._vy, this._vx);
		double direction_2 = Math.atan2(ball.get_vy(), ball.get_vx());
		double new_xspeed_1 = speed1 * Math.cos(direction_1 - collision_angle);
		double new_yspeed_1 = speed1 * Math.sin(direction_1 - collision_angle);
		double new_xspeed_2 = speed2 * Math.cos(direction_2 - collision_angle);
		double new_yspeed_2 = speed2 * Math.sin(direction_2 - collision_angle);

		double final_xspeed_1 = ((this._mass - ball.get_mass()) * new_xspeed_1
				+ (ball.get_mass() + ball.get_mass()) * new_xspeed_2) / (this._mass + ball.get_mass());
		double final_xspeed_2 = ((this._mass + this._mass) * new_xspeed_1
				+ (ball.get_mass() - this._mass) * new_xspeed_2) / (this._mass + ball.get_mass());
		double final_yspeed_1 = new_yspeed_1;
		double final_yspeed_2 = new_yspeed_2;

		double cosAngle = Math.cos(collision_angle);
		double sinAngle = Math.sin(collision_angle);
		this._vx0 = cosAngle * final_xspeed_1 - sinAngle * final_yspeed_1;
		this._vy0 = sinAngle * final_xspeed_1 + cosAngle * final_yspeed_1;
		ball.set_vx0(cosAngle * final_xspeed_2 - sinAngle * final_yspeed_2);
		ball.set_vy0(sinAngle * final_xspeed_2 + cosAngle * final_yspeed_2);

		// get the mtd
		Point2D.Double posDiff = new Point2D.Double((this._x - ball.get_x()), (this._y - ball.get_y()));
		double d = Math.sqrt(posDiff.x * posDiff.x + posDiff.y * posDiff.y);

		// minimum translation distance to push balls apart after intersecting
		double mtdx = posDiff.x * (((this.get_radius() + ball.get_radius()) - d) / d);
		double mtdy = posDiff.y * (((this.get_radius() + ball.get_radius()) - d) / d);
		// resolve intersection --
		// computing inverse mass quantities
		double im1 = 1 / this._mass;
		double im2 = 1 / ball.get_mass();

		// push-pull them apart based off their mass
		this._x0 = this._x + mtdx * (im1 / (im1 + im2));
		this._y0 = this._y + mtdy * (im1 / (im1 + im2));
		this._t = 0.01;
		ball.set_x0(ball.get_x() - mtdx * (im2 / (im1 + im2)));
		ball.set_y0(ball.get_y() - mtdy * (im2 / (im1 + im2)));
		ball.set_t(0.01);
	}

}