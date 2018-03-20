package model;

import java.awt.Point;
import java.util.ArrayList;

public class Ball {

	public static double COR = 1; // Coefficient of Restitution
	private Vector _location;
	private Vector _velocity;
	private double _mass;
	private int _radius;
	private ArrayList<Point> _trace;

	public Ball(double x, double y, int radius, double mass) {
		_location = new Vector(x, y);
		_velocity = new Vector(0, 0);
		_radius = radius;
		_mass = mass;
		_trace = new ArrayList<Point>();
		_trace.add(new Point((int) x, (int) y));
	}

	/******************************
	 * Part of physical engine
	 *************************/

	public void setAll(double x, double y, int radius, double mass) {
		_location.setCartesian(x, y);
		_radius = radius;
		_mass = mass;
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

	public void set_speed(double x, double y) {
		_velocity.setCartesian(x, y);
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

	public void set_radius(int radius) {
		_radius = radius;
	}

	public Vector get_velocity() {
		return _velocity;
	}

	public double get_speed() {
		double speed = Math.sqrt(Math.pow(_velocity.getX(), 2) + Math.pow(_velocity.getY(), 2));
		return speed;
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

	public void step(Vector acceleration) {
		double x0 = _location.getX();
		double y0 = _location.getY();

		Vector new_speed = Vector.vectorSum(_velocity, acceleration);
		set_speed(new_speed.getX(), new_speed.getY());

		Vector new_location = Vector.vectorSum(_location, _velocity);
		set_location(new_location.getX(), new_location.getY());

		if ((int) _location.getX() != (int) x0 || (int) _location.getY() != (int) y0)
			_trace.add(new Point((int) _location.getX(), (int) _location.getY()));
	}

}