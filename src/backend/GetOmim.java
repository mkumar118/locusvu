package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * This class fetches OMIM data and creates an array (omimArray) to be later
 * displayed in the results table. Each entry is an OMIM accession number, which
 * is related to the gene (and thus the locus). The accession numbers are
 * obtained by querying the locus against serialized objects, themselves
 * generated from ftp downloaded files from omim.org.
 * <p>
 * omimAccessionNumMap is a hashmap which contains mapping from gene name : omim
 * accession number. This map is generated from ftp downloaded files from omim,
 * and regularly updated.
 * </p>
 * 
 * @author mkumar
 * @since v1.0
 * 
 */
public class GetOmim implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GetOmim.class);
	private String[] m_geneArray;
	private String[] m_omimArray;
	private HashMap<String, String> m_omimAccessionNumMap = new HashMap<String, String>();

	/**
	 * Constructor, takes geneArray as input
	 * 
	 * @param geneArray
	 */
	public GetOmim(String[] geneArray) {
		super();
		this.m_geneArray = geneArray;
		try {
			this.m_omimArray = new String[m_geneArray.length];
		} catch (NullPointerException npe) {
			logger.info("could not fetch omim data. It depends on gene names." +
					" Please make sure you fetched Genes info before fetching OMIM data");
		}

		this.m_omimAccessionNumMap = getAccessionNumMap();
		this.findOmimAccNums();
	}

	/**
	 * Method to unserialize hashmap's serialized object
	 * 
	 * @return omimAccessionNumMap a hashmap which contains mapping of gene name
	 *         : omim accession numbers
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> getAccessionNumMap() {
		HashMap<String, String> accNumMap = new HashMap<String, String>();
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(GlobalParameters.PATH_OMIM_ACC_NR_MAP);
		} catch(FileNotFoundException fnfex){
			logger.fatal("OMIM data file not found. If you are using the tool for the first time, " +
					"please read the User Manual on how to obtain this file or please choose" +
					" to NOT fetch OMIM data from the Settings panel");
		}
		try {
			ObjectInputStream in_accNumMap = new ObjectInputStream(fin);
			Object obj = in_accNumMap.readObject();
			if (obj instanceof HashMap<?, ?>) {
				accNumMap = (HashMap<String, String>) obj;
			} else {
				logger.fatal("Cannot read OMIM data from serialized object - object is not of type hashmap");
			}

		} catch (IOException ioe) {
			logger.debug("cannot read serialized omim accession numbers object");
		} catch (ClassNotFoundException cnfe) {
			logger.debug("cannot read serialized omim accession numbers object");
		}

		return accNumMap;
	}

	/**
	 * Method that checks whether a given gene name is contained in the map or
	 * not, and if yes, returns the accession number
	 */
	public void findOmimAccNums() {

		// check whether gene name is contained in map or not
		// if yes, add to omimArray; else, not
		for (int i = 0; i < m_geneArray.length; i++) {
			if (m_omimAccessionNumMap.containsKey(m_geneArray[i])) {
				m_omimArray[i] = m_omimAccessionNumMap.get(m_geneArray[i]);
			} else {
				m_omimArray[i] = GlobalParameters.STR_EMPTY_VALUE;
			}
		}
	}

	/**
	 * Get omimArray, an array of omim accession numbers
	 * 
	 * @return omimArray an array of omim accession numbers, for respective
	 *         gene/loci
	 */
	public String[] getOmimArray() {
		return m_omimArray;
	}
}
