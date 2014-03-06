package MainPackage.Agents;

import MainPackage.World;
import MainPackage.Case;
import java.util.ArrayList;

public abstract class Agent {

    protected World _world;
    protected int _x;
    protected int _y;
    protected int _etat;
    protected int _redValue;
    protected int _greenValue;
    protected int _blueValue;
    
    
    protected boolean _alive; //Si l'agent est vivant. Sinon il décède
    protected int _moveSpeed; //0=rapide, grand = pas rapide (nb d'itérations entre chaque déplacements)
    protected int _faim;
    protected int _faimMax;
    protected int _vision;
    protected int _age;
    protected int _ageMax;
    protected int _tpsGestation;
    protected boolean dort;
    protected int gestation; //durée de gestation: -1 = pas enceint, 0 = fait un bébé.
    protected Agent partenaire;
    
    protected boolean _aquatique;
    protected boolean diurne;
    
    private int tryMove;
    private int sommeil;
    
    protected ADN _adn;
    
    protected int[] _objectif;
    protected boolean _fuis;
    protected int constitution; // représente l'état actuel du mob mort (intact, mangé, pourri...)
    
    private int _itMS;
    private int _orient;
    
    public int getX(){return _x;}
    public int getY(){return _y;}
    
    public Agent(int __x, int __y, World __w) {
        this(__x, __y, __w, 9999, 9999, 5, 0, 9999, new ADN());
    }
    
    
    public Agent(int __x, int __y, World __w,
            int __faimMax, int __ageMax, int __moveSpeed, int __vision, int __tpsGestation, ADN __adn) {
        
        tryMove=0;
        dort=false;
        constitution=10;
        gestation = -1;

        //partie commune à tout les agents
        _x = __x;
        _y = __y;
        _world = __w;

        _aquatique = false;
        _orient = 0;
        _objectif = new int[2];
        _objectif[0]=_x;
        _objectif[1]=_y;
        _alive = true;
        _itMS = 0;
        _fuis = false;
        _age=0;
        _adn= __adn;
        _tpsGestation=__tpsGestation;
        sommeil=_world.getDureeJour();
        diurne=true;
        
        
        //Création génétique
        _faimMax = __faimMax+((_adn.hasTrait(ADN.FAIM1))?__faimMax:0)+((_adn.hasTrait(ADN.FAIM2))?__faimMax:0);
        _faim=(int)(__faimMax*0.50); //les agents commencents avec 30% de faim max
        
        _ageMax=__ageMax+((_adn.hasTrait(ADN.AGE1))?__ageMax:0)+((_adn.hasTrait(ADN.AGE2))?__ageMax:0);
        
        _moveSpeed = __moveSpeed+((_adn.hasTrait(ADN.VITESSE1))?-1:0)+((_adn.hasTrait(ADN.VITESSE2))?-1:0);
        if(_moveSpeed<0){ _moveSpeed=0; }
        
        _vision = __vision+((_adn.hasTrait(ADN.VISION1))?1:0)+((_adn.hasTrait(ADN.VISION2))?1:0);
        _vision = (_adn.hasTrait(ADN.AVEUGLE))?0:_vision;
        
        _ageMax = (_adn.hasTrait(ADN.MALADIEMORTELLE))?_ageMax/3:_ageMax;
        
        _moveSpeed = (_adn.hasTrait(ADN.TETARPLEGIQUE))?_moveSpeed+10:_moveSpeed;
    }

    abstract public void step();
    
    
    //Déplacement
    public void move() {
        if(_alive){
            if (_itMS <= 0) {
                _orient=_world.getDirection(_x, _y, _objectif[0], _objectif[1]); //obtient la direction en fct de l'objectif
                if(_fuis){ //inverse l'orientation si on fuit l'objectif
                    _orient=(_orient+2)%4;
                }
                // met a jour: la position de l'agent (depend de l'orientation)
                tryMove=0;
                deplacement();
                _itMS = _moveSpeed;
            } else {
                _itMS--;
            }
        }
    }
    
    /**
     * Déplacement des agents.
     * appelle la fonction de déplacement voulue (torique ou non thorique)
     */
    private void deplacement()
    {
        deplacementNT();
    }
    
