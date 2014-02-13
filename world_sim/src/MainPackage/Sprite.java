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

public class Sprite extends JPanel {


	private JFrame frame;
	
	private Image waterSprite;
	private Image grassSprite;
	private Image treeSprite;
	private Image sandSprite;
	private Image rockSprite;
	private Image earthSprite;
	
	private int spriteLength = 16;
	
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
                
                world =w;
		frame = new JFrame("World simulator");
		frame.add(this);
		frame.setSize(spriteLength*(world.getWidth()+1),spriteLength*(world.getHeight()+1));
		frame.setVisible(true);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
                g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		for ( int i = 0 ; i < world.getWidth() ; i++ ){
			for ( int j = 0 ; j < world.getHeight() ; j++ ){
                            
                            switch(Case.getTerrain(world.getCellVal(i, j))){
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
                            
                            switch(world.getCellType(i, j)){
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
                                    switch (Case.getVar(world.getCellVal(i, j))) {
                                        case 0:
                                        case 1:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        case 2:
                                        case 3:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        case 4:
                                        case 5:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        case 6:
                                        case 7:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        case 8:
                                        case 9:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            break;
                                        default:
                                            g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
                                            }
                                    break;
                            }
                        }
                    }
	}

}