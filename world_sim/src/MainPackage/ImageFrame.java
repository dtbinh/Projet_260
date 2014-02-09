package MainPackage;




import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class makes it easy to display an image -- Display is also refreshed anytime the enclosed source image is modified.
 * You may want to use an ImageBuffer object (or a BufferedImage object) as enclosed image.
 *  
 * @author nicolas
 * 20070919
 *
 */

public class ImageFrame extends JPanel implements Runnable {

	//private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int refreshDelay = 1000; // in ms. -- default is 1s.

	public ImageFrame() 
	{
		super();

		new Thread(this).start();
	}

	public void setImage( BufferedImage __image)
	{
		this.image = __image;
	}
	
	public void paintComponent(Graphics g) 
	{
		//g.drawImage(this.image, 0, 0, this); // fixed size
		g.drawImage(this.image,0,0,getWidth(),getHeight(),this); // resize image wrt. window size
	}

	public void run() 
	{
		while (true) {
			repaint();
			try {
				Thread.sleep(this.refreshDelay);
			} catch (InterruptedException e) 
			{
			}
		}
	}
	
	public void setRefreshDelay(int delay)
	{
		this.refreshDelay = delay;
	}
	
	/**
	 * create and display an ImageFrame object
	 * @param __name
	 * @param __image
	 * @param __refreshDelay
	 * @return object created
	 */
	static public ImageFrame makeFrame ( String __name, BufferedImage __image, int __refreshDelay )
	{
		return makeFrame(__name, __image, __refreshDelay, __image.getWidth(), __image.getHeight());
	}
	
	/**
	 * create and display an ImageFrame object
	 * @param __name
	 * @param __image
	 * @param __refreshDelay
	 * @param __width initial window width
	 * @param __height initial window height
	 * @return
	 */
	static public ImageFrame makeFrame ( String __name, BufferedImage __image, int __refreshDelay, int __width, int __height )
	{
		ImageFrame imageFrame = new ImageFrame();
		JFrame frame = new JFrame(__name);
		
		frame.setSize(__width, __height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(imageFrame);
		frame.setVisible(true);
		
		imageFrame.setImage(__image);
		imageFrame.setRefreshDelay(__refreshDelay);
		
		return imageFrame;
	}
	
	
	// *** demo *** 
	
	public static void main(String[] args) 
	{
		int w = 100;
	    int h = 100;
		ImageBuffer image = new ImageBuffer(w,h);
	    
		// create and display frame
		ImageFrame imageFrame =	makeFrame( "ImageFrame Demo", image, 1000, 400, 400 );
        
		// randomly change the pixels color in the enclosed image
        do {
		    for ( int j = 0 ; j != h ; j++ )
		    	for ( int i = 0 ; i != w ; i++ )
		    	{
		    		int r = (int)(Math.random()*255.);
		    		int g = (int)(Math.random()*255.);
		    		int b = (int)(Math.random()*255.);
		    		image.setPixel(i, j, r, g ,b );
		    	}
        } while ( true );
	}
}
