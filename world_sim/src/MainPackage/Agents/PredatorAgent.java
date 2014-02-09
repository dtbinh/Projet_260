package MainPackage.Agents;

import MainPackage.World;
import MainPackage.Case;
import java.util.ArrayList;

public class PredatorAgent extends Agent {

    boolean _predator;

    public PredatorAgent(int __x, int __y, World __w) {
        super(__x, __y, __w);
        _redValue = 0;
        _greenValue = 0;
        _blueValue = 0;
        _predator = true;

        _moveSpeed = 0;
        _faim = 25;
        _faimMax = 75;
        _reprod = 75;
        _ageMax=(int)(300+Math.random()*50-25);
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
        _world.add(new PredatorAgent(_x, _y, _world));
    }
}
