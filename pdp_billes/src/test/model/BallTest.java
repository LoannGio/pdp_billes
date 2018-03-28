package test.model;

import static org.junit.Assert.assertEquals;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Ball;
import model.Circuit;
import model.Vector;

public class BallTest {
	Ball ball1;
	Ball ball2;
	Circuit _circuit;
	
	@Before
	public void setUp() throws Exception {
		ball1 = new Ball(0, 0, 0, 1);
		ball2 = new Ball(0, 0, 0, 1);
		_circuit = new Circuit(1000,800);
	}

	@After
	public void tearDown() throws Exception {
		ball1 = null;
		ball2 = null;
		_circuit = null;
	}

	
	@Test
	public void test_ballSetLocation() {
		assert(ball1.get_x() == 0 && ball1.get_y()==0);
		ball1.set_location(10, 10);
		assert(ball1.get_x() == 10 && ball1.get_y() == 10);
	}

	
	@Test
	public void test_ballSetSpeed() {
		assert(ball1.get_velocity().getX() == 0.0);
		assert(ball1.get_velocity().getY() == 0.0);
		ball1.set_speed(2.5, 4.0);
		assert(ball1.get_velocity().getX() == 2.5);
		assert(ball1.get_velocity().getY() == 4.0);
	}
	
	
	@Test
	public void testStep() {
		double x_init = 100;
		double y_init = 100;
		ball1 = new Ball(x_init, y_init, 3, 1);
		ball1.step(_circuit.get_acceleration());
		boolean b1 = (x_init == ball1.get_x());
		boolean b2 = (y_init < ball1.get_y());
		assertEquals(true, b1);
		assertEquals(true, b2);
	}
	
	@Test
	public void testStepLoop() {
		double x_init = 100;
		double y_init = 100;
		double x_prec = x_init;
		double y_prec = y_init; 
		ball1 = new Ball(x_init, y_init, 3, 1);
		for(int i=0; i<10; i++) {
			ball1.step(_circuit.get_acceleration());
			boolean b1 = (x_prec == ball1.get_x());
			boolean b2 = (y_prec < ball1.get_y());
			assertEquals(true, b1);
			assertEquals(true, b2);
			x_prec = ball1.get_x();
			y_prec = ball1.get_y();
		}	
	}
	
	@Test
	public void testStepInclinaison() {
		double x_init = 100;
		double y_init = 100;
		ball1 = new Ball(x_init, y_init, 3, 1);
		ball2 = new Ball(x_init, y_init, 3, 1);
		double angle1 = 20;
		double angle2 = 35;
		
		_circuit.set_inclinaison(angle1);
		Vector acceleration1 = new Vector(_circuit.get_acceleration().getX(),
										  _circuit.get_acceleration().getY());
		_circuit.set_inclinaison(angle2);
		Vector acceleration2 = new Vector(_circuit.get_acceleration().getX(),
				  _circuit.get_acceleration().getY());
		
		for(int i=0; i<10; i++) {
			ball1.step(acceleration1);
			ball2.step(acceleration2);
			boolean b1 = (ball1.get_x() == ball2.get_x());
			boolean b2 = (ball1.get_y() < ball2.get_y());
			assertEquals(true, b1);
			assertEquals(true, b2);
		}	
	}
	
}
