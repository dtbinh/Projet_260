package MainPackage;

import MainPackage.Agents.Agent;
import java.util.ArrayList;
import Quadtree.QuadTree;

public class World {

    private int _dx;
    private int _dy;
    private int[][] tableauItem;
    private int[][] bufferItem;
    private int[][] tableauAltitude;
    private int[][] tableauTerrain;
    private ArrayList<Agent> agents;
    private QuadTree quadtree;
    
    private Sprite sprite;
    
    //Environnement:
    /*
     * Cases:
     * Variables relatives au terrain
     */
    private final double pHerbe = 0.005; // proba que l'herbe pousse sur de la terre
    private final double pArbreFeu = 0.00001; //proba qu'un arbre prenne spontanément feu
    private final double pArbreApparait = 0.005; //proba qu'un arbre apparaisse à coté d'un autre arbre (cumulable: 2 arbre = pArbreApparait * 2)
    private final double pCendreDisparait = 0.20; //proba qu'une cendre disparaisse (0.20 = 5 iteration en moyenne)
    private final double pEvaporation = 0.01; //proba d'évaporation de l'eau
    private final double pBuisson = 0.0001; // Proba qu'un puisson pop in the wild
    private final double pBuissonPourri = 0.05; // Proba que les baies d'un buisson pourrissent
    
    private double varFeu = 0.; // % de variation du feu selon l'environnement
    
    private final double densite = 0.90; //Densité de la forêt; seuil de percolation a 0.55
    /*
     * Pluie:
     * Démarre et s'arrête aléatoirement,
     * Quand il pleut, le feu a moins de chances de se propager
     * Quand il pleut, les cases vides peuvent se remplir d'eau.
     */
    // LA PLUIE BUG,NE PAS L'ACTIVER
    private boolean pluie = false;
    private final double pDebutPluie = 0.;//0.0005; //chances qu'il commence à pleuvoir
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
    private final double pEruption = 0.0001; // probabilité que les genlave entrent en eruption
    private final double pFinErupt = 0.1; // probabilité que l'eruption diminue
    /*
     * Cycles jours/nuits
     * De 0 à transition c'est le jour, de transition à duree c'est la nuit.
     */
    private int temps; // compte les itérations du temps de la journée
    private boolean jour; // indique si on est le jour ou la nuit
    private final int dureeJour = 300; //durée en itérations d'une journée complète
    public World(String nom){
        int tab[][][] = Case.generateurImage1(nom);
        
        _dx = tab[0].length;
        _dy = tab[0][0].length;
        tableauItem = new int[_dx][_dy];
        tableauTerrain = new int[_dx][_dy];
        tableauAltitude = new int[_dx][_dy];
        
        for(int i=0;i<_dx;i++){
            for(int j=0;j<_dy;j++){
                tableauItem[i][j]=tab[2][i][j];
                tableauTerrain[i][j]=tab[1][i][j];
                tableauAltitude[i][j]=tab[0][i][j];
            }
        }
        
        temps = 0;
        jour = true;
        agents = new ArrayList<Agent>();

        bufferItem = new int[_dx][_dy];
        
        for(int i=0;i<_dx;i++){
            for(int j=0;j<_dy;j++){
                bufferItem[i][j]=tableauItem[i][j];
            }
        }
        
        for (int x = 0; x != _dx; x++) {
            for (int y = 0; y != _dy; y++) {
                if((tableauTerrain[x][y]==Case.TERRE
                        || tableauTerrain[x][y]==Case.HERBE)
                        && tableauAltitude[x][y]>=65){
                    if (densite >= Math.random()) {
                        tableauItem[x][y] = Case.ARBRE;
                    }
                }
            }// arbres
        }
        
        quadtree = new QuadTree(new java.awt.Rectangle(0, 0, _dx, _dy));
        
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
    
        if(temps++ > dureeJour){
            temps=0;
        }
        if(temps > dureeJour/2){
            jour=false;
        }else{
            jour=true;
        }
        
    }

