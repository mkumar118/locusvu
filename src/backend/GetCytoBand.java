package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * This  class contains methods to fetch cytoband data
 * from the database.
 * <p>All the variables that begin with an 'm_' denote class-specific variables.</p>
 * 
 * @author mkumar
 * @since v1.0
 */
public class GetCytoBand {
	private static final Logger logger = Logger.getLogger(GetCytoBand.class);
	private Locus[] m_locusArray;
	private Statement m_st;
	private String[] m_cytoBandArray;
	private String dbTableName = "cytoBand";
	private String target = "name";
	private String entry1 = "";
	private String entry2 = "";

	/**
	 * Constructor - assigns value to class-specific variables from global ones. 
	 * @param locusArray array of loci, from input list.
	 * @param st statement object, from database connection.
	 */
	public GetCytoBand(Locus[] locusArray, Statement st) {
		this.m_locusArray = locusArray;
		this.m_st = st;
	}

	/**
	 * Method to fetch cytoband data from database.
	 * 
	 * @return cytoBandArray array of cytoband data
	 */
	public String[] fetchCytoBand() {
		logger.debug("inside fetch cytoband");
		m_cytoBandArray = new String[m_locusArray.length];
		ResultSet rs = null;

		String chr;
		int startPos;
		int endPos;

		for (int i = 0; i < m_locusArray.length; i++) {
			chr = m_locusArray[i].getChr();
			startPos = m_locusArray[i].getStartPos();
			endPos = m_locusArray[i].getEndPos();

			// sql query
			String queryCytoBand = "SELECT " + target + " FROM " + dbTableName
					+ " WHERE chrom = \'" + chr + "\' AND chromStart < "
					+ startPos + " AND chromEnd > " + endPos;

			try {
				// submit query to the database
				rs = m_st.executeQuery(queryCytoBand);
				// if results exist
				if (rs.next()) {
					m_cytoBandArray[i] = chr.substring(3).concat(
							rs.getString(1)); // remove 'chr' from chr name
												// (only chr number remains then)
												// and concatenate to resultset

				} else {
					// also look separately for start and end positions.
					// for use cases where locus spans more than one cytobands.
					String startPosSeparateQueryCytoBand = "SELECT " + target
							+ " FROM " + dbTableName + " WHERE chrom = \'"
							+ chr + "\' AND chromStart < " + startPos
							+ " AND chromEnd > " + Integer.toString(startPos + 1);

					String endPosSeparateQueryCytoBand = "SELECT " + target
							+ " FROM " + dbTableName + " WHERE chrom = \'"
							+ chr + "\' AND chromStart < " + Integer.toString(endPos - 1)
							+ " AND chromEnd > " + endPos;
					
					try {
						rs = m_st.executeQuery(startPosSeparateQueryCytoBand);
						if (rs.next()) {
							entry1 = rs.getString(1);
						} else
							entry1 = "";
						rs = m_st.executeQuery(endPosSeparateQueryCytoBand);
						if (rs.next()) {
							entry2 = rs.getString(1);
						} else
							entry2 = "";
					} catch (final SQLException ex) {
						System.out
								.println("cannot execute ELSE query for cytoband Name");
					}
					if (entry1.equals(entry2)) {
						m_cytoBandArray[i] = entry1;
					} else {
						m_cytoBandArray[i] = entry1 + "," + entry2;
					}
				}
			} catch (final SQLException ex) {
				logger.debug("cannot execute cytoband query");

			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException ex) {
					logger.debug("cannot close resultset for cytoband query");
				}
			}
		}
		logger.info("cytoBand queries successfully submitted and data retrieved");
		return m_cytoBandArray;
	}
}
