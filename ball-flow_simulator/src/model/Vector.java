package model;

public class Vector {

	private double _x; // projection on X axis
	private double _y; // projection on Y axis

	public Vector() {
		this._x = 0;
		this._y = 0;
	}

	public Vector(double x, double y) {
		setCartesian(x, y);
	}

	public double getX() {
		return this._x;
	}

	public double getY() {
		return this._y;
	}

	public void setCartesian(double x, double y) {
		this._x = x;
		this._y = y;
	}

	public static Vector vectorSum(Vector A, Vector B) { 
		return new Vector(A.getX() + B.getX(), A.getY() + B.getY());
	}
	
	public static Vector vectorSubtract(Vector A, Vector B) { 
		return new Vector(A.getX() - B.getX(), A.getY() - B.getY());
	}
	
	public static double dotProduct(Vector A, Vector B) { 
		return (A.getX() * B.getX() + A.getY() * B.getY());
	}
	
	public static Vector vectorProductConstant(Vector A, double val) {
		return new Vector(val*A.getX(), val*A.getY());
	}
	
	public static Vector Product(Vector A, Vector B) {
		return new Vector(A.getX()*B.getX(), A.getY()*B.getY());
	}

}