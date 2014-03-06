package MainPackage;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import MainPackage.World;
import MainPackage.Case;
import MainPackage.Agents.*;

public class Sprite extends JPanel {


	private JFrame frame;
	
	private Image waterSprite;
	private Image grassSprite;
	private Image treeSprite;
	private Image sandSprite;
	private Image rockSprite;
	private Image earthSprite;
        private Image flaqueSprite;
        private Image laveSprite;
        private Image feuSprite;
        
	private Image loupSprite;
	private Image moutonSprite;
	
        
        private Image noirSprite;
	private int spriteLength = 24;
	
	private World world;

	public Sprite(World w)
	{
		try
		{
			waterSprite = ImageIO.read(new File("water.png"));
			treeSprite = ImageIO.read(new File("tree.png"));
			grassSprite = ImageIO.read(new File("grass.png"));
			sandSprite = ImageIO.read(new File("sand.png"));
			rockSprite = ImageIO.read(new File("rock.png"));
			earthSprite = ImageIO.read(new File("earth.png"));
                        flaqueSprite = ImageIO.read(new File("flaque.png"));
                        laveSprite = ImageIO.read(new File("lave.png"));
                        feuSprite = ImageIO.read(new File("feu.png"));
                        
                        loupSprite = ImageIO.read(new File("loupN.png"));
                        moutonSprite = ImageIO.read(new File("moutonB.png"));
                        noirSprite = ImageIO.read(new File("sombre.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
                
                world =w;
		frame = new JFrame("World simulator");
		frame.add(this);
		frame.setSize(spriteLength*(world.getWidth()),spriteLength*(world.getHeight()));
		frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
                g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		for ( int i = 0 ; i < world.getWidth() ; i++ ){
			for ( int j = 0 ; j < world.getHeight() ; j++ ){
                            
                            switch(Case.getVal(world.getCellTerrain(i, j))){
                                case Case.SABLE:
                                    g2.drawImage(sandSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                case Case.TERRE:
                                    g2.drawImage(earthSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                case Case.ROCHE:
                                    g2.drawImage(rockSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                case Case.HERBE:
                                    g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                }
                            
                            switch(Case.getVal(world.getCellItem(i, j))){
                                case Case.ARBRE:
                                    g2.drawImage(treeSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                /*case Case.FEU:
                                    g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                case Case.CENDRES:
                                    g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;*/
                                case Case.EAU:
                                    switch (Case.getVar(world.getCellItem(i, j))) {
                                        case 0:
                                        case 1:
                                        case 2:
                                            g2.drawImage(flaqueSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8:
                                        case 9:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        default:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            }
                                    break;
                                case Case.LAVE:
                                    g2.drawImage(laveSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                case Case.FEU:
                                    g2.drawImage(feuSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;
                                /*case Case.GENLAVE:
                                    g2.drawImage(feuSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                    break;*/
                                
                            }
                            
                        }
                    }
                for(Agent a:world.getAgentsArray()){
                    if(a.getClass()==Moutons.class){
                        g2.drawImage(moutonSprite,spriteLength*a.getX(),spriteLength*a.getY(),spriteLength,spriteLength, frame);
                    }else if(a.getClass()==Loups.class){
                        g2.drawImage(loupSprite,spriteLength*a.getX(),spriteLength*a.getY(),spriteLength,spriteLength, frame);
                    }else{
                        g2.drawImage(moutonSprite,spriteLength*a.getX(),spriteLength*a.getY(),spriteLength,spriteLength, frame);
                    }
                }
                if(!world.getJour()){
                    g2.drawImage(noirSprite,0,0,spriteLength*world.getHeight(),spriteLength*world.getWidth(), frame);
                }
	}

}