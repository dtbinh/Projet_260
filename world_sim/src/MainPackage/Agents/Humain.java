package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;
import java.util.HashMap;

public class Humain extends Agent {

    private HashMap<String, Integer> inventaire;
    private String equipement;
    private int humeur;
    private final int CHASSE=1, BEZ=3, FATIGUE=4, BUILD=5, CHERCHE=6, BUCHERON=7;
    private Maison maison;
    
    //constructeur initial
    public Humain(int __x, int __y, World __w) {
        this(__x, __y, __w, new ADN());
    }
    
    //constructeur reprod
    public Humain(int __x, int __y, World __w, ADN _adn) {
        super(__x, __y, __w, 800, 3000, 1, 4, 30, _adn);
        inventaire = new HashMap<String, Integer>();
        equipement="";
        humeur=CHASSE;
        maison = null;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if(!dort){
                if( _faim < _faimMax){
                    if(inventaire.containsKey("nourriture")){
                        _faim += 150;
                        retirerInventaire("nourriture");
                    }
                }
                
                if(_faim <_faimMax*0.25){
                    humeur = CHASSE;
                }else if(maison == null){
                    humeur = BUILD;
                }else if(sommeil <40){
                    humeur = FATIGUE;
                }else if ((getMature())?gestation == -1:false){
                    humeur = BEZ;
                }else if(!maison.cheminay()){
                    humeur = BUCHERON;
                }else{
                    humeur = CHASSE;
                }
                
                ArrayList<Agent> mmcase = _world.getAgentCase(this);
                switch(humeur){
                    case CHASSE:
                        if (Case.getVal(_world.getCellItem(_x, _y)) == Case.BUISSON && Case.getVar(_world.getCellItem(_x, _y)) > 0) {
                            ajouterInventaire("nourriture", 1);
                            _world.setCellTypeVal(_x, _y, _world.getCellItem(_x, _y)-1);
                        }
                        if (!mmcase.isEmpty()) {
                            for (Agent ag : mmcase) {
                                if (ag.getClass() == Moutons.class) {
                                    if(ag.getAlive()){
                                        ag.setmort();
                                    }else{
                                        ajouterInventaire("nourriture", 3);
                                    }
                                }else if (ag.getClass() == Loups.class) {
                                    if(ag.getAlive()){
                                        ag.setmort();
                                    }else{
                                        ajouterInventaire("nourriture", 4);
                                    }
                                }
                            }
                        }
                        break;
                    case FATIGUE:
                        if(maison != null){
                            if(_x == maison._x && _y == maison._y){
                                while(inventaire.containsKey("bois")){
                                    maison.ajouterInventaire("bois", 1);
                                    retirerInventaire("bois");
                                }
                                dort = true;
                                if(maison.cheminay()){
                                    sommeil+=2;
                                }
                            }
                            break;
                        }
                    case BUILD:
                        if(inventaire.containsKey("bois")){
                            if(inventaire.get("bois") >= 5){
                                if((hasMeute())?_world.distanceTotale(_x, _y, meute.getX(), meute.getY()) > meute.getDistMax() : false){
                                    humeur = CHERCHE;
                                    break;
                                }else{
                                    if (!mmcase.isEmpty()) {
                                        for (Agent ag : mmcase) {
                                            if (ag.getClass() == Maison.class) {
                                                humeur = CHERCHE;
                                                break;
                                            }
                                        }
                                        retirerInventaire("bois");
                                        retirerInventaire("bois");
                                        retirerInventaire("bois");
                                        retirerInventaire("bois");
                                        retirerInventaire("bois");
                                        Maison mezon = new Maison(_x, _y, _world);
                                        _world.add(mezon);
                                        maison = mezon;
                                        break;
                                    }
                                }
                            }
                        }
                        humeur = BUCHERON;
                    case BUCHERON:
                        if (Case.getVal(_world.getCellItem(_x, _y)) == Case.ARBRE){
                            _world.setCellTypeVal(_x, _y, Case.VIDE);
                            ajouterInventaire("bois", 1);
                        }
                        break;
                    case BEZ:
                        reproduction();
                        break;
                }
                
            }
            
            if (_world.containVoisinsItem(_x, _y,Case.FEU) || _world.containVoisinsItem(_x, _y,Case.LAVE)) {
                setmort();
                constitution=-1;
            }
            setDir();
        }
    }

    private void setDir() {
        int feuProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.FEU);
        if(feuProche[0]!=-1){
            _objectif=feuProche;
            _fuis=true;
            return;
        }
        int laveProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.LAVE);
        if(laveProche[0]!=-1){
            _objectif=laveProche;
            _fuis=true;
            return;
        }
        
        //Interaction humain
        Agent proche;
        proche = _world.getAgentsProches(this, Humain.class, getVision()*2);
        if(proche!=null)
        {
            //VillageS
            if(!hasMeute()){
                //Creation de Village
                if(!proche.hasMeute()){
                    meute = new Village(this, proche);
                    proche.meute=meute;
                }else{
                    proche.meute.tenteRecrute(this);
                }
            } else {
                // mix des autres Villages
                if(proche.hasMeute()){
                    if(proche.meute!=meute){
                        meute.merge(proche.meute);
                    }
                }
            }
        }
        
        switch(humeur){
                case CHASSE:
                    // Si on a faim, ou si on mange, on cherche la nourriture la plus proche
                    int buissonProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.BUISSON);
                    if(buissonProche[0]!=-1){
                        _objectif=buissonProche;
                        _fuis=false;
                        return;
                    }
                    proche = _world.getAgentsProches(this, Moutons.class, getVision()*2);
                    if(proche != null){
                        _objectif[0]=proche._x;
                        _objectif[1]=proche._y;
                        _fuis=false;
                        return;
                    }
                    proche = _world.getAgentsProches(this, Loups.class, getVision()*2);
                    if(proche != null){
                        _objectif[0]=proche._x;
                        _objectif[1]=proche._y;
                        _fuis=false;
                        return;
                    }
                    break;
                    
                case FATIGUE:
                    // Si on est fatigué, on cherche sa maison si on en a une
                    if(maison != null){
                        _objectif[0]=maison._x;
                        _objectif[1]=maison._y;
                        _fuis=false;
                        return;
                    }
                case BUILD:
                    // Si on est en mode build, on reste là où on est pour construire sa maison
                    _objectif[0] = _x;
                    _objectif[1] = _y;
                    _fuis=false;
                    return;
                case BUCHERON:
                    // En mode bucheron, on cherche du bois
                    int arbreProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.ARBRE);
                    if(arbreProche[0]!=-1){
                        _objectif=arbreProche;
                        _fuis=false;
                        return;
                    }
                    break;
                case CHERCHE:
                    // En mode cherche, on cherche un emplacement vide près du centre du village
                    // CAD, si on est loin, on s'en rapproche, sinon même que GLANDE
                    if(hasMeute()){
                        if(_world.distanceTotale(_x, _y, meute.getX(), meute.getY()) > meute.getDistMax()){
                            _objectif[0] = meute.getX();
                            _objectif[1] = meute.getX();
                            _fuis = false;
                            return;
                        }
                    }
                case BEZ:
                    if(hasMeute()){
                        proche=meute.getPlusProche(this);
                    }else{
                        proche = _world.getAgentsProches(this, Humain.class, getVision()*2);
                    }
                    if(proche!=null){
                        if(proche._faim>proche._faimMax*0.4){
                        _objectif[0]=proche._x;
                        _objectif[1]=proche._y;
                        _fuis=false;
                        return;
                        }
                    }
                    break;
            }
        
        // MODE GLANDE :D
        _objectif[0]=_x+((Math.random() > 0.5)?(1):(-1));
        _objectif[1]=_y+((Math.random() > 0.5)?(1):(-1));
    }

    @Override public Agent creationBebe(Agent reproducteur)
    {
        Humain BB = new Humain(_x, _y, _world, new ADN(this._adn, reproducteur._adn));
        _world.add(BB);
        if(0.5 >= Math.random()){
            BB.maison = maison;
        }
        return (Agent) BB;
    }
    
    @Override public boolean getMature()
    {
        return (_age>_ageMax*0.05);
    }
    
    private void retirerInventaire(String s)
    {
        int nbr = inventaire.get(s)-1;
        if(nbr==0){
            inventaire.remove(s);
        }else{
            inventaire.put(s, nbr);
        }
    }
    
    private void ajouterInventaire(String s, int i)
    {
        if(inventaire.containsKey(s)){
            int nbr = inventaire.get(s)+i;
            inventaire.put(s, nbr);
        }else{
            inventaire.put(s, i);
        }
    }
    
    public void reproduction(Humain h) {
            _faim-=_faimMax*0.1;
            h._faim-=h._faimMax*0.1;
            gestation=_tpsGestation;
            partenaire=h;
        }

}
