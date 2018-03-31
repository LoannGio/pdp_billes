package test.controller;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.PhysicalEngine;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import model.Vector;

public class PhysicalEngineTest {
	Ball ball1, ball2;
	PhysicalEngine physical_engine;
	Circuit circuit;
	ObstacleLine obstacle;
	
	@Before
	public void setUp() throws Exception {
		ball1 = new Ball(0,0,10,1);
		ball2 = new Ball(0,0,10,1);
		obstacle = new ObstacleLine(new Point(2,2), new Point(5,5), 0.5);
		circuit = new Circuit(640,200);
		physical_engine = new PhysicalEngine(circuit);
	}

	@After
	public void tearDown() throws Exception {
		ball1 = null;
		ball2 = null;
		obstacle = null;
		circuit = null;
		physical_engine = null;
	}

	@Test
	public void testRun() {
		
	}
	
	
	/**
	 * This test checks that the trajectory and speed of balls are correctly
	 * modified during a collision.
	 * 2 balls of same mass are created, their speed are the same and their trajectory
	 * are opposed when the collision occurs.
	 */
	
	@Test
	public void test_resolveCollisionBallBallSameMass() {
		/* Event: horizontal collision*/
		try {
		Method resolveCollBallBall = PhysicalEngine.class.getMethod(
				"resolveCollisionBallBall", Ball.class, Ball.class);
		resolveCollBallBall.setAccessible(true);
		ball1.setAll(50, 100, 10, 2);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(1, 0);
		ball2.set_speed(-1, 0);
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		boolean b1 = Math.abs(ball1.get_velocity().getX()+1) < 1E-10;
		boolean b2 = Math.abs(ball2.get_velocity().getX()-1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Event: vertical collision*/ 
		ball1.setAll(70, 100, 10, 2);
		ball2.setAll(70, 120, 10, 2);
		ball1.set_speed(0, 1);
		ball2.set_speed(0, -1);
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		b1 = Math.abs(ball1.get_velocity().getY()+1) < 1E-10;
		b2 = Math.abs(ball2.get_velocity().getY()-1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Event: 45 degrees collision*/ 
		ball1.setAll(50, 100, 10, 2);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(1, 1);
		ball2.set_speed(-1, -1);
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		b1 = ( Math.abs(ball1.get_velocity().getX()+1) < 1E-10 ) && 
			 ( Math.abs(ball1.get_velocity().getY()-1) < 1E-10);
		b2 = ( Math.abs(ball2.get_velocity().getX()-1) < 1E-10 ) && 
			 ( Math.abs(ball2.get_velocity().getY()+1) < 1E-10);
		assertEquals(true, b1);
		assertEquals(true, b2);
		}catch(Exception e) {
			
		}
	}
	
	
	/**
	 *  This test checks that the speed of balls is correctly modified during a
	 *  ball-ball collision of balls with different mass.
	 *  2 balls of different mass are created, and we make their speed vary.
	 *  Then, we check that the engine obeys the formulas of elastic impact:
	 *  preservation of movement quantity and preservation of kinetic energy.
	 */
	@Test
	public void test_resolveCollisionBallBallDifferentMass() {
		try {
		Method resolveCollBallBall = PhysicalEngine.class.getDeclaredMethod(
						"resolveCollisionBallBall", Ball.class, Ball.class);
		resolveCollBallBall.setAccessible(true);
		/* Event: same speed */
		
		// Preservation of movement quantity 
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(1, 0);
		ball2.set_speed(-1, 0);
		Vector qmi  = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
					                   Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		Vector qmf = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
                                      Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));	
		Vector Diff = Vector.vectorSubtract(qmi, qmf);
		boolean b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		
		// Preservation of kinetic energy
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(1, 0);
		ball2.set_speed(-1, 0);
		Vector v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		Vector v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		Vector eci  = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
					                   Vector.vectorProductConstant(v2square, ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		Vector ecf = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
                                      Vector.vectorProductConstant(v2square, ball2.get_mass()));	
		Diff = Vector.vectorSubtract(eci, ecf);
		b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		
		/* Event: one unmoving ball */
			
		// Preservation of movement quantity 
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(0, 0);
		ball2.set_speed(-5, 0);
		qmi  = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
					                   Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		qmf = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
                                      Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));	
		Diff = Vector.vectorSubtract(qmi, qmf);
		b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		
		// Preservation of kinetic energy
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(0, 0);
		ball2.set_speed(-5, 0);
		v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		eci  = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
					                   Vector.vectorProductConstant(v2square, ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		ecf = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
                                      Vector.vectorProductConstant(v2square, ball2.get_mass()));	
		Diff = Vector.vectorSubtract(eci, ecf);
		b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		/* Event : 2 different not null speeds */
			