    private void deplacementT()
    {
        boolean obstacles[] = obstacles();
            switch (_orient) {
                case 0: // nord	
                    if(obstacles[3]){
                        _y = (_y - 1 + _world.getHeight()) % _world.getHeight();
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 1:	// est
                     if(obstacles[1]){
                        _x = (_x + 1 + _world.getWidth()) % _world.getWidth();
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 2:	// sud
                     if(obstacles[2]){
                        _y = (_y + 1 + _world.getHeight()) % _world.getHeight();
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 3:	// ouest
                     if(obstacles[0]){
                        _x = (_x - 1 + _world.getWidth()) % _world.getWidth();
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                    // 4 ou autre = pas bouger
            }
    }
    
    private void deplacementNT()
    {
        boolean obstacles[] = obstacles();
            switch (_orient) {
                case 0: // nord
                    if(obstacles[3] && _y > 0){
                        _y--;
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 1:	// est
                     if(obstacles[1] && _x < _world.getWidth()-1){
                        _x++;
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 2:	// sud
                     if(obstacles[2] && _y < _world.getHeight()-1){
                        _y++;
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 3:	// ouest
                     if(obstacles[0] && _x > 0){
                        _x--;
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                    // 4 ou autre = pas bouger
            }
    }
    
    /**
     * Verifie les 4 cases autour de l'agent, renvoie un tableau de boolean qui
     * indique si il peut se déplacer dans la case correspondante (true) ou pas.
     * Les cases "bloquantes" sont: EAU+2, FEU, LAVE
     * 0=ouest, 1=est, 2=sud, 3=nord
     * @return le tableau de boolean indiquant les directions bloquées.
     */
    private boolean[] obstacles()
    {
        boolean ret[] = new boolean[4];
        int voisins[] = new int[4];
        int j = 3;
        for (int i = 1; i < 8; i += 2) {
            voisins[j] = _world.getCellItem((_x - 1 + i % 3 + _world.getWidth()) % _world.getHeight(), (_y - 1 + i / 3 + _world.getWidth()) % _world.getHeight());
            j=(j+1)%4;
        }
        for (int i=0; i<4; i++){
            if((Case.getVal(voisins[i]) == Case.EAU && Case.getVar(voisins[i]) > 2) || (Case.getVal(voisins[i]) == Case.LAVE) || (Case.getVal(voisins[i]) == Case.FEU)) {
                ret[i]=false;
            }else{
                ret[i]=true;
            }
        }
        return ret;
    }
    
    //passage du temps, morts et naissances
    protected void temps(){
        if(getAlive()){
            if (_faim <= 0) {
                setmort();
            } else {
                _faim--;
            }
            if(_age>=_ageMax){
                _faim-=_age-_ageMax;
            }
            _age++;
            if(dort){
                sommeil++;
            }else if(_world.getJour() != diurne){
                sommeil-=2;
            }
            if(sommeil<-10 || (_world.getJour() != diurne && sommeil < 50 && _faim>_faimMax/10)){
                dort=true;
            }
            if(sommeil>90 || (_world.getJour() == diurne && sommeil > 20) || (sommeil>0 && _faim<_faimMax/10)){
                dort=false;
            }
            if(gestation==0){
                creationBebe(partenaire);
                gestation--;
            }else if(gestation>0){
                gestation--;
            }
        }
    }

    public void setmort() {
        _alive = false;
    }

    public void estmort() {
        if (!_alive) {
            if(constitution<=0){
                _world.remove(this);
            }else{
                constitution--;
            }
        }
    }

    public boolean getAlive() {
        return _alive;
    }
    
    public boolean getMature() {
        return (_age>_ageMax*0.2);
    }
    
    public void reproduction() { 
        if(_faim>(_faimMax*0.3) && getMature() && gestation==-1){
            Agent proche = _world.getAgentsProches(this, this.getClass(), 1);
            if (proche!=null) {
                if (proche._faim>(proche._faimMax*0.3) && proche.getMature() && proche.gestation==-1) {
                    _faim-=_faimMax*0.1;
                    proche._faim-=proche._faimMax*0.1;
                    gestation=_tpsGestation;
                    partenaire=proche;
                }
            }
        }
    }
    
    public abstract void creationBebe(Agent reproducteur);
}
