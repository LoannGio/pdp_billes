package model;

public class Vector {

	private double x; // projection sur l'axe x
	private double y; // projection sur l'axe y

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

	/* Cette fonction permet de calculer la somme du deux vecteurs */ 
	public static Vector vectorSum(Vector A, Vector B) { 
		return new Vector(A.getX() + B.getX(), A.getY() + B.getY());
	}
	
	/* Cette fonction permet de calculer la soustraction entre deux vecteurs */ 
	public static Vector vectorSubtract(Vector A, Vector B) { 
		return new Vector(A.getX() - B.getX(), A.getY() - B.getY());
	}
	
	/* Cette fonction calcule le produit scalaire du deux vecteurs */ 
	public static double dotProduct(Vector A, Vector B) { 
		return (A.getX() * B.getX() + A.getY() * B.getY());
	}
	
	/*Cette fonction calcule la multiplication d'un vecteur par une valeur*/
	public static Vector vectorProductConstant(Vector A, double val) {
		return new Vector(val*A.getX(), val*A.getY());
	}
	
	/*Cette fonction permet d'obtenir le produit cartesien entre deux vecteurs*/
	public static Vector Product(Vector A, Vector B) {
		return new Vector(A.getX()*B.getX(), A.getY()*B.getY());
	}

}