package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * This class contains methods to fetch Repeats information from the 
 * database.
 * 
 * Repeats information is fetched from the rmsk tables, whose original
 * source is the RepeatMasker program developed by Arian Smit. We fetch here
 * three different attributes from the track: repeats name, repeats class
 * and repeats family.
 * @author mkumar
 * @since v1.0
 */
public class GetRepeats {
	private static final Logger logger = Logger.getLogger(GetRepeats.class);
	private Locus[] m_locusArray;
	private Statement m_st;
	private String[] m_repMaskNameArray;
	private String[] m_repMaskClassArray;
	private String[] m_repMaskFamilyArray;
	// don't forget to initialize arrays!
	private String dbTableName = "rmsk";

	private String chr;
	private int startPos;
	private int endPos;

	/**
	 * Constructor for class which gets repeats information from database
	 * @param locusArray
	 * @param st
	 */
	public GetRepeats(Locus[] locusArray, Statement st) {
		this.m_locusArray = locusArray;
		this.m_st = st;
		submitRepQuery();
	}

	/**
	 * Method to fetch repeats information from the track.
	 * 
	 * This method contains instructions to submit sql queries
	 * to the database, and to fetch data from the repeat masker track.
	 *
	 */
	public void submitRepQuery() {
		logger.debug("submitting query to fetch repMasker information from the database");
		ResultSet rs = null;
		m_repMaskNameArray = new String[m_locusArray.length];
		m_repMaskClassArray = new String[m_locusArray.length];
		m_repMaskFamilyArray = new String[m_locusArray.length];

		for (int i = 0; i < m_locusArray.length; i++) {
			chr = m_locusArray[i].getChr();
			startPos = m_locusArray[i].getStartPos();
			endPos = m_locusArray[i].getEndPos();

			// sql query
			String queryRepMask = "SELECT repName, repClass, repFamily FROM " + chr
					+ "_" + dbTableName + " WHERE " + startPos + " > genoStart"
					+ " AND " + endPos + " < genoEnd";

			try {
				// submit query to the database
				rs = m_st.executeQuery(queryRepMask);
				// if some result exists
				if (rs.next()) {
					m_repMaskNameArray[i] = rs.getString(1);
					m_repMaskClassArray[i] = rs.getString(2);
					m_repMaskFamilyArray[i] = rs.getString(3);
				}
				else{
					m_repMaskNameArray[i] = GlobalParameters.STR_EMPTY_VALUE;
					m_repMaskClassArray[i] = GlobalParameters.STR_EMPTY_VALUE;
					m_repMaskFamilyArray[i] = GlobalParameters.STR_EMPTY_VALUE;
				}
			} catch (final SQLException ex) {
				logger.debug("cannot execute repeats query");

			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException ex) {
					logger.debug("cannot close resultset for repeats query");
				}
			}

		}
		logger.info("repeats query successfully submitted and data retrieved");
	}
	
	/**
	 * This method returns the repeats name data array
	 * @return repeats name array
	 */
	public String[] getRepNameArray(){
		return m_repMaskNameArray;
	}
	
	/**
	 * This method returns the repeats class data array
	 * @return repeats class array
	 */
	public String[] getRepClassArray(){
		return m_repMaskClassArray;
	}
	
	/**
	 * This method returns the repeats family data array
	 * @return repeats family array
	 */
	public String[] getRepFamilyArray(){
		return m_repMaskFamilyArray;
	}
}