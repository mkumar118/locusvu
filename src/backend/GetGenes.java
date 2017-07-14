package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * This class contains methods that submit sql queries to the database and fetch
 * gene information for each locus. The table in the UCSC database that is queried
 * is 'refGene'. The 'name2' field in the 'refGene' table contains the information
 * that we need.
 * @author mkumar
 *@since v1.0
 */
public class GetGenes {
	private static final Logger logger = Logger.getLogger(GetGenes.class);
	private Locus[] m_locusArray;
	private Statement m_statement;
	private String[] m_geneArray;
	// don't forget to initialize arrays and maps!
	private String dbTableName = "refGene";
	private String m_strName2 = "name2";

	public GetGenes(Locus[] locusArray, Statement st) {
		super();
		this.m_locusArray = locusArray;
		this.m_statement = st;

		this.fetchGenes();
	}

	/**
	 * This method contains instructions to fetch gene data from the
	 * database for each locus, using the mysql interface.
	 */
	public void fetchGenes() {
		// initialize arrays
		logger.debug("inside fetch refGene");
		m_geneArray = new String[m_locusArray.length];
		ResultSet rs = null;

		String chr;
		int startPos;
		int endPos;

		for (int i = 0; i < m_locusArray.length; i++) {
			// make sure initial value is zero
			chr = m_locusArray[i].getChr();
			startPos = m_locusArray[i].getStartPos();
			endPos = m_locusArray[i].getEndPos();

			// sql query
			String queryRefGene = "SELECT " + m_strName2 + " FROM "
					+ dbTableName + " WHERE chrom = \'" + chr
					+ "\' AND txStart < " + startPos + " AND txEnd > " + endPos;

			try {
				// if locus lies in some known gene
				rs = m_statement.executeQuery(queryRefGene);
				// if some result exists
				if (rs.next()) {
					m_geneArray[i] = rs.getString(1);
				} else {
					m_geneArray[i] = GlobalParameters.STR_EMPTY_VALUE;
				}
			} catch (final SQLException ex) {
				logger.debug("cannot execute refGene query");
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException ex) {
					logger.debug("cannot close resultset for refGene query");
				}
			}
		}

		logger
				.info("refGene queries successfully submitted and data retrieved");
	}

	/**
	 * Returns the gene array after fetching all information from the database.
	 * @return geneArray array containing gene data
	 */
	public String[] getGeneArray() {
		return m_geneArray;
	}
}
