package MainPackage.Agents;

import MainPackage.World;


public abstract class Agent {

    static final int redId = 0;
    static final int greenId = 1;
    static final int blueId = 2;
    
    protected World _world;
    protected int _x;
    protected int _y;
    protected int _etat;
    protected int _redValue;
    protected int _greenValue;
    protected int _blueValue;
    
    protected boolean _alive; //Si l'agent est vivant. Sinon il décède
    protected int _moveSpeed; //0=rapide, grand = pas rapide (nb d'itérations entre chaque déplacements)
    protected int _reprod; //nbr d'itératiosn entre chaque reprod
    protected int _faim;
    protected int _faimMax;
    protected int _vision;
    protected int _age;
    protected int _ageMax;
    
    protected int _ADN; // code génétique de la bestiole.
    
    protected int[] _objectif;
    protected boolean _fuis;
    
    private int _itMS;
    private int _itReprod;
    private int _orient;
    
    public int getX(){return _x;}
    public int getY(){return _y;}
    public int[] getColors(){int ret[]={_redValue, _greenValue, _blueValue}; return ret;}
    
    public Agent(int __x, int __y, World __w) {
        this(__x, __y, __w, 255, 0, 0, 9999, 9999, 5, 3);
    }
    
    public Agent(int __x, int __y, World __w, int rouge, int vert, int bleu,
            int __faimMax, int __ageMax, int __moveSpeed, int __vision) {
        _redValue = rouge;
        _greenValue = vert;
        _blueValue = bleu;

        //partie commune à tout les agents
        _x = __x;
        _y = __y;
        _world = __w;

        _orient = 0;
        _objectif = new int[2];
        _objectif[0]=_x;
        _objectif[1]=_y;
        _alive = true;
        _itMS = 0;
        _itReprod = 0;
        _fuis = false;
        _age=0;
        _ADN=makeADN(5, 25);
        
        
        _moveSpeed = __moveSpeed;
        _vision = __vision;
        
        _reprod = -1;
        _faimMax = __faimMax;
        _faim=__faimMax/2; //les agents commencents avec 50% de faim max
        _ageMax=(int)(__ageMax+Math.random()*(__ageMax/10))-(__ageMax/5); //ageMax = ageMax moyen +- 5%
    }

    abstract public void step();
/**
 * Crée de l'ADN (lol)
 * potentielGénétique = nombre de points à répartir. Doit être inferieur à
 * taille * 9 (par ex, avec taille = 2, potentielGenetique < 18
 * @param taille: la taille de l'ADN à renvoyer
 * @param potentielGenetique: le nombre de points à répartir
 * @return un brin d'ADN
 */
    private int makeADN(final int taille, int potentielGenetique)
    {
        int ret=0;
        while(potentielGenetique>0)
        {
            for(int i=0;i<taille;i++)
            {
                int pdix=(int)(Math.pow(10, i));
                if(((ret/pdix)*pdix)%(pdix*10) < 9){
                    if((1./taille)>Math.random()){
                        ret+=pdix;
                        potentielGenetique--;
                    }
                }
            }
        }
        System.out.println(ret);
        return ret;
    }
    
    public void move() {
        if (_itMS <= 0) {
            _orient=_world.getDirection(_x, _y, _objectif[0], _objectif[1]); //obtient la direction en fct de l'objectif
            if(_fuis){ //inverse l'orientation si on fuit l'objectif
                _orient=(_orient+2)%4;
            }
            
// met a jour: la position de l'agent (depend de l'orientation)
            switch (_orient) {
                case 0: // nord	
                    _y = (_y - 1 + _world.getHeight()) % _world.getHeight();
                    break;
                case 1:	// est
                    _x = (_x + 1 + _world.getWidth()) % _world.getWidth();
                    break;
                case 2:	// sud
                    _y = (_y + 1 + _world.getHeight()) % _world.getHeight();
                    break;
                case 3:	// ouest
                    _x = (_x - 1 + _world.getWidth()) % _world.getWidth();
                    break;
                    // 4 ou autre = pas bouger
            }
            _itMS = _moveSpeed;
        } else {
            _itMS--;
        }
    }
    
    public void temps(){
        if (_faim <= 0) {
            setmort();
        } else {
            _faim--;
        }
        if(_age>=_ageMax){
            setmort();
        } else {
            _age++;
        }
    }

    public void setmort() {
        _alive = false;
    }

    public void estmort() {
        if (!_alive) {
            _world.remove(this);
        }
    }

    public boolean getAlive() {
        return _alive;
    }
    
    public void reproduction() { 
        if (_itReprod >= _reprod) {
            _itReprod = 0;
            creationBebe();
        } else {
            _itReprod += (int) (Math.random() * 3);
        }
        if (_itReprod < 0) {
            _itReprod = 0;
        }
    }
    
    public abstract void creationBebe();
}
