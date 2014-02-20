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
        super(__x, __y, __w, 255, 128, 0, 50, 150, 4, 5, __ADN);
        _reprod = 30;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if (_world.getCellTerrain(_x, _y) == Case.HERBE) {
                _faim += (int) (Math.random() * 5) + 10;
                _world.setCellVal(_x, _y, Case.setTerrain(_world.getCellVal(_x, _y),Case.TERRE));
            }
            if (_world.containVoisins(_x, _y,Case.FEU) || _world.containVoisins(_x, _y,Case.LAVE)) {
                setmort();
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
        
        ArrayList<Agent>[] proches = _world.getAgentsProches(_x,_y,_vision);
        for (int i = 0; i < _vision; i++) {
            if (!proches[i].isEmpty()) {
                for (Agent ag : proches[i]) {
                    if (ag.getClass() == Loups.class) {
                        _objectif[0]=ag._x;
                        _objectif[1]=ag._y;
                        _fuis=true;
                        return;
                    }
                }
            }
        }
        
        int herbeProche[]=_world.getPlusProche(_x,_y,_vision,Case.HERBE);
        if(herbeProche[0]!=-1){
            _objectif=herbeProche;
            _fuis=false;
            return;
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

    @Override public void creationBebe()
    {
        _world.add(new Moutons(_x, _y, _world,muteADN(_ADN)));
    }
}
