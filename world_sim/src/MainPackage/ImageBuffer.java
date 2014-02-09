package MainPackage;




import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * This class is a wrap-up of the classic BufferedImage class. It provides simplified accessing method for RGB image.
 * You may want to display this image by enclosing it into an ImageFrame object.
 * 
 * @author nicolas
 * 20070919
 *
 */
public class ImageBuffer extends BufferedImage {

	public ImageBuffer(int width, int height) {
		super(width, height, BufferedImage.TYPE_INT_ARGB);
	}

    static public ImageBuffer loadFromDisk( String __filename)
    {
    	ImageBuffer image = null; 
    	try {
    		BufferedImage bi = ImageIO.read(new File(__filename));
    		image = new ImageBuffer(bi.getWidth(),bi.getHeight());
    		for ( int x = 0 ; x != bi.getWidth() ; x++ )
    			for ( int y = 0 ; y != bi.getHeight() ; y++ )
    				image.setRGB(x, y, bi.getRGB(x, y));
    	}
    	catch ( IOException e )
    	{
    		System.err.println("[error] image \""+__filename+"\" could not be loaded.");
    	}
    	return image;
    }
    
    /**
     * save current image buffer to PNG file. 
     * @param __filename output filename without extension
     */
    public void saveToDisk( String __filename )
    {
    	try {
    		ImageIO.write(this, "png", new File(__filename + ".png"));
    	}
    	catch ( IOException e )
    	{
    		System.err.println("[error] image \""+__filename+"\" could not be written to disk.");
    	}
    }
    
    /**
     * set target pixel color (RGB) -- color values btw 0 and 255
     * @param x
     * @param y
     * @param rgb a 3-values array containing [red,green,blue] values
     */
    public void setPixel( int x, int y , int rgb[] )
    {
    	this.setPixel(x, y, rgb[0] , rgb[1] , rgb[2]);
    }


    /**
     * set target pixel color (RGB) -- color values btw 0 and 255
     * @param x
     * @param y
     * @param red
     * @param green
     * @param blue
     */
    public void setPixel( int x, int y , int red, int green, int blue )
    {
    	int colorValues;
    	colorValues = 0xFF000000; // alpha set non transparent
		colorValues += red * 256*256; // red
		colorValues += green * 256; // green
		colorValues += blue ; // blue
    	this.setRGB(x, y, colorValues);
    }
    
    /**
     * return target pixel color
     * @param x
     * @param y
     * @return a 3-values array containing [red,green,blue] values -- color values btw 0 and 255
     */
    public int[] getPixel( int x, int y )
    {
    	int [] rgb = new int[3];
		int rawvalue = this.getRGB(x, y);
		rgb[0] = ( rawvalue & 0x00FF0000 ) / (int)Math.pow(256,2); // red
		rgb[1] = ( rawvalue & 0x0000FF00 ) / 256; // green
		rgb[2] = ( rawvalue & 0x000000FF ); // blue
    	return rgb;
    }

    // HSV methods (same as RGB but handles HSV values in and out) -- all values btw [0..1]
    public double[] getPixelHSB( int x, int y )
    {
    	int[] rgb = this.getPixel(x, y);
    	float[] hsvFloat = java.awt.Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], null);
    	double[] hsv = new double[3];
    	for (int i = 0; i < hsv.length; i++) 			
    		hsv[i] = hsvFloat[i];
    	return hsv;
    }
    public void setPixelHSB( int x, int y , double[] hsb)
    {
    	this.setPixelHSB(x, y, hsb[0], hsb[1], hsb[2]);
    }
    public void setPixelHSB( int x, int y , double h, double s, double b )
    {
    	int rgb = java.awt.Color.HSBtoRGB((float)h,(float)s,(float)b);
    	this.setRGB(x, y, rgb);
    }
    
    /**
     * fill screen with given color values (cls means "clear screen")
     * @param r
     * @param g
     * @param b
     */
	public void cls(int r, int g, int b) 
	{
		for ( int x = 0 ; x != this.getWidth() ; x++ )
			for ( int y = 0 ; y != this.getHeight() ; y++ )
				this.setPixel(x,y,r,g,b);
	}


    
    public void writeText(String __text)
    {
    	Graphics2D g = this.createGraphics();
    	g.setColor(Color.GREEN);
        g.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 36));
        g.drawString("Hello, World!", 10, 50);
        g.dispose();
    }
    
	public static void main(String[] args) throws IOException {
	    
		// build an ImageBuffer
		ImageBuffer image = new ImageBuffer(16,16); // method 1 : call constructor
		//ImageBuffer image = loadFromDisk("test"); // method 2 : image loading factory
	    
	    // Set a pixel
		int[] rgb = {255,0,0};
	    image.setPixel(0,0,rgb); // method 1a : wrap-up method from ImageBuffer
	    //image.setPixel(0,0,255,255,255,255); // method 1b : wrap-up method from ImageBuffer
		//image.setRGB(0, 0, 0xFF00FF00); // method 2 : standard method from BufferedImage using only one integer value

	    // various specific wrap-up methods from ImageBuffer or general accessing method inherited from the BufferedImage object ...
	    int w = image.getWidth();
	    int h = image.getHeight();
	    
	    for ( int j = 0 ; j != h ; j++ )
	    	for ( int i = 0 ; i != w ; i++ )
	    		image.setPixel(i, j, (int)((double)(i)/(double)w*255.),(int)((double)(j)/(double)h*255.),(int)((double)(j)/(double)h*255.));
	    		
	    System.out.println("number of bands : " + image.getRaster().getNumBands());
	    System.out.println("colors of origin pixel: " + image.getRGB(0, 0));
	    rgb = image.getPixel(0, 0);
	    System.out.println("colors of origin pixel (detailed) : ("+rgb[0]+","+rgb[1]+","+rgb[2]+")");
	    System.out.println("image size is ("+w+","+h+")");
	    
	    // save to disk
	    image.saveToDisk("testdev");
	    System.out.println("file \"testdev.png\" created.");
	
	
	    // display image -- you may prefer using ImageFrame if you want to display an image.
	    
	    Icon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);

        final JFrame f = new JFrame("Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
        
	}
}
