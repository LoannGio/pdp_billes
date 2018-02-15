package model;

import java.util.ArrayList;

public class Circuit {
	private int _width;
	private int _height;
	private ArrayList<Ball> _balls = new ArrayList<Ball>();
	private ArrayList<ObstacleLine> _lines = new ArrayList<ObstacleLine>();
	private int _defaultBallRadius, _defaultLineThickness;
	private double _defaultBallMass;
	private static double _gravitation = 9.80665;
	private double _defaultInclinaison;

	public Circuit(int width, int height) {
		_width = width;
		_height = height;
		_defaultBallMass = 1;
		_defaultBallRadius = 10;
		_defaultLineThickness = 1;
		_defaultInclinaison = 45;
	}

	public void addBall(Ball b) {
		_balls.add(b);
	}

	public Boolean removeBall(Ball b) {
		return _balls.remove(b);
	}

	public void clearAll() {
		_balls = new ArrayList<Ball>();
		_lines = new ArrayList<ObstacleLine>();
	}

	public Boolean removeLine(ObstacleLine o) {
		return _lines.remove(o);
	}

	public void addLine(ObstacleLine ol) {
		_lines.add(ol);
	}

	public static double get_gravitation() {
		return _gravitation;
	}

	public static void set_gravitation(double gravitation) {
		_gravitation = gravitation;
	}

	public double get_inclinaison() {
		return _defaultInclinaison;
	}

	public void set_inclinaison(double inclinaison) {
		_defaultInclinaison = inclinaison;
		for (Ball b : _balls) {
			b.setAcceleration(_defaultInclinaison);
		}
	}

	public int get_width() {
		return _width;
	}

	public void set_width(int _width) {
		this._width = _width;
	}

	public int get_height() {
		return _height;
	}

	public void set_height(int _height) {
		this._height = _height;
	}

	public ArrayList<Ball> get_balls() {
		return _balls;
	}

	public ArrayList<ObstacleLine> get_lines() {
		return _lines;
	}

	public int get_defaultBallRadius() {
		return _defaultBallRadius;
	}

	public void set_defaultBallRadius(int _ballRadius) {
		this._defaultBallRadius = _ballRadius;
	}

	public int get_defaultLineThickness() {
		return _defaultLineThickness;
	}

	public void set_defaultLineThickness(int _lineThickness) {
		this._defaultLineThickness = _lineThickness;
	}

	public double get_defaultBallMass() {
		return _defaultBallMass;
	}

	public void set_defaultBallMass(double _ballMass) {
		this._defaultBallMass = _ballMass;
	}

}
