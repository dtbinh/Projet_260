/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage.Agents;

/**
 *
 * @author 3361692
 */
public class Village extends Meute{
    
    public Village(Agent a, Agent b)
    {
        super(a, b, 15, 20);
        int valX=0, valY=0;
        valX+=a.getX();
        valY+=a.getY();
        X=valX/nbAgents;
        Y=valY/nbAgents;
    }
    
    @Override public void majPos()
    {
        // Le centre du village ne change pas
    }
    
    @Override public boolean merge(Meute x){
        boolean ret = super.merge(x);
        if(ret){
            X = (X+x.X)/2;
            Y = (Y+x.Y)/2;
            x.X = (X+x.X)/2;
            x.Y = (Y+x.Y)/2;
        }
        return ret;
    }
}
