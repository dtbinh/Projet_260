package MainPackage;

import MainPackage.Agents.Loups;
import MainPackage.Agents.Moutons;

public class MyEcosystem_predprey extends CAtoolbox {

    public static void main(String[] args) {

        // initialisation generale

        int delai = 200;//100; // -- delay before refreshing display -- program is hold during delay, even if no screen update was requested. USE WITH CARE. 
        int nombreDePasMaximum = Integer.MAX_VALUE;
        int it = 0;

        // initialise l'ecosysteme

        World world = new World("world.pgm");
        
        // Ajouts d'agents
      for (int i = 0; i != 10; i++) {
            world.add(new Moutons((int) (Math.random() * world.getWidth()), (int) (Math.random() * world.getHeight()), world));
        }
        for (int i = 0; i != 10; i++) {
            world.add(new Loups((int) (Math.random() * world.getWidth()), (int) (Math.random() * world.getHeight()), world));
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
