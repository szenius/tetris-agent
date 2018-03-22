public class RunPso {
	public static void main(String[] args) {
		int numRuns = Integer.parseInt(args[0]);
		for (int i = 0; i < numRuns; i++) {
			PSO.main(new String[0]);
		}
	}
}