package MainPackage.Agents;

import MainPackage.World;
import MainPackage.Case;

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
    protected static final int TAILLEADN=6;
    protected static final int POTENTIELADN=10;
    
    
    protected boolean _alive; //Si l'agent est vivant. Sinon il décède
    protected int _moveSpeed; //0=rapide, grand = pas rapide (nb d'itérations entre chaque déplacements)
    protected int _reprod; //nbr d'itératiosn entre chaque reprod
    protected int _faim;
    protected int _faimMax;
    protected int _vision;
    protected int _age;
    protected int _ageMax;
    protected boolean dort;
    
    protected boolean _aquatique;
    protected boolean diurne;
    
    private int tryMove;
    private int sommeil;
    
    /*
     * Code genetique des agents. chaque chiffre représente une variable.
     * Cette variable varie en fonction du nbr de pts dans le chiffre.
     * 10^0 = _faimMax (+10% par point)
     * 10^1 = _ageMax (+10% par point)
     * 10^2 = _moveSpeed (+1/3 pts)
     * 10^3 = _vision (+1/2 pts)
     * //TODO: 10^4 = _fréquenceReproduction
     * 
     * 2 denières valeures = tares génétiques et trash (binaire):
     * 1000 0000 = tetraplegique (vitesse diminuée)
     * 0100 0000 = maladie genetique mortelle (agemax diminué)
     * 0000 1000 = aveugle (vision = 0)
     * 0000 0100 = estomac en carton (faimMax diminuiée)
     */
    
    protected int _ADN;
    
    protected int[] _objectif;
    protected boolean _fuis;
    protected int constitution; // représente l'état actuel du mob mort (intact, mangé, pourri...)
    
    private int _itMS;
    private int _itReprod;
    private int _orient;
    
    public int getX(){return _x;}
    public int getY(){return _y;}
    public int[] getColors(){int ret[]={_redValue, _greenValue, _blueValue}; return ret;}
    
    public Agent(int __x, int __y, World __w) {
        this(__x, __y, __w, 255, 0, 0, 9999, 9999, 5, 3, makeADN());
    }
    
    
    public Agent(int __x, int __y, World __w, int rouge, int vert, int bleu,
            int __faimMax, int __ageMax, int __moveSpeed, int __vision, int __ADN) {
        _redValue = rouge;
        _greenValue = vert;
        _blueValue = bleu;
        
        tryMove=0;
        sommeil=100;
        dort=false;
        constitution=10;

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
        _itReprod = 0;
        _fuis = false;
        _age=0;
        _ADN=__ADN;
        diurne=true;
        
        
        
        _faimMax = __faimMax+(getBrinADN(1)* (__faimMax/10 ));
        _faim=__faimMax/2; //les agents commencents avec 50% de faim max
        _ageMax=(int)(__ageMax + Math.random()*(__ageMax/10))-(__ageMax/5)+(getBrinADN(2)*__ageMax/10) ; //ageMax = ageMax moyen +- 5%
        _moveSpeed = __moveSpeed - (getBrinADN(3)/3);
        if(_moveSpeed<0){ _moveSpeed=0; }
        _vision = __vision + (getBrinADN(4)/2);
        
        taresADN();
        
        _reprod = -1;
    }

    abstract public void step();
    
    
    
    //génétique:
    
    private void taresADN()
    {
        //9 = 1001 (fois deux)
        //8 = 1000 (improbable)
        //4 = 0100 (moyenprobable)
        //2 = 0010 (probable)
        //1 = 0001 (très probable)
        //première moitiée
        
        if((getBrinADN(TAILLEADN-1) & 4)==4){ //estomac en carton
            _faimMax/=3;
        }
        if((getBrinADN(TAILLEADN-1) & 8)==8){ //aveugle
            _vision=0;
        }
        
        //deuxième moitiée
        
        if((getBrinADN(TAILLEADN-2) & 4)==4){ //maladie mortelle
            _ageMax/=3;
        }
        if((getBrinADN(TAILLEADN-2) & 8)==8){//paraplégique
            _moveSpeed+=10;
        }
    }
    
/**
 * Crée de l'ADN (ADN = un nombre entre 0 et 10^TAILLEADN)
 * potentielGénétique = nombre de points à répartir. Doit être inferieur à
 * TAILLEADN * 9 (par ex, avec TAILLEADN = 2, potentielGenetique < 18
 * @param potentielGenetique: le nombre de points à répartir
 * @return un brin d'ADN
 */
    public static int makeADN()
    {
        return addADN(0, POTENTIELADN);
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
                    if(obstacles[0]){
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
                     if(obstacles[3]){
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
                    if(obstacles[0] && _y > 0){
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
                     if(obstacles[2] && _y < _world.getHeight()){
                        _y++;
                    }else if(tryMove<=3){
                        _orient=(_orient+1)%4;
                        tryMove++;
                        deplacement();
                    }
                    break;
                case 3:	// ouest
                     if(obstacles[3] && _x > 0){
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
     * @return le tableau de boolean indiquant les directions bloquées.
     */
    private boolean[] obstacles()
    {
        boolean ret[] = new boolean[4];
        int voisins[] = new int[4];
        int j = 3;
        for (int i = 1; i < 8; i += 2) {
            voisins[j] = _world.getCellVal((_x - 1 + i % 3 + _world.getWidth()) % _world.getHeight(), (_y - 1 + i / 3 + _world.getWidth()) % _world.getHeight());
            j=(j+1)%4;
        }
        for (int i=0; i<4; i++){
            if((Case.getType(voisins[i]) == Case.EAU && Case.getVar(voisins[i]) > 2) || (Case.getType(voisins[i]) == Case.LAVE) || (Case.getType(voisins[i]) == Case.FEU)) {
                ret[i]=false;
            }else{
                ret[i]=true;
            }
        }
        return ret;
    }
    
    //passage du temps, morts et naissances
    protected void temps(){
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
        if(dort){
            sommeil++;
        }else{
            sommeil--;
        }
        if(sommeil<-10 || (_world.getJour() != diurne && sommeil < 50 && _faim>_faimMax/10)){
            dort=true;
        }
        if(sommeil>90 || (_world.getJour() == diurne && sommeil > 20) || (sommeil>0 && _faim<_faimMax/10)){
            dort=false;
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
