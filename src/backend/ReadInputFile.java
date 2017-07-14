package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.log4j.Logger;

/**
 * Class to read the input file and generate the locusArray.
 * 
 * This class contains methods which read the input file. For files
 * with many columns, it performs regular expression matching
 * on the row to automatically determine the column number
 * containing the genomic loci.
 * In the end a locusArray is generated which is used in the rest of the
 * tool to fetch results from the database and such.
 * @author mkumar
 * @since v1.0
 */
public class ReadInputFile {
	private static final Logger logger = Logger.getLogger(ReadInputFile.class);
	private static Locus[] locusArray;
	private String m_inputFileName;
	int nrOfCols = -1;
	int nrOfRows = -1;
	int colIndex = -1;
	LineNumberReader lnr;

	/**
	 * Constructor, takes inputFileName as input
	 * @param inputFileName filename of the user specified input file
	 */
	public ReadInputFile(String inputFileName) {
		super();
		this.m_inputFileName = inputFileName;

		findLocusInInputFile();
		createLocusArray();
	}

	/**
	 * This method locates the column number in which the genomic locus lies.
	 * 
	 * It counts the number of rows, and columns in the input file, and
	 * passes these arguments to other methods. The method also contains
	 * instructions to locate and save the column number of the column which contains
	 * the locus. It does this with a regular expression matching.
	 */
	public void findLocusInInputFile() {
		try {
			// Count nr of rows
			lnr = new LineNumberReader(new FileReader(m_inputFileName));
			lnr.skip(Long.MAX_VALUE);
			nrOfRows = lnr.getLineNumber();
			nrOfRows = nrOfRows - 1; // ignoring the first line (headers)

			// Count nr of columns
			BufferedReader br = new BufferedReader(new FileReader(
					m_inputFileName));
			br.readLine();
			String line = br.readLine(); // skip the first line (headers)
			nrOfCols = line.split("\t").length;

			// get the index of the column which contains the locus information
			String[] column = line.split("\t");
			for (int i = 0; i < nrOfCols; i++) {
				
				column[i] = column[i].replaceAll(" ", ""); // remove extra
															// whitespaces
				if (column[i].matches("chr[0-9XY][0-9]?:[0-9]+-[0-9]+")) {
					colIndex = i;
					break;
				}
				
				// in case locus is not found in the entire row
				if (i == nrOfCols - 1) {
					logger.debug("Locus not found in input file");
					break;
				}
			}

			lnr.close();
			br.close();
		} catch (final Exception ioexcep) {
			logger
					.debug("unable to read input file - (Counting the nr of rows and columns)");
		}
	}

	/**
	 * This method gets parameters like number of rows, number of columns in the
	 * input file, and the index of the column which contains the locus, and
	 * then creates an array containing Locus entries for the input file.
	 */
	public void createLocusArray() {
		// locus is of the format --> chr3:5000-45000
		Locus chrLocus;
		locusArray = new Locus[nrOfRows];
	
		try {
			String line = null;
			BufferedReader br = new BufferedReader(new FileReader(
					m_inputFileName));
			int j = 0;
			br.readLine(); // first line is headers --ignore
	
			while ((line = br.readLine()) != null) {
				String[] column = line.split("\t");
				String locus = column[colIndex];
				locus = locus.replaceAll(" ", ""); // remove extra white spaces
				String chrNr = locus.split(":")[0];
				int startPos = Integer
						.parseInt(locus.split(":")[1].split("-")[0]);
				int endPos = Integer
						.parseInt(locus.split(":")[1].split("-")[1]);

				chrLocus = new Locus(chrNr, startPos, endPos);
				locusArray[j] = chrLocus;
				j++;
			}
			br.close();
		} catch (final IOException ioexcep) {
			logger
					.debug("unable to read from input file - (inputFileTo2DArray)");
			ioexcep.printStackTrace();
		}
	}

	/**
	 * Returns the number of rows in the input file
	 * @return nrOfRows number of rows in the input file
	 */
	public int getNrOfRows() {
		return nrOfRows;
	}

	/**
	 * Returns the index of the column which contains the genomic locus.
	 * @return colIndex an index of column containing the loci.
	 */
	public int getColIndex() {
		return colIndex;
	}

	/**
	 * Returns the number of columns in the input file
	 * @return nrOfCols number of columns in the input file
	 */
	public int getNrOfCols() {
		return nrOfCols;
	}

	/**
	 * Returns the array of locus entries.
	 * @return locusArray array of locus entries.
	 */
	public Locus[] getLocusArray() {
		return locusArray;
	}
}