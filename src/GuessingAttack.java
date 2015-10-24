import java.awt.geom.Point2D;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class GuessingAttack {
	private Point2D.Double[] points;
	private List<Double> offsets;
	private String correctHash, username;
	private double tolerance;
	private int width, height;
	public static void main(String args[]) {
		GuessingAttack testAttack = new GuessingAttack("test1", "pwd.txt", 800, 500, 20);
		testAttack.crackPassword();
		//testAttack = new GuessingAttack("seanbean", "pwd.txt", 800, 500, 10);
		//testAttack.crackPassword();
		
		System.out.println("Done!");
	}
	
	public GuessingAttack(String username, String filepath, int w, int h, double t) {
		if (!loadDetails(username, filepath)) {
			System.out.println("No such user or file: " + username + ", " + filepath);
			System.exit(1);
		}
		width = w;
		height = h;
		tolerance = t;
		int numPoints = offsets.size()/2;
		points = new Point2D.Double[numPoints];
		
		for (int i = 0; i < numPoints; i++) {
			points[i] = new Point2D.Double(0, 0);
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
		Date start = new Date();
		int count = 0;
		System.out.println(username);
		System.out.println(correctHash);
		System.out.println("Password Length: " + points.length);
		System.out.println("Tolerance: " + tolerance);
		System.out.println("Attempting to crack password using brute force guessing attack...");
		while (true) {
			count++;
			String attempt = hashed();
			System.out.println(count);
			System.out.println(attempt);
			try {
				if (PasswordHash.validatePassword(attempt, correctHash)) {
					  Date end = new Date();
					  System.out.println(count);
		              System.out.println("Possible password coordinates: " + printPoints() + "\nTotal time to crack: " + ((end.getTime() - start.getTime()) / 1000) + " seconds." + "\n");
		              return true;
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
			increment(tolerance * 2);
		}
	}
	
	private String hashed() {
		String s = "";
		CenteredDiscretization2D check = new CenteredDiscretization2D(Arrays.asList(points), tolerance, offsets);
		s = check.getPasswordString();
		return s;
	}

	private void increment(double increment) {
		int index = points.length - 1;
		while (index >= 0) {
			if (points[index].getX() >= width + increment && points[index].getY() >= height + increment) {
				points[index].setLocation(0, 0);
				index--;
			}
			else if (points[index].getX() > width + increment) {
				points[index].setLocation(0, points[index].getY() + increment);
				break;
			}
			else {
				points[index].setLocation(points[index].getX() + increment, points[index].getY());
				break;
			}
        }
	}
	private String printPoints() {
		String s = "";
		for (int i = 0; i < points.length; i++) {
			s+= "(" + points[i].getX() + ", " + points[i].getY() + "), ";
		}
		return s;
	}
}