    public void stepWorld() // world THEN agents
    {
        for (int x = 0; x != tableauItem.length; x++) {
            for (int y = 0; y != tableauItem[0].length; y++) {
                int voisins[];
                switch (Case.getVal(tableauItem[x][y])) {
                    case Case.VIDE:
                        if(tableauTerrain[x][y]==Case.TERRE
                                || tableauTerrain[x][y]==Case.HERBE){
                            voisins = getVoisins(tableauItem, x, y);
                            double pArbre=0;
                            for(int i=0; i<4; i++){
                                if(voisins[i]==Case.ARBRE){
                                    pArbre += pArbreApparait;
                                }
                            }
                            if (pArbre >= Math.random()) {
                                bufferItem[x][y] = Case.ARBRE;
                                break;
                            }
                            if (pBuisson >= Math.random()) {
                                bufferItem[x][y] = Case.BUISSON+8;
                                break;
                                
                            }
                        }
                        if (pluie) {
                            if (pGoutte >= Math.random()) {
                                bufferItem[x][y] = Case.EAU;
                                tableauItem[x][y] = Case.MODEAU;
                            }
                        }
                        break;
                    
                    case Case.BUISSON:
                        if (pBuissonPourri >= Math.random()){
                            bufferItem[x][y] = tableauItem[x][y]-1;
                        }
                        if(Case.getVar(tableauItem[x][y]) == 0){
                            bufferItem[x][y] = Case.VIDE;
                        }
                        break;
                        
                    case Case.ARBRE:
                        voisins = getVoisins(tableauItem, x, y);
                        boolean feu = false;
                        if ((pArbreFeu * (1 + varFeu)) >= Math.random()) {
                            feu = true;
                        } else {
                            for (int i = 0; i < 4; i++) { // modifier le 4 => 8 pour Moore
                                if (voisins[i] == Case.FEU) {
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
                                }else if(voisins[i] == Case.LAVE){
                                    feu = true;
                                }
                            }
                        }

                        if (feu) {
                            bufferItem[x][y] = Case.FEU;
                        } else {
                            bufferItem[x][y] = Case.ARBRE;
                        }
                        break;

                    case Case.FEU:
                        bufferItem[x][y] = Case.CENDRES;
                        break;

                    case Case.CENDRES:
                        if (pCendreDisparait >= Math.random()) {
                            bufferItem[x][y] = Case.VIDE;
                        }
                        break;

                    case Case.EAU:
                        //sablifie
                        if (Case.getVar(tableauItem[x][y]) >4) {
                            tableauTerrain[x][y]=Case.SABLE;
                        }
                        //Evaporation
                        if (Case.getVar(tableauItem[x][y]) == 0) {
                            if (pEvaporation >= Math.random()) {
                                bufferItem[x][y] = Case.VIDE;
                            }
                        } else {
                            voisins = getVoisins(tableauItem, x, y);
                            int voisinsAltitude[] = getVoisins(tableauAltitude, x, y);
                            int voisinsM[] = getVoisins(bufferItem, x, y);
                            bufferItem[x][y] = tableauItem[x][y];
                            int rand = (int) (Math.random() * 4);
                            // Etalage
                            // Sur une case vide d'abord
                            for (int i = 0; i < 4 && Case.getVar(bufferItem[x][y]) > 0; i++) {
                                if ((Case.getVal(voisins[(i + rand) % 4]) == Case.VIDE
                                        || Case.getVal(voisins[(i + rand) % 4]) == Case.CENDRES)
                                        && voisinsAltitude[(i + rand) % 4]<=tableauAltitude[x][y]) {
                                    int mod= Case.getVar(bufferItem[x][y])/2;
                                    bufferItem[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + bufferItem.length) % bufferItem.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + bufferItem[0].length) % bufferItem[0].length]
                                            = Case.EAU+mod;
                                    bufferItem[x][y] -= mod;
                                }
                            }
                            // Puis sur de l'eau moins profonde
                            for (int i = 0; i < 4 && Case.getVar(bufferItem[x][y]) > 0; i++) {
                                boolean modeau=(voisins[(i + rand) % 4] == Case.MODEAU);
                                if (modeau && (
                                        Case.getVal(voisinsM[((i + rand) % 4)]) == Case.EAU
                                        && Case.getVar(voisinsM[((i + rand) % 4)]) < 9
                                        && Case.getVar(voisinsM[((i + rand) % 4)]) < Case.getVar(bufferItem[x][y]))
                                        || !modeau && (
                                        Case.getVal(voisins[((i + rand) % 4)]) == Case.EAU
                                        && Case.getVar(voisins[((i + rand) % 4)]) < 9
                                        && Case.getVar(voisins[((i + rand) % 4)]) < Case.getVar(bufferItem[x][y]))
                                        && voisinsAltitude[(i + rand) % 4]<=tableauAltitude[x][y]){
                                    int valx=(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + bufferItem.length) % bufferItem.length;
                                    int valy=(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + bufferItem[0].length) % bufferItem[0].length;
                                    int mod = 1;
                                    if(modeau){
                                        bufferItem[valx][valy]
                                            += mod;
                                    }else{
                                        tableauItem[valx][valy]
                                            += mod;
                                    }
                                    bufferItem[x][y] -= mod;
                                }
                            }
                            
                        }
                        if (pluie && Case.getVar(bufferItem[x][y]) < 8) {
                            if (pGoutte >= Math.random()) {
                                bufferItem[x][y] += 1;
                            }
                        }
                        tableauItem[x][y]=Case.MODEAU;
                        break;
                        
                        
                    case Case.LAVE:
                        //Evaporation
                        if (Case.getVar(tableauItem[x][y]) == 0) {                        
                            if (pEvaporation >= Math.random()) {
                                bufferItem[x][y] = Case.VIDE;
                            }
                        } else {
                            voisins = getVoisins(tableauItem, x, y);
                            int voisinsAltitude[] = getVoisins(tableauAltitude, x, y);
                            bufferItem[x][y] = tableauItem[x][y];
                            int rand = (int) (Math.random() * 4);
                            // Etalage
                            // Sur de la pas lave d'abord
                            for (int i = 0; i < 4 && Case.getVar(bufferItem[x][y]) > 0; i++) {
                                if ((Case.getVal(voisins[(i + rand) % 4]) == Case.VIDE
                                        || Case.getVal(voisins[(i + rand) % 4]) == Case.CENDRES
                                        || (Case.getVal(voisins[(i + rand) % 4]) == Case.EAU && Case.getVar(voisins[(i + rand) % 4]) <3))
                                        && voisinsAltitude[(i + rand) % 4]<=tableauAltitude[x][y]) {
                                    bufferItem[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + bufferItem.length) % bufferItem.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + bufferItem[0].length) % bufferItem[0].length]
                                            = Case.LAVE;
                                    bufferItem[x][y]--;
                                }
                            }
                            // Puis sur de la lave moins profonde
                            for (int i = 0; i < 4 && Case.getVar(bufferItem[x][y]) > 0; i++) {
                                if (Case.getVal(voisins[((i + rand) % 4)]) == Case.LAVE
                                            && Case.getVar(voisins[((i + rand) % 4)]) < 9
                                            && Case.getVar(voisins[((i + rand) % 4)]) < Case.getVar(bufferItem[x][y])
                                            && voisinsAltitude[((i + rand) % 4)]<=tableauAltitude[x][y]) {
                                    bufferItem[(x - 1 + (((i + rand) % 4) * 2 + 1) % 3 + bufferItem.length) % bufferItem.length][(y - 1 + (((i + rand) % 4) * 2 + 1) / 3 + bufferItem[0].length) % bufferItem[0].length]
                                            += 1;
                                    bufferItem[x][y] -= 1;
                                }
                            }
                            if (pEvaporation >= Math.random() && Case.getVar(bufferItem[x][y]) > 0) {
                                bufferItem[x][y] -= 1;
                            }
                        }
                        break;
                        
                        
                        
