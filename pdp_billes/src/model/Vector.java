package model;

public class Vector {

	private double x; // projection of vector on x
	private double y; // projection of vector on y

	public Vector() {
		this.x = 0;
		this.y = 0;
	}

	public Vector(double x, double y) {
		this();
		setCartesian(x, y);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setCartesian(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector vectorSum(Vector A, Vector B) { // sum two vectors
		Vector vector = new Vector();
		vector.setCartesian(A.getX() + B.getX(), A.getY() + B.getY());
		return vector;
	}

	public static Vector vectorSubtract(Vector A, Vector B) { // subtract two
																// vectors
		Vector vector = new Vector();
		vector.setCartesian(A.getX() - B.getX(), A.getY() - B.getY());
		return vector;
	}

	public static double dotProduct(Vector A, Vector B) { // scalar vector
															// multibly
		return (A.getX() * B.getX() + A.getY() * B.getY());
	}
	
	public static Vector vectorProductConstant(Vector A, double val) {
		Vector vector = new Vector(val*A.getX(), val*A.getY());
		return vector;
	}
	
	public static Vector Product(Vector A, Vector B) {
		Vector vector = new Vector(A.getX()*B.getX(), A.getY()*B.getY());
		return vector;
	}

}