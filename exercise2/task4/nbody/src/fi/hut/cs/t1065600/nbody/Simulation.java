package fi.hut.cs.t1065600.nbody;

public abstract class Simulation {
	
	//Keep track of how many steps the simulation has executed
	private int stepCounter = 0;
	
	protected final StarField galaxies;
	protected final StarViewer viewer;
	protected final float simulationStep;
	
	private long timestampBeginCompute;
	private long timestampBeginRender;
	private long timestampAllDone;
	
	/**
	 * Construct a new Simulation.
	 * @param galaxies
	 * @param viewer
	 * @param step
	 */
	public Simulation(StarField galaxies, StarViewer viewer, float step) {
		
		this.galaxies = galaxies;
		this.viewer = viewer;
		this.simulationStep = step;
	}

	/**
	 * Initialize the simulation. Implementations should start threads and
	 * create synchronization primitives during this step. init() is only
	 * called once.
	 */
	public abstract void init();
	 
	/**
	 * Perform an update step on the simulation and render the new position
	 * of all of the stars on screen.
	 * 
	 * @param step
	 */
	public abstract void update();
	
	/**
	 * Render the galaxies to screen.
	 */
	public void render() {
		viewer.render(galaxies);
	}
	
	public final void timerMarkBeginCompute() {
		timestampBeginCompute = System.currentTimeMillis();
	}
	
	public final void timerMarkBeginRender() {
		timestampBeginRender = System.currentTimeMillis();
	}
	
	public final void timerMarkDone() {
		timestampAllDone = System.currentTimeMillis();

		stepCounter++;
	}
	
	public final void printTimings() {
		//Calculate runtime
		final long updateTime = (timestampBeginRender - timestampBeginCompute);
		final long renderTime = (timestampAllDone - timestampBeginRender);
		
		//Print out the runtime
		System.out.print("Step " + stepCounter + "\t\t");
		System.out.print("Update " + updateTime + " ms\t\t");
		System.out.print("Render " + renderTime + " ms\t\t");
		System.out.print("\n");
	}
}