		// Preservation of movement quantity
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(10, 0);
		ball2.set_speed(-5, 0);
		qmi  = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
					                   Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		qmf = Vector.vectorSum(Vector.vectorProductConstant(ball1.get_velocity(), ball1.get_mass()),
                                      Vector.vectorProductConstant(ball2.get_velocity(), ball2.get_mass()));	
		Diff = Vector.vectorSubtract(qmi, qmf);
		b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		
		// Preservation of kinetic energy
		ball1.setAll(50, 100, 10, 1);
		ball2.setAll(70, 100, 10, 2);
		ball1.set_speed(10, 0);
		ball2.set_speed(-5, 0);
		v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		eci  = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
					                   Vector.vectorProductConstant(v2square, ball2.get_mass()));
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		v1square = Vector.Product(ball1.get_velocity(), ball1.get_velocity());
		v2square = Vector.Product(ball2.get_velocity(), ball2.get_velocity());
		ecf = Vector.vectorSum(Vector.vectorProductConstant(v1square, ball1.get_mass()),
                                      Vector.vectorProductConstant(v2square, ball2.get_mass()));	
		Diff = Vector.vectorSubtract(eci, ecf);
		b = (Math.abs(Diff.getX()) < 1E-10)  && (Math.abs(Diff.getY()) < 1E-10);
		assertEquals(true, b);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This test checks that the trajectory and speed of a ball are correctly
	 * modified during a collision with an obstacle.
	 * The coefficient of restitution is equal to 1 (the movement quantity
	 * and the kinetic energy are preserved).
	 */
	@Test
	public void test_resolveCollisionBallObstacle() {
		
		try {
		Method resolveCollBallObstacle = PhysicalEngine.class.getDeclaredMethod(
				"resolveCollisionBallObstacle", Ball.class, ObstacleLine.class);
		resolveCollBallObstacle.setAccessible(true);
		obstacle.setCOR(1);
		// Event: horizontal obstacle collision, ball moving towards bottom
		obstacle.set_begin(new Point(20,100));
		obstacle.set_end(new Point(100,100));
		ball1.setAll(50, 90, 10, 2);
		ball1.set_speed(0, 6);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		boolean b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		boolean b2 = Math.abs(ball1.get_velocity().getY()+6) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		// Event: horizontal obstacle collision, ball moving towards top
		ball1.set_location(50, 110);
		ball1.set_speed(0, -5);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()-5) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		// Event: vertical obstacle collision, ball moving towards right
		obstacle.set_begin(new Point(100,100));
		obstacle.set_end(new Point(100,200));
		ball1.set_location(90, 150);
		ball1.set_speed(3, 0);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()+3) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		// Event: vertical obstacle collision, ball moving towards left
		obstacle.set_begin(new Point(100,100));
		obstacle.set_end(new Point(100,200));
		ball1.set_location(110, 150);
		ball1.set_speed(-4, 0);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()-4) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		// Event: 45 degrees obstacle collision, ball moving perpendicularly
		obstacle.set_begin(new Point(100,300));
		obstacle.set_end(new Point(200,200));
		ball1.set_location(145, 245);
		ball1.set_speed(1, 1);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()+1) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()+1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle 45+180 degres, bille direction perpendiculaire*/
		// Event: 45+180 degrees obstacle collision, ball moving perpendicularly
		obstacle.set_begin(new Point(100,100));
		obstacle.set_end(new Point(200,200));
		ball1.set_location(155, 145);
		ball1.set_speed(-1, 1);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()-1) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()+1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/*
	 * Ce test permet de verifier que la collision fonctionne toujours
	 * lorsqu'on modifie le coefficient de rebond.
	 * On verifie que vitesse apres le rebond diminue correctement selon l'axe Y
	 * ce qui diminue la quantite de mouvement et l'energie cinetique (choc non elastique)
	 */
	@Test
	public void test_resolveCollisionBallObstacleCOR() {
		
		try {
		Method resolveCollBallObstacle = PhysicalEngine.class.getDeclaredMethod(
				"resolveCollisionBallObstacle", Ball.class, ObstacleLine.class);
		resolveCollBallObstacle.setAccessible(true);	
		obstacle.setCOR(0.5);
		/* Cas collision obstacle horizontal, bille direction vers le bas */ 
		obstacle.set_begin(new Point(20,100));
		obstacle.set_end(new Point(100,100));
		ball1.setAll(50, 90, 10, 2);
		ball1.set_speed(0, 6);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		boolean b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		boolean b2 = Math.abs(ball1.get_velocity().getY()+3) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle 45 degres, bille direction perpendiculaire */
		obstacle.set_begin(new Point(100,300));
		obstacle.set_end(new Point(200,200));
		ball1.set_location(145, 245);
		ball1.set_speed(8, 8);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()+8) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()+4) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	
	@Test
	public void testAngleCollisionBall() {
		
	}
	
	/* Ce test permet de verifier que le calcul de la 
	 * normale d'un point par rapport a une droite est correct.
	 * On verifie si l'angle entre le vecteur normal et la droite
	 * est egal a 90.
	 */
	@Test 
	public void test_getNormale() {
		try {
			/* normale sur une droite horizontale */	
			Method normale = PhysicalEngine.class.getDeclaredMethod(
					"GetNormale", Point.class, Point.class, Point2D.Double.class);	
			normale.setAccessible(true);
			Point2D.Double center = new Point2D.Double(140, 70);
			Point A = new Point(100, 100);
			Point B = new Point(200, 100);
			Vector norm = (Vector) normale.invoke(physical_engine, A, B, center);
			Vector AB = new Vector(B.x - A.x, B.y - A.y);
			double sizeAB = Math.sqrt(Math.pow(AB.getX(), 2) + Math.pow(AB.getY(), 2));
			double sizeNorm = Math.sqrt(Math.pow(norm.getX(), 2) + Math.pow(norm.getY(), 2));
			double cosAngle = Vector.dotProduct(AB, norm)/(sizeAB*sizeNorm);
			assertEquals(true, Math.abs(cosAngle) < 1E-10);
			/* normale sur une droite diagonale */	
			center.setLocation(100, 200);
			A.setLocation(100, 300);
			B.setLocation(200, 200);
			norm = (Vector) normale.invoke(physical_engine, A, B, center);
			AB = new Vector(B.x - A.x, B.y - A.y);
			sizeAB = Math.sqrt(Math.pow(AB.getX(), 2) + Math.pow(AB.getY(), 2));
			sizeNorm = Math.sqrt(Math.pow(norm.getX(), 2) + Math.pow(norm.getY(), 2));
			cosAngle = Vector.dotProduct(AB, norm)/(sizeAB*sizeNorm);
			assertEquals(true, Math.abs(cosAngle) < 1E-10);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/*	Ce test permet de verifier que le calcul du point de projection
	 * d'un point par rapport a une droite est correct.
	 * On a calcule la position du point de projection pour different cas,
	 * on verifie que notre fonction renvoie la meme valeur.
	 */
	@Test
	public void test_projectionI() {
		try {
		/* projection sur une droite horizontale */	
		Method projection = PhysicalEngine.class.getDeclaredMethod(
				"ProjectionI", Point.class, Point.class, Point2D.Double.class);
		projection.setAccessible(true);
		Point2D.Double center = new Point2D.Double(140, 70);
		Point A = new Point(100, 100);
		Point B = new Point(200, 100);
		Point2D.Double proj = (Point2D.Double) projection.invoke(physical_engine, A, B, center);
		boolean b1 = Math.abs(proj.getX() - center.getX()) < 1E-10;
		boolean b2 = Math.abs(proj.getY() - 100) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* projection sur une droite verticale */
		center.setLocation(60, 150);
		A.setLocation(100, 100);
		B.setLocation(100, 200);
		proj = (Point2D.Double) projection.invoke(physical_engine, A, B, center);
		b1 = Math.abs(proj.getX() - 100) < 1E-10;
		b2 = Math.abs(proj.getY() - center.getY() ) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		/* projection sur une droite diagonale */
		center.setLocation(100, 200);
		A.setLocation(100, 300);
		B.setLocation(200, 200);
		proj = (Point2D.Double) projection.invoke(physical_engine, A, B, center);
		b1 = Math.abs(proj.getX() - 150) < 1E-10;
		b2 = Math.abs(proj.getY() - 250 ) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	
	@Test
	public void test_ballIsOutOfCircuit() {
		Ball ball = new Ball(635,200,10,1);
		
		//Balle dedans
		Boolean result = null;
		try {
			Method ballIsOutOfCircuit = PhysicalEngine.class.getDeclaredMethod("ballIsOutOfCircuit", Ball.class);
			ballIsOutOfCircuit.setAccessible(true);
			result = (Boolean) ballIsOutOfCircuit.invoke(physical_engine, ball);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(result, false);
		
		//Balle sortie en X
		ball.set_location(651, 200);
		try {
			Method ballIsOutOfCircuit = PhysicalEngine.class.getDeclaredMethod("ballIsOutOfCircuit", Ball.class);
			ballIsOutOfCircuit.setAccessible(true);
			result = (Boolean) ballIsOutOfCircuit.invoke(physical_engine, ball);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(result, true);
		
		//Balle sortie en Y
		ball.set_location(640, 211);
		try {
			Method ballIsOutOfCircuit = PhysicalEngine.class.getDeclaredMethod("ballIsOutOfCircuit", Ball.class);
			ballIsOutOfCircuit.setAccessible(true);
			result = (Boolean) ballIsOutOfCircuit.invoke(physical_engine, ball);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(result, true);
	}

}
