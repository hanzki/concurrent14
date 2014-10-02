package fi.hut.cs.t1065600.nbody;

public class SequentialSimulation extends Simulation {
	
	//Use the super class constructor
	public SequentialSimulation(StarField galaxies, StarViewer viewer, float step) {
		super(galaxies, viewer, step);
	}
	
	@Override
	public void init() {
		//The simple simulation doesn't need initialization
	}

	@Override
	public void update() {
		
		final Star[] stars = super.galaxies.getStars();
		
		/* CALCULATION BEGINS HERE - PHASE 1 - GRAVITATION */
		
		//Calculate changes to the velocities from gravity interactions
		for(Star currentStar : stars) {
			for(Star otherStar : stars) {
				currentStar.accelerationFrom(otherStar, super.simulationStep);
			}
		}
		
		/* CALCULATION CONTINUES - PHASE 2 - POSITION */
		
		//Move the stars according to their updated velocity
		for(Star currentStar : stars) {
			currentStar.updatePosition(super.simulationStep);
		}
		
		/* CALCULATION IS DONE */
	}

}
