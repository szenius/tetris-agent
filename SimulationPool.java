import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;


class SimulationPool {
	private int games;
	private double[][] weightSets;
    private boolean isBatch; // default true
    private boolean findAverage; // default false
    private int numRepetitions;

    public SimulationPool() {
        this.games = 0;
        this.weightSets = null;
        this.isBatch = true;
        this.findAverage = false;
        this.numRepetitions = 1;
    }

	public SimulationPool(int games, double[][] weightSets) {
		this.games = games;
        this.weightSets = weightSets;
        this.isBatch = true;
        this.findAverage = false;
        this.numRepetitions = 1;
	}

    public SimulationPool(int games, double[][] weightSets, boolean isBatch) {
        this.games = games;
        this.weightSets = weightSets;
        this.isBatch = isBatch;
        this.findAverage = false;
        this.numRepetitions = 1;
    }

    public SimulationPool(int games, double[][] weightSets, boolean isBatch, boolean findAverage, int numRepetitions) {
        this.games = games;
        this.weightSets = weightSets;
        this.isBatch = isBatch;
        this.findAverage = findAverage;
        this.numRepetitions = numRepetitions;
    }

    /**
    * This method runs X games simulations and assigns each game (with new chromosome/weight) to a thread. 
    * @return 
    **/
	public int[] startScheduler() {
	 	if(games <= 0 && weightSets == null) {  
            System.out.println("Please specify number of games and weights for each game");
            System.exit(-1);
        }

        //Gets the number of available processors on computer right now.
        int processors = Runtime.getRuntime().availableProcessors();

        long start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(2); //Threadpool size = ?
        List<Future<Integer>> results = new ArrayList<Future<Integer>>();
       
       	//For each generation, we run a 1000 games. For each game, we assign a thread.
        for(int i=0; i< games; i++){
            Simulation game;
            if (isBatch) {
                game = new Simulation(weightSets[i], findAverage, numRepetitions);
            } else {
                game = new Simulation(weightSets[0]);
            }
            Future<Integer> future = executor.submit(game); //Add Thread to be executed by thread pool
            results.add(future); //For retrieving results from thread.
        }

        //shut down the executor service now
        executor.shutdown();
        /*while (!executor.isTerminated()) {
        }
        for(Future<Integer> result : results){
            try {
                //There will be output delay here as the Future.get() waits for task to be complete.
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }*/

        //This can be abit excessive.
        int[] gamesResult = new int[games];
        for(int j=0; j<results.size(); j++) {
            try {
                gamesResult[j] = results.get(j).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        long end  = System.nanoTime();
        System.out.printf("Simulation took %.2g seconds\n", (double)(end-start)/1e9);

        return gamesResult;
    }

}