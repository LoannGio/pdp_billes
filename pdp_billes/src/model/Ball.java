package model;

import java.awt.Point;
import java.util.ArrayList;

/** A ball is characterized by :
 * - the location of its center : vector _location
 * - a velocity : vector _velocity
 * - a mass : _mass
 * - a radius : _radius
 * - a track : a history of its positions
 */

public class Ball {
	private Vector _location;
	private Vector _velocity;
	private double _mass;
	private int _radius;
	private ArrayList<Point> _track;

	public Ball(double x, double y, int radius, double mass) {
		_location = new Vector(x, y);
		_velocity = new Vector(0, 0);
		_radius = radius;
		if (_radius <= 0)
			_radius = 1;
		_mass = mass;
		_track = new ArrayList<Point>();
		_track.add(new Point((int) x, (int) y));
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

	/** We calculate the new velocity : old velocity + acceleration.
	 * Then we calculate the new position : old position + new velocity
	 */
	public void step(Vector acceleration) {
		double x0 = _location.getX();
		double y0 = _location.getY();

		Vector new_speed = Vector.vectorSum(_velocity, acceleration);
		set_speed(new_speed.getX(), new_speed.getY());

		Vector new_location = Vector.vectorSum(_location, _velocity);
		set_location(new_location.getX(), new_location.getY());

		if ((int) _location.getX() != (int) x0 || (int) _location.getY() != (int) y0)
			_track.add(new Point((int) _location.getX(), (int) _location.getY()));
	}
	
	public void setAll(double x, double y, int radius, double mass) {
		_location.setCartesian(x, y);
		set_radius(radius);
		set_mass(mass);
	}
	
	public double get_speed() {
		return Math.sqrt(Math.pow(_velocity.getX(), 2) + Math.pow(_velocity.getY(), 2));
	}
	
	public void set_location(double x, double y) {
		_location.setCartesian(x, y);
	}

	public void set_speed(double x, double y) {
		_velocity.setCartesian(x, y);
	}
	
	public void set_radius(int radius) {
		_radius = radius;
		if (_radius <= 0)
			_radius = 1;
	}
	
	public double get_x() {
		return _location.getX();
	}

	public double get_y() {
		return _location.getY();
	}

	public Vector get_location() {
		return _location;
	}

	public int get_radius() {
		return _radius;
	}

	public Vector get_velocity() {
		return _velocity;
	}

	public double get_mass() {
		return _mass;
	}

	public void set_mass(double mass) {
		_mass = mass;
	}

	public ArrayList<Point> get_track() {
		return _track;
	}
}