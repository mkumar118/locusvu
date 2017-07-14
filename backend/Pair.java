package backend;

/**
 * A class of the generic type Pair, where a pair denotes a combination
 * of start coordinate and end coordinate.
 * @author mkumar
 * @since v1.0
 */
public class Pair {
	int sp;
	int ep;
	public boolean[] boolArr;

	/**
	 * Constructor for pair class
	 * @param startPos start coordinate of the locus
	 * @param endPos end coordinate of the locus
	 */
	public Pair(int startPos, int endPos) {
		this.sp = startPos;
		this.ep = endPos;
	}

	/**
	 * Returns the start position for a locus
	 * @return startPos start position
	 */
	public int getStartPos() {
		return sp;
	}

	/**
	 * Returns the end position for the given locus
	 * @return endPos end position
	 */
	public int getEndPos() {
		return ep;
	}
	
	/**
	 * Returns the number of 'true's in the boolean array
	 * @return count of 'true' in the boolean array
	 */
	public int getCountOfTrueValuesInBoolArr(){
		int countTrue = 0;
		for(boolean value : boolArr){
			if (value)
				countTrue++;
		}
		return countTrue;
	}

	/**
	 * Returns the locus in string format
	 * @return pair in string format, separated by a '-'
	 */
	public String toString() {
		return Integer.toString(sp) + "-" + Integer.toString(ep);
	}
}
