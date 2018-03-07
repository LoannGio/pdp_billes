package model;

import java.awt.Point;
import java.util.ArrayList;

import controller.Controller;

public class Ball {

	public static double COR = 1; // Coefficient of Restitution
	private Vector _location;
	private Vector _init_location;
	private Vector _velocity;
	private Vector _init_velocity;
	private Vector _acceleration;
	private double _mass;
	private int _radius;
	private double _time;
	private ArrayList<Point> _trace;

	public Ball(double x, double y, int radius, double mass) {
		_location = new Vector(x, y);
		_velocity = new Vector(0, 0);
		_radius = radius;
		_mass = mass;
		_trace = new ArrayList<Point>();
		setAcceleration();
	}

	/******************************
	 * Part of physical engine
	 *************************/

	public void setAcceleration() {
		double _inclinaison = Controller.getInstance().get_defaultInclinaison();
		double _px = -_mass * Circuit.get_gravitation() * Math.cos(Math.toRadians(_inclinaison));
		double _py = _mass * Circuit.get_gravitation() * Math.sin(Math.toRadians(_inclinaison));

		Vector _gravity_force = new Vector(_px, _py);
		Vector _reaction_force = new Vector(-_px, 0);
		Vector _total_force = new Vector((_gravity_force.getX() + _reaction_force.getX()),
				(_gravity_force.getY() + _reaction_force.getY()));

		_acceleration = new Vector((_total_force.getX() / _mass), (_total_force.getY() / _mass));
		_init_location = new Vector(_location.getX(), _location.getY());
		_init_velocity = new Vector(_velocity.getX(), _velocity.getY());
		_time = 0;
	}

	public void setAll(double x, double y, int radius, double mass, double inclinaison) {
		_location.setCartesian(x, y);
		_radius = radius;
		_mass = mass;
		setAcceleration();
	}

	public Boolean contains(Point p) {
		double x = p.getX();
		double y = p.getY();
		double calc = Math.pow(x - _location.getX(), 2) + Math.pow(y - _location.getY(), 2);
		double radius_squared = Math.pow(_radius, 2);
		if (calc <= radius_squared)
			return true;
		return false;
	}

	public void set_location(double x, double y) {
		_location.setCartesian(x, y);
	}

	public void set_init_location(double x0, double y0) {
		_init_location.setCartesian(x0, y0);
	}

	public void set_speed(double x, double y) {
		_velocity.setCartesian(x, y);
	}

	public void set_init_speed(double x0, double y0) {
		_init_velocity.setCartesian(x0, y0);
	}

	public void set_time(double t) {
		this._time = t;
	}

	public Vector get_location() {
		return _location;
	}

	public double get_x() {
		return _location.getX();
	}

	public double get_y() {
		return _location.getY();
	}

	public int get_radius() {
		return _radius;
	}

	public Vector get_velocity() {
		return _velocity;
	}

	public double get_speed() {
		return _velocity.getR();
	}

	public double get_mass() {
		return _mass;
	}

	public ArrayList<Point> get_trace() {
		return _trace;
	}

	/*******************************
	 * Part of physical engine
	 *****************************************/

	public void step() {

		int x = (int) _location.getX();
		int y = (int) _location.getY();
		double _x0 = _init_location.getX();
		double _y0 = _init_location.getY();
		double _vx0 = _init_velocity.getX();
		double _vy0 = _init_velocity.getY();
		double _ax = _acceleration.getX();
		double _ay = _acceleration.getY();

		double _vx = (_vx0 + _ax * this._time);
		double _vy = (_vy0 + _ay * this._time);
		this.set_speed((_vx), (_vy));

		double t2 = this._time * this._time;
		double _x = _x0 / Circuit.get_scale() + _vx0 * this._time + (_ax * t2) / 2;
		double _y = _y0 / Circuit.get_scale() + _vy0 * this._time + (_ay * t2) / 2;
		this.set_location((_x * Circuit.get_scale()), (_y * Circuit.get_scale()));

		if (_location.getX() != x || _location.getY() != y)
			_trace.add(new Point((int) _location.getX(), (int) _location.getY()));
		this._time += AnimationTimer.MSSTEP;

	}

}