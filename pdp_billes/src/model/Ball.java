package model;

import java.awt.Point;
import java.awt.geom.Point2D;
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
		/*
		 * Point2D.Double a = new Point2D.Double(); Point2D.Double b = new
		 * Point2D.Double();
		 * 
		 * a.x = obstacle.get_depart().getX()/40; a.y =
		 * obstacle.get_depart().getY()/40; b.x =
		 * obstacle.get_arrivee().getX()/40; b.y =
		 * obstacle.get_arrivee().getY()/40;
		 * 
		 * Point2D.Double c = new Point2D.Double();
		 * 
		 * if(a.y <= b.y) { c.y = b.y; c.x = a.x; }else { c.y = a.y; c.x = b.x;
		 * }
		 * 
		 * double tanBeta = distance(maxPoint(a,b),c)/distance(minPoint(a,b),c);
		 * double Beta = Math.toDegrees(Math.atan(tanBeta)); double Alpha =
		 * 90-Beta; this._vx0 = this._vx * Math.cos(Math.toRadians(Alpha));
		 * this._vy0 = -this._vy * Math.sin(Math.toRadians(Alpha));
		 */
		this._ax = 0;
		this._ay = 0;
		this._x0 = this._x;
		this._y0 = this._y;
		this._t = 0;
	}

	public void resolveCollisionBall(Ball ball) {
		/*
		 * this._vx0 =
		 * (this._vx*(this._mass-ball.get_mass())+2*ball.get_mass()*ball.get_vx(
		 * ))/(this._mass+ball.get_mass()) ; this._vy0 =
		 * (this._vy*(this._mass-ball.get_mass())+2*ball.get_mass()*ball.get_vy(
		 * ))/(this._mass+ball.get_mass()) ;
		 * ball.set_vx0(ball.get_vx()*(ball.get_mass()-this._mass)+2*this._mass*
		 * this.get_vx() /(this._mass+ball.get_mass()));
		 * ball.set_vy0(ball.get_vy()*(ball.get_mass()-this._mass)+2*this._mass*
		 * this.get_vy() /(this._mass+ball.get_mass()));
		 */

		/*
		 * double alpha=0,teta1=0,teta2=0;
		 * 
		 * this._vx0 =
		 * (((this._vx*Math.cos(teta1-alpha)*(this._mass-ball.get_mass())) +
		 * (2*ball.get_mass()*ball.get_vx()*Math.cos(teta2-alpha)) )/
		 * this._mass+ball.get_mass()) * Math.cos(alpha)-
		 * this._vx*Math.sin(teta1-alpha)*Math.sin(alpha); this._vy0 =
		 * (((this._vy*Math.cos(teta1-alpha)*(this._mass-ball.get_mass())) +
		 * (2*ball.get_mass()*ball.get_vx()*Math.cos(teta2-alpha)) )/
		 * this._mass+ball.get_mass()) * Math.sin(alpha)-
		 * this._vy*Math.sin(teta1-alpha)*Math.cos(alpha);
		 * 
		 * 
		 * ball.set_vx0((((ball.get_vx()*Math.cos(teta2-alpha)*(ball.get_mass()-
		 * this._mass)) + (2*this._mass*this._vx*Math.cos(teta1-alpha)) )/
		 * this._mass+ball.get_mass()) * Math.cos(alpha)-
		 * ball.get_vx()*Math.sin(teta2-alpha)*Math.sin(alpha));
		 * ball.set_vy0((((ball.get_vy()*Math.cos(teta2-alpha)*(ball.get_mass()-
		 * this._mass)) + (2*this._mass*this._vy*Math.cos(teta1-alpha)) )/
		 * this._mass+ball.get_mass()) * Math.sin(alpha)-
		 * ball.get_vx()*Math.sin(teta2-alpha)*Math.cos(alpha));
		 * 
		 * 
		 * this._x0 = this._x; this._y0 = this._y; this._t = 0;
		 */
		// handleCollision (ball);
		this._ax = 0;
		this._ay = 0;
		this._x0 = this._x;
		this._y0 = this._y;
		this._t = 0;

	}

	public double distance(Point2D.Double a, Point2D.Double b) {
		return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
	}

	public Point2D.Double maxPoint(Point2D.Double a, Point2D.Double b) {
		if (a.getY() >= b.getY())
			return a;
		else
			return b;
	}

	public Point2D.Double minPoint(Point2D.Double a, Point2D.Double b) {
		if (a.getY() <= b.getY())
			return a;
		else
			return b;
	}

	/*
	 * private void handleCollision (Ball ball) { translate(ball); double nxs =
	 * ball.get_x() - this._x; double nys = ball.get_y() - this._y; double unxs
	 * = nxs / Math.sqrt((nxs*nxs + nys*nys)); double unys = nys /
	 * Math.sqrt((nxs*nxs + nys*nys)); double utxs = -unys; double utys = unxs;
	 * double n1 = unxs * this._vx + unys * this._vy; double nt1 = utxs *
	 * this._vx + utys * this._vy; double n2 = unxs * ball.get_vx() + unys *
	 * ball.get_vy(); double nt2 = utxs * ball.get_vx() + utys * ball.get_vy();
	 * 
	 * // double nn1 = ((Window.RESISTITION+1)*ball.area*n2 + n1*(this.area -
	 * Window.RESISTITION*ball.area))/(this.area + ball.area); // double nn2 =
	 * ((Window.RESISTITION+1)*this.area*n1 - n2*(this.area -
	 * Window.RESISTITION*ball.area))/(this.area + ball.area); double nn1 =
	 * ((ball.get_radius()*ball.get_radius()*Math.PI)*(n2 - n1)*0.25 +
	 * (ball.get_radius()*ball.get_radius()*Math.PI) * n2 +
	 * (this._radius*this._radius*Math.PI) *
	 * n1)/((this._radius*this._radius*Math.PI) +
	 * (ball.get_radius()*ball.get_radius()*Math.PI)); double nn2 =
	 * ((this._radius*this._radius*Math.PI)*(n1 - n2)*0.25 +
	 * (ball.get_radius()*ball.get_radius()*Math.PI) * n2 +
	 * (this._radius*this._radius*Math.PI) *
	 * n1)/((this._radius*this._radius*Math.PI) +
	 * (ball.get_radius()*ball.get_radius()*Math.PI)); this._vx0 = nn1 * unxs +
	 * nt1 * utxs; this._vy0 = nn1 * unys + nt1 * utys;
	 * 
	 * ball.set_vx0( nn2 * unxs + nt2 * utxs ); ball.set_vy0( nn2 * unys + nt2 *
	 * utys ); this._x0 = this._x; this._y0 = this._y; this._t = 0;
	 * 
	 * } private void translate (Ball ball) { double dx = (this._x -
	 * ball.get_x()); double dy = (this._y - ball.get_y()); double d =
	 * Math.sqrt(dx*dx + dy*dy); dx *= (this._radius + ball.get_radius() - d)/d;
	 * dy *= (this._radius + ball.get_radius() - d)/d; double im1 = 1 /
	 * (this._radius*this._radius*Math.PI); double im2 = 1 /
	 * (ball.get_radius()*ball.get_radius()*Math.PI); this._x += dx * im1 /(im1
	 * + im2); this._y += dy * im1 /(im1 + im2); ball.set_x( ball.get_x() - (dx
	 * * im2 /(im1 + im2)) ); ball.set_y( ball.get_y() - (dy * im2 /(im1 + im2))
	 * ); //repaint(); }
	 */
}
