package backend;

import org.apache.log4j.Logger;

/**
 * This class opens a link in the browser.
 * 
 * It is called when a user selects to view a locus online.
 * 
 * @author mkumar
 * @since v1.0
 */
public class OpenBrowser {
	private static final Logger logger = Logger.getLogger(OpenBrowser.class);
	private Locus[] m_locusArray;
	private int m_selectedRowIndex;
	private String m_chr;
	private int m_startPos;
	private int m_endPos;

	public OpenBrowser(Locus[] locusArray, int selectedRowIndex) {
		super();
		m_locusArray = locusArray;
		m_selectedRowIndex = selectedRowIndex;

		m_chr = m_locusArray[m_selectedRowIndex].getChr();
		m_startPos = m_locusArray[m_selectedRowIndex].getStartPos();
		m_endPos = m_locusArray[m_selectedRowIndex].getEndPos();
	}

	/**
	 * method to open the selected genomic loci in the UCSC Genome Browser window.
	 */
	public void goToUCSC() {

		if (!java.awt.Desktop.isDesktopSupported()) {
			logger.fatal("Desktop is not supported (fatal)");
			System.err.println("Desktop is not supported (fatal)");
			System.exit(1);
		}

		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			logger.fatal("Desktop doesn't support the browse action (fatal)");
			System.err
					.println("Desktop doesn't support the browse action (fatal)");
			System.exit(1);
		}
		
		String assembly;
		if(GlobalParameters.IS_HG18_ON_UCSC){
			assembly = "hg18";
		} else {
			assembly = "hg19";
		}

		try {
			desktop.browse(new java.net.URI(
					"http://genome.ucsc.edu/cgi-bin/hgTracks?db="+assembly+"&position="
							+ m_chr + ":" + m_startPos + "-" + m_endPos));
		} catch (Exception e) {
			logger.debug(e.getMessage());
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * method to open the selected gene in the Genecards.org resource online.
	 * @param geneArray array of gene name entries
	 */
	public void goToGenecards(String[] geneArray) {

		if (!java.awt.Desktop.isDesktopSupported()) {
			logger.fatal("Desktop is not supported (fatal)");
			System.err.println("Desktop is not supported (fatal)");
			System.exit(1);
		}

		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			logger.fatal("Desktop doesn't support the browse action (fatal)");
			System.err
					.println("Desktop doesn't support the browse action (fatal)");
			System.exit(1);
		}

		try {
			desktop.browse(new java.net.URI("http://genecards.org/cgi-bin/carddisp.pl?gene="+geneArray[m_selectedRowIndex]));
		} catch (Exception e) {
			logger.debug(e.getMessage());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * method to open the selected omim accession number in the OMIM database window.
	 * @param omimArray an array of omim accession numbers, respective for each loci.
	 */
	public void goToOMIM(String[] omimArray) {
		if (!java.awt.Desktop.isDesktopSupported()) {
			logger.fatal("Desktop is not supported (fatal)");
			System.err.println("Desktop is not supported (fatal)");
			System.exit(1);
		}

		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			logger.fatal("Desktop doesn't support the browse action (fatal)");
			System.err
					.println("Desktop doesn't support the browse action (fatal)");
			System.exit(1);
		}

		try {
			desktop.browse(new java.net.URI("http://omim.org/entry/"
					+ omimArray[m_selectedRowIndex] + "?search="
					+ omimArray[m_selectedRowIndex] + "&highlight="
					+ omimArray[m_selectedRowIndex]));
		} catch (Exception e) {
			logger.debug(e.getMessage());
			System.err.println(e.getMessage());
		}
	}
}