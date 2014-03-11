package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class Moutons extends Agent {
    
    //constructeur initial
    public Moutons(int __x, int __y, World __w) {
        this(__x, __y, __w, new ADN());
    }
    
    //constructeur reprod
    public Moutons(int __x, int __y, World __w, ADN _adn) {
        super(__x, __y, __w, 45, 300, 4, 5, 5, _adn);
        diurne=true;
    }
    
    @Override public void step() {
        temps();

        if (_alive) {
            if(!dort){
                if(_faim<_faimMax){
                    if (_world.getCellTerrain(_x, _y) == Case.HERBE) {
                        _faim += 15;
                        _world.setCellTerrainVal(_x, _y, Case.TERRE);
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
        
        Agent proche = _world.getAgentsProches(this, Loups.class, getVision()*2);
        if(proche!=null)
        {
            _objectif[0]=proche._x;
            _objectif[1]=proche._y;
            _fuis=true;
            return;
        }
        
        if(hasMeute()){
            if(_world.distanceTotale(_x, _y, meute.getX(), meute.getY()) > meute.getDistMax()){
                _objectif[0]=meute.getX();
                _objectif[1]=meute.getY();
            }
        }
        
        //Interaction moutons
        
        proche = _world.getAgentsProches(this, Moutons.class, getVision()*2);
        if(proche!=null)
        {
            //MEUTES
            if(!hasMeute()){
                //Creation de meute
                if(!proche.hasMeute()){
                    meute = new Meute(this, proche);
                    proche.meute=meute;
                }else{
                //Recrutage de meute, sinon fuite
                    if(!proche.meute.tenteRecrute(this)){
                        _objectif[0]=proche.meute.getX();
                        _objectif[1]=proche.meute.getY();
                        _fuis=true;
                        return;
                    }
                }
            } else {
                // Fuite des autres meutes
                if(proche.hasMeute()){
                    _objectif[0]=proche.meute.getX();
                    _objectif[1]=proche.meute.getY();
                    _fuis=true;
                    return;
                }
            }
            //tentative de reproduction
            if(_faim>_faimMax*0.3 && getMature()){
                    if(proche._faim>proche._faimMax*0.3){
                    _objectif[0]=proche._x;
                    _objectif[1]=proche._y;
                    _fuis=false;
                    return;
                }
            }
        }
        
        if(_faim<_faimMax){
            int herbeProche[]=_world.getPlusProcheTerrain(_x,_y,getVision(),Case.HERBE);
            if(herbeProche[0]!=-1){
                _objectif=herbeProche;
                _fuis=false;
                return;
            }
        }
        
        if (Math.random() > 0.5) // au hasard
        {
            _objectif[0]=_x+((Math.random() > 0.5)?(1):(-1));
            _fuis=false;
        } else {
            _objectif[1]=_y+((Math.random() > 0.5)?(1):(-1));
            _fuis=false;
        }
    }
    
    @Override public void creationBebe(Agent reproducteur)
    {
        _world.add(new Moutons(_x, _y, _world,new ADN(this._adn, reproducteur._adn)));
    }
    
    @Override public boolean getMature() {
        return (_age>_ageMax*0.1);
    }
}
