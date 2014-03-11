/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage.Agents;

import java.util.ArrayList;

/**
 *
 * @author patrate
 */
public class Meute {
    
    private ArrayList<Agent> membres;
    private Agent chef;
    private int nbAgents;
    private int X, Y;
    private int distanceMax;
    private int nbMax;
    
    /**
     * Pour créer une meute, il faut au moins un couple d'agents.
     * @param a Le premier agent, qui devient chef
     * @param b Le second agent
     */
    public Meute(Agent a, Agent b)
    {
        this(a, b, 10, 10);
    }
    
    /**
     * Crée une meute avec une distanceMax et un nbMax personnalisé.
     * @param a
     * @param b
     * @param disM
     * @param nbM 
     */
    public Meute(Agent a, Agent b, int disM, int nbM)
    {
        membres = new ArrayList<Agent>();
        membres.add(a);
        membres.add(b);
        nbAgents = 2;
        chef = a;
        X = (a.getX()+b.getX())/2;
        Y = (a.getY()+b.getY())/2;
        distanceMax=disM;
        nbMax=nbM;
    }
    
    /**
     * Recrute l'agent si il reste de la place. Renvoie true si l'agent a été recruté.
     * @param a l'agent à recruter
     * @return Si l'agent a été recruté ou non.
     */
    public boolean tenteRecrute(Agent a)
    {
        if(nbAgents < nbMax){
            recrute(a);
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Ajoute un agent à la meute
     * @param a : L'agent à ajouter
     */
    public void recrute(Agent a)
    {
        membres.add(a);
        nbAgents++;
    }
    
    /**
     * Retire l'agent a de la meute
     * @param a : L'agent à retirer
     */
    public void retire(Agent a)
    {
        membres.remove(a);
        nbAgents--;
        if(a==chef){ 
            if(nbAgents>0){
                chef=membres.get(0);
            }else{
                chef=null;
            }
        }
    }
    
    
    public void majPos()
    {
        int valX=0, valY=0;
        for(Agent a: membres){
            valX+=a.getX();
            valY+=a.getY();
        }
        X=valX/nbAgents;
        Y=valY/nbAgents;
    }
    
    public int getNbAgents(){return nbAgents;}
    
    public int getDistMax(){return distanceMax;}
            
    public int getX(){return X;}
    public int getY(){return Y;}
    
    public Agent getChef(){return chef;}
    
}
