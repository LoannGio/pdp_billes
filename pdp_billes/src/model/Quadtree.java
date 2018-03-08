package model;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Quadtree {
	 
	  private int MAX_OBJECTS = 4;
	  private int MAX_LEVELS = 20;
	  
	  private int level;
	  private List<Ball> objects;
	  private Rectangle bounds;
	  private Quadtree[] nodes;	  
	  
	 /*
	  * Constructor
	  */
	  public Quadtree(int pLevel, Rectangle pBounds) {
	   level = pLevel;
	   objects = new ArrayList<Ball>();
	   bounds = pBounds;
	   nodes = new Quadtree[4];
	  }
	  
	  /*
	   * Clears the quadtree
	   */
	   public void clear() {
	     objects.clear();
	   
	     for (int i = 0; i < nodes.length; i++) {
	       if (nodes[i] != null) {
	         nodes[i].clear();
	         nodes[i] = null;
	       }
	     }
	   }
	   
	   /*
	    * Splits the node into 4 subnodes
	    */
	    private void split() {
	      int subWidth = (int)(bounds.getWidth() / 2);
	      int subHeight = (int)(bounds.getHeight() / 2);
	      int x = (int)bounds.getX();
	      int y = (int)bounds.getY();
	    
	      nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
	      nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
	      nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
	      nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	    }
	    
	    
	    
	    /*
	     * Determine which node the object belongs to. -1 means
	     * object cannot completely fit within a child node and is part
	     * of the parent node
	     */
	    private int getIndex(Ball ball) {
	    	   int index = -1;
	    	   double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
	    	   double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);
	    	  
	    	   // Object can completely fit within the top quadrants
	    	   boolean topQuadrant = ((ball.get_y() + ball.get_radius()) < horizontalMidpoint);
	    	   // Object can completely fit within the bottom quadrants
	    	   boolean bottomQuadrant = ((ball.get_y()-ball.get_radius()) > horizontalMidpoint);
	    	  
	    	   // Object can completely fit within the left quadrants
	    	   if ((ball.get_x()+ball.get_radius()) < verticalMidpoint) {
	    	      if (topQuadrant) {
	    	        index = 1;
	    	      }
	    	      else if (bottomQuadrant) {
	    	        index = 2;
	    	      }
	    	    }
	    	    // Object can completely fit within the right quadrants
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
	    
	    
	    
	    public void insert(Ball ball) {
	    	   if (nodes[0] != null) {
	    	     int index = getIndex(ball);
	    	     
	  
	    	     
	    	     if (index != -1) {
	    	       nodes[index].insert(ball);	 
	    	       return;
	    	     }
	    	   }
	    	 
	    	   objects.add(ball);
	    	 
	    	   if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
	    	      if (nodes[0] == null) { 
	    	         split(); 
	    	      }
	    	 
	    	     int i = 0;
	    	     while (i < objects.size()) {
	    	       int index = getIndex(objects.get(i));
	    	       if (index != -1) {
	    	         nodes[index].insert(objects.remove(i));
	    	       }
	    	       else {
	    	         i++;
	    	       }
	    	     }
	    	   }
	    	 }
	    
	    public List<Ball> retrieve(List<Ball> returnObjects, Ball ball) {
	    	   int index = getIndex(ball);
	    	   if (index != -1 && nodes[0] != null) {
	    	     nodes[index].retrieve(returnObjects, ball);
	    	   }
	    	 
	    	   returnObjects.addAll(objects);
	    	 
	    	   return returnObjects;
	    	 }
	}
