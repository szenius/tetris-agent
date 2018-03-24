public class RunPSO {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Not enough arguments.");
			System.out.println("Usage: java RunPSO <num_runs> <(optional) use_old_features>");
			System.exit(-1);
		}

		if (args.length > 1) {
			if (Boolean.parseBoolean(args[1])) {
				// use old features + combi of new features
				String bitString = "11111";
				for (int i = 1; i < Math.pow(2, Heuristic.INDEX_OLD_FEATURES.length); i++) {
					PSO.main(new String[]{"true", "false", bitString + Integer.toBinaryString(i)});
				}
			} else {
				// use new features + combi of old features
				String bitStringStart = "1";
				String bitStringEnd = "11111";
				for (int i = 1; i < Math.pow(2, Heuristic.INDEX_NEW_FEATURES.length); i++) {
					PSO.main(new String[]{"false", "true", bitStringStart + Integer.toBinaryString(i) + bitStringEnd});
				}
			}
		} else {
			for (int i = 0; i < Integer.parseInt(args[0]); i++) {
				PSO.main(new String[]{"false", "true"});
				PSO.main(new String[]{"true", "false"});
			}
		}
	}
}