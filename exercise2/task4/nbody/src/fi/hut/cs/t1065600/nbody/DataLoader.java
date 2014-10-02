package fi.hut.cs.t1065600.nbody;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class DataLoader {

	//UTF-8 support is nice, works as well for ASCII
	private static final Charset charset = Charset.forName("UTF-8");

	//The utility class it no meant to be instantiated
	private DataLoader() { }

	/**
	 * Load the n-body data from a file
	 * @param istream
	 * @return
	 */
	public static StarField load(final Path path) {
		
		String line;
		List<Star> stars = new LinkedList<Star>();
		
		//Use try-with-resource for automatic closing of IO resources
		try (BufferedReader reader = Files.newBufferedReader(path , charset)) {
			
			//Read through the entire file, line by line
			while ((line = reader.readLine()) != null ) {
				
				//Split the line 
				String[] entries = line.split("\\s+");
				
				//Only accept lines with the correct number of arguments
				if(entries.length == 7) {
					float mass = Float.parseFloat(entries[0]);
					final Star star = new Star(mass);
					
					float xpos = Float.parseFloat(entries[1]);
					float ypos = Float.parseFloat(entries[2]);
					float zpos = Float.parseFloat(entries[3]);
					star.setPosition(xpos, ypos, zpos);
					
					float xvel = Float.parseFloat(entries[4]);
					float yvel = Float.parseFloat(entries[5]);
					float zvel = Float.parseFloat(entries[6]);
					star.setVelocity(xvel, yvel, zvel);
					
					stars.add(star);
				}
				
			}
			
			return new StarField(stars);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			throw new AssertionError("this should never happen", e);
		}
	}

}
