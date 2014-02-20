package MainPackage;

import MainPackage.Agents.Agent;
import java.util.ArrayList;

public class World {

    private int _dx;
    private int _dy;
    private int[][] tableauCourant;
    private int[][] nouveauTableau;
    private ArrayList<Agent> agents;
    private Sprite sprite;
    
    //Environnement:
    /*
     * Cases:
     * Variables relatives au terrain
     */
    private final double pArbreFeu = 0.00001; //proba qu'un arbre prenne spontanément feu
    private final double pArbreApparait = 0.005; //proba qu'un arbre apparaisse à coté d'un autre arbre (cumulable: 2 arbre = pArbreApparait * 2)
    private final double pCendreDisparait = 0.20; //proba qu'une cendre disparaisse (0.20 = 5 iteration en moyenne)
    private final double pEvaporation = 0.01; //proba d'évaporation de l'eau
    
    private double varFeu = 0.; // % de variation du feu selon l'environnement
    
    private final double densite = 0.90; //Densité de la forêt; seuil de percolation a 0.55
    /*
     * Pluie:
     * Démarre et s'arrête aléatoirement,
     * Quand il pleut, le feu a moins de chances de se propager
     * Quand il pleut, les cases vides peuvent se remplir d'eau.
     */
    private boolean pluie = false;
    private final double pDebutPluie = 0.005; //chances qu'il commence à pleuvoir
    private final double pFinPluie = 0.10; //chances qu'il s'arrete de pleuvoir
    private final double pGoutte = 0.01; //chance qu'une goutte fasse augmenter la taille d'une case d'eau
    private final double vfPluie = -0.25; //réduction du feu quand il pleut
    /*
     * Vent:
     * Le vent change de direction ou s'arrête aléatoirement.
     * Quand il y a du vent, le feu se propage plus dans sa direction et quasiment
     * pas dans la direction inverse.
     */
    private Directions vent = Directions.NONE; // NONE, SUD, EST, OUEST, NORD
    private final double pContreVent = 0.25; // probabilité de prendre feu contre le vent
    private final double pPerpVent = 0.75; // probabilité de prendre feu Perpendiculairement au vent.
    private final double pChangeVent = 0.01; //probabilité que le vent change de direction
    private final double vfVent = 0.10; // augmentation du feu quand il y a du vent
    
    /*
     * Eruption:
     * Parfois les générateurs de lavent entrent en éruption et créent de la lave.
     * 
     */
    private final double pEruption = 0.001; // probabilité que les genlave entrent en eruption
    private final double pFinErupt = 0.1; // probabilité que l'eruption diminue
    
    public World(String nom){
        tableauCourant = Case.generateurImage1(nom);
        _dx = tableauCourant.length;
        _dy = tableauCourant[0].length;

        agents = new ArrayList<Agent>();

        nouveauTableau = new int[_dx][_dy];
        
        for(int i=0;i<_dx;i++){
            for(int j=0;j<_dy;j++){
                nouveauTableau[i][j]=tableauCourant[i][j];
            }
        }
        
        for (int x = 0; x != _dx; x++) {
            for (int y = 0; y != _dy; y++) {
                if((Case.getTerrain(tableauCourant[x][y])==Case.TERRE
                        || Case.getTerrain(tableauCourant[x][y])==Case.HERBE)
                        && Case.getAltitude(tableauCourant[x][y])>=650000 ){
                    if (densite >= Math.random()) {
                        tableauCourant[x][y] = Case.setType(tableauCourant[x][y], Case.ARBRE);
                    }
                }
            }// arbres
        }
        
        sprite = new Sprite(this);
    }

    // Fonctions itérations
    
    /**
     * Update the world state and return an array for the current world state (may be used for display)
     * @return
     */
    public void step() {
        stepEnvironnement();
        stepWorld();
        stepAgents();
        
        sprite.repaint();
    }
    
    public void stepEnvironnement() // Modifie les variables de l'environnement (vent, pluie etc...)
    {
        if (pluie) {
            if (pFinPluie >= Math.random()) {
                pluie = false;
            }
        } else {
            if (pDebutPluie >= Math.random()) {
                pluie = true;
            }
        }


        if (pChangeVent >= Math.random()) {
            vent = Directions.getRandDir();
        }
        
        varFeu=( (pluie)?vfPluie:0 ) + ( (vent==Directions.NONE)?vfVent:0 );
    }

