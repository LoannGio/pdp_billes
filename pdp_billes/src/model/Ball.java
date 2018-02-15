package model;

import java.awt.Point;
import java.util.ArrayList;

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
	private boolean _isOut;
	private ArrayList<Point> _points;

	public Ball(double x, double y, int radius, double mass, double inclinaison) {
		_x = x;
		_y = y;
		_radius = radius;
		_mass = mass;
		setAcceleration(inclinaison);
		_vx0 = _vx = 0; // vitesse initiale 0 m/s
		_vy0 = _vy = 0;
		_x0 = _x; // position initiale
		_y0 = _y; // (Panel.YMIN + Panel.YMAX)/2;
		_t = 0;
		_isOut = false;
		calcPoints();
	}

	public void setAcceleration(double inclinaison) {
		_px = -_mass * Circuit.get_gravitation() * Math.cos(Math.toRadians(inclinaison));
		_py = _mass * Circuit.get_gravitation() * Math.sin(Math.toRadians(inclinaison));
		_rx = -_px;
		_ry = 0;
		_fy = _py + _ry;
		_ax = _fx / _mass; // acceleration initiale
		_ay = _fy / _mass;
	}

	public void calcPoints() {
		_points = new ArrayList<Point>();
		for (double x = _x - _radius; x <= _x + _radius; x++) {
			for (double y = _y - _radius; y <= _y + _radius; y++) {
				Point p = new Point();
				double calc = Math.pow(x - _x, 2) + Math.pow(y - _y, 2);
				double radius_squared = Math.pow(_radius, 2);
				if (calc <= radius_squared) {
					p.setLocation(x, y);
					_points.add(p);
				}
			}
		}
	}

	public ArrayList<Point> get_points() {
		return _points;
	}

	public void setAll(double x, double y, int radius, double mass) {
		_x = x;
		_y = y;
		_radius = radius;
		_mass = mass;
		calcPoints();
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
		calcPoints();
	}

	public double get_y() {
		return _y;
	}

	public void set_y(int _y) {
		this._y = _y;
		calcPoints();
	}

	public int get_radius() {
		return _radius;
	}

	public void set_radius(int _radius) {
		this._radius = _radius;
		calcPoints();
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

	public boolean is_isOut() {
		return _isOut;
	}

	public void set_isOut(boolean _isOut) {
		this._isOut = _isOut;
	}

	public void set_x(double _x) {
		this._x = _x;
	}

	public void set_y(double _y) {
		this._y = _y;
	}

	public void step() {
		double scale = 40;
		this._vx = (this._vx0 + this._ax * this._t);
		this._vy = (this._vy0 + this._ay * this._t);
		double t2 = this._t * this._t;
		this._x = this._x0 / scale + this._vx0 * this._t + (this._ax * t2) / 2;
		this._y = this._y0 / scale + this._vy0 * this._t + (this._ay * t2) / 2;
		this._x *= 40;
		this._y *= 40;
		this._t += AnimationTimer.MSSTEP;
	}

	public void collisionCircuit(int width, int height) {
		if (this._x >= width + this._radius || this._x <= 0) {
			this._vx0 = -this._vx;
			this._vy0 = this._vy;
			this._x0 = this._x;
			this._y0 = this._y;
			this._t = 0;
			_isOut = true;
		}

		if (this._y >= height - this._radius || this._y <= 0) {
			this._vx0 = this._vx;
			this._vy0 = -this._vy;
			this._x0 = this._x;
			this._y0 = this._y;
			this._t = 0;
			_isOut = true;
		}
	}

	public void resolveCollisionObstacle(ObstacleLine obstacle) {

		this._ax = 0;
		this._ay = 0;
		this._x0 = this._x;
		this._y0 = this._y;
		this._t = 0;
	}

	public void resolveCollisionBall(Ball ball) {

		this._ax = 0;
		this._ay = 0;
		this._x0 = this._x;
		this._y0 = this._y;
		this._t = 0;

		ball.set_ax(0);
		ball.set_ay(0);
		ball.set_x0(ball.get_x());
		ball.set_y0(ball.get_y());
		ball.set_t(0);

	}
}