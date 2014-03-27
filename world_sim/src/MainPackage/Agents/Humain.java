package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;
import java.util.HashMap;

public class Humain extends Agent {

    private HashMap<String, Integer> inventaire;
    private String equipement;
    private int humeur;
    private final int GLANDE=0, CHASSE=1, MANGE=2, BEZ=3, FATIGUE=4;
    
    //constructeur initial
    public Humain(int __x, int __y, World __w) {
        this(__x, __y, __w, new ADN());
    }
    
    //constructeur reprod
    public Humain(int __x, int __y, World __w, ADN _adn) {
        super(__x, __y, __w, 800, 2000, 1, 4, 200, _adn);
        inventaire = new HashMap<String, Integer>();
        equipement="";
        humeur=GLANDE;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if(!dort){
                if(_faim<_faimMax){
                    humeur = MANGE;
                }else{
                    humeur = CHASSE;
                }
                
                
                if(humeur == MANGE){
                    if(inventaire.containsKey("nourriture")){
                            _faim += 50;
                            retirerInventaire("nourriture");
                    }
                }
                if( humeur == MANGE || humeur == CHASSE){
                    if (Case.getVal(_world.getCellItem(_x, _y)) == Case.BUISSON && Case.getVar(_world.getCellItem(_x, _y)) > 0) {
                        ajouterInventaire("nourriture", 1);
                        _world.setCellTypeVal(_x, _y, _world.getCellItem(_x, _y)-1);
                    }
                    ArrayList<Agent> mmcase = _world.getAgentCase(this);
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
                }
                
                
                
            }
            
            
            
            
            
            
            if (_world.containVoisinsItem(_x, _y,Case.FEU) || _world.containVoisinsItem(_x, _y,Case.LAVE)) {
                setmort();
                constitution=-1;
            }
            setDir();
            reproduction();
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
        
        Agent proche = _world.getAgentsProches(this, Crocodile.class, getVision());
        if(proche!=null)
        {
            _objectif[0]=proche._x;
            _objectif[1]=proche._y;
            _fuis=true;
            return;
        }
        
        if(humeur == CHASSE || humeur == MANGE){
            int buissonProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.BUISSON);
            if(buissonProche[0]!=-1){
                _objectif=buissonProche;
                _fuis=false;
                return;
            }
            
            proche = _world.getAgentsProches(this, Moutons.class, getVision()*2);
            if(proche!=null)
            {
                _objectif[0]=proche._x;
                _objectif[1]=proche._y;
                _fuis=false;
                return;
            }
            
            proche = _world.getAgentsProches(this, Loups.class, getVision()*2);
            if(proche!=null)
            {
                _objectif[0]=proche._x;
                _objectif[1]=proche._y;
                _fuis=false;
                return;
            }
        }
        
        
        //Interaction humain
        proche = _world.getAgentsProches(this, Humain.class, getVision()*2);
        if(proche!=null)
        {
            //MEUTES
            if(!hasMeute()){
                //Creation de meute
                if(!proche.hasMeute()){
                    meute = new Meute(this, proche);
                    proche.meute=meute;
                }else{
                    proche.meute.tenteRecrute(this);
                }
                if(_faim>_faimMax*0.4 && getMature())
                {
                    if(proche!=null)
                    {
                        if(proche._faim>proche._faimMax*0.4){
                            _objectif[0]=proche._x;
                            _objectif[1]=proche._y;
                            _fuis=false;
                            return;
                        }
                    }
                }
            } else {
                // Fuite et mix des autres meutes
                if(proche.hasMeute()){
                    if(proche.meute!=meute){
                        meute.merge(proche.meute);
                    }
                }
            }
        }
        if(hasMeute()){
            if( humeur == BEZ)
            {
                proche=meute.getPlusProche(this);
                Humain pr = (Humain) proche;
                if(pr.humeur == BEZ){
                    _objectif[0]=pr._x;
                    _objectif[1]=pr._y;
                    _fuis=false;
                    return;
                }
            }
        }
        
        if(humeur == CHASSE){
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
            // TODO: créer les buissons et modifier ça pour que les humains broutent pas.
            int buissonProche[]=_world.getPlusProcheTerrain(_x,_y,getVision(),Case.HERBE);
            if(buissonProche[0]!=-1){
                _objectif=buissonProche;
                _fuis=false;
                return;
            }
        }
        
        _objectif[0]=_x+((Math.random() > 0.5)?(1):(-1));
        _objectif[1]=_y+((Math.random() > 0.5)?(1):(-1));
    }

    @Override public Agent creationBebe(Agent reproducteur)
    {
        Agent BB = new Humain(_x, _y, _world, new ADN(this._adn, reproducteur._adn));
        _world.add(BB);
        return BB;
    }
    
    @Override public boolean getMature()
    {
        return (_age>_ageMax*0.2);
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
}
