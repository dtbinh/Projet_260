package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class Moutons extends Agent {
    
    //constructeur initial
    public Moutons(int __x, int __y, World __w) {
        this(__x, __y, __w, makeADN());
    }
    
    //cosntructeur reprod
    public Moutons(int __x, int __y, World __w, int __ADN) {
        super(__x, __y, __w, 100, 300, 2, 3, 1, __ADN);
        diurne=true;
    }

    @Override public void step() {
        temps();

        if (_alive && !dort) {
            if (_world.getCellTerrain(_x, _y) == Case.HERBE) {
                _faim += 10;
                _world.setCellVal(_x, _y, Case.setTerrain(_world.getCellVal(_x, _y),Case.TERRE));
            }
            if (_world.containVoisins(_x, _y,Case.FEU) || _world.containVoisins(_x, _y,Case.LAVE)) {
                setmort();
                constitution=-1;
            }
            
            
            setDir();
            reproduction();
        }

    }

    private void setDir() {
        int feuProche[]=_world.getPlusProche(_x,_y,_vision,Case.FEU);
        if(feuProche[0]!=-1){
            _objectif=feuProche;
            _fuis=true;
            return;
        }
        int laveProche[]=_world.getPlusProche(_x,_y,_vision,Case.LAVE);
        if(laveProche[0]!=-1){
            _objectif=laveProche;
            _fuis=true;
            return;
        }
        
        Agent proche = _world.getAgentsProches(this, Loups.class, _vision*2);
        if(proche!=null)
        {
            _objectif[0]=proche._x;
            _objectif[1]=proche._y;
            _fuis=true;
            return;
        }
        
        //tentative de reproduction
        if(_faim>_faimMax*0.3 && getMature())
        {
            proche = _world.getAgentsProches(this, Moutons.class, _vision*2);
            if(proche!=null)
            {
                if(proche._faim>proche._faimMax*0.3){
                    _objectif[0]=proche._x;
                    _objectif[1]=proche._y;
                    _fuis=false;
                    return;
                }
            }
        }
        
        if(_faim<_faimMax){
            int herbeProche[]=_world.getPlusProche(_x,_y,_vision,Case.HERBE);
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
        _world.add(new Moutons(_x, _y, _world,muteADN(_ADN, reproducteur._ADN)));
    }
}
