package backend;

/**
 * This is a generic Locus class, designed to hold and operate
 * on the information specific to a locus. It also implements
 * comparable, to compare two loci.
 * @author mkumar
 * @since v1.0
 */
public class Locus implements Comparable<Locus> {
	private String chr;

	private int startPos;

	private int endPos;

	private Pair pair;

	/**
	 * Constructor. Takes chr, startPos and endPos as parameters.
	 * @param chr chromosome number
	 * @param startPos start coordinate of the locus
	 * @param endPos end coordinate of the locus
	 */
	public Locus(String chr, int startPos, int endPos) {
		this.chr = chr;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	/**
	 * Second constructor. Takes chr and pair as parameters.
	 * @param chr chromosome number.
	 * @param pair pair of start and end coordinates.
	 */
	public Locus(String chr, Pair pair) {
		this.chr = chr;
		this.startPos = pair.getStartPos();
		this.endPos = pair.getEndPos();
		this.pair = pair;
	}

	/**
	 * Returns the chromosome number for a locus.
	 * @return chr chromosome number
	 */
	public String getChr() {
		return this.chr;
	}

	/**
	 * Returns the start coordinate for a locus.
	 * @return startPos start coordinate for a locus
	 */
	public int getStartPos() {
		return this.startPos;
	}

	/**
	 * Returns the end coordinate for a locus.
	 * @return endPos end coordinate for a locus
	 */
	public int getEndPos() {
		return this.endPos;
	}

	/**
	 * Returns the pair object related to this locus.
	 * @return pair an object of pair class, contains both start and end coordinates
	 */
	public Pair getPair() {
		return this.pair;
	}

	/**
	 * Converts locus to string, in standard format
	 * "chrNr:startPos-endPos"
	 * @return string in standard UCSC format
	 */
	@Override
	public String toString() {
		return chr + ":" + startPos + "-" + endPos;
	}

	/**
	 * Returns size of the locus. Since the start coordinate
	 * from UCSC is 0-based, 1 is added to the size.
	 * @return size size of the locus
	 */
	public int size() {
		return endPos - startPos + 1;
	}

	/**
	 * This method compares two loci and returns true / false
	 * @param chr chromosome number
	 * @param start start coordinate
	 * @param end end coordinate
	 * @return boolean for equality comparison
	 */
	public boolean equals(String chr, int start, int end) {
		boolean test = false;
		if (this.chr.equals(chr) && this.startPos == start
				&& this.endPos == end) {

			test = true;
		}

		return test;
	}

	/**
	 * Overrides the compareTo method.
	 * 
	 * Compares one locus with another.
	 * @return boolean true / false for comparison.
	 */
	@Override
	public int compareTo(Locus otherLocus) {
		return chr.compareTo(otherLocus.getChr());
	}
}