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
        for(int i=0;i<4;i++){
            ret[i]=false;
        }
        if(r.getX() + r.getWidth() > bounds.getX() + bounds.getWidth()
                || r.getX() < bounds.getX()
                || r.getY() + r.getHeight() > bounds.getY() + bounds.getHeight()
                || r.getY() < bounds.getY()){
            ret[4] = true;
        }
        double verticalMidpoint = bounds.x + (bounds.width / 2);
        double horizontalMidpoint = bounds.y + (bounds.height / 2);
        boolean topQuadrant = (r.y <= horizontalMidpoint);
        boolean bottomQuadrant = (r.y + r.height >= horizontalMidpoint);
        if (r.x <= verticalMidpoint){
            if (topQuadrant){
                ret[NW] = true;
            }if (bottomQuadrant){
                ret[SW] = true;
            }
        }if (r.x + r.width >= verticalMidpoint){
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
                    parent.insert(Agents.remove(i));
                }
            }
            Agents.clear();
        }
    }
    
    /**
     * Met à jour la position de l'agent dans le quadtree.
     */
    public void maj(Agent r){
        int index = getIndex(r);
        if(nodes[0]!=null){
            nodes[index].maj(r);
        }else{
            if(!Agents.contains(r)){
                QuadTree test;
                boolean done=false;
                test=getLeft();
                if(test!=null){
                    if(test.Agents.contains(r)){
                        test.remove(r);
                        done=true;
                    }
                }
                if(done=false){
                    test=getRight();
                    if(test!=null){
                        if(test.Agents.contains(r)){
                            test.remove(r);
                            done=true;
                        }
                    }
                }
                if(done=false){
                    test=getUp();
                    if(test!=null){
                        if(test.Agents.contains(r)){
                            test.remove(r);
                            done=true;
                        }
                    }
                }
                if(done=false){
                    test=getDown();
                    if(test!=null){
                        if(test.Agents.contains(r)){
                            test.remove(r);
                            done=true;
                        }
                    }
                }
                insert(r);
            }
        }
    }

    /**
     * Renvoie le noeud à gauche du noeud courant, null s'il n'y en a pas.
     * @return 
     */
    private QuadTree getLeft(){
        QuadTree ret=null;
        if(parent!=null){
            int index = parent.getIndex(new java.awt.Point(bounds.x-1, bounds.y));
            if(index==-1){
                ret = parent.getLeft();
            }else{
                ret = parent.nodes[index];
            }
        }
        return ret;
    }
    
    /**
     * Renvoie le noeud à droite du noeud courant, null s'il n'y en a pas.
     * @return 
     */
    private QuadTree getRight(){
        QuadTree ret=null;
        if(parent!=null){
            int index = parent.getIndex(new java.awt.Point(bounds.x+bounds.width+1, bounds.y));
            if(index==-1){
                ret = parent.getRight();
            }else{
                ret = parent.nodes[index];
            }
        }
        return ret;
    }
    
    /**
     * Renvoie le noeud au dessus du noeud courant, null s'il n'y en a pas.
     * @return 
     */
    private QuadTree getUp(){
        QuadTree ret=null;
        if(parent!=null){
            int index = parent.getIndex(new java.awt.Point(bounds.x, bounds.y-1));
            if(index==-1){
                ret = parent.getUp();
            }else{
                ret = parent.nodes[index];
            }
        }
        return ret;
    }
    
    /**
     * Renvoie le noeud en dessous du noeud courant, null s'il n'y en a pas.
     * @return 
     */
    private QuadTree getDown(){
        QuadTree ret=null;
        if(parent!=null){
            int index = parent.getIndex(new java.awt.Point(bounds.x, bounds.y+bounds.height+1));
            if(index==-1){
                ret = parent.getUp();
            }else{
                ret = parent.nodes[index];
            }
        }
        return ret;
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
           }else{
               maj(r);
               remove(r);
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
        }else{
            retrieveList.addAll(Agents);
        }
        return retrieveList;
    }
   
    /**
     * Returns the collidable Agents with the given rectangle
     */
    public ArrayList<Agent> retrieve(Rectangle r){
        retrieveList.clear();
        boolean index[] = getIndex(r);
        if(nodes[0] != null){
            for(int i=0; i<4; i++){
                if(index[i]){
                    retrieveList.addAll(nodes[i].retrieve(r));
                }
            }
        }else{
            retrieveList.addAll(Agents);
        }
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
