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
    protected static final int TAILLEADN=5;
    protected static final int POTENTIELADN=10;
    
    
    protected boolean _alive; //Si l'agent est vivant. Sinon il décède
    protected int _moveSpeed; //0=rapide, grand = pas rapide (nb d'itérations entre chaque déplacements)
    protected int _reprod; //nbr d'itératiosn entre chaque reprod
    protected int _faim;
    protected int _faimMax;
    protected int _vision;
    protected int _age;
    protected int _ageMax;
    
    /*
     * Code genetique des agents. chaque chiffre représente une variable.
     * Cette variable varie en fonction du nbr de pts dans le chiffre.
     * 10^0 = _faimMax (+10% par point)
     * 10^1 = _ageMax (+10% par point)
     * 10^2 = _moveSpeed (+1/3 pts)
     * 10^3 = _vision (+1/2 pts)
     * //TODO: 10^4 = _fréquenceReproduction
     * 
     * TODO: 2 denières valeures = tares génétiques et trash
     * avec la valeure en binaire de cette valeure, on met à true ou false
     * des tares génétiques ou pas(ex:
     * 1 = 01 rien,
     * 2 = 10 jambe de bois
     * 3 = 11 jambe en bois + rien
     * 4 = 100 oeil de verre
     * 5 = 101 oeil de verre + rien
     * 6 = 110 oeil de verre + jambe en bois
     * 7 = 111 oeil de verre + jambe en bois + rien
     * pour que ce soit pas trop chiant, plus la valeure est grande plus les 
     * tares sont handicapantes, puisque plus rares.
     */
    
    protected int _ADN;
    
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
        this(__x, __y, __w, rouge, vert, bleu, __faimMax, __ageMax,
                __moveSpeed, __vision, makeADN(POTENTIELADN));
    }
    
    public Agent(int __x, int __y, World __w, int rouge, int vert, int bleu,
            int __faimMax, int __ageMax, int __moveSpeed, int __vision, int __ADN) {
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
        _ADN=__ADN;
        
        
        
        _faimMax = __faimMax+(getBrinADN(1)* (__faimMax/10 ));
        _faim=__faimMax/2; //les agents commencents avec 50% de faim max
        _ageMax=(int)(__ageMax + Math.random()*(__ageMax/10))-(__ageMax/5)+(getBrinADN(2)*__ageMax/10) ; //ageMax = ageMax moyen +- 5%
        _moveSpeed = __moveSpeed - (getBrinADN(3)/3);
        if(_moveSpeed<0){ _moveSpeed=0; }
        _vision = __vision + (getBrinADN(4)/2);
        
        _reprod = -1;
    }

    abstract public void step();
/**
 * Crée de l'ADN (ADN = un nombre entre 0 et 10^TAILLEADN)
 * potentielGénétique = nombre de points à répartir. Doit être inferieur à
 * TAILLEADN * 9 (par ex, avec TAILLEADN = 2, potentielGenetique < 18
 * @param potentielGenetique: le nombre de points à répartir
 * @return un brin d'ADN
 */
    public static int makeADN(int potentielGenetique)
    {
        return addADN(0, potentielGenetique);
    }
    
    /**
     * renvoie le brin d'adn spécifié (ex: 0 = le chiffre des unités de l'ADN)
     * @param brin
     * @return le chiffre 'brin' de l'ADN
     */
    protected final int getBrinADN(int brin)
    {
        return (int)(_ADN/Math.pow(10, brin))%10;
    }
    
    /**
     * prend un ADN, redistribue certains points et renvoie l'ADN modifié
     * (redistribue jusqu'à la moitié des pts de chaque brin)
     * @param ADN
     * @return l'ADN modifié
     */
    protected int muteADN(int ADN)
    {
        int potentiel=0;
        for(int i=0;i<TAILLEADN;i++){
            int val=(int)(Math.random()*getBrinADN(i)/2);
            potentiel+=val;
            ADN-=(int)(val*Math.pow(10, i));
        }
        return addADN(ADN, potentiel);
    }
    
    /**
     * ajoute le potentiel à l'ADN en paramètre
     * @param ADN
     * @param potentiel
     * @return l'ADN modifié
     */
    protected static int addADN(int ADN, int potentiel)
    {
        while(potentiel>0)
        {
            for(int i=0;i<TAILLEADN;i++)
            {
                int pdix=(int)(Math.pow(10, i));
                if((ADN/pdix)%10 < 9){
                    if((1./TAILLEADN)>Math.random()){
                        ADN+=pdix;
                        potentiel--;
                    }
                }
            }
        }
        return ADN;
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
