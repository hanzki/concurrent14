package fi.hut.cs.t1065600.nbody;

/**
 * 		
 * Note: This implementation is NOT exact physics, but something mimicking
 * how the system would actually work
 *
 */
public final class Star {
	
	//Minor adjustment to the calculations, as the masses are not
	//truly point-like
	private static final float PLUMER_SOFTENING = 0.025f;
	
	//Stabilize the simulation a bit by dampening velocities each step
	private static final float VELOCITY_DAMPENING = 0.999f;

	//As we are using Newton's law of universal gravitation
	private static final float GRAVITATIONAL_CONSTANT = 0.667f;
	
	//Magic non-physical constant for adjusting how much of the acceleration
	//from gravity is applied to the velocity of the stars. It can compensate
	//for the reduction of stars, which may leave the galaxies at a mass deficit
	//for the purpose of of keeping themselves together. 
	private static final float AMPLIFY_ACCELERATION = 1.9f;
	
	
	//Mass of the celestial object
	private final float mass;
	
	//Position in the Cartesian coordinates
	private float xposition;
	private float yposition;
	private float zposition;
	
	//Velocity along the axis
	private float xvelocity;
	private float yvelocity;
	private float zvelocity;
	
	//Velocity along the axis
	private float xacceleration;
	private float yacceleration;
	private float zacceleration;
	
	/**
	 * Construct a new Start with the given mass
	 * @param mass
	 */
	public Star(float mass) {
		this.mass = mass;
	}
	
	/**
	 * Set the X, Y and Z position of the Star.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		this.xposition = x;
		this.yposition = y;
		this.zposition = z;
	}
	
	/**
	 * Set the X, Y and Z velocities of the Star.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setVelocity(float x, float y, float z) {
		this.xvelocity = x;
		this.yvelocity = y;
		this.zvelocity = z;
	}
	
	/**
	 * Create an exact copy of this Star.
	 * 
	 * @return
	 */
	public Star copy() {
		Star s = new Star(mass);
		s.setPosition(xposition, yposition, zposition);
		s.setVelocity(xvelocity, yvelocity, zvelocity);
		return s;
	}
	
	/**
	 * Update the stars position based on the current velocities. The position
	 * changes according to how long the time step is, since:
	 * 
	 * <ul>
	 *   <li>velocity change = acceleration * time</li>
	 *   <li>distance traveled = velocity * time</li>
	 * </ul>
	 * 
	 * @param step
	 */
	public void updatePosition(float step) {
		
		xvelocity += xacceleration * step * AMPLIFY_ACCELERATION;
		yvelocity += yacceleration * step * AMPLIFY_ACCELERATION;
		zvelocity += zacceleration * step * AMPLIFY_ACCELERATION;
		
		xvelocity *= VELOCITY_DAMPENING;
		yvelocity *= VELOCITY_DAMPENING;
		zvelocity *= VELOCITY_DAMPENING;
		
		xposition += xvelocity * step;
		yposition += yvelocity * step;
		zposition += zvelocity * step;
		
		xacceleration = 0;
		yacceleration = 0;
		zacceleration = 0;
	}
	
	/**
	 * Calculate the acceleration from the gravitonic pull of another star's
	 * mass. The other star's velocity is not affected.
	 * 
	 * @param other
	 */
	public void accelerationFrom(Star other, float step) {
		
		//Delta distances along the coordinate axis'
		final float dx = other.getXposition() - xposition;
		final float dy = other.getYposition() - yposition;
		final float dz = other.getZposition() - zposition;
		
		//The squared distance, distance^2
		//Calculating i*i is generally much faster than pow(i,2)
		final float dist2 = (dx * dx + dy * dy + dz * dz) + PLUMER_SOFTENING;
		
		final float dist = (float) Math.sqrt(dist2);
		final float dist3 = dist * dist * dist;
		
		if(dist3 < 1.0f) {
			return;
		}
		
		final float s = this.mass / dist3;
		
		xacceleration += dx * s * GRAVITATIONAL_CONSTANT; 
		yacceleration += dy * s * GRAVITATIONAL_CONSTANT; 
		zacceleration += dz * s * GRAVITATIONAL_CONSTANT; 
	}

	public float getMass() {
		return mass;
	}

	public float getXposition() {
		return xposition;
	}

	public float getYposition() {
		return yposition;
	}

	public float getZposition() {
		return zposition;
	}
}
