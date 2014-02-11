package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.ArrayList;

public class PredatorAgent extends Agent {

    //constructeur initial
    public PredatorAgent(int __x, int __y, World __w) {
        this(__x, __y, __w, makeADN());
    }
    
    //constructeur reprod
    public PredatorAgent(int __x, int __y, World __w, int __ADN) {
        super(__x, __y, __w, 0, 0, 0, 75, 300, 2, 5, __ADN);
        _reprod = 75;
    }

    @Override public void step() {
        temps();

        if (_alive) {
            if(_faim<_faimMax){
                ArrayList<Agent> mmcase = _world.getAgentCase(this);
                if (!mmcase.isEmpty()) {
                    for (Agent ag : mmcase) {
                        if (ag.getClass() == PreyAgent.class) {
                            PreyAgent proie = (PreyAgent) ag;
                            _faim += 50;
                            proie.setmort();
                        }
                    }
                }
            }
            if (_world.containVoisins(_x, _y,Case.FEU)) {
                setmort();
            }
            
            
            setDir();
            reproduction();
        }
    }

    private void setDir() {
        if(_faim<_faimMax){
            ArrayList<Agent>[] proches = _world.getAgentsProches(_x,_y,_vision);
            for (int i = 0; i < _vision; i++) {
                if (!proches[i].isEmpty()) {
                    for (Agent ag : proches[i]) {
                        if (ag.getClass() == PreyAgent.class) {
                            PreyAgent age = (PreyAgent) ag;
                            if (age.getAlive()) {
                                _objectif[0]=age._x;
                                _objectif[1]=age._y;
                                _fuis=false;
                                return;
                            }
                        }
                    }
                }
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

    @Override public void creationBebe()
    {
        _world.add(new PredatorAgent(_x, _y, _world, muteADN(_ADN)));
    }
}
