package backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * This class contains methods that perform all calculations required to draw
 * plots on the data.
 * 
 * @author mkumar
 * @since v1.0
 */
public class Statistics {
	private static final Logger logger = Logger.getLogger(Statistics.class);
	private Map<String, Integer> chrMapForBarChart;
	private Map<String, Integer> chrMapForPieChart;

	public Statistics() {
		super();
		logger.info("inside statistics");
	}

	/**
	 * Creates an array containing sizes of loci.
	 * 
	 * @param locusArray
	 *            array of loci.
	 * @return sizeArray array containing sizes of loci.
	 */
	public double[] getSizeDist(Locus[] locusArray) {
		double[] sizeArray = new double[locusArray.length];
		for (int j = 0; j < locusArray.length; j++) {
			sizeArray[j] = locusArray[j].size();
		}

		return sizeArray;
	}

	/**
	 * This method counts the number of times each chromosome occurs in the
	 * given dataset, for a bar chart distribution.
	 * 
	 * For single digit chromosome numbers, it adds a zero in front of the
	 * single digit so that a sorted map is generated. The map it generates
	 * contains a mapping of chrNr : frequency-of-occurrence
	 * 
	 * @param locusArray
	 *            array of loci.
	 * @return map a sorted map with mapping chrNr : frequency
	 */
	public Map<String, Integer> getChrDistForBarChart(Locus[] locusArray) {
		chrMapForBarChart = new HashMap<String, Integer>();
		// create a map, and create 25 keys all with zero values
		for (int i = 1; i < 23; i++) {
			if (i < 10) {
				chrMapForBarChart.put("0" + Integer.toString(i), 0);
			} else {
				chrMapForBarChart.put(Integer.toString(i), 0);
			}
		}
		chrMapForBarChart.put("X", 0);
		chrMapForBarChart.put("Y", 0);
		chrMapForBarChart.put("?", 0); //unknown chr

		/**
		 * now count how many times each chr appears in the dataset and update
		 * its value in the map
		 */
		// for each locus
		for (Locus locus : locusArray) {
			// remove the letters "chr" from in front of the chromosome
			String chrNr = locus.getChr().substring(3);
			// if its X or Y chromosome
			if(chrNr.matches("X|Y")){
				// do nothing, ignore (dont delete this case!)
			}
			// else if chr is a number, and is single digit
			else if (chrNr.matches("[0-9]")) {
				// add zero in front, if chrNr is of single digit
				chrNr = "0" + chrNr;
			// 	else if it is a number, and is two digit
			} else if (chrNr.matches("[0-9][0-9]")){
				// do nothing
			// for all other cases	
			} else{
				// assign it the "?" string
				chrNr = "?";
			}

			// add to map
			if (chrMapForBarChart.containsKey(chrNr)) {
				int freq = chrMapForBarChart.get(chrNr);
				freq = freq + 1;
				chrMapForBarChart.put(chrNr, freq);
			}
		}
		return sortMap(chrMapForBarChart);
	}

	/**
	 * This method counts the number of times each chromosome occurs in the
	 * given dataset, for a pie chart distribution.
	 * 
	 * @param locusArray
	 *            array of loci.
	 * @return map with mapping chromosomeNr : frequency-of-occurrence.
	 */
	public Map<String, Integer> getChrDistForPieChart(Locus[] locusArray) {
		chrMapForPieChart = new HashMap<String, Integer>();

		for (Locus locus : locusArray) {
			String chr = locus.getChr();
			//String chrNr = locus.getChr().substring(3);
			if (chrMapForPieChart.containsKey(chr)) {
				int freq = chrMapForPieChart.get(chr);
				freq = freq + 1;
				chrMapForPieChart.put(chr, freq);
			} else {
				chrMapForPieChart.put(chr, 1);
			}
		}

		return chrMapForPieChart;
	}

	/**
	 * This method counts how many loci lie within a known gene, and how many do
	 * not.
	 * 
	 * @param geneArray
	 *            array of gene data.
	 * @return geneMap mapping for gene distribution. ((yes/no : frequency))
	 */
	public Map<String, Integer> getGeneDist(String[] geneArray) {
		Map<String, Integer> geneMap = new HashMap<String, Integer>();
		String m_within = "Within Gene";
		String m_outside = "Outside Gene";
		geneMap.put(m_within, 0);
		geneMap.put(m_outside, 0);
		for (String gene_entry : geneArray) {
			if (gene_entry != GlobalParameters.STR_EMPTY_VALUE) {
				int freq = geneMap.get(m_within);
				freq = freq + 1;
				geneMap.put(m_within, freq);
			} else {
				int notFreq = geneMap.get(m_outside);
				notFreq = notFreq + 1;
				geneMap.put(m_outside, notFreq);
			}
		}
		return geneMap;
	}

