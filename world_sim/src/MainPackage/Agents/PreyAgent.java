package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class PreyAgent extends Agent {
    
    public PreyAgent(int __x, int __y, World __w) {
        super(__x, __y, __w, 255, 128, 0, 50, 300, 2, 5);
        _reprod = 30;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if (_world.getCellType(_x, _y) == Case.ARBRE) {
                _faim += (int) (Math.random() * 5) + 10;
                _world.setCellVal(_x, _y, Case.VIDE);
            }
            if (_world.containVoisins(_x, _y,Case.FEU)) {
                setmort();
            }
            
            
            setDir();
            reproduction();
        }

    }

    private void setDir() {
        ArrayList<Agent>[] proches = _world.getAgentsProches(_x,_y,_vision);
        for (int i = 0; i < _vision; i++) {
            if (!proches[i].isEmpty()) {
                for (Agent ag : proches[i]) {
                    if (ag.getClass() == PredatorAgent.class) {
                        _objectif[0]=ag._x;
                        _objectif[1]=ag._y;
                        _fuis=true;
                        return;
                    }
                }
            }
        }
        
        int herbeProche[]=_world.getPlusProche(_x,_y,_vision,Case.ARBRE);
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
        _world.add(new PreyAgent(_x, _y, _world));
    }
}
