import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;


class SimulationPool {
    private Heuristic h;
	private int games;
	private double[][] weightSets;
    private int numRepetitions;

    private EvaluationResult[] evaluations;
    private Logger LOGGER = Logging.getInstance();

    public SimulationPool() {
        this.h = new Heuristic(false, true);
        this.games = 0;
        this.weightSets = null;
        this.numRepetitions = 1;
    }

    public SimulationPool(Heuristic h, int games, double[][] weightSets) {
        this.h = h;
        this.games = games;
        this.weightSets = weightSets;
        this.numRepetitions = 1;
    }

    public SimulationPool(Heuristic h, int games, double[][] weightSets, int numRepetitions) {
        this.h = h;
        this.games = games;
        this.weightSets = weightSets;
        this.numRepetitions = numRepetitions;
    }

    public EvaluationResult[] startGAScheduler(EvaluationResult[] evaluations) {
        this.evaluations = evaluations;
        startScheduler();
        return evaluations; 
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

        //long start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(2); //Threadpool size = ?
        List<Future<Integer>> results = new ArrayList<Future<Integer>>();
       
       	//For each generation, we run a 1000 games. For each game, we assign a thread.
        for(int i=0; i< games; i++){
            Simulation game = new Simulation(h, weightSets[i], numRepetitions);
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
                evaluations[j] = new EvaluationResult(weightSets[j], gamesResult[j]);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "an exception was thrown", e);
            }
        }

        //long end  = System.nanoTime();
        // System.out.printf("Simulation took %.2g seconds\n", (double)(end-start)/1e9);

        return gamesResult;
    }

}