	/**
	 * This method counts how many loci have an omim accession number related to
	 * them, i.e. for how many loci do we know some disease information already.
	 * 
	 * @param omimArray
	 *            array of omim data.
	 * @return omimMap mapping of omim distribution (yes/no : frequency)
	 */
	public Map<String, Integer> getOmimDist(String[] omimArray) {
		Map<String, Integer> omimMap = new HashMap<String, Integer>();
		String m_yes = "Are";
		String m_no = "Are-not";
		omimMap.put(m_yes, 0);
		omimMap.put(m_no, 0);
		for (String omim_entry : omimArray) {
			if (omim_entry != GlobalParameters.STR_EMPTY_VALUE) {
				int freq = omimMap.get(m_yes);
				freq = freq + 1;
				omimMap.put(m_yes, freq);
			} else {
				int notFreq = omimMap.get(m_no);
				notFreq = notFreq + 1;
				omimMap.put(m_no, notFreq);
			}
		}
		return omimMap;
	}

	/**
	 * Generates a dataset object from JfreeChart for size distribution chart.
	 * 
	 * @param sizeArray
	 *            array of sizes of loci.
	 * @return dataset for size distribution histogram.
	 */
	public IntervalXYDataset getHistogramDatasetForSizeDist(double[] sizeArray) {
		HistogramDataset histogramDatasetForSizeDist = new HistogramDataset();
		histogramDatasetForSizeDist.setType(HistogramType.FREQUENCY);
		histogramDatasetForSizeDist.addSeries("Size", sizeArray, 25); // 25 is
																		// number
																		// of
																		// bars

		return histogramDatasetForSizeDist;
	}

	/**
	 * Generates a dataset from jFreeChart for a bar chart of chromosome
	 * distribution.
	 * 
	 * @param chrMapWithZeroValues
	 *            map with mapping chrNr : frequency-of-occurrence
	 * @return dataset for chromosome distribution, bar chart.
	 */
	public DefaultCategoryDataset getBarDatasetForChrDist(
			Map<String, Integer> chrMapWithZeroValues) {
		DefaultCategoryDataset barDatasetForChrDist = new DefaultCategoryDataset();

		for (String name : chrMapWithZeroValues.keySet()) {
			barDatasetForChrDist.setValue(chrMapWithZeroValues.get(name),
					"Frequency", name);
		}
		return barDatasetForChrDist;
	}

	/**
	 * Generates a dataset from jFreeChart for pie chart of chromosome
	 * distribution.
	 * 
	 * @param chrMapWithoutZeroValues
	 *            map with mapping chrNr : frequency-of-occurrence
	 * @return dataset for chromosome distribution, pie chart.
	 */
	public PieDataset getPieDatasetForChrDist(
			Map<String, Integer> chrMapWithoutZeroValues) {
		DefaultPieDataset pieDatasetForChrDist = new DefaultPieDataset();
		for (String key_in_chrMap : chrMapWithoutZeroValues.keySet()) {
			pieDatasetForChrDist.setValue(key_in_chrMap,
					chrMapWithoutZeroValues.get(key_in_chrMap));
		}
		return pieDatasetForChrDist;
	}

	/**
	 * Generates a dataset from jFreeChart for pie chart of gene distribution.
	 * 
	 * The map is a boolean mapping of how many loci lie within a known gene,
	 * and how many are outside.
	 * 
	 * @param geneMap
	 *            map of within-gene (yes/no) : frequency
	 * @return dataset for genes distribution, pie chart.
	 */
	public PieDataset getPieDatasetForGeneDist(Map<String, Integer> geneMap) {
		DefaultPieDataset pieDatasetForGeneDist = new DefaultPieDataset();
		for (String key_in_geneMap : geneMap.keySet()) {
			pieDatasetForGeneDist.setValue(key_in_geneMap,
					geneMap.get(key_in_geneMap));
		}
		return pieDatasetForGeneDist;
	}

	/**
	 * This method generates a PieDataset object for omim data.
	 * 
	 * @param omimMap
	 *            a map with mapping of with-known-omim-relation (yes/no) :
	 *            frequency
	 * @return pieDatasetForOmim pie dataset for omim data
	 */
	public PieDataset getPieDatasetForOmim(Map<String, Integer> omimMap) {
		DefaultPieDataset pieDatasetForOmim = new DefaultPieDataset();
		for (String key_in_geneMap : omimMap.keySet()) {
			pieDatasetForOmim.setValue(key_in_geneMap,
					omimMap.get(key_in_geneMap));
		}
		return pieDatasetForOmim;
	}

	/**
	 * Sorts the map by key, given an unsorted map.
	 * 
	 * @param unsortedMap
	 * @return sortedMap sorted map
	 */
	private Map<String, Integer> sortMap(Map<String, Integer> unsortedMap) {
		Map<String, Integer> sortedMap = new TreeMap<String, Integer>();

		TreeSet<String> keys = new TreeSet<String>(unsortedMap.keySet());
		for (String key : keys) {
			int value = unsortedMap.get(key);
			sortedMap.put(key, value);
		}

		return sortedMap;
	}
}
