package model;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A Quadtree if useful for optimizing the program by reducing the number of calls
 * to checkCollisionBallBall() method. Thanks to space partitioning,
 * a ball only checks collisions with its neighboring balls (and not all balls of circuit).
 * 
 * At the beginning, the Quadtree is a root node that represents one area : the circuit.
 * Then, each ball is added to the Quadtree and its number of balls is incremented by 1.
 * When it has more than 4 balls, 4 QuadTree children are created, each one representing
 * a sub-area of its parent. These sub-areas all have the same size.
 * Balls included in the parent are distributed to its children. This process is
 * calls recursively until all balls are added to the Quadtree (max depth = 20).
 * 
 * The retrieved() method allows to find neighboring balls of a ball by a recursive
 * research in the Quadtree.
 */	

public class Quadtree {

	private int _MAX_OBJECTS = 4;
	private int _MAX_LEVELS = 20; 
	private int _level;
	private List<Ball> _objects;
	private Rectangle _bounds;
	private Quadtree[] _nodes;	  

	public Quadtree(int pLevel, Rectangle pBounds) {
		_level = pLevel;
		_objects = new ArrayList<Ball>();
		_bounds = pBounds;
		_nodes = new Quadtree[4];
	}

	/**
	 * Removes all leaves and nodes until coming back to original Quadtree :
	 * 1 node and 4 children.
	 */
	public void clear() {
		_objects.clear();

		for (int i = 0; i < _nodes.length; i++) {
			if (_nodes[i] != null) {
				_nodes[i].clear();
				_nodes[i] = null;
			}
		}
	}

	/**
	 * Divides one area in 4 sub-areas of same size.
	 * So, it comes back to creating 4 children Quadtrees.
	 */
	private void split() {
		int subWidth = (int)(_bounds.getWidth() / 2);
		int subHeight = (int)(_bounds.getHeight() / 2);
		int x = (int)_bounds.getX();
		int y = (int)_bounds.getY();

		_nodes[0] = new Quadtree(_level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
		_nodes[1] = new Quadtree(_level+1, new Rectangle(x, y, subWidth, subHeight));
		_nodes[2] = new Quadtree(_level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
		_nodes[3] = new Quadtree(_level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}



	/**
	 * This method returns the sub-area in which the Ball is located in.
	 * 0 : top-right corner
	 * 1 : top-left corner
	 * 2 : bottom-left corner
	 * 3 : bottom-right corner
	 * If Ball is in more than one sub-area, it is then considered as in parent sub-area
	 * and -1 is returned.
	 */
	private int getIndex(Ball ball) {
		int index = -1;
		double verticalMidpoint = _bounds.getX() + (_bounds.getWidth() / 2);
		double horizontalMidpoint = _bounds.getY() + (_bounds.getHeight() / 2);

		// Ball is only in top quadrant
		boolean topQuadrant = ((ball.get_y() + ball.get_radius()) < horizontalMidpoint);
		// Ball is only in bottom quadrant
		boolean bottomQuadrant = ((ball.get_y()-ball.get_radius()) > horizontalMidpoint);

		// Ball is only in left quadrant
		if ((ball.get_x()+ball.get_radius()) < verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			}
			else if (bottomQuadrant) {
				index = 2;
			}
		}
		//Ball is only in right quadrant
		else if ((ball.get_x()-ball.get_radius()) > verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			}
			else if (bottomQuadrant) {
				index = 3;
			}
		}
		return index;
	}


	/**
	 * This method inserts a Ball in the Quadtree.
	 * If the number of balls is more than 4, split() is called
	 * and balls are distributed to the new children Quadtrees.
	 */
	public void insert(Ball ball) {
		if (_nodes[0] != null) {
			int index = getIndex(ball);

			if (index != -1) {
				_nodes[index].insert(ball);	 
				return;
			}
		}

		_objects.add(ball);

		if (_objects.size() > _MAX_OBJECTS && _level < _MAX_LEVELS) {
			if (_nodes[0] == null) { 
				split(); 
			}

			int i = 0;
			while (i < _objects.size()) {
				int index = getIndex(_objects.get(i));
				if (index != -1) {
					_nodes[index].insert(_objects.remove(i));
				}
				else {
					i++;
				}
			}
		}
	}

	/**
	 * This method finds the neighboring balls of a Ball by recursively reseraching
	 * in the Quadtree. If a ball has an index equal to -1, then the ball is located
	 * in a parent Quadtree and all the balls from the 4 children Quadtrees are returned.
	 */
	public List<Ball> retrieve(List<Ball> returnObjects, Ball ball) {
		int index = getIndex(ball);
		if(index == -1 && _nodes[0] != null) {
			_nodes[0].retrieve(returnObjects, ball);
			_nodes[1].retrieve(returnObjects, ball);
			_nodes[2].retrieve(returnObjects, ball);
			_nodes[3].retrieve(returnObjects, ball);
		}
		else if (index != -1 && _nodes[0] != null) {
			_nodes[index].retrieve(returnObjects, ball);
		}
		returnObjects.addAll(_objects);
		return returnObjects;
	}
}
