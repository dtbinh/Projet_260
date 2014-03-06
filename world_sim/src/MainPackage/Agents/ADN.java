/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage.Agents;

/**
 *
 * @author 3361692
 */
public class ADN {
    private int valeur;
    
    protected static final int TAILLEADN=12;
    public static final int
            FAIM1=1,
            FAIM2=2,
            AGE1=4,
            AGE2=8,
            VITESSE1=16,
            VITESSE2=32,
            VISION1=64,
            VISION2=128,
            AVEUGLE=256,
            TETARPLEGIQUE=512,
            MALADIEMORTELLE=1024,
            VISION_NOCTURNE=2048
            ;
    
    private final double pModGen=0.01;
    
    public int getVal(){return valeur;}
    
    /*
     * Code genetique des agents.
     * chaque bit de valeur représente un trait génétique.
     * la taille de l'ADN est de (2^TAILLEADN)-1
     */
    
    public ADN()
    {
        this.valeur=makeADN();
    }
    
    public ADN(ADN parent1, ADN parent2)
    {
        this.valeur=mergeADN(parent1, parent2);
    }
    
/**
 * Crée de l'ADN (ADN = un nombre entre 0 et 10^TAILLEADN)
 * potentielGénétique = nombre de points à répartir. Doit être inferieur à
 * TAILLEADN * 9 (par ex, avec TAILLEADN = 2, potentielGenetique < 18
 * @param potentielGenetique: le nombre de points à répartir
 * @return un brin d'ADN
 */
    public static int makeADN()
    {
        return (int)(Math.random()*Math.pow(2,TAILLEADN));
    }
    
    /**
     * renvoie true ou false selon le trait génétique spécifié
     * @param brin
     * @return true si l'ADN a le brin spécifié à 1.
     */
    protected boolean hasTrait(int trait)
    {
        return ((valeur & trait) == trait);
    }
    
    /**
     * prend un ADN, redistribue certains points et renvoie l'ADN modifié
     * (redistribue jusqu'à la moitié des pts de chaque brin)
     * @param ADN
     * @return l'ADN modifié
     */
    protected int mergeADN(ADN parent1, ADN parent2)
    {
        int newADN=0;
        for(int i=1; i<Math.pow(2,TAILLEADN); i*=2){
            if(pModGen > Math.random()){
                newADN+=(0.5 > Math.random())?i:0;
            }else{
                newADN+=(0.5 > Math.random())? ((parent1.hasTrait(i))?i:0) : ((parent2.hasTrait(i))?i:0);
            }
        }
        return newADN;
    }
}
