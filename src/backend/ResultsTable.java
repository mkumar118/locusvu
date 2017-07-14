package backend;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * This class contains methods which handle display and organization of data in
 * the results table.
 * 
 * @author mkumar
 * 
 */
public class ResultsTable {
	private static final Logger logger = Logger.getLogger(ResultsTable.class);

	public ResultsTable() {
		super();
		logger.info("inside ResultsTable class");
	}

	/**
	 * Sets the table -- invokes the method to populate the table and makes it
	 * look good / presentable.
	 * 
	 * @param locusArray
	 *            array of loci
	 * @param cytoBandArray
	 *            array of cytoband data
	 * @param geneArray
	 *            array of gene data
	 * @param repeatsNameArray
	 *            array of repeats name data
	 * @param repeatsClassArray
	 *            array of repeats class data
	 * @param repeatsFamilyArray
	 *            array of repeats family data
	 * @param omimArray
	 *            array of omim data
	 * @see backend.ResultsTable#populateTable(Locus[], String[], String[],
	 *      String[], String[], String[], String[])
	 */
	public void setTable(Locus[] locusArray, String[] cytoBandArray,
			String[] geneArray, String[] repeatsNameArray,
			String[] repeatsClassArray, String[] repeatsFamilyArray,
			String[] omimArray) {

		m_table = populateTable(locusArray, cytoBandArray, geneArray,
				repeatsNameArray, repeatsClassArray, repeatsFamilyArray,
				omimArray);

		makeTablePresentable(m_table);

	}

