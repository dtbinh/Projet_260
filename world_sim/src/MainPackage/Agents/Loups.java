package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class Loups extends Agent {

    //constructeur initial
    public Loups(int __x, int __y, World __w) {
        this(__x, __y, __w, makeADN());
    }
    
    //constructeur reprod
    public Loups(int __x, int __y, World __w, int __ADN) {
        super(__x, __y, __w, 150, 500, 1, 3, 10, __ADN);
        diurne=false;
    }

    @Override public void step() {
        temps();

        if (_alive && !dort) {
            if(_faim<_faimMax){
                ArrayList<Agent> mmcase = _world.getAgentCase(this);
                if (!mmcase.isEmpty()) {
                    for (Agent ag : mmcase) {
                        if (ag.getClass() == Moutons.class) {
                            if(ag.getAlive()){
                                ag.setmort();
                            }else{
                                ag.constitution--;
                                _faim += 30;
                            }
                        }
                    }
                }
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
        
        //tentative de reproduction
        if(_faim>_faimMax*0.4 && getMature())
        {
            Agent proche = _world.getAgentsProches(this, Loups.class, _vision*2);
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
        
        if(_faim<_faimMax){
            Agent proche = _world.getAgentsProches(this, Moutons.class, _vision*2);
            if(proche != null){
                _objectif[0]=proche._x;
                _objectif[1]=proche._y;
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
        _world.add(new Loups(_x, _y, _world, muteADN(_ADN, reproducteur._ADN)));
    }
    
    @Override public boolean getMature()
    {
        return (_age>_ageMax*0.1);
    }
}
