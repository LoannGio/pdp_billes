package test.controller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;

public class PhysicalEngineTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRun() {
		
	}

	@Test
	public void testStop() {
		
	}

	@Test
	public void testBallIsOutOfPanel() {
		
	}

	
	@Test
	public void testAngleCollisionBall() {
		
	}
	
	@Test
	public void testResolveCollisionBallBall() {
		
	}
	

	
	@Test
	public void testResolveCollisionBallObstacle() {
		
	}
	
	@Test 
	public void testGetNormale() {
		
	}
	
	


	@Test
	public void testProjectionI() {
		
	}
	

	
	@Test
	public void testBallIsOutOfCircuit() {
		Ball ball = new Ball(495,400,10,1);
		assertEquals(c.ballIsOutOfCircuit(ball), false);
		ball.set_location(510, 500);
		assertEquals(c.ballIsOutOfCircuit(ball), true);
	}

}
