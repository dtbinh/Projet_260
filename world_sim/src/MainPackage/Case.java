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
    public static final int VIDE=0, ARBRE=10, FEU=20 , EAU=30 , CENDRES=40;
    // types de terrain: 0XXX à 9XXX
    public static final int TERRE=0000, ROCHE=1000, SABLE=2000, HERBE=3000;
    // altitudes: 0XXXX à 99XXXX, avec 0 = bas et 99 = haut          EXEMPLE: 852035 = 85 d'altitude, 2= sable, 03 = eau, 5 = profondeur moyenne
    
    public static int getVar(int i){return i%10;}
    public static int setVar(int val, int ajout){return (val/10)*10+(((val%10)+ajout)%10);}
    public static int getType(int i){return ((i/10)%100)*10;}
    public static int setType(int val, int type){return (val-(getType(val))+type);}
    public static int getTerrain(int i){return ((i/1000)%10)*1000;}
    public static int setTerrain(int val, int terrain){return (val-(getTerrain(val))+terrain);}
    public static int getAltitude(int i){return (i/10000)*10000;}
    
    
    // GENERATEUR DE MONDE:
    
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
    
    public static final int NIVEAUEAU=50000, NIVEAUSABLE=80000, NIVEAUHERBE=700000;
    
    /**
     * Génère un monde au hasard avec le bruit de Perlin. D'abord génère la hauteur,
     * Puis rajoute ce qu'il faut en fct de la hauteur (l'eau là où il faut etc...
     * @param largeur
     * @param hauteur
     * @return LE MONDE
     */
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
                
                for(int i=0; i<largeur;i++)
                {
                    for(int j=0; j<hauteur;j++)
                    {
                        ret[i][j]*=10000;
                        if(getAltitude(ret[i][j]) <= NIVEAUEAU){
                            ret[i][j] = setType(ret[i][j], EAU)+(int)(Math.random()*5)+3;
                        }
                        
                        if(getAltitude(ret[i][j]) <= NIVEAUSABLE){
                            ret[i][j] = setTerrain(ret[i][j], SABLE);
                        }else if(getAltitude(ret[i][j]) <= NIVEAUHERBE){
                            ret[i][j] = setTerrain(ret[i][j], HERBE);
                        }else{
                            ret[i][j] = setTerrain(ret[i][j], ROCHE);
                        }
                        
                    }
                }
                
                
                
                
                
                return ret;
            }
}