                    case Case.GENLAVE:
                       if(Case.getVar(tableauItem[x][y]) == 0){
                            if(pEruption >= Math.random()){
                                bufferItem[x][y] = Case.GENLAVE+8;
                            }
                        }else{
                            for (int i = 0; i < 4; i++) {
                                bufferItem[(x - 1 + ((i * 2 + 1) % 3) + bufferItem.length) % bufferItem.length][(y - 1 + ((i * 2 + 1) / 3) + bufferItem[0].length) % bufferItem[0].length]
                                        = Case.LAVE+9;
                            }
                            bufferItem[x][y] += (pFinErupt>= Math.random())?-1:0;
                        }
                        break;
                        
                    case Case.GENEAU:
                        for (int i = 0; i < 9; i++) {
                            if(i!=4){
                                bufferItem[(x - 1 + ((i) % 3) + bufferItem.length) % bufferItem.length][(y - 1 + ((i) / 3) + bufferItem[0].length) % bufferItem[0].length]
                                    = Case.EAU+9;
                            }
                        }
                        break;

                    default:
                        bufferItem[x][y] = Case.VIDE;
                }
                switch(Case.getVal(tableauTerrain[x][y])){
                    case Case.TERRE:
                        if (pHerbe >= Math.random()) {
                            tableauTerrain[x][y] = Case.HERBE;
                        }
                        break;
                }
            }
        }

        for (int x = 0; x != tableauItem.length; x++){
            for (int y = 0; y != tableauItem[0].length; y++) {
                tableauItem[x][y] = bufferItem[x][y];
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
            if(a.move()){
                quadtree.maj(a);
            }
        }
    }

    
    // Fonctions pour les agents et l'environnement:
    
    /**
     * Renvoie un Array des agents sur la case de l'agent passé en paramètre
     * @param a
     */
    public ArrayList<Agent> getAgentCase(Agent a) {
        ArrayList<Agent> ret = new ArrayList<Agent>();
        for(Agent ag: quadtree.retrieve(a)){
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
        for (Agent ag : quadtree.retrieve(a)) {
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
     * Renvoie la position de la case item la plus proche de x/y (dans un tableau {x,y} ) dans un rayon portée
     * @param x, y, portee, type
     */
    public int[] getPlusProcheItem(int x, int y, int portee, int type)
    {
        int ret[] = {-1,-1};
        if(Case.getVal(getCellItem(x,y))==type){
            ret[0]=x; ret[1]=y;
            return ret;
        }
        for(int iP=1; iP<=portee;iP++){
            for(int j=0; j<=iP; j++){
                for(int k=0;k<4;k++){
                    switch(k){
                        case 0:
                            if((Case.getVal(getCellItem((x-(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy))) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y-(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 1:
                            if((Case.getVal(getCellItem((x+(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy))) == type){
                                ret[0]=(x+(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 2:
                            if((Case.getVal(getCellItem((x-(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy))) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 3:
                            if((Case.getVal(getCellItem((x+(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy))) == type){
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
    
    public int[] getPlusProcheTerrain(int x, int y, int portee, int type)
    {
     int ret[] = {-1,-1};
        if(Case.getVal(getCellItem(x,y))==type){
            ret[0]=x; ret[1]=y;
            return ret;
        }
        for(int iP=1; iP<=portee;iP++){
            for(int j=0; j<=iP; j++){
                for(int k=0;k<4;k++){
                    switch(k){
                        case 0:
                            if((getCellTerrain((x-(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy)) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y-(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 1:
                            if((getCellTerrain((x+(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy)) == type){
                                ret[0]=(x+(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 2:
                            if((getCellTerrain((x-(iP-j)+_dx)%_dx, (y+(iP-(iP-j))+_dy)%_dy)) == type){
                                ret[0]=(x-(iP-j)+_dx)%_dx;
                                ret[1]=(y+(iP-(iP-j))+_dy)%_dy;
                                return ret;
                            }
                            break;
                        case 3:
                            if((getCellTerrain((x+(iP-j)+_dx)%_dx, (y-(iP-(iP-j))+_dy)%_dy)) == type){
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
        for(Agent a:quadtree.retrieve(new java.awt.Rectangle(x-portee/2, y-portee/2,portee,portee))){
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
     * Renvoie l'agent de type AG le plus proche de l'agent ag dans un rayon portée. renvoie NULL si yen a pas
     * @param x, y, portee
     */
    public Agent getAgentsProches(Agent ag, Class type, int portee)
    {
        Agent ret=null;
        int distance=portee;
        for(Agent a:quadtree.retrieve(new java.awt.Rectangle(ag.getX()-portee/2, ag.getY()-portee/2,portee,portee))){
            if(a!=ag){
                if(a.getClass() == type){
                    int dist[]= distance(ag.getX(), ag.getY(), a.getX(), a.getY());
                    int dist2=dist[0]+dist[1];
                    if(dist2<=distance){
                        distance=dist2;
                        ret=a;
                    }
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
     * Appelle une des deux méthodes "distance" en fonction de si le monde est thorique ou pas.
     * @param x1, y1, x2, y2
     */
    public int[] distance(int x1, int y1, int x2, int y2)
    {
        return distanceNT(x1, y1, x2, y2);
    }
    //Version torique de distance
    public int[] distanceT(int x1, int y1, int x2, int y2)
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
        return ret;
    }
    
    /**
     * renvoie la distance en un entier entre les premiers poinst et le second.
     */
    public int distanceTotale(int x1, int y1, int x2, int y2)
    {
        return distance(x1, y1, x2, y2)[0]+distance(x1, y1, x2, y2)[1];
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
        if(dist[0]!=0 && (.5 >= Math.random() || dist[1]==0)){
            return (x1 > x2)?3:1;
        }else if(dist[1]!=0){
            return (y1 > y2)?0:2;
        }else{
            return 4;
        }
        /*
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
        return 4;*/
    }
    
    /**
     * Renvoie un tableau de 4 entier contenant les valeur des cases adjacentes à la position cellX/cellY dans le tableau tab
     * @param tab, cellX, cellY
     */
    public static int[] getVoisins(int tab[][], int cellX, int cellY) {
        // Pour von neuman, changer la taille du tableau à 8
        int voisins[] = new int[4];
        int j = 0;
        for (int i = 1; i < 8; i += 2) { // commencer à 0, changer le 8 en 9, incrémenter de 1
            //if(i!=4){
                voisins[j] = tab[(cellX - 1 + i % 3 + tab.length) % tab.length][(cellY - 1 + i / 3 + tab[0].length) % tab[0].length];
            //}
            j++;
        }
        return voisins;
    }

    /**
     * Renvoie true si une case adjacente à cellX/cellY contient un type égale à type dans tableauItem.
     * @param cellX, cellY, type
     */
    public boolean containVoisinsItem(int cellX, int cellY, int type) {
        for (int i = 1; i < 8; i += 2) {
            if (Case.getVal(tableauItem[(cellX - 1 + i % 3 + tableauItem.length) % tableauItem.length][(cellY - 1 + i / 3 + tableauItem[0].length) % tableauItem[0].length]) == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Renvoie true si une case adjacente à cellX/cellY contient un type égale à type dans tableauItem.
     * @param cellX, cellY, type
     */
    public boolean containVoisinsTerrain(int cellX, int cellY, int type) {
        for (int i = 1; i < 8; i += 2) {
            if (Case.getVal(tableauTerrain[(cellX - 1 + i % 3 + tableauTerrain.length) % tableauTerrain.length][(cellY - 1 + i / 3 + tableauTerrain[0].length) % tableauTerrain[0].length]) == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Renvoie la valeur de tableauItem à la position x/y
     * @param x, y
     */
    public int getCellItem(int x, int y) {
        return tableauItem[x][y];
    }
    
    /**
     * Renvoie le terrain de tableauTerrain à la position x/y
     * @param x, y
     */
    public int getCellTerrain(int x, int y) {
        return tableauTerrain[x][y];
    }

    /**
     * Met la valeure z à la position x, y de l'environnement.
     * @param x, y
     */
    public void setCellTypeVal(int x, int y, int z) { // La modification ne fonctionne à partir d'un agent que si les deux tab.
        tableauItem[x][y] = z;
        bufferItem[x][y] = z;
    }
    
    /**
     * Met la valeure z à la position x, y de l'environnement.
     * @param x, y
     */
    public void setCellTerrainVal(int x, int y, int z) {
        tableauTerrain[x][y] = z;
    }

    /**
     * Ajoute l'agent agent à la liste d'agents
     * @param agent
     */
    public void add(Agent agent) {
        agents.add(agent);
        quadtree.insert(agent);
    }
    
    /**
     * Retire l'agent agent de la liste d'agents
     * @param agent
     */
    public void remove(Agent agent) {
        if (agents.contains(agent)) {
            quadtree.remove(agent);
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
    
    public boolean getJour(){return jour;}
    
    public int getDureeJour(){return dureeJour/2;}
}
