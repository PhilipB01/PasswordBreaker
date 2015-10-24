import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//Utilizes Harris Corner Detection image analysis technique to select likely click coordinates for specific image
public class CornerAttack {
		private GridIdentifier[] points;
		private List<Double> offsets;
		private String correctHash, username;
		private int imageID;
		private BufferedImage image;
		private double tolerance;
		private int width, height;
		private List<List<GridIdentifier>> checkArray;
		
		public static void main(String args[]) {
			CornerAttack testAttack = new CornerAttack("test1", "pwd.txt", 20);
			int bNum = testAttack.countBluePixels();
			System.out.println("Number of blue pixels is: " + bNum);
			
			List<List<GridIdentifier>> check = testAttack.getCheckArray();
			System.out.println(check.size());
			int sum = 0;
			for (int i = 0; i < check.size(); i++) {
				sum += check.get(i).size();
				//System.out.println(check.get(i).size());
				/*List<GridIdentifier> s = check.get(i);
				
				int j=0;
				for(GridIdentifier g : s){
					j++;
		            System.out.println(" Looping over Set in Java element : " + g + "\t" + j);
		        }*/
			}
			if (check.size() != 0)
			System.out.println("Average number of checkable grids per click point: " + sum/check.size());
			
			testAttack.crackPassword();
			
			System.out.println("Done!");
		}
		
		public CornerAttack(String username, String filepath, double t) {
			if (!loadDetails(username, filepath)) {
				System.out.println("No such user or file: " + username + ", " + filepath);
				System.exit(1);
			}
			String file = "ImageAnalysis/2.png";
			ImageLoader il = new ImageLoader("ImageAnalysis/" + imageID + ".png");
			image = il.getImage();
			width = image.getWidth();
			height = image.getHeight();
			tolerance = t;
			int numPoints = offsets.size()/2;
			points = new GridIdentifier[numPoints];
			checkArray = new ArrayList<List<GridIdentifier>>(numPoints);
			
			for (int i = 0; i < numPoints; i++) {
				points[i] = new GridIdentifier(-1, -1);
			}
		}
		
		private Boolean loadDetails(String username, String filepath) {
				String content = null;
				Scanner s = null;
				try {
					content = FileIO.readFile(filepath);
					s = new Scanner(content);
					String line = "";
					while (s.hasNextLine()) {
						line = s.nextLine();
						Scanner nextString = new Scanner(line);
						String nextUser = "";
						if (nextString.hasNext()) {
							nextUser = nextString.next();
						}
						nextString.close();
						
						if (username.equals(nextUser)) {
							// Load Login data from file
							offsets = new ArrayList<Double>();
							this.username = username;
							String id = line.substring(username.length()+1, username.length()+2);
							imageID = Integer.parseInt(id);
							
							line = line.substring(username.length()+3);
							Scanner	scan = new Scanner(line);
							scan.useDelimiter("\t|,| ");
							while(scan.hasNextDouble())
							{
								double d = scan.nextDouble();
								   offsets.add(d);
							}
							correctHash = scan.next();
							scan.close();
							return true;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("the password file does not exist : " + false);
				} finally {
					s.close();
				}
				return false;
		}

		public Boolean crackPassword() {
			List<Iterator<GridIdentifier>> iterators = new ArrayList();
			for (int i = 0; i < checkArray.size(); i++) {
				iterators.add(checkArray.get(i).iterator());
			}
			initialise(iterators);
			Date start = new Date();
			int count = 0;
			boolean exists = true;
			System.out.println("User name: " + username);
			System.out.println("Image ID: " + imageID);
			System.out.println("Stored Hash: " + correctHash);
			System.out.println("Password Length: " + points.length);
			System.out.println("Tolerance: " + tolerance);
			System.out.println("Attempting to crack password using corner guessing attack...");
			while (exists) {
				count++;
				String attempt = hashed();
				System.out.println(count);
				System.out.println(attempt);
				try {
					if (PasswordHash.validatePassword(attempt, correctHash)) {
						  Date end = new Date();
						  System.out.println(count);
			              System.out.println("Possible password grid identifiers: " + printPoints() + "\nTotal time to crack: " + ((end.getTime() - start.getTime()) / 1000) + " seconds." + "\n");
			              return true;
					}
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
				exists = increment(iterators);
			}
			// nothing found
			System.out.println("Search complete. Password could not be found!");
			return false;
		}
		
		private void initialise(List<Iterator<GridIdentifier>> iterators) {
			for(int i = 0; i < points.length; i++) {
				points[i] = iterators.get(i).next();
			}
		}

		private String hashed() {
			String s = "";
			CenteredDiscretization2D check = new CenteredDiscretization2D(tolerance, offsets, Arrays.asList(points));
			s = check.getPasswordString();
			return s;
		}

		private boolean increment(List<Iterator<GridIdentifier>> iterators) {
			int index = points.length - 1;
			while (index >= 0) {
				if (!iterators.get(index).hasNext()) {
					/*Iterator<GridIdentifier> iterator = checkArray.get(index).iterator();
					if (!iterator.hasNext()) {
						points[index] = iterator.next();
						//System.out.print(iterator.next() + " ");
					}*/
					iterators.set(index, checkArray.get(index).iterator());
					points[index] = iterators.get(index).next();
					index--;
				}
				else {
					points[index] = iterators.get(index).next();
					return true;
				}
	        }
			if (index<0) {
				return false;
			}
			return true;
		}
		private String printPoints() {
			String s = "";
			for (int i = 0; i < points.length; i++) {
				s+= "(" + points[i].getX() + ", " + points[i].getY() + "), ";
			}
			return s;
		}
		
		private int countBluePixels() {
			int count = 0;
			int size = points.length;
			List<Set<GridIdentifier>> tempSetList = new ArrayList<Set<GridIdentifier>>(size);
			for (int c = 0; c < size; c++) {
				tempSetList.add(new LinkedHashSet<GridIdentifier>());
			}
			
			for (int i = 0; i<height; i++) {
				for (int j = 0; j<width; j++) {
					Color c = null;
					if (image == null) {
						System.out.println("Why is image null?");
					} else {
					c = new Color(image.getRGB(j, i));
					}
					if (c.getBlue() > 250 && c.getRed()<200 && c.getGreen()<200) {
						count++;
						for (int n = 0; n<tempSetList.size(); n++) {
							GridIdentifier g = CenteredDiscretization2D.getIdentifier(new Point2D.Double(j,i), offsets.get(2*n), offsets.get(2*n+1), tolerance);
							tempSetList.get(n).add(g);
						}
					}
				}
			}
			for (int n = 0; n<size; n++) {
				checkArray.add(new ArrayList<GridIdentifier>(tempSetList.get(n)));
			}
			return count;
		}
		
		private List<List<GridIdentifier>> getCheckArray() {
			return checkArray;
		}

}