	/**
	 * Populates the table, i.e. fills all cells with contents from various
	 * arrays.
	 * 
	 * @param locusArray
	 *            array of loci
	 * @param cytoBandArray
	 *            array of cytoband data
	 * @param geneArray
	 *            array of gene data
	 * @param repeatsNameArray
	 *            array of repeats name data
	 * @param repeatsClassArray
	 *            array of repeats class data
	 * @param repeatsFamilyArray
	 *            array of repeats family data
	 * @param omimArray
	 *            array of omim data
	 * @return table
	 */
	private JTable populateTable(Locus[] locusArray, String[] cytoBandArray,
			String[] geneArray, String[] repeatsNameArray,
			String[] repeatsClassArray, String[] repeatsFamilyArray,
			String[] omimArray) {

		JTable table = new JTable();

		int numOfRecords = locusArray.length;

		String[] headers = getHeaders();
		int numOfColumns = headers.length;

		String[][] tableContents = getTableContents(locusArray, cytoBandArray,
				geneArray, repeatsNameArray, repeatsClassArray,
				repeatsFamilyArray, omimArray, numOfRecords, numOfColumns);

		table.setModel(new DefaultTableModel(tableContents, headers) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		listOfUneditedTables.add(table);

		return table;
	}

	/**
	 * This method creates the headers for the results table.
	 * 
	 * @return headers of the table
	 */
	private String[] getHeaders() {
		String[] headers = new String[] { GlobalParameters.STR_SR_NR,
				GlobalParameters.STR_LOCUS, GlobalParameters.STR_SIZE,
				GlobalParameters.STR_CYTOBAND, GlobalParameters.STR_GENE,
				GlobalParameters.STR_REPEATS_NAME,
				GlobalParameters.STR_REPEATS_CLASS,
				GlobalParameters.STR_REPEATS_FAMILY, GlobalParameters.STR_OMIM };

		if (!GlobalParameters.FIND_CYTOBAND)
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_CYTOBAND);
		if (!GlobalParameters.FIND_GENE)
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_GENE);
		if (!GlobalParameters.FIND_REPEATS) {
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_REPEATS_NAME);
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_REPEATS_CLASS);
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_REPEATS_FAMILY);
		}
		if (!GlobalParameters.FIND_OMIM)
			headers = ArrayUtils.removeElement(headers,
					GlobalParameters.STR_OMIM);

		listOfHeaders.add(headers);
		return headers;
	}

	/**
	 * This method gets the contents of the table, i.e. contents of each cell in
	 * the table from the different arrays.
	 * 
	 * @param locusArray
	 *            array of loci
	 * @param cytoBandArray
	 *            array of cytoband data
	 * @param geneArray
	 *            array of gene data
	 * @param repeatsNameArray
	 *            array of repeats name data
	 * @param repeatsClassArray
	 *            array of repeats class data
	 * @param repeatsFamilyArray
	 *            array of repeats family data
	 * @param omimArray
	 *            array of omim data
	 * @param numOfRows
	 *            number of rows in the table
	 * @param numOfColumns
	 *            number of columns in the table
	 * @return tableContents
	 */
	private String[][] getTableContents(Locus[] locusArray,
			String[] cytoBandArray, String[] geneArray,
			String[] repeatsNameArray, String[] repeatsClassArray,
			String[] repeatsFamilyArray, String[] omimArray, int numOfRows,
			int numOfColumns) {

		String[][] tableContents = new String[numOfRows][numOfColumns];
		ArrayList<Integer> tempArrayList = new ArrayList<Integer>();

		if (GlobalParameters.FIND_CYTOBAND)
			tempArrayList.add(GlobalParameters.CYTOBAND_INDEX);
		if (GlobalParameters.FIND_GENE)
			tempArrayList.add(GlobalParameters.GENE_INDEX);
		if (GlobalParameters.FIND_REPEATS) {
			tempArrayList.add(GlobalParameters.REPEATS_NAME_INDEX);
			tempArrayList.add(GlobalParameters.REPEATS_CLASS_INDEX);
			tempArrayList.add(GlobalParameters.REPEATS_FAMILY_INDEX);
		}
		if (GlobalParameters.FIND_OMIM)
			tempArrayList.add(GlobalParameters.OMIM_INDEX);

		Integer[] tempArray = tempArrayList.toArray(new Integer[tempArrayList
				.size()]);
		// ignore 3 columns, for sr nr, locus name, and size
		String[][] data = new String[GlobalParameters.MAX_NR_OF_COLUMNS
				- GlobalParameters.NR_OF_COLUMNS_TO_IGNORE][numOfRows];
		data[GlobalParameters.CYTOBAND_INDEX] = cytoBandArray;
		data[GlobalParameters.GENE_INDEX] = geneArray;
		data[GlobalParameters.REPEATS_NAME_INDEX] = repeatsNameArray;
		data[GlobalParameters.REPEATS_CLASS_INDEX] = repeatsClassArray;
		data[GlobalParameters.REPEATS_FAMILY_INDEX] = repeatsFamilyArray;
		data[GlobalParameters.OMIM_INDEX] = omimArray;

		// fill the table contents from all arrays
		for (int i = 0; i < numOfRows; i++) {
			tableContents[i][0] = Integer.toString(i + 1);
			tableContents[i][1] = locusArray[i].toString();
			tableContents[i][2] = Integer.toString(locusArray[i].size());
			for (int j = GlobalParameters.NR_OF_COLUMNS_TO_IGNORE; j < numOfColumns; j++) {
				tableContents[i][j] = data[tempArray[j
						- GlobalParameters.NR_OF_COLUMNS_TO_IGNORE]][i];
			}
		}

		listOfTableContents.add(tableContents);
		return tableContents;
	}

	/**
	 * Makes the table look good / presentable by invoking other methods.
	 * 
	 * @param table
	 * @see gui.CompareDatasets#centerTextInTable
	 * @see gui.CompareDatasets#adjustColumnWidths
	 */
	private void makeTablePresentable(final JTable table) {
		table.getTableHeader().setReorderingAllowed(false);
		adjustColumnWidths(table);
		centerTextInTable(table);
		table.revalidate();
	}

	/**
	 * Align text as center in all the columns
	 * 
	 * @param table
	 */
	private void centerTextInTable(JTable table) {
		int numOfColumns = table.getColumnCount();

		// align text CENTER in headers
		TableCellRenderer renderer = table.getTableHeader()
				.getDefaultRenderer();
		JLabel label = (JLabel) renderer;
		label.setHorizontalAlignment(JLabel.CENTER);

		// align text CENTER in cells
		for (int i = 0; i < numOfColumns; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			col.setCellRenderer(dtcr);
		}
	}

	/**
	 * Adjust column widths to be as wide as the longest column entry for that
	 * respective column, for a given table.
	 * 
	 * @param table
	 */
	private void adjustColumnWidths(JTable table) {

		TableColumnModel columnModel = table.getColumnModel();
		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxwidth = 0;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = table.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(table,
						value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			}

			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer
					.getTableCellRendererComponent(table, headerValue, false,
							false, 0, col);

			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width);
			column.setPreferredWidth(maxwidth);
		}
	}

	/**
	 * This method contains instructions to update the table so as to reflect
	 * changes in the View menu.
	 */
	public void updateTablesForViewMenu() {

		int numOfTables = listOfUneditedTables.size();

		for (int i = 0; i < numOfTables; i++) {
			JTable table = listOfUneditedTables.get(i);
			table.setModel(new DefaultTableModel(listOfTableContents.get(i),
					listOfHeaders.get(i)));

			if (!GlobalParameters.SHOW_SIZE) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_SIZE)));
			}
			if (GlobalParameters.FIND_CYTOBAND
					&& !GlobalParameters.SHOW_CYTOBAND) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_CYTOBAND)));
			}
			if (GlobalParameters.FIND_GENE && !GlobalParameters.SHOW_GENE) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_GENE)));
			}
			if (GlobalParameters.FIND_REPEATS
					&& !GlobalParameters.SHOW_REPEATS_NAME) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_REPEATS_NAME)));
			}
			if (GlobalParameters.FIND_REPEATS
					&& !GlobalParameters.SHOW_REPEATS_CLASS) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_REPEATS_CLASS)));
			}
			if (GlobalParameters.FIND_REPEATS
					&& !GlobalParameters.SHOW_REPEATS_FAMILY) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_REPEATS_FAMILY)));
			}
			if (GlobalParameters.FIND_OMIM && !GlobalParameters.SHOW_OMIM) {
				table.removeColumn(table.getColumnModel().getColumn(
						table.getColumnModel().getColumnIndex(
								GlobalParameters.STR_OMIM)));
			}

			makeTablePresentable(table);
			table.revalidate();
		}
	}

	/**
	 * Returns the table object.
	 * 
	 * @return table
	 */
	public JTable getTable() {
		// enable sorting on table columns
		m_table.setAutoCreateRowSorter(true);
		return m_table;
	}

	// variable declaration
	private JTable m_table = new JTable();
	private ArrayList<JTable> listOfUneditedTables = new ArrayList<JTable>();
	private ArrayList<String[]> listOfHeaders = new ArrayList<String[]>();
	private ArrayList<String[][]> listOfTableContents = new ArrayList<String[][]>();
	// end of variable declaration
}
