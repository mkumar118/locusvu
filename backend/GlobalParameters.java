package backend;


/**
 * 
 * This class contains all the global variables. It is a one
 * stop to change variables in the entire tool.
 * @author mkumar
 * @since v1.0
 */
public class GlobalParameters {

	// string names for all fields in results table
	final public static String STR_SR_NR = "Sr. Nr.";
	final public static String STR_LOCUS = "Locus";
	final public static String STR_SIZE = "Size";
	final public static String STR_CYTOBAND = "Cytogenetic band";
	final public static String STR_GENE = "Gene";
	final public static String STR_REPEATS = "Repeats information (name, class, family)";
	final public static String STR_REPEATS_NAME = "Repeats Name";
	final public static String STR_REPEATS_CLASS = "Repeats Class";
	final public static String STR_REPEATS_FAMILY = "Repeats Family";
	final public static String STR_OMIM = "OMIM";
	
	// booleans for format of output file
	public static boolean IS_OUTPUT_TXT = false;
	public static boolean IS_OUTPUT_XLS = false;
	
	// booleans to determine selected assembly
	public static boolean IS_HG18_ON_UCSC = true;
	public static boolean IS_HG19_ON_UCSC = false;

	// booleans for which tracks to query
	public static boolean FIND_CYTOBAND = false;
	public static boolean FIND_GENE = false;
	public static boolean FIND_REPEATS = false;
	public static boolean FIND_OMIM = false;

	// global integers for the results table
	final public static int MAX_NR_OF_COLUMNS = 9;
	final public static int NR_OF_COLUMNS_TO_IGNORE = 3;

	// booleans for the View menu after results are displayed
	public static boolean SHOW_SIZE = false;
	public static boolean SHOW_CYTOBAND = false;
	public static boolean SHOW_GENE = false;
	public static boolean SHOW_REPEATS_NAME = false;
	public static boolean SHOW_REPEATS_CLASS = false;
	public static boolean SHOW_REPEATS_FAMILY = false;
	public static boolean SHOW_OMIM = false;

	// integers for Neighboring genes class
	public static int MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY = 5;
	public static int MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES = 10000000;

	// assign index to each track, used in making results table dynamic
	final public static int CYTOBAND_INDEX = 0;
	final public static int GENE_INDEX = 1;
	final public static int REPEATS_NAME_INDEX = 2;
	final public static int REPEATS_CLASS_INDEX = 3;
	final public static int REPEATS_FAMILY_INDEX = 4;
	final public static int OMIM_INDEX = 5;

	// default paths and filenames
	final public static String PATH_DEFAULT_OUTPUT_DIR = "/home/mkumar/Desktop";
	final public static String DEFAULT_OUTPUT_FILENAME = "output";
	final public static String DEFAULT_OUTPUT_CHARTNAME = "chart.png";
	

	// path to files in /etc directory
	final public static String PATH_OMIM_ACC_NR_MAP = ".//etc//omimAccessionNumMap.data";
	final public static String PATH_TO_TICK = ".//etc//tick.png";

	// index of which tab is active in the tabbed pane, results table
	public static int TABBED_PANE_ACTIVE_TAB_INDEX = 0;
	
	// overlap threshold for merging overlapping loci, compare datasets
	public static int THRESHOLD_MERGE_OVERLAPPING_LOCI = 250;
	
	// string to denote empty value
	final public static String STR_EMPTY_VALUE = "--";
}
