import java.util.Arrays;

public class Heuristic {
	public static final int TOTAL_NUM_FEATURES = 9;

	// Indices for each feature to keep track of which feature to use
	public static final int INDEX_ROWS_CLEARED = 0;
	public static final int INDEX_COL_HEIGHT = 1;
	public static final int INDEX_COL_HEIGHT_DIFF = 2;
	public static final int INDEX_MAX_COL_HEIGHT = 3;
	public static final int INDEX_NUM_HOLES = 4;
	public static final int INDEX_LANDING_HEIGHT = 5;
	public static final int INDEX_ROW_TRANSITIONS = 6;
	public static final int INDEX_COL_TRANSITIONS = 7;
	public static final int INDEX_WELL_SUMS = 8;
	public static final int[] INDEX_OLD_FEATURES = new int[]{INDEX_ROWS_CLEARED, INDEX_COL_HEIGHT, INDEX_COL_HEIGHT_DIFF,
		INDEX_MAX_COL_HEIGHT, INDEX_NUM_HOLES};
	public static final int[] INDEX_NEW_FEATURES = new int[]{INDEX_ROWS_CLEARED, INDEX_NUM_HOLES, INDEX_LANDING_HEIGHT, 
		INDEX_ROW_TRANSITIONS, INDEX_COL_TRANSITIONS, INDEX_WELL_SUMS};

	// Flags for features
	private char[] featureFlags;
	private int numFeatures;


    public Heuristic(boolean useOldFeatures, boolean useNewFeatures) {
        this.featureFlags = new char[TOTAL_NUM_FEATURES];
        setNumFeatures(useOldFeatures, useNewFeatures);
    }

 	public Heuristic(boolean useOldFeatures, boolean useNewFeatures, String featureBits) {
        this.featureFlags = featureBits.toCharArray();
        setNumFeatures(useOldFeatures, useNewFeatures);
    }
    

	private void setFixedFeatures(int[] indices, boolean isUse) {
		if (isUse) {
			for (int i = 0; i < indices.length; i++) {
				featureFlags[indices[i]] = '1';
			}
		}
	}

	public void setNumFeatures(boolean useOldFeatures, boolean useNewFeatures) {
		// Transfer useNewFeatures/useOldFeatures to feature bits
		setFixedFeatures(INDEX_NEW_FEATURES, useNewFeatures);
		setFixedFeatures(INDEX_OLD_FEATURES, useOldFeatures);

		numFeatures = 0;
		for (int i = 0; i < featureFlags.length; i++) {
			if (featureFlags[i] == '1') {
				if (i == INDEX_COL_HEIGHT) {
					numFeatures += 10;
				} else if (i == INDEX_COL_HEIGHT_DIFF) {
					numFeatures += 9;
				} else {
					numFeatures++;
				}
			}
		}
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public char[] getFeatureFlags() {
		return featureFlags;
	}

	public String printFeatureInfo() {
		return "Feature set used: " + Arrays.toString(featureFlags);
	}

	public static int getNumAvailableFeatures() {
		return TOTAL_NUM_FEATURES;
	}
}
