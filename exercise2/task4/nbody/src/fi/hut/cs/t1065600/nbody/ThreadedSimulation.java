package fi.hut.cs.t1065600.nbody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadedSimulation extends Simulation {

    private ExecutorService es = null;

    //Use the super class constructor
    public ThreadedSimulation(StarField galaxies, StarViewer viewer, float step) {
        super(galaxies, viewer, step);
    }

    @Override
    public void init() {
        // TODO implement

        // Hint: use Runtime.getRuntime().availableProcessors() to determine
        // how many cores or virtual cores (e.g. Intel Hyper-Threading) your
        // system has. For this application, there is no use creating more
        // threads than that can execute at a time

        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    }

    @Override
    public void update() {
        if (es == null) throw new IllegalStateException("Simulation needs to be initialized first.");

        final Star[] stars = super.galaxies.getStars();
        final float simulationStep = super.simulationStep;
        final CountDownLatch gravityLatch = new CountDownLatch(stars.length);
        final CountDownLatch positionLatch = new CountDownLatch(stars.length);

        class GravityUpdater implements Runnable {
            private final AtomicInteger ai;

            GravityUpdater(AtomicInteger ai) {
                this.ai = ai;
            }

            @Override
            public void run() {
                while(true){
                    int i = ai.getAndAdd(1);
                    if(i >= stars.length) break;
                    for (Star otherStar : stars) {
                        stars[i].accelerationFrom(otherStar, simulationStep);
                    }
                    gravityLatch.countDown();
                }
            }
        }

        class PositionUpdater implements Runnable {
            private final AtomicInteger ai;

            PositionUpdater(AtomicInteger ai) {
                this.ai = ai;
            }

            @Override
            public void run() {
                while(true) {
                    int i = ai.getAndAdd(1);
                    if (i >= stars.length) break;
                    stars[i].updatePosition(simulationStep);
                    positionLatch.countDown();
                }
            }
        }

        AtomicInteger ai = new AtomicInteger(0);

        for (int i = 0; i < Runtime.getRuntime().availableProcessors() ; i++) {
            es.execute(new GravityUpdater(ai));
        }
        try {
            gravityLatch.await();
        } catch (InterruptedException e) {
        }
        System.out.println("gravity done");
        ai.set(0);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors() ; i++) {
            es.execute(new PositionUpdater(ai));
        }
        try {
            positionLatch.await();
        } catch (InterruptedException e) {
        }
        System.out.println("update done");
        // TODO implement
        // The call to update() must block until the update has been completely done

        // There are two good strategies to start with:
        //   - spawn threads and each thread takes a portion of the work
        //   - create an ExecutorService with enough threads to saturate the CPU and
        //     create asynchronous tasks for performing the computations

        // The workload can be split many ways, but one rather simple method is
        // to divide the array containing the Star objects into smaller pieces:
        // Indexes 1-100, 101-200, 201-300 etc.. Then each block is given to
        // a thread or to the ExecutorService to compute as an asynchronous task.
        //
        // Another approach is to divide the original array into N sub-arrays,
        // one for each thread if your solution uses N threads (where, N is the
        // number of cores, calculated in init())
        //
        // A third approach is to have an atomic counter (implemented using e.g.
        // AtomicInteger and the incrementAndGet() -method) for iterating over
        // the array of starts. Each thread gets the index of the next array
        // element to process from the atomic counter, until the counter value
        // exceeds the length of the array.
        //
        // There are also several other valid strategies, you have apply whatever
        // scheme you want, as long as the solution computes the update step
        // efficiently

        // Remember that the two phases: calculating acceleration and updating positions
        // must be performed sequentially in respect to each other.
    }

    // TODO If your solutions uses classes extending Thread, Callable or Runnable,
    // write the classes as inner classes inside ThreadedSimulation so that you can
    // submit your entire solution in this one file.
}
