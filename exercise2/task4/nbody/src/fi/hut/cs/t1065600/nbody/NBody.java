package fi.hut.cs.t1065600.nbody;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

import javax.swing.SwingUtilities;

public class NBody {
	
	//Change these to suit your screen. 
	//A good default setup is 1280x720 at 10x zoom
	private static final int WINDOW_WIDTH = 1280;
	private static final int WINDOW_HEIGHT = 720;
	private static final float ZOOM = 10f;
	
	//How long is a single update
	//The step will significantly affect the outcome of the simulation. 
	//You can experiment with other values in the range [0.01f ... 0.5f]
	private static final float TIME_STEP = 0.1f;
	
	//Randomly reduce the number of stars by x%
	//For most computers, the entire 80k star field is a very high load
	//Note: If you reduce the number of stars too much, the galaxies lose
	//too much mass and can no longer hold themselves together, so the
	//stars just... scatter
	//The variable should be between [0f ... 0.80f]
	private static final float STAR_REDUCTION = 0.00f;
	
	
	public static void main(String[] args) throws InterruptedException {
		//Load the n-body data
		Path p = Paths.get("data", "dubinski.tab");
		StarField field = DataLoader.load(p);

		field = field.reduce(STAR_REDUCTION);
		
		final StarViewer viewer = new StarViewer(WINDOW_WIDTH, WINDOW_HEIGHT, ZOOM);
		final Semaphore sem = new Semaphore(0);

		//All Swing related should pumped through the Event Dispatch Thread
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	 viewer.init(sem);
		    }
		});
		
		//Wait for the graphics to be up and ready
		sem.acquire();
		System.out.println("Galaxy viewer is ready");
		System.out.println();
		
		//Render the view once before starting to run the simulation
		viewer.render(field);
		
		//You can use the sequential version to see how long a single step takes
		//without multithreading. Warning, it may take minutes...
		//Simulation simulation = new SequentialSimulation();
		
		Simulation simulation = new ThreadedSimulation(field, viewer, TIME_STEP);
		simulation.init();
		
		while(true) {
			//Start a timer running to see how long the update will take
			simulation.timerMarkBeginCompute();
			
			//Perform the actual update operation
			simulation.update();
			
			//Update the graphics
			simulation.timerMarkBeginRender();
			simulation.render();
			
			//Mark the entire update done and print how long it took
			simulation.timerMarkDone();
			simulation.printTimings();
		}
	}
}
