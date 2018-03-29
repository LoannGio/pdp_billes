package model;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/* L'utilisation d'un Quadtree nous permet d'optimiser le programme
 * en diminuant le nombre d'appels a la fonction checkCollisionBallBall.
 * Grace a un partionnement de l'espace, une bille verifie les collisions
 * avec ses billes voisines uniquement (et non plus toutes les billes
 * du circuit).
 * 
 * Au debut, notre QuadTree est un noeud racine qui represente une region : le circuit. 
 * On ajoute chaque bille dans le QuadTree et on incremente de 1 son nombre de billes.
 * Lorsqu'il a plus de 4 billes, on cree 4 fils QuadTree qui representent 4 sous-regions
 * de la region du pere. Ces sous regions sont de taille egale. Les billes du pere
 * sont reparties dans les fils. Cela est recursif jusqu'a ce que toutes les billes soient
 * ajoutees au Quadtree (jusqu'a un certain niveau de profondeur max = 20).
 * 
 * La fonction retrieve nous permet de retrouver les billes voisines d'une bille
 * grace a une recherche recursive dans le QuadTree.
 */	

public class Quadtree {
	 
	  private int MAX_OBJECTS = 4;
	  private int MAX_LEVELS = 20; 
	  private int level;
	  private List<Ball> objects;
	  private Rectangle bounds;
	  private Quadtree[] nodes;	  
	  
	  public Quadtree(int pLevel, Rectangle pBounds) {
	   level = pLevel;
	   objects = new ArrayList<Ball>();
	   bounds = pBounds;
	   nodes = new Quadtree[4];
	  }
	  
	  /* Supprime toutes les feuilles et noeuds jusqu'a avoir uniquement le QuadTree
	   * de depart : un noeud et 4 fils.
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
	   
	   /* Divise une region en 4 sous-regions de meme taille.
	    * Cela revient a creer 4 QuadTree fils.
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
	    
	    
	    
	    /* Cette fonction renvoie la sous-region dans laquelle se trouve la balle.
	     * 0 : cadre en haut a droite
	     * 1 : cadre en haut a gauche
	     * 2 : cadre en bas a gauche
	     * 3 : cadre en bas a droite
	     * Si la balle ne rentre pas completement dans une sous-region et qu'elle deborde
	     * sur une ou plusieurs autres, on considere qu'elle est dans la region du pere
	     * et on renvoie -1.
	     */
	    private int getIndex(Ball ball) {
	    	   int index = -1;
	    	   double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
	    	   double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);
	    	  
	    	   // La bille rentre entierement dans la partie superieure de l'ecran
	    	   boolean topQuadrant = ((ball.get_y() + ball.get_radius()) < horizontalMidpoint);
	    	   // La bille rentre entierement dans la partie inferieur de l'ecran
	    	   boolean bottomQuadrant = ((ball.get_y()-ball.get_radius()) > horizontalMidpoint);
	    	  
	    	   // La bille rentre entierement dans la partie gauche de l'ecran
	    	   if ((ball.get_x()+ball.get_radius()) < verticalMidpoint) {
	    	      if (topQuadrant) {
	    	        index = 1;
	    	      }
	    	      else if (bottomQuadrant) {
	    	        index = 2;
	    	      }
	    	    }
	    	   	//La bille rentre entierement dans la partie droite de l'ecran
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
	    
	    
	    /* Cette fonction nous permet d'inserer une bille dans le QuadTree.
	     * Si le nombre de billes depasse 4, on appelle la fonction qui
	     * divise le Quadtree en 4 Quadtree fils et on reinsert les billes
	     * dans ces fils.
	     */
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
	    
	   /* La fonction retrieve nous permet de retrouver les billes voisines d'une bille
	    * grace a une recherche recursive dans le QuadTree. Si une bille a un index de -1,
	    * elle est situee dans un Quadtree pere, on renvoie donc toutes les billes des 4 Quadtree fils.
	    */
	    public List<Ball> retrieve(List<Ball> returnObjects, Ball ball) {
            int index = getIndex(ball);
            if(index == -1 && nodes[0] != null) {
                nodes[0].retrieve(returnObjects, ball);
                nodes[1].retrieve(returnObjects, ball);
                nodes[2].retrieve(returnObjects, ball);
                nodes[3].retrieve(returnObjects, ball);
            }
            else if (index != -1 && nodes[0] != null) {
              nodes[index].retrieve(returnObjects, ball);
            }
            returnObjects.addAll(objects);
            return returnObjects;
          }
	}
