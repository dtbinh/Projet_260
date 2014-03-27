/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage.Agents;

import MainPackage.Case;
import MainPackage.World;
import java.util.HashMap;

/**
 *
 * @author 3361692
 */
public class Maison extends Agent{
    
    private HashMap<String, Integer> inventaire;
    
    public Maison(int __x, int __y, World __w) {
        super(__x, __y, __w, -1, -1, 0, 0, -1, new ADN());
        inventaire = new HashMap<String, Integer>();
        _world.setCellTypeVal(_x, _y, Case.OQP);
        bruleFeu =_world.getDureeJour()/4;
    }
    private int bruleFeu;
    
    @Override public void step() {
        if(inventaire.containsKey("bois")){
            bruleFeu--;
        }
        if(bruleFeu==0){
            bruleFeu =_world.getDureeJour()/4;
            retirerInventaire("bois");
        }
        if (_world.containVoisinsItem(_x, _y,Case.FEU) || _world.containVoisinsItem(_x, _y,Case.LAVE)) {
                setmort();
                constitution=-1;
                _world.setCellTypeVal(_x, _y, Case.FEU);
        }
    }
    
    public void retirerInventaire(String s)
    {
        int nbr = inventaire.get(s)-1;
        if(nbr==0){
            inventaire.remove(s);
        }else{
            inventaire.put(s, nbr);
        }
    }
    
    public void ajouterInventaire(String s, int i)
    {
        if(inventaire.containsKey(s)){
            int nbr = inventaire.get(s)+i;
            inventaire.put(s, nbr);
        }else{
            inventaire.put(s, i);
        }
    }
    
    @Override public Agent creationBebe(Agent reproducteur)
    {
        return null;
    }
    
    public boolean cheminay(){
        if(!inventaire.containsKey("bois")){
            return false;
        }else{
            return inventaire.get("bois")>=4;
        }
    }
    
}
