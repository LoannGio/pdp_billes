package test.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Vector;

public class VectorTest {
	Vector A, B, C;
	boolean b1, b2;
	double val;
	
	@Before
	public void setUp() throws Exception {
		A = new Vector();
		B = new Vector();
		val = 0;
		b1 = false; 
		b2 = false;
	}

	@After
	public void tearDown() throws Exception {
		A = null;
		B = null;
		C = null;
	}

	/* On test la somme des deux vecteurs A et B*/
	@Test
	public void test_vectorSum() {
		/* Cas valeurs positives */ 
		A.setCartesian(1, 6);
		B.setCartesian(3, 10);
		C = Vector.vectorSum(A, B);
		b1 = Math.abs(C.getX() - 4) < 1E-10;
		b2 = Math.abs(C.getY() - 16) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* Cas valeurs negatives */ 
		A.setCartesian(-12, 15);
		B.setCartesian(-1, -10);
		C = Vector.vectorSum(A, B);
		b1 = Math.abs(C.getX() + 13) < 1E-10;
		b2 = Math.abs(C.getY() - 5) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
	}

	/*On verifie la soustraction des deux vecteurs A et B*/
	@Test
	public void Test_vectorSubtract() { 
		/* Cas valeurs positives */ 
		A.setCartesian(1, 6);
		B.setCartesian(3, 10);
		C = Vector.vectorSubtract(A, B);
		b1 = Math.abs(C.getX() + 2) < 1E-10;
		b2 = Math.abs(C.getY() + 4) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* Cas valeurs negatives */ 
		A.setCartesian(-12, 15);
		B.setCartesian(-1, -10);
		C = Vector.vectorSubtract(A, B);
		b1 = Math.abs(C.getX() + 11) < 1E-10;
		b2 = Math.abs(C.getY() - 25) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
	}
	
	/*Cette fonction permet de tester le produit scalaire entre deux vecteurs A et B*/
	@Test
	public void Test_dotProduct() { 
		/* Cas valeurs positives */ 
		A.setCartesian(2, 2);
		B.setCartesian(1, 6);
		val = Vector.dotProduct(A, B);
		b1 = Math.abs(val - 14) < 1E-10;
		assertEquals(true, b1);
		/* Cas valeurs negatives */ 
		A.setCartesian(-10, 5);
		B.setCartesian(2, -1);
		val = Vector.dotProduct(A, B);
		b1 = Math.abs(val + 25 ) < 1E-10;
		assertEquals(true, b1);
	}
	
	/*On teste la multiplication d'un vecteur A par une valeur val */
	@Test
	public void test_vectorProductConstant() {
		/* multiplication par une valeur positive */ 
		A.setCartesian(-11, -6);
		val = 2;
		C = Vector.vectorProductConstant(A, val);
		b1 = Math.abs(C.getX() + 22 ) < 1E-10;
		b2 = Math.abs(C.getY() + 12 ) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* multiplication par une valeur negative */ 
		A.setCartesian(2, -7);
		val = -4;
		C = Vector.vectorProductConstant(A, val);
		b1 = Math.abs(C.getX() + 8) < 1E-10;
		b2 = Math.abs(C.getY() - 28) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
	}
	
	/* On verifie le produit cartesien des deux vecteurs */
	@Test
	public void test_Product() {
		/* Cas valeurs positives */ 
		A.setCartesian(3, 5);
		B.setCartesian(1, 4);
		C = Vector.Product(A, B);
		b1 = Math.abs(C.getX() - 3) < 1E-10;
		b2 = Math.abs(C.getY() - 20) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* Cas valeurs negatives */ 
		A.setCartesian(10, -2);
		B.setCartesian(-2, 3);
		C = Vector.Product(A, B);
		b1 = Math.abs(C.getX() + 20) < 1E-10;
		b2 = Math.abs(C.getY() + 6) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
	}
	
}
