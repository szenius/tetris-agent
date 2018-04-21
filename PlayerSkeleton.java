import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlayerSkeleton {

	public static long SEED_1 = Long.parseLong("HEXENACHT IN THE MIDDLE OF DAY");
	public static long SEED_2 = Long.parseLong("WfboNJjcuDeKHthYEFSnZPixwqvyXB");
	public static long SEED_3 = Long.parseLong("Hepburn symbol: 981.12.22-75-A");
	public static long SEED_4 = Long.parseLong("さざんたじいふえかりそぢこせわべばでぶをづゆよくてめぜぱはひ");
	public static long SEED_5 = Long.parseLong("LAytÄwDuqVlORpÖsHiadöGZEüÜßSbK");
	public static long SEED_6 = Long.parseLong("СцаШЫЦчТВИГшМЮЯюДйРяХОЙЗёрзЛлы");
	public static long SEED_7 = Long.parseLong("ptLNVDWzHYKATdbyokexnXUcÑuqFIi");
	public static long SEED_8 = Long.parseLong("ՇԾձըգՂկաոՏՈԵցՕՍԷԻԺֆՋքդՓԳլմյՆօծ");
	public static long SEED_9 = Long.parseLong("哦伊艾艾尺贼德吾艾艾克斯维开艾勒比吉豆贝尔维伊吾艾娜艾丝杰提吉吾诶艾儿艾马西屁艾弗迪");
	public static long SEED_10 = Long.parseLong("yZAJHhLSEoîrICcâPnDœsMRkVvYdmz");

	public static long[] SEED_ARRAY = {SEED_1, SEED_2, SEED_3, SEED_4, SEED_5, SEED_6, SEED_7, SEED_8, SEED_9, SEED_10};

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		// TODO before submission:
		// Create a Heuristic object with the chosen features and also input the chosen weights

		// e.g. for old feature set:
		// return pickMove(s, legalMoves, new double[]{-10.0, 3.1866102280509017, -10.0, 
		// 	-2.6166250910102162, 10.0, 9.079192461274538, -10.0, -10.0, -3.5319369732970207, 
		// 	-1.3557653061908144, -1.2930516421214744, 8.937515278308917, -10.0, 9.695111692980946, 
		// 	-10.0, -8.04781332965197, -10.0, -10.0, -10.0, -6.576121188396466, -9.577979914066237, 
		// 	-10.0}, new Heuristic(true, false));

		// e.g. for new feature set:
		// return pickMove(s, legalMoves, new double[]{5.549082691824574, 4.2301207149571525, 
		// 	-10.0, -4.8941246823863676, -10.0, -10.0}, new Heuristic(false, true));
		
    return pickMove(s, legalMoves, null, null);
	}

	public int pickMove(State s, int[][] legalMoves, double[] weights) {
		//Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(s, legalMoves, weights, null);

		return best;
	}

	public int pickMove(State s, int[][] legalMoves, double[] weights, Heuristic h) {
		//Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(s, legalMoves, weights, h);

		return best;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param s (current state), legalMoves (possible moves for current piece)
	* @return move with best (highest) heuristic score
	**/
	private int tryPossibleMoves(State s, int[][] legalMoves, double[] weights, Heuristic h) {

		// By default use new set of features
		if (h == null) {
			h = new Heuristic(false, true);
		}

		//legalMoves = all possible moves of the current piece.
		//Step 1. Apply each move (action) to get the change field (board configuration).
		//Step 2. Apply Heuristic.evaluate(s) to get the heuristic score from the move.
		//Step 3. Get the best move.
		double bestScore = Double.NEGATIVE_INFINITY;
		int bestMove = 0;

		for(int i=0; i<legalMoves.length; i++) {
			TempState ts = new TempState(s);
			int prevCleared = s.getRowsCleared();
			//Step 1
			int orient = legalMoves[i][0];
			int slot = legalMoves[i][1];
			boolean possible = ts.makeMove(orient, slot);
			if(!possible) {
				continue;
			} 
			//Step 2
            ts.setPrevCleared(prevCleared);
			ts.setOrientAndSlot(orient, slot);
			Evaluator e = new Evaluator(h, ts, weights);
			double score = e.evaluate(); 

			//Step 3
			if(score > bestScore) {
				bestScore = score;
				bestMove = i;
			}
		}
		return bestMove;
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		State s = new State();
		//new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		double[] weightSet = {2.7895984211534275, -13.37688135664955, -1.9816705250880007, -3.2057093957962914, -6.870156949785474, -3.901091580878856};
		ExecutorService executor = Executors.newFixedThreadPool(10); //Threadpool size = ?
		for (int i = 0; i < 10; i++) {
			ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
			Simulation game = new Simulation(weightSet, SEED_ARRAY[i], i);
			Future<Integer> future = executor.submit(game); //Add Thread to be executed by thread pool
			results.add(future);
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
/*	public static void main(String[] args) {
		//Genetic gen = new Genetic();
		Genetic gen = new Genetic();
		//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}*/
	// public static void main(String[] args) {
	// 	System.out.println("Started");
	// 	GeneticAlgo gen = new GeneticAlgo();
	// 	//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	// 	System.out.println("Done");
	// }

}
