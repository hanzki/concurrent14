package fi.hut.cs.t1065600.nbody;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class StarField {
	
	//Use an array to store the individual stars, it's decently fast and 
	//easy enough to iterate over. The star count does not change after 
	//initialization
	private final Star[] stars;
	
	/**
	 * Construct a new StarField from the supplied list of stars.
	 * @param starList
	 */
	public StarField(List<Star> starList) {
		assert starList != null;
		
		stars = new Star[starList.size()];
		starList.toArray(stars);
	}
	
	public StarField(Star[] starArray) {
		assert starArray != null;
		
		stars = new Star[starArray.length];
		
		//Perform a deep copy
		for(int i = 0; i < stars.length; i++) {
			stars[i] = starArray[i].copy();
		}
	}

	public int getSize() {
		return stars.length;
	}
	
	public StarField copy() {
		return new StarField(this.stars);
	}
	
	public Star[] getStars() {
		return stars;
	}
	
	/**
	 * Create a subset of this Star field using random sampling. The new StarField
	 * will have approximately (1.0 - factor) * 100% of the stars the original
	 * had.
	 * 
	 * @param factor
	 * @return
	 */
	public StarField reduce(float factor) {
		assert factor > 0.0f && factor <= 1.0f;
		
		Random r = new Random(System.currentTimeMillis());
		List<Star> list = new LinkedList<Star>();
		
		for(Star s : stars) {
			if(r.nextFloat() > factor) {
				list.add(s);
			}
		}
		
		return new StarField(list);
	}
}
