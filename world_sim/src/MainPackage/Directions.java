package MainPackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author clement
 */
public enum Directions {
    NONE(-1,-1), SUD(0,3), EST(1,2), OUEST(2,1), NORD(3,0);
    private int val;
    private int inverse;
    private Directions(int val, int inverse){this.val=val; this.inverse=inverse;}
    
    public int getVal(){return this.val;}
    public int getInv(){return this.inverse;}
    public static Directions getRandDir()
    {
        int r=(int)(Math.random()*5);
        switch(r){
            case 0:
                return Directions.SUD;
            case 1:
                return Directions.EST;
            case 2:
                return Directions.OUEST;
            case 3:
                return Directions.NORD;
            default:
                return Directions.NONE;
        }
    }
}
