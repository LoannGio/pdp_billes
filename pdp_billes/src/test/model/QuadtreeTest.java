package test.model;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.junit.Test;

import model.Ball;
import model.Quadtree;

public class QuadtreeTest {
    Quadtree _quad;

    /* PARTIE 1:
     * On cree 4 billes qu'on ajoute au QuadTree.
     * On considere que le circuit est de taille 400x400.
     * Chaque bille est positionnee dans un coin de l'ecran.
     * A l'aide la fonction retrieve on verifie que chaque balle
     * est dans la meme region que les 3 autres, c'est-a-dire qu'elles
     * sont toutes des feuilles du meme noeud.
     *
     * PARTIE 2:
     * On cree une 5eme bille juste a cote de la balle 4 et on l'ajoute au QuadTree.
     * A l'aide de la fonction retrieve, on verifie que le partitionnement de l'espace
     * (fonction split) est correct. Les balles 1,2 et 3 se retrouve seules dans leur region
     * (feuille unique d'un nouveau noeud). Les balles 4 et 5 sont toutes les 2 dans la meme region.
     */
    
    @Test
    public void testCas1() {
        
        /* PART 1 */
        
        int width = 400;
        int height = 400;
        _quad = new Quadtree(0, new Rectangle(0, 0, width, height));
        
        Ball b1 = new Ball(30,30,1,1);
        Ball b3 = new Ball(30,380,1,1);
        Ball b2 = new Ball(380,30,1,1);
        Ball b4 = new Ball(380,380,1,1);

        _quad.insert(b1);
        _quad.insert(b2);
        _quad.insert(b3);
        _quad.insert(b4);

        ArrayList<Ball> returnObjects = new ArrayList<Ball>();
        _quad.retrieve(returnObjects, b1);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b2);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b3);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b4);
        assertEquals(returnObjects.size(),4);
        
        /* PART 2 */
        
        Ball b5 = new Ball(370,370,1,1);
        _quad.insert(b5);
            
        returnObjects.clear();
        _quad.retrieve(returnObjects, b1);
        assertEquals(returnObjects.size(),1);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b2);
        assertEquals(returnObjects.size(),1);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b3);
        assertEquals(returnObjects.size(),1);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b4);
        assertEquals(returnObjects.size(),2);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b5);
        assertEquals(returnObjects.size(),2);
            
    }
    
    
    /* PARTIE 1:
     * Idem que Cas1
     *
     * PARTIE 2:
     * On cree une 5eme bille de gros rayon au centre du circuit et on l'ajoute au QuadTree.
     * La 5ème bille doit appartenir aux
     * A l'aide de la fonction retrieve, on verifie que le partitionnement de l'espace
     * (fonction split) est correct. Les balles 1,2 et 3 se retrouve seules dans leur region
     * (feuille unique d'un nouveau noeud). Les balles 4 et 5 sont toutes les 2 dans la meme region.
     */
    @Test
    public void testCas2() {
        
        /* PART 1 */
        
        int width = 400;
        int height = 400;
        _quad = new Quadtree(0, new Rectangle(0, 0, width, height));
        
        Ball b1 = new Ball(30,30,1,1);
        Ball b3 = new Ball(30,380,1,1);
        Ball b2 = new Ball(380,30,1,1);
        Ball b4 = new Ball(380,380,1,1);

        _quad.insert(b1);
        _quad.insert(b2);
        _quad.insert(b3);
        _quad.insert(b4);

        ArrayList<Ball> returnObjects = new ArrayList<Ball>();
        _quad.retrieve(returnObjects, b1);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b2);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b3);
        assertEquals(returnObjects.size(),4);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b4);
        assertEquals(returnObjects.size(),4);
        
        /* PART 2 */
        
        Ball b5 = new Ball(200,200,50,1);
        _quad.insert(b5);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b1);
        assertEquals(returnObjects.size(),2);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b2);
        assertEquals(returnObjects.size(),2);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b3);
        assertEquals(returnObjects.size(),2);
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b4);
        assertEquals(returnObjects.size(),2);    
        
        returnObjects.clear();
        _quad.retrieve(returnObjects, b5);
        assertEquals(returnObjects.size(),5);        
    }

}