    public void stepWorld() // world THEN agents
    {
        for (int x = 0; x != tableauCourant.length; x++) {
            for (int y = 0; y != tableauCourant[0].length; y++) {
                int voisins[];
                switch (Case.getType(tableauCourant[x][y])) {
                    case Case.VIDE:
                        if( (Case.getTerrain(tableauCourant[x][y])==Case.TERRE || Case.getTerrain(tableauCourant[x][y])==Case.HERBE)){
                            voisins = getVoisins(tableauCourant, x, y);
                            double pArbre=0;
                            for(int i=0; i<4; i++){
                                if(Case.getType(voisins[i])==Case.ARBRE){
                                    pArbre += pArbreApparait;
                                }
                            }
                            if (pArbre >= Math.random()) {
                                nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.ARBRE);
                                break;
                            }
                        }
                        if (pluie) {
                            if (pGoutte >= Math.random()) {
                                nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.EAU);
                            }
                        }
                        break;

                    case Case.ARBRE:
                        voisins = getVoisins(tableauCourant, x, y);
                        boolean feu = false;
                        if ((pArbreFeu * (1 + varFeu)) >= Math.random()) {
                            feu = true;
                        } else {
                            for (int i = 0; i < 4; i++) {
                                if (Case.getType(voisins[i]) == Case.FEU) {
                                    if (i == vent.getVal()) {
                                        if((1 + varFeu) >= Math.random()) {
                                            feu = true;
                                            break;
                                        }
                                    } else if (i == vent.getInv()) {
                                        if (pContreVent * (1 + varFeu) >= Math.random()) {
                                            feu = true;
                                            break;
                                        }
                                    } else {
                                        if (pPerpVent * (1 + varFeu) >= Math.random()) {
                                            feu = true;
                                            break;
                                        }
                                    }
                                }else if(Case.getType(voisins[i]) == Case.LAVE){
                                    feu = true;
                                }
                            }
                        }

                        if (feu) {
                            nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.FEU);
                        } else {
                            nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.ARBRE);
                        }
                        break;

