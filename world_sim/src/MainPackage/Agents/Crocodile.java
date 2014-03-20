package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class Crocodile extends Agent {

    //constructeur initial
    public Crocodile(int __x, int __y, World __w) {
        this(__x, __y, __w, new ADN());
    }
    
    //constructeur reprod
    public Crocodile(int __x, int __y, World __w, ADN _adn) {
        super(__x, __y, __w, 1200, 2000, 1, 4, 100, _adn);
        _aquatique=true;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if(!dort){
                if(_faim<_faimMax){
                    ArrayList<Agent> mmcase = _world.getAgentCase(this);
                    if (!mmcase.isEmpty()) {
                        for (Agent ag : mmcase) {
                            if (ag.getClass() == Moutons.class || ag.getClass() == Loups.class) {
                                if(ag.getAlive()){
                                    ag.setmort();
                                }else{
                                    ag.constitution--;
                                    _faim += 400;
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
        
        if(hasMeute()){
            if(_world.distanceTotale(_x, _y, meute.getX(), meute.getY()) > meute.getDistMax()){
                _objectif[0]=meute.getX();
                _objectif[1]=meute.getY();
                return;
            }
        }
        
        //Interaction Crocodiles
        Agent proche = _world.getAgentsProches(this, Crocodile.class, getVision()*2);
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
            if(_faim>_faimMax*0.4 && getMature())
            {
                proche=meute.getPlusProche(this);
                if(proche._faim>proche._faimMax*0.4){
                    _objectif[0]=proche._x;
                    _objectif[1]=proche._y;
                    _fuis=false;
                    return;
                }
            }
        }
        
        if(_faim<_faimMax){
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
        }
        
        if(Case.getVal(_world.getCellItem(_x, _y)) != Case.EAU){
            int eauProche[]=_world.getPlusProcheItem(_x,_y,getVision(),Case.EAU);
            if(eauProche[0]!=-1){
                _objectif=eauProche;
                _fuis=false;
                return;
            }
        }
        
        _objectif[0]=_x+((Math.random() > 0.5)?(1):(-1));
        _objectif[1]=_y+((Math.random() > 0.5)?(1):(-1));
    }

    @Override public Agent creationBebe(Agent reproducteur)
    {
        Agent BB = new Crocodile(_x, _y, _world, new ADN(this._adn, reproducteur._adn));
        _world.add(BB);
        return BB;
    }
    
    @Override public boolean getMature()
    {
        return (_age>_ageMax*0.4);
    }
}
