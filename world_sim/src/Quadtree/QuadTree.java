package Quadtree;

import java.awt.Rectangle;
import java.util.ArrayList;

import MainPackage.Agents.Agent;

/**
 * A QuadTree implementation to reduce collision checks. Every level contains
 * a maximum of 10 Agents and the tree sub divides on exceeding this limit.
 *
 * @author Sri Harsha Chilakapati
 */
public class QuadTree {

    //Directions
    private final int  NE = 0, NW = 1, SW = 2, SE = 3;
    
    // The MAX_AgentS and MIN_taille constants
    private final int MAX_AgentS = 10;
    private final int MIN_taille = 9;
   
    // The Agents list
    private ArrayList<Agent> Agents;
    // The retrieve list
    private ArrayList<Agent> retrieveList;

    // The bounds of this tree
    private Rectangle bounds;
    
    // Parent du noeud
    private QuadTree parent;

    // Branches of this tree a.k.a the quadrants
    private QuadTree[] nodes;


    /**
     * Construit le noeud racine, avec un parent null.
     * @param b The bounds of this tree
     */
    public QuadTree(Rectangle b) {
        this(b, null);
    }
    
    
    
    /**
     * Construct a QuadTree with custom values. Used to create sub trees or branches
     * @param b The bounds of this tree
     * @param p Le noeud parent. Null pour la racine
     */
    public QuadTree(Rectangle b, QuadTree p) {
        parent = p;
        bounds = b;
        Agents = new ArrayList<Agent>();
        retrieveList = new ArrayList<Agent>();
        nodes = new QuadTree[4];
    }
   

    /**
     * Clear this tree. Also clears any subtrees.
     */
    public void clear(){
        Agents.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    // Split the tree into 4 quadrants
    private void split(){
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();
        nodes[NW] = new QuadTree(new Rectangle(x, y, subWidth, subHeight), this);
        nodes[NE] = new QuadTree(new Rectangle(x + subWidth, y, subWidth, subHeight), this);
        nodes[SW] = new QuadTree(new Rectangle(x, y + subHeight, subWidth, subHeight), this);
        nodes[SE] = new QuadTree(new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight), this);
    }

