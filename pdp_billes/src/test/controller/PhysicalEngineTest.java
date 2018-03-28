package test.controller;

import static org.junit.Assert.*;

import java.awt.Point;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.PhysicalEngine;
import model.Ball;
import model.Circuit;
import model.ObstacleLine;
import model.Vector;
import view.AParamObject;

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
	}

	@Test
	public void testRun() {
		
	}
	
	
	/* Ce test permet de verifier que la trajectoire et la vitesse des billes 
	 * sont correctement modifiees lors d'une collision.
	 * On cree deux billes de meme masse, leur vitesse est la meme et leur
	 * trajectoire est opposee au moment de la collision.
	 */
	
	@Test
	public void testResolveCollisionBallBallSameMass() {
		/* Cas  Collision horizontal*/
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
		
		/* Cas  Collision Vertical*/ 
		ball1.setAll(70, 100, 10, 2);
		ball2.setAll(70, 120, 10, 2);
		ball1.set_speed(0, 1);
		ball2.set_speed(0, -1);
		resolveCollBallBall.invoke(physical_engine, ball1, ball2);
		b1 = Math.abs(ball1.get_velocity().getY()+1) < 1E-10;
		b2 = Math.abs(ball2.get_velocity().getY()-1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas Collision a 45 degres */ 
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
	
	
	/* Ce test permet de verifier que la vitesse des billes
	 * est correctement modifiee lors d'une collision entre billes
	 * de masses differentes.
	 * On cree deux billes de masse differente et on varie leur vitesse.
	 * On verifie que notre moteur respecte les lois generales du choc elastique :
	 * conversation de la quantite de mouvement et conversation de l'energie cinetique. 
	 */
	@Test
	public void testResolveCollisionBallBallDifferentMass() {
		try {
		Method resolveCollBallBall = PhysicalEngine.class.getDeclaredMethod(
						"resolveCollisionBallBall", Ball.class, Ball.class);
		resolveCollBallBall.setAccessible(true);
		/* Cas meme vitesse */
		
		// Conservation quantite de mouvement 
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
		
		// Conservation energie cinetique
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
		
		/* Cas une bille immobile */
			
		// Conservation quantite de mouvement 
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
		
		// Conservation energie cinetique
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
		/* Cas deux vitesses diffirentes non nulles */
			
		// Conservation quantite de mouvement 
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
		
		// Conservation energie cinetique
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
	
	
	/* Ce test permet de verifier que la trajectoire et la vitesse d'une bille
	 * sont correctement modifiees lors d'une collision avec un obstacle.
	 * Le coefficient de rebond est egal a 1 (la quantite de mouvement et l'energie cinetique est conservee)
	 * choc elestique
	 */
	
	@Test
	public void testResolveCollisionBallObstacle() {
		
		try {
		Method resolveCollBallObstacle = PhysicalEngine.class.getDeclaredMethod(
				"resolveCollisionBallObstacle", Ball.class, ObstacleLine.class);
		resolveCollBallObstacle.setAccessible(true);
		obstacle.setCOR(1);
		/* Cas collision obstacle horizontal, bille direction vers le bas */ 
		obstacle.set_depart(new Point(20,100));
		obstacle.set_arrivee(new Point(100,100));
		ball1.setAll(50, 90, 10, 2);
		ball1.set_speed(0, 6);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		boolean b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		boolean b2 = Math.abs(ball1.get_velocity().getY()+6) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle horizontal, bille direction vers le haut */ 
		ball1.set_location(50, 110);
		ball1.set_speed(0, -5);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()-5) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle vertical, bille direction vers la droite */
		obstacle.set_depart(new Point(100,100));
		obstacle.set_arrivee(new Point(100,200));
		ball1.set_location(90, 150);
		ball1.set_speed(3, 0);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()+3) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle vertical, bille direction vers la gauche */
		obstacle.set_depart(new Point(100,100));
		obstacle.set_arrivee(new Point(100,200));
		ball1.set_location(110, 150);
		ball1.set_speed(-4, 0);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()-4) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle 45 degres, bille direction perpendiculaire */
		obstacle.set_depart(new Point(100,300));
		obstacle.set_arrivee(new Point(200,200));
		ball1.set_location(145, 245);
		ball1.set_speed(1, 1);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		b1 = Math.abs(ball1.get_velocity().getX()+1) < 1E-10;
		b2 = Math.abs(ball1.get_velocity().getY()+1) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle 45+180 degres, bille direction perpendiculaire*/
		obstacle.set_depart(new Point(100,100));
		obstacle.set_arrivee(new Point(200,200));
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
	public void testResolveCollisionBallObstacleCOR() {
		
		try {
		Method resolveCollBallObstacle = PhysicalEngine.class.getDeclaredMethod(
				"resolveCollisionBallObstacle", Ball.class, ObstacleLine.class);
		resolveCollBallObstacle.setAccessible(true);	
		obstacle.setCOR(0.5);
		/* Cas collision obstacle horizontal, bille direction vers le bas */ 
		obstacle.set_depart(new Point(20,100));
		obstacle.set_arrivee(new Point(100,100));
		ball1.setAll(50, 90, 10, 2);
		ball1.set_speed(0, 6);	
		resolveCollBallObstacle.invoke(physical_engine, ball1, obstacle);
		boolean b1 = Math.abs(ball1.get_velocity().getX()) < 1E-10;
		boolean b2 = Math.abs(ball1.get_velocity().getY()+3) < 1E-10;
		assertEquals(true, b1);
		assertEquals(true, b2);
		
		/* Cas collision obstacle 45 degres, bille direction perpendiculaire */
		obstacle.set_depart(new Point(100,300));
		obstacle.set_arrivee(new Point(200,200));
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
	
		
	@Test 
	public void testGetNormale() {
		
	}
	
	


	@Test
	public void testProjectionI() {
		
	}
	

	
	@Test
	public void testBallIsOutOfCircuit() {
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
