package MainPackage;

import MainPackage.Agents.Loups;
import MainPackage.Agents.Moutons;
import MainPackage.Agents.Crocodile;

public class MyEcosystem_predprey{

    public static void main(String[] args) {

        // initialisation generale

        int delai = 2;//200; // -- delay before refreshing display -- program is hold during delay, even if no screen update was requested. USE WITH CARE. 
        int nombreDePasMaximum = Integer.MAX_VALUE;
        int it = 0;
        int nbMoutons = 50;
        int nbLoups = 10;
        int nbCroco = 50;
        String map = "world2.pgm";
        
        //world.pgm = monde 1er
        //world2.pgm = monde version grand
        //test.pgm = monde 10*10 100% herbe

        // initialise l'ecosysteme

        World world = new World(map);
        
        // Ajouts d'agents/*
      for (int i = 0; i < nbMoutons; i++) {
          int valX = (int) (Math.random() * world.getWidth());
          int valY = (int) (Math.random() * world.getHeight());
          while(world.getCellTerrain(valX, valY)== Case.SABLE){
            valX = (int) (Math.random() * world.getWidth());
            valY = (int) (Math.random() * world.getHeight());
            }
            world.add(new Moutons(valX, valY, world));
        }
        for (int i = 0; i < nbLoups; i++) {
          int valX = (int) (Math.random() * world.getWidth());
          int valY = (int) (Math.random() * world.getHeight());
          while(world.getCellTerrain(valX, valY)== Case.SABLE){
            valX = (int) (Math.random() * world.getWidth());
            valY = (int) (Math.random() * world.getHeight());
            }
            world.add(new Loups(valX, valY, world));
        }
        for (int i = 0; i < nbCroco; i++) {
          int valX = (int) (Math.random() * world.getWidth());
          int valY = (int) (Math.random() * world.getHeight());
          while(world.getCellTerrain(valX, valY)!= Case.SABLE){
            valX = (int) (Math.random() * world.getWidth());
            valY = (int) (Math.random() * world.getHeight());
            }
            world.add(new Crocodile(valX, valY, world));
        }
        // mise a jour de l'�tat du monde

        while (it != nombreDePasMaximum) {
            
            // 1 - update

            world.step();

            // 2 - iterate

            it++;

            try {
                Thread.sleep(delai);
            } catch (InterruptedException e) {
            }
        }

    }
}
