package test.model;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.junit.Test;

import model.Ball;
import model.Quadtree;

public class QuadtreeTest {
	Quadtree _quad;

	/*
	 * PART 1 : We create and add 4 balls to the QuadTree. We consider a circuit
	 * of size 400x400 Every ball is placed on a circuit corner. Calling
	 * "retrieve" method, we check that every ball is in the same region as the
	 * 3 others.
	 *
	 * PART 2 : We create a 5th ball next to the ball number 4 and we add it to
	 * the QuadTree. Calling "retrieve" method, we check that the space
	 * partitioning (split method) is correct. Balls number 1, 2 and 3 remain
	 * alone in their region. Balls 4 and 5 are both in the same region.
	 */

	@Test
	public void test_case1() {

		/* PART 1 */

		int width = 400;
		int height = 400;
		_quad = new Quadtree(0, new Rectangle(0, 0, width, height));

		Ball b1 = new Ball(30, 30, 1, 1);
		Ball b3 = new Ball(30, 380, 1, 1);
		Ball b2 = new Ball(380, 30, 1, 1);
		Ball b4 = new Ball(380, 380, 1, 1);

		_quad.insert(b1);
		_quad.insert(b2);
		_quad.insert(b3);
		_quad.insert(b4);

		ArrayList<Ball> returnObjects = new ArrayList<Ball>();
		_quad.retrieve(returnObjects, b1);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b2);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b3);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b4);
		assertEquals(returnObjects.size(), 4);

		/* PART 2 */

		Ball b5 = new Ball(370, 370, 1, 1);
		_quad.insert(b5);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b1);
		assertEquals(returnObjects.size(), 1);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b2);
		assertEquals(returnObjects.size(), 1);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b3);
		assertEquals(returnObjects.size(), 1);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b4);
		assertEquals(returnObjects.size(), 2);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b5);
		assertEquals(returnObjects.size(), 2);

	}

	/*
	 * PART 1: Same as case 1
	 *
	 * PART 2 : We create a 5th ball with a large radius that we place at the
	 * center of the circuit. We add it to the Quadtree and the split() method
	 * is called. Due to her large size, the ball belongs to the four regions.
	 * We verify with the retrieve() method that the balls 1, 2, 3 and 4 belong
	 * to the same region that ball 5.
	 */
	@Test
	public void test_case2() {

		/* PART 1 */

		int width = 400;
		int height = 400;
		_quad = new Quadtree(0, new Rectangle(0, 0, width, height));

		Ball b1 = new Ball(30, 30, 1, 1);
		Ball b3 = new Ball(30, 380, 1, 1);
		Ball b2 = new Ball(380, 30, 1, 1);
		Ball b4 = new Ball(380, 380, 1, 1);

		_quad.insert(b1);
		_quad.insert(b2);
		_quad.insert(b3);
		_quad.insert(b4);

		ArrayList<Ball> returnObjects = new ArrayList<Ball>();
		_quad.retrieve(returnObjects, b1);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b2);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b3);
		assertEquals(returnObjects.size(), 4);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b4);
		assertEquals(returnObjects.size(), 4);

		/* PART 2 */

		Ball b5 = new Ball(200, 200, 50, 1);
		_quad.insert(b5);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b1);
		assertEquals(returnObjects.size(), 2);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b2);
		assertEquals(returnObjects.size(), 2);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b3);
		assertEquals(returnObjects.size(), 2);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b4);
		assertEquals(returnObjects.size(), 2);

		returnObjects.clear();
		_quad.retrieve(returnObjects, b5);
		assertEquals(returnObjects.size(), 5);
	}
}
