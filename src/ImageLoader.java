

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/* Class loads appropriate image files to display on interface depending 
 * on ID, selection or user name
 */
public class ImageLoader {
	private BufferedImage image;

	// loads image from loginImgs folder
	// display on appropriate UI section
	
	public ImageLoader(String filename) {
		
		File file = new File(filename);
		if (!file.exists()) {
		// It returns false if File or directory does not exist
		System.out.println("the image " + filename + " does not exist : " + false);
		}else{
		// It returns true if File or directory exists
		System.out.println("the image " + filename + " does exist : " + true);
		}
		
		image = null;
		try {
		    image = ImageIO.read(file);
		} 
		catch (IOException e) {
			System.out.println("WOWIWOW");
		    e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}
}