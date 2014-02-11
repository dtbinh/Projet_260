/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage;

/**
 *
 * @author patrate
 */
public class Case {
    // contenu dans les cases: 0X à 99X
    public static final int VIDE=0, SOL=100, ARBRE=10, FEU=20 , EAU=30 , CENDRES=40;
    // types de terrain: 0XXX à 9XXX
    public static final int TERRE=0000, ROCHE=1000, SABLE=2000;
    // altitudes: 0XXXX à 99XXXX, avec 0 = bas et 99 = haut
    
    public static int getVar(int i){return i%10;}
    public static int setVar(int val, int ajout){return (val/10)*10+(((val%10)+ajout)%10);}
    public static int getType(int i){return ((i/10)%100)*10;}
    public static int setType(int val, int type){return val-(getType(val))+type;}
    public static int getTerrain(int i){return ((i/1000)%10)*1000;}
    public static int setTerrain(int val, int terrain){return val-(getTerrain(val))+terrain;}
    public static int getAltitude(int i){return ((i/10000)%100)*10000;}
}