                    case Case.FEU:
                        nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.CENDRES);
                        break;

                    case Case.CENDRES:
                        if (pCendreDisparait >= Math.random()) {
                            nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.VIDE);
                        }
                        break;

                    case Case.EAU:
                        //Evaporation
                        if (Case.getVar(tableauCourant[x][y]) == 0) {
                            if (pEvaporation >= Math.random()) {
                                nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.VIDE);
                            }
                        } else {
                            voisins = getVoisins(tableauCourant, x, y);
                            nouveauTableau[x][y] = tableauCourant[x][y];
                            int rand = (int) (Math.random() * 4);
                            // Etalage
                            // Sur une case vide d'abord
                            for (int i = 0; i < 4 && Case.getVar(nouveauTableau[x][y]) > 0; i++) {
                                if ((Case.getType(voisins[(i + rand) % 4]) == Case.VIDE
                                        || Case.getType(voisins[(i + rand) % 4]) == Case.CENDRES)
                                        && Case.getAltitude(voisins[(i + rand) % 4])<=Case.getAltitude(tableauCourant[x][y])) {
                                    nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length]
                                            = Case.setType(nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length],Case.EAU);
                                    nouveauTableau[x][y] = Case.setVar(nouveauTableau[x][y], -1);
                                }
                            }
                            // Puis sur de l'eau moins profonde
                            for (int i = 0; i < 4 && Case.getVar(nouveauTableau[x][y]) > 0; i++) {
                                if (Case.getType(voisins[((i + rand) % 4)]) == Case.EAU
                                            && Case.getVar(voisins[((i + rand) % 4)]) < 9
                                            && Case.getVar(voisins[((i + rand) % 4)]) < Case.getVar(nouveauTableau[x][y])
                                            && Case.getAltitude(voisins[((i + rand) % 4)])<=Case.getAltitude(tableauCourant[x][y])) {
                                    nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length] = Case.setVar(nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length], 1);
                                    nouveauTableau[x][y] = Case.setVar(nouveauTableau[x][y], -1);
                                }
                            }
                            
                        }
                        if (pluie && Case.getVar(nouveauTableau[x][y]) < 8) {
                            if (pGoutte >= Math.random()) {
                                nouveauTableau[x][y] = Case.setVar(nouveauTableau[x][y], 1);
                            }
                        }
                        break;
                        
                        
                    case Case.LAVE:
                        //Evaporation
                        if (Case.getVar(tableauCourant[x][y]) == 0) {
                            if (pEvaporation >= Math.random()) {
                                nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.VIDE);
                            }
                        } else {
                            voisins = getVoisins(tableauCourant, x, y);
                            nouveauTableau[x][y] = tableauCourant[x][y];
                            int rand = (int) (Math.random() * 4);
                            // Etalage
                            // Sur de la pas lave d'abord
                            for (int i = 0; i < 4 && Case.getVar(nouveauTableau[x][y]) > 0; i++) {
                                if ((Case.getType(voisins[(i + rand) % 4]) == Case.VIDE
                                        || Case.getType(voisins[(i + rand) % 4]) == Case.CENDRES
                                        || (Case.getType(voisins[(i + rand) % 4]) == Case.CENDRES && Case.getVar(voisins[(i + rand) % 4]) <3))
                                        && Case.getAltitude(voisins[(i + rand) % 4])<=Case.getAltitude(tableauCourant[x][y])) {
                                    nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length]
                                            = Case.setType(nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length],Case.LAVE);
                                    nouveauTableau[x][y] = Case.setVar(nouveauTableau[x][y], -1);
                                }
                            }
                            // Puis sur de la lave moins profonde
                            for (int i = 0; i < 4 && Case.getVar(nouveauTableau[x][y]) > 0; i++) {
                                if (Case.getType(voisins[((i + rand) % 4)]) == Case.LAVE
                                            && Case.getVar(voisins[((i + rand) % 4)]) < 9
                                            && Case.getVar(voisins[((i + rand) % 4)]) < Case.getVar(nouveauTableau[x][y])
                                            && Case.getAltitude(voisins[((i + rand) % 4)])<=Case.getAltitude(tableauCourant[x][y])) {
                                    nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length] = Case.setVar(nouveauTableau[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + nouveauTableau.length) % nouveauTableau.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + nouveauTableau[0].length) % nouveauTableau[0].length], 1);
                                    nouveauTableau[x][y] = Case.setVar(nouveauTableau[x][y], -1);
                                }
                            }
                        }
                        break;
                        
                        
                        
                    case Case.GENLAVE:
                       if(Case.getVar(tableauCourant[x][y]) == 0){
                            if(pEruption >= Math.random()){
                                nouveauTableau[x][y] = Case.setVar(tableauCourant[x][y], 8);
                            }
                        }else{
                            voisins = getVoisins(tableauCourant, x, y);
                            for (int i = 0; i < 4; i++) {
                                if(Case.getType(voisins[i])!= Case.GENLAVE) {
                                    nouveauTableau[(x - 1 + ((i * 2 + 1) % 3) + nouveauTableau.length) % nouveauTableau.length][(y - 1 + ((i * 2 + 1) / 3) + nouveauTableau[0].length) % nouveauTableau[0].length]
                                            = Case.setType(nouveauTableau[(x - 1 + ((i * 2 + 1) % 3) + nouveauTableau.length) % nouveauTableau.length][(y - 1 + ((i * 2 + 1) / 3) + nouveauTableau[0].length) % nouveauTableau[0].length], Case.LAVE);
                                    nouveauTableau[(x - 1 + ((i * 2 + 1) % 3) + nouveauTableau.length) % nouveauTableau.length][(y - 1 + ((i * 2 + 1) / 3) + nouveauTableau[0].length) % nouveauTableau[0].length]
                                            = Case.setVar(nouveauTableau[(x - 1 + ((i * 2 + 1) % 3) + nouveauTableau.length) % nouveauTableau.length][(y - 1 + ((i * 2 + 1) / 3) + nouveauTableau[0].length) % nouveauTableau[0].length],
                                            9 - Case.getVar(nouveauTableau[(x - 1 + ((i * 2 + 1) % 3) + nouveauTableau.length) % nouveauTableau.length][(y - 1 + ((i * 2 + 1) / 3) + nouveauTableau[0].length) % nouveauTableau[0].length]));
                                }
                            }
                            nouveauTableau[x][y] = Case.setVar(tableauCourant[x][y], (pFinErupt>= Math.random())?-1:0);
                        }
                        break;

                    default:
                        nouveauTableau[x][y] = Case.setType(tableauCourant[x][y], Case.VIDE);
                }

            }
        }

        for (int x = 0; x != tableauCourant.length; x++){
            for (int y = 0; y != tableauCourant[0].length; y++) {
                tableauCourant[x][y] = nouveauTableau[x][y];
            }
        }
        
    }

    public void stepAgents() // world THEN agents
    {
        for (int i = 0; i != agents.size(); i++) {
                agents.get(i).step();
        }
        for (int i = agents.size() - 1; i >= 0; i--) {
                agents.get(i).estmort();
        }
        for (Agent a : agents) {
            a.move();
        }
    }

    
    // Fonctions pour les agents et l'environnement:
    
    /**
     * Renvoie un Array des agents sur la case de l'agent passé en paramètre
     * @param a
     */
    public ArrayList<Agent> getAgentCase(Agent a) {
        ArrayList<Agent> ret = new ArrayList<Agent>();
        for (Agent ag : agents) {
            if (a != ag) {
                if (ag.getX() == a.getX() && ag.getY() == a.getY()) {
                    ret.add(ag);
                }
            }
        }
        return ret;
    }
    
    public Agent[] getAgentsArray(){
        Agent ret[] = new Agent[agents.size()];
        agents.toArray(ret);
        return ret;
    }
    
    /**
     * Renvoie un Array des agents adjacents à l'agent passé en paramètre.
     * @param a
     */
    public ArrayList<Agent>[] getAgentsProches(Agent a) {
        ArrayList<Agent>[] ret = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = new ArrayList<Agent>();
        }
        for (Agent ag : agents) {
            if (a != ag) {
                if (ag.getX() == a.getX() && ag.getY() == (a.getY() - 1 + getHeight()) % getHeight()) {
                    ret[1].add(ag);
                } else if (ag.getX() == (a.getX() + 1 + getWidth()) % getWidth() && ag.getY() == a.getY()) {
                    ret[2].add(ag);
                } else if (ag.getX() == a.getX() && ag.getY() == (a.getY() + 1 + getHeight()) % getHeight()) {
                    ret[3].add(ag);
                } else if (ag.getX() == (a.getX() - 1 + getWidth()) % getWidth() && ag.getY() == a.getY()) {
                    ret[0].add(ag);
                }
            }
        }
        return ret;
    }

    /**
     * Renvoie la position de la case type la plus proche de x/y (dans un tableau {x,y} ) dans un rayon portée
     * @param x, y, portee, type
     */
    public int[] getPlusProche(int x, int y, int portee, int type)
    {
        int ret[] = {-1,-1};
        if(getCellType(x,y)==type){
            ret[0]=x; ret[1]=y;
            return ret;
        }
        for(int iP=1; iP<=portee;iP++){
            for(int j=0; j<=iP; j++){
                for(int k=0;k<4;k++){
                    switch(k){
                        case 0:
                            if((getCellType((x-(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy) & type) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y-(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 1:
                            if((getCellType((x+(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy) & type) == type){
                                ret[0]=(x+(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 2:
                            if((getCellType((x-(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy) & type) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 3:
                            if((getCellType((x+(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy) & type) == type){
                                ret[0]=(x+(iP-j)+_dx)%_dx;
                                ret[1]=(y-(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        default:
                            System.out.println("I AM ERROR, getPlusProche");
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * Renvoie l'agent de type AG le plus proche de x,y dans un rayon portée. renvoie NULL si yen a pas
     * @param x, y, portee
     */
    public Agent getAgentsProches(int x, int y, Class type, int portee)
    {
        Agent ret=null;
        int distance=portee;
        for(Agent a:agents){
            if(a.getClass() == type){
                int dist[]= distance(x, y, a.getX(), a.getY());
                int dist2=dist[0]+dist[1];
                if(dist2<=distance){
                    distance=dist2;
                    ret=a;
                }
            }
        }
        return ret;
    }
    
    /**
     * renvoie la distance (nombre de cases à parcourir) entre le point x1/y1 et x2/y2
     * Retourne un tableau tel que ret[0]=distance entre x1 et x2, ret[1]=distance entre y1 et y2.
     * Les déplacement ne se font pas en diagonales, donc la distance totale est juste égale à la distance
     * entre x1 et x2 + la distance entre y1 et y2.
     * @param x1, y1, x2, y2
     */
    public int[] distance(int x1, int y1, int x2, int y2)
    {
        int ret[]=new int[2];
        ret[0]=(((x1-x2+_dx)%_dx) < ((x2-x1+_dx)%_dx))?((x1-x2+_dx)%_dx):((x2-x1+_dx)%_dx);
        ret[1]=(((y1-y2+_dy)%_dy) < ((y2-y1+_dy)%_dy))?((y1-y2+_dy)%_dy):((y2-y1+_dy)%_dy);
        return ret;
    }
    //Version non torique de distance
    public int[] distanceNT(int x1, int y1, int x2, int y2)
    {
        int ret[]=new int[2];
        ret[0]=((x1-x2) > (x2-x1))?(x1-x2):(x2-x1);
        ret[1]=((y1-y2) > (y2-y1))?(y1-y2):(y2-y1);
        //System.out.println(ret[0]+" "+ret[1]);
        return ret;
    }
    
    /**
     * Renvoie la direction pour aller de la case x1/y1 à la case x2/y2
     * avec: 0=nord, 1=est, 2=sud, 3=ouest 4=pas bouger
     * (pour avoir la direction inverse, faire (getDirection+2)%4)
     * @param x1, y1, x2, y2
     */
    public int getDirection(int x1, int y1, int x2, int y2)
    {
        int dist[]=distance(x1,y1,x2,y2);
        //Monde non torique:
        /*if(dist[0]>dist[1]){
            return (x1 > x2)?3:1;
        }else{
            return (y1 > y2)?0:2;
        }*/
        //Monde torique:
        if(dist[0]>dist[1]){
            if(dist[0]==distanceNT(x1,y1,x2,y2)[0]){
                return (x1 > x2)?3:1;
            }else{
                return (x1 > x2)?1:3;
            }
        }else if(dist[1]!=0){
            if(dist[1]==distanceNT(x1,y1,x2,y2)[1]){
                return (y1 > y2)?0:2;
            }else{
                return (y1 > y2)?2:0;
            }
        }
        return 4;
    }
    
    /**
     * Renvoie un tableau de 4 entier contenant les valeur des cases adjacentes à la position cellX/cellY dans le tableau tab
     * @param tab, cellX, cellY
     */
    public static int[] getVoisins(int tab[][], int cellX, int cellY) {
        int voisins[] = new int[4];
        int j = 0;
        for (int i = 1; i < 8; i += 2) {
            voisins[j] = tab[(cellX - 1 + i % 3 + tab.length) % tab.length][(cellY - 1 + i / 3 + tab[0].length) % tab[0].length];
            j++;
        }
        return voisins;
    }

    /**
     * Renvoie true si une case adjacente à cellX/cellY contient un type égale à type dans tableauCourant.
     * @param cellX, cellY, type
     */
    public boolean containVoisins(int cellX, int cellY, int type) {
        int j = 0;
        for (int i = 1; i < 8; i += 2) {
            if ((tableauCourant[(cellX - 1 + i % 3 + tableauCourant.length) % tableauCourant.length][(cellY - 1 + i / 3 + tableauCourant[0].length) % tableauCourant[0].length] & type) == type) {
                return true;
            }
            j++;
        }
        return false;
    }

    /**
     * Renvoie la valeur complète de tableauCourant à la position x/y
     * @param x, y
     */
    public int getCellVal(int x, int y) {
        return tableauCourant[x][y];
    }

    /**
     * Renvoie l'index de la valeur (seulement les dizaines) de tableauCourant à la position x/y
     * @param x, y
     */
    public int getCellType(int x, int y) { //renvoie la val /10 *10
        return Case.getType(tableauCourant[x][y]);
    }
    
    /**
     * Renvoie le terrain de tableauCourant à la position x/y
     * @param x, y
     */
    public int getCellTerrain(int x, int y) { //renvoie la val /10 *10
        return Case.getTerrain(tableauCourant[x][y]);
    }

    /**
     * Met la valeure z à la position x, y de l'environnement.
     * @param x, y
     */
    public void setCellVal(int x, int y, int z) { // La modification ne fonctionne à partir d'un agent que si les deux tab.
        tableauCourant[x][y] = z;
        nouveauTableau[x][y] = z;
    }

    /**
     * Ajoute l'agent agent à la liste d'agents
     * @param agent
     */
    public void add(Agent agent) {
        agents.add(agent);
    }
    
    /**
     * Retire l'agent agent de la liste d'agents
     * @param agent
     */
    public void remove(Agent agent) {
        if (agents.contains(agent)) {
            agents.remove(agent);
        }
    }

    // Accesseurs:
    
    public int getWidth() {
        return _dx;
    }

    public int getHeight() {
        return _dy;
    }

    public void checkBounds(int __x, int __y) {
        if (__x < 0 || __x > _dx || __y < 0 || __y > _dy) {
            System.err.println("[error] out of bounds (" + __x + "," + __y + ")");
            System.exit(-1);
        }
    }
}
