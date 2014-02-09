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
    public static final int VIDE=0, SOL=100, ARBRE=10, FEU=20 , EAU=30 , CENDRES=40;
    public static int getType(int i){return (i/10)*10;}
    public static int getVar(int i){return i%10;}
    public static int setVar(int val, int ajout){return (val/10)*10+(((val%10)+ajout)%10);}
}