    // Get the index of an Agent
    private int getIndex(Agent r){
        if(r.getX() > bounds.getX() + bounds.getWidth()
                || r.getX() < bounds.getX()
                || r.getY() > bounds.getY() + bounds.getHeight()
                || r.getY() < bounds.getY()){
            return -1;
        }
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);
        boolean topQuadrant = (r.getY() < horizontalMidpoint);
        boolean bottomQuadrant = (r.getY() >= horizontalMidpoint);
        if (r.getX() < verticalMidpoint){
            if (topQuadrant){
                index = NW;
            } else if (bottomQuadrant){
                index = SW;
            }
        } else if (r.getX() >= verticalMidpoint){
            if (topQuadrant){
                index = NE;
            } else if (bottomQuadrant){
                index = SE;
            }
        }
        return index;
    }
   
    /**
     * Renvoie une liste de boolean correspondant aux endroits occupés du rectangle.
     * @param r le rectangle 
     * @return les booleans indiquant les cases occupées (5eme emplacement si ça sort du cadre)
     */
    private boolean[] getIndex(Rectangle r){
        boolean ret[] = new boolean[5];
        if(r.getX() + r.getWidth() > bounds.getX() + bounds.getWidth()
                || r.getX() < bounds.getX()
                || r.getY() + r.getHeight() > bounds.getY() + bounds.getHeight()
                || r.getY() < bounds.getY()){
            ret[4] = true;
        }
        double verticalMidpoint = bounds.x + (bounds.width / 2);
        double horizontalMidpoint = bounds.y + (bounds.height / 2);
        boolean topQuadrant = (r.y < horizontalMidpoint);
        boolean bottomQuadrant = (r.y + r.height >= horizontalMidpoint);
        if (r.x < verticalMidpoint){
            if (topQuadrant){
                ret[NW] = true;
            }if (bottomQuadrant){
                ret[SW] = true;
            }
        } else if (r.x + r.width >= verticalMidpoint){
            if (topQuadrant){
                ret[NE] = true;
            }if (bottomQuadrant){
                ret[SE] = true;
            }
        }
        return ret;
    }
   
    // Get the index of a Point
    private int getIndex(java.awt.Point r){
        if(r.getX() > bounds.getX() + bounds.getWidth()
                || r.getX() < bounds.getX()
                || r.getY() > bounds.getY() + bounds.getHeight()
                || r.getY() < bounds.getY()){
            return -1;
        }
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);
        boolean topQuadrant = (r.getY() < horizontalMidpoint);
        boolean bottomQuadrant = (r.getY() >= horizontalMidpoint);
        if (r.getX() < verticalMidpoint){
            if (topQuadrant){
                index = NW;
            } else if (bottomQuadrant){
                index = SW;
            }
        } else if (r.getX() >= verticalMidpoint){
            if (topQuadrant){
                index = NE;
            } else if (bottomQuadrant){
                index = SE;
            }
        }
        return index;
    }
    
    /**
     * Insert an Agent into this tree
     */
    public void insert(Agent r){
        if (nodes[0]!=null){
            int index = getIndex(r);
            if (index!=-1){
                nodes[index].insert(r);
                return;
            }else{
                parent.insert(r);
            }
        }
        Agents.add(r);
        if (Agents.size() > MAX_AgentS
                && (bounds.getWidth() / 2) >= MIN_taille
                && (bounds.getHeight() / 2) >= MIN_taille){
            if (nodes[0]==null){
                split();
            }
            for (int i=0; i<Agents.size(); i++){
                int index = getIndex(Agents.get(i));
                if (index!=-1){
                    nodes[index].insert(Agents.remove(i));
                }else{
                    parent.insert(r);
                }
            }
            Agents.clear();
        }
    }
    
    /**
     * Met à jour l'emplacement des agents dans le quadtree.
     * Trop manavore, pas utiliser
     */
    @Deprecated public void maj(){
        if(nodes[0]!=null){
            for(int i=0; i<4; i++){
                nodes[i].maj();
            }
        }else{
            for (int i=0; i<Agents.size(); i++){
                int index = getIndex(Agents.get(i));
                if (index==-1){
                    parent.insert(Agents.get(i));
                    Agents.remove(Agents.get(i));
                }
            }
        }
    }
    
    /**
     * Met à jour la position de l'agent dans le quadtree.
     */
    public void maj(Agent r){
        if(nodes[0]!=null){
            for(int i=0; i<4; i++){
                nodes[i].maj(r);
            }
        }else{
            int index = getIndex(r);
            if (index==-1){
                parent.insert(r);
                Agents.remove(r);
            }
        }
    }
   
    /**
     * Insert an ArrayList of Agents into this tree
     */
    public void insert(ArrayList<Agent> o){
        for (int i=0; i<o.size(); i++){
            insert(o.get(i));
        }
    }
    
    /**
     * Retire un agent du quadtree si il est dedans
     */
   public void remove(Agent r){
       if(nodes[0]!=null){
           nodes[getIndex(r)].remove(r);
       }else{
           if(Agents.contains(r)){
               Agents.remove(r);
           }
       }
   }
    
    
    /**
     * Returns the collidable Agents with the given Agent
     */
    public ArrayList<Agent> retrieve(Agent r){
        retrieveList.clear();
        int index = getIndex(r);
        if (index != -1 && nodes[0] != null){
            retrieveList = nodes[index].retrieve(r);
        }
        retrieveList.addAll(Agents);
        return retrieveList;
    }
   
    /**
     * Returns the collidable Agents with the given rectangle
     */
    public ArrayList<Agent> retrieve(Rectangle r){
        retrieveList.clear();
        boolean index[] = getIndex(r);
        if (nodes[0] != null){
            for(int i=0; i<4; i++){
                if(index[i]){
                    retrieveList.addAll(nodes[i].retrieve(r));
                }
            }
        }
        retrieveList.addAll(Agents);
        return retrieveList;
    }
    
    /**
     * Returns the collidable Agents with the given Point
     */
    public ArrayList<Agent> retrieve(java.awt.Point r){
        retrieveList.clear();
        int index = getIndex(r);
        if (index != -1 && nodes[0] != null){
            retrieveList = nodes[index].retrieve(r);
        }
        retrieveList.addAll(Agents);
        return retrieveList;
    }
   
}
