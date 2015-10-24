

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FileIO {

	// Read method: input file name
	// output file content
	
	// Write method: input file name, String content
	// output content to file
	
	public static String readFile(String filepath) throws IOException  {
		BufferedReader bufReader = new BufferedReader(new FileReader(filepath));
		try {
			StringBuilder contents = new StringBuilder();
			String line = bufReader.readLine();
			
			while (line != null) {
	            contents.append(line);
	            contents.append(System.lineSeparator());
	            line = bufReader.readLine();
	        }
	        return contents.toString();
	    } 
		finally {
	        bufReader.close();
	    }
	}
	
	public static void writeFile(String filepath, String contents) throws IOException {
		PrintWriter out = null;
		try {
			// creates new file if it does not yet exist
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(filepath, true);
			out = new PrintWriter(new BufferedWriter(fw));
			out.println();
		}
		finally {
			out.close();
		}
	}
}
