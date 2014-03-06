/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage;

import java.io.*;
import java.util.Scanner;

/**
 *
 * @author patrate
 */
public class Case {
    // contenu dans les cases: 0X à 99X
    public static final int VIDE=0, ARBRE=10, FEU=20 , EAU=30 , CENDRES=40, LAVE=50, GENLAVE=100, GENEAU=110, EAUMOD=990;
    // types de terrain: 0X à 99X
    public static final int TERRE=0, ROCHE=10, SABLE=20, HERBE=30;
    // altitudes: 0 à 99, avec 0 = bas et 99 = haut
    
    public static int getVar(int i){return i%10;}
    public static int getVal(int i){return (i/10)*10;}
    
    // GENERATEUR DE MONDE:
    
    public static final int NIVEAUEAU=5, NIVEAUSABLE=8, NIVEAUHERBE=70, NIVEAULAVE=98;
    /**
     * Fabrique le monde
     * Tableau 1= altitude
     * Tableau 2= terrain
     * Tableau 3= objets
     */
    private static int[][][] makeWorld(int tab[][])
    {
        //tableau 2 = item,
        //tableau 1 = terrain
        //tableau 0 = altitude
        int returnTab[][][] = new int[3][tab.length][tab[0].length];
        for(int i=0; i<tab.length;i++){
            for(int j=0; j<tab[0].length;j++){
                returnTab[0][i][j]=tab[i][j];
                if(tab[i][j] <= NIVEAUEAU){
                    if(tab[i][j]==0)
                        returnTab[2][i][j] = Case.GENEAU;
                    else
                        returnTab[2][i][j] = Case.EAU+4;
                }
                if(tab[i][j] <= NIVEAUSABLE){
                    returnTab[1][i][j] = Case.SABLE;
                }else if(tab[i][j] <= NIVEAUHERBE){
                    returnTab[1][i][j] = Case.HERBE;
                }else{
                    returnTab[1][i][j] = Case.ROCHE;
                    if(tab[i][j] >= NIVEAULAVE){
                        returnTab[2][i][j] = Case.GENLAVE;
                    }
                }
            }
        }
            
        return returnTab;
    }
    
    // A partir d'images niveau de gris (PGM)
    
    /**
     * Crée un monde dont les altitudes correspondent aux niveaux de gris de l'image
     * au format pgm passé en paramètre.
     * @param nom
     * @return 
     */
    public static int[][][] generateurImage1(String nom)
    {
        int ret[][] = new int[0][0];
        short max=0;
        
        //On ouvre le fichier nom, on met ses valeurs dans ret
        try{
            Scanner lecteur;
            File image = new File(nom);
            lecteur = new Scanner(image);
            
            
            short etape=0;
            int hauteur=0,largeur=0;  //largeur = nb de lignes, hauteur= nb de colonnes
            short lecture=0;
            
            String ligne=lecteur.next();
            while (lecteur.hasNext()){
                if(!ligne.startsWith("#")){
                    switch(etape){
                        case 0:
                            if(ligne.equals("P2")){
                                etape = 1;
                                ligne = lecteur.next();
                            }else{
                                etape = -1;
                                ligne = lecteur.next();
                            }
                            break;
                        case 1:
                            largeur = Integer.valueOf(ligne);
                            ligne = lecteur.next();
                            hauteur = Integer.valueOf(ligne);
                            ret = new int[largeur][hauteur];
                            etape = 2;
                            ligne = lecteur.next();
                            break;
                        case 2:
                            max=Short.valueOf(ligne);
                            etape = 3;
                            ligne = lecteur.next();
                            break;
                        case 3:
                            for(int i=0;i<largeur;i++){
                                ret[i][lecture] = Integer.valueOf(ligne);
                                if(lecteur.hasNext()){
                                    ligne=lecteur.next();
                                    while(ligne.startsWith("#")){
                                        ligne=lecteur.next();
                                    }
                                }
                            }
                            lecture++;
                            break;
                        default:
                            System.out.println("Error");
                    }
                }else{
                    lecteur.nextLine();
                    ligne = lecteur.next();
                }
            }
            lecteur.close();
        }
        catch (Exception e){
                System.out.println("ERREUR: "+ e.toString());
        }
        
        // Pour chaque pixel de l'image, altitude du pixel du tableau = (int)((pixel/255)*99)
        for(int i=0;i<ret.length;i++){
            for(int j=0;j<ret[0].length;j++){
                ret[i][j] = (ret[i][j]*99)/max;
            }
        }
        
        int returnTab[][][] = makeWorld(ret);
        
        return returnTab;
    }
    
    
    
    //ALEATOIRE + BRUIT DE PERLIN
    
    // SOURCE: http://fr.openclassrooms.com/informatique/cours/bruits-et-nombres-aleatoires-coherents
    private static final double PERSISTANCE=0.7;
    private static final int PAS=5;
    private static final int OCTAVES=2;
    
    
    private static double  interpolation_lineaire(double a, double b, double x)
        {
            return a * (1 - x) + b * x;
        }
    
    private static double interpolation_cos(double a, double b, double x)
        {
            double k = (1 - Math.cos(x * Math.PI)) / 2;
            return interpolation_lineaire(a, b, k);
        }
    
    
    private static double interpolation_cos2D(double a, double b, double c, double d, double x, double y)
        {
            double x1 = interpolation_cos(a, b, x);
            double x2 = interpolation_cos(c, d, x);
            return interpolation_cos(x1, x2, y);
        }
    
    private static double fonction_bruit2D(double x, double y, int tab[][]) 
    {
       int tmpPAS=((tab.length+tab[0].length)/2)/PAS;
       int i = (int) (x / tmpPAS);
       int j = (int) (y / tmpPAS);
       return interpolation_cos2D(tab[j][i], tab[j][i+1], tab[j+1][i], tab[j+1][i+1], (x / PAS) % 1, (y / PAS) % 1);
    }
    
    
    private static double bruit_coherent2D(double x, double y, double persistance, int nombre_octaves, int tab[][]) {
        
            double somme = 0;
            double p = 1;
            int f = 1;

            for(int i = 0 ; i < nombre_octaves ; i++) {
                    somme += p * fonction_bruit2D(x * f, y * f, tab);
                    p *= persistance;
                    f *= 2;
            }

            return somme * (1 - persistance) / (1 - p);
    }
    
    
    private static int[][] initBruit2D(int longueur, int hauteur){
            
            int[][] ret = new int[longueur][hauteur];
            int tmpPAS=((longueur+hauteur)/2)/PAS;
            for(int i = 0; i < longueur/tmpPAS; i++){
                for(int j = 0; j < hauteur/tmpPAS; j++){
                    ret[i*tmpPAS][j*tmpPAS] = (int)(Math.random()*99);
                }
            }
            
            return ret;
        }
    
    //FIN DU CODE C/C (qu'on a adapté et compris quand meme)
    
    
    /**
     * Génère un monde au hasard avec le bruit de Perlin. D'abord génère la hauteur,
     * Puis rajoute ce qu'il faut en fct de la hauteur (l'eau là où il faut etc...
     * @param largeur
     * @param hauteur
     * @return LE MONDE
     */
    /*
    public static int[][] generateurPerlin(int largeur, int hauteur)
            {
                int[][] ret = initBruit2D(largeur, hauteur);
                for(int i=0; i<largeur;i++)
                {
                    for(int j=0; j<hauteur;j++)
                    {
                        ret[i][j]=(int)(bruit_coherent2D((double) i, (double) j, PERSISTANCE, OCTAVES, ret));
                    }
                }
                
                ret = makeWorld(ret);
                
                return ret;
            }
    */
}