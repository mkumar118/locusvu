package backend;

import gui.GUI;

import java.sql.Connection;
import java.sql.Statement;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * This class manages all the fetching of data from databases, and updating
 * progress bar.
 * 
 * This class handles all tasks that run in the background after the user has
 * specified the input file. It must implement "Runnable" since this is executed
 * in a new thread.
 * 
 * @author mkumar
 * @since v1.0
 */
public class TaskManager implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskManager.class);
	private GUI m_gui;

	public TaskManager(GUI gui) {
		super();
		this.m_gui = gui;
	}

	/**
	 * This method overrides the run method for the Runnable interface. It
	 * handles all tasks that need to be done, including calling all classes
	 * responsible for fetching data from the database, and finally transfers
	 * control back to the GUI with all arrays.
	 */
	@Override
	public void run() {
		logger.debug("in TaskManager (new thread): runs in the background");
		updateProgressBar(0);

		locusArray = new ReadInputFile(m_gui.getInputFileName())
				.getLocusArray();

		updateProgressBar(10);

		st = new Database().establishConnection(m_gui, con);
		updateProgressBar(35);

		if (GlobalParameters.FIND_CYTOBAND) {
			cytoBandArray = new GetCytoBand(locusArray, st).fetchCytoBand();
		}
		updateProgressBar(55);

		if (GlobalParameters.FIND_GENE) {
			gene = new GetGenes(locusArray, st);
			geneArray = gene.getGeneArray();
		}
		updateProgressBar(75);
		
		if (GlobalParameters.FIND_REPEATS) {
			GetRepeats repInfo = new GetRepeats(locusArray, st);
			repeatsNameArray = repInfo.getRepNameArray();
			repeatsClassArray = repInfo.getRepClassArray();
			repeatsFamilyArray = repInfo.getRepFamilyArray();
		}
		updateProgressBar(90);

		if (GlobalParameters.FIND_OMIM)
			omimArray = new GetOmim(geneArray).getOmimArray();
		updateProgressBar(100);

		m_gui.displayResults(locusArray, cytoBandArray, geneArray,
				repeatsNameArray, repeatsClassArray, repeatsFamilyArray,
				omimArray, st);

	}

	/**
	 * Method to update value of progress bar on runtime.
	 * 
	 * @param value
	 *            percentage of task complete.
	 */
	private void updateProgressBar(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				m_gui.progressBar.setValue(value);
			}
		});
	}

	// Variables declaration
	private Connection con = null;
	private Statement st;
	private GetGenes gene;
	private Locus[] locusArray;
	private String[] cytoBandArray = null;
	private String[] geneArray = null;
	private String[] repeatsNameArray = null;
	private String[] repeatsClassArray = null;
	private String[] repeatsFamilyArray = null;
	private String[] omimArray = null;
	// End of variables declaration
}
