package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import backend.GlobalParameters;
import backend.Locus;
import backend.Pair;

/**
 * This class contains methods to compare multiple datasets for overlaps of
 * loci.
 * 
 * @author mkumar
 * @since v1.0
 */
public class CompareDatasets extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(CompareDatasets.class);

	/**
	 * Constructor
	 * 
	 * @param listOfLocusArray
	 *            list containing all locus arrays.
	 * @param listOfFileNames
	 *            list containing all file names
	 */
	public CompareDatasets(ArrayList<Locus[]> listOfLocusArray,
			ArrayList<String> listOfFileNames) {
		logger.info("generating comparison table for all datasets");
		setTitle("Comparison Table");

		m_listOfLocusArray = new ArrayList<Locus[]>(listOfLocusArray);
		m_listOfFileNames = new ArrayList<String>(listOfFileNames);
		comparisonMap = createComparisonMap();
		createTable(comparisonMap);
		setButtonsAndLabels();
		setLayoutOfComponents();
	}

	/**
	 * This method contains instructions on creating a comparisonMap, which is a
	 * map with global mapping from each-chromosome-number (1-22 and X and Y) :
	 * list-of-all-pairs-associated-with-that-chrNr-in-all-datasets.
	 * 
	 * To compare all datasets for overlaps, a global map is created. This map contains
	 * 24 keys, one for each chromosome (1-22 and X and Y). Then, beginning from the first locus
	 * in the first dataset, it associates a pair-list with that chromosome, and it does this iteratively.
	 * This pair list contains all the pairs that are associated with that chromosome, where a pair is a
	 * combination of the start and the end coordinates for each locus. Thus, together with the chromosome,
	 * each pair in the pair list denotes one locus.
	 * 
	 * For each iteration (i.e. locus), the method separates the chrNr and the start and the end coordinates.
	 * Next, for that chromosome, it retrieves the pair list. It then checks whether this new pair can be
	 * merged with an element of the pair list, given the overlap threshold (which is specified by the user).
	 * An overlap is counted when both the start or the end coordinates are within the overlap threshold.
	 * If an overlap is found, it merges both these loci, saving the smaller of the two start coordinates and
	 * the larger of the two end coordinates. The pair list is updated by removing the old pair and adding this
	 * new pair to the list.
	 * 
	 * Additionally, a boolean array is associated with each pair in the list, which maintains a global list of
	 * the presence / absence of this pair in all datasets. This is later used to determine which datasets contain
	 * a given locus and which ones do not.
	 * 
	 * @param listOfLocusArray
	 *            list of locus array
	 * @return comparisonMap a map with global mapping from
	 *         each-chromosome-number (1-22 and X and Y) :
	 *         list-of-all-pairs-associated-with-that-chrNr-in-all-datasets
	 *
	 */
	private Map<String, ArrayList<Pair>> createComparisonMap() {

		Map<String, ArrayList<Pair>> compareMap = new HashMap<String, ArrayList<Pair>>();
		ArrayList<Pair> pairList;
		mergedLociList = new ArrayList<Locus>();

		// for each locus array in the list of locus array
		for (Locus[] thisLocusArray : m_listOfLocusArray) {

			// for each individual locus in locus array
			for (Locus thisLocus : thisLocusArray) {

				String thisChr = thisLocus.getChr();
				int thisStartPos = thisLocus.getStartPos();
				int thisEndPos = thisLocus.getEndPos();
				Pair thisPair = new Pair(thisStartPos, thisEndPos);
				thisPair.boolArr = new boolean[m_listOfLocusArray.size()];

				// if map already contains this chr
				if (compareMap.containsKey(thisChr)) {

					// get pair list associated with this chr
					pairList = compareMap.get(thisChr);

					// for each pair in list, check whether loci overlap, i.e.
					// whether distance between both start positions and/or
					// end positions is less than the threshold
					for (Pair existingPair : pairList) {

						int existingStartPos = existingPair.getStartPos();
						int existingEndPos = existingPair.getEndPos();

						// if yes, then remove old entries, merge these loci,
						// and add a fresh entry to map and lists
						if (Math.abs(thisStartPos - existingStartPos) <= GlobalParameters.THRESHOLD_MERGE_OVERLAPPING_LOCI
								|| Math.abs(thisEndPos - existingEndPos) <= GlobalParameters.THRESHOLD_MERGE_OVERLAPPING_LOCI) {

							// remove existing pair from list
							pairList.remove(existingPair);
							// remove from mergedLocusArrayList
							mergedLociList.remove(getIndexOfElement(
									mergedLociList, thisChr, existingStartPos,
									existingEndPos));

							// update pair values
							// minimum of start positions
							int updatedStartPos = Math.min(thisStartPos,
									existingPair.getStartPos());
							// maximum of end positions
							int updatedEndPos = Math.max(thisEndPos,
									existingPair.getEndPos());

							Pair updatedPair = new Pair(updatedStartPos,
									updatedEndPos);
							thisPair = updatedPair;
							thisPair.boolArr = existingPair.boolArr;

							break;
						}
					}
				} else {
					pairList = new ArrayList<Pair>();
				}

				thisPair.boolArr[m_listOfLocusArray.indexOf(thisLocusArray)] = true;
				pairList.add(thisPair);
				mergedLociList.add(new Locus(thisChr, thisPair));
				// finally, add to map
				compareMap.put(thisChr, pairList);
			}
		}
		return compareMap;
	}

	/**
	 * Generates the GUI table for comparison of loci for overlaps.
	 * 
	 * @param listOfFileNames
	 *            list of file names
	 * @param comparisonMap
	 *            map with mapping from each chrNr : list-of-all-pairs-for-that-chrNr
	 * @see gui.CompareDatasets#comparisonMap
	 * 
	 */
	private void createTable(Map<String, ArrayList<Pair>> comparisonMap) {
		logger.info("received comparisonMap, now creating comparison table");

		scrollPane = new JScrollPane();
		table = new JTable();

		int numOfRows = mergedLociList.size();

		ignoreColumns = 3;
		int numOfColumns = m_listOfFileNames.size() + ignoreColumns;

		// populate headers
		String[] headers = new String[numOfColumns];
		headers[0] = "Sr. No.";
		headers[1] = "Frequency";
		headers[2] = "Locus(overlaps merged)";
		for (int k = ignoreColumns; k < numOfColumns; k++) {
			headers[k] = m_listOfFileNames.get(k - ignoreColumns);
		}

		// sort list
		Collections.sort(mergedLociList);

		// populate tableContents
		String[][] tableContents = new String[numOfRows][numOfColumns];
		for (int i = 0; i < numOfRows; i++) {
			tableContents[i][0] = Integer.toString(i + 1);
			tableContents[i][1] = Integer.toString(mergedLociList.get(i).getPair().getCountOfTrueValuesInBoolArr());
			tableContents[i][2] = mergedLociList.get(i).toString();
			for (int j = ignoreColumns; j < numOfColumns; j++) {

				tableContents[i][j] = mergedLociList.get(i).getPair().boolArr[j
						- ignoreColumns] ? "Y" : "N";
			}
		}
		
		// enable sorting on table columns
		table.setAutoCreateRowSorter(true);

		table.setModel(new MyTableModel(tableContents, headers));
		scrollPane.setViewportView(table);
		makeTablePresentable(table);

	}

	/**
	 * sets the buttons and labels in the GUI frame
	 */
	private void setButtonsAndLabels() {
		label = new JLabel("Overlap Threshold: ");
		thresholdTextField = new JFormattedTextField();
		refreshButton = new JButton("Refresh");
		closeButton = new JButton("Close");

		thresholdTextField.setValue(GlobalParameters.THRESHOLD_MERGE_OVERLAPPING_LOCI);
		thresholdTextField.setColumns(4);
		thresholdTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fvt) {
			}

			public void focusLost(FocusEvent fvt) {
				String text = thresholdTextField.getText();
				int threshold = Integer.parseInt(text);
				GlobalParameters.THRESHOLD_MERGE_OVERLAPPING_LOCI = threshold;
				thresholdTextField
						.setValue(GlobalParameters.THRESHOLD_MERGE_OVERLAPPING_LOCI);
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				refreshButtonActionPerformed(evt);
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				CompareDatasets.this.dispose();
			}
		});
	}

	/**
	 * sets the layout of all components on the UI window.
	 */
	private void setLayoutOfComponents() {

		this.setLayout(new MigLayout());
		this.add(label, "split 3");
		this.add(thresholdTextField);
		this.add(refreshButton, "wrap");
		scrollPane.setPreferredSize(new Dimension(650, 350));
		this.add(scrollPane, "grow, push, span, wrap");
		this.add(closeButton);

		pack();
		setLocationRelativeTo(null);
		setPreferredSize(new Dimension(600, 350));
		setVisible(true);
	}

	/**
	 * Makes the table look good / presentable by invoking other methods.
	 * 
	 * @param table
	 * @see gui.CompareDatasets#centerTextInTable
	 * @see gui.CompareDatasets#adjustColumnWidths
	 */
	private void makeTablePresentable(final JTable table) {
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
		// align text CENTER in headers
		TableCellRenderer renderer = table.getTableHeader()
				.getDefaultRenderer();
		JLabel label = (JLabel) renderer;
		label.setHorizontalAlignment(JLabel.CENTER);

		// align text CENTER in cells
		for (int i = 0; i < ignoreColumns; i++) {
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
			for (int row = 0; row < ignoreColumns; row++) {
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
	 * Action performed on the refresh button.
	 * 
	 * @param evt
	 *            event generated when refresh button is clicked.
	 */
	private void refreshButtonActionPerformed(ActionEvent evt) {

		this.remove(label);
		this.remove(thresholdTextField);
		this.remove(refreshButton);
		this.remove(scrollPane);
		this.remove(closeButton);
		repaint();

		comparisonMap = null;
		comparisonMap = createComparisonMap();
		createTable(comparisonMap);
		setButtonsAndLabels();
		setLayoutOfComponents();

	}

	/**
	 * Gets the index of an element (locus) which matches the provided chr,
	 * startPos and the endPos.
	 * 
	 * @param list
	 *            input list in which index of element needs to be found
	 * @param chr
	 *            chromosome number
	 * @param startPos
	 *            start coordinate of the locus
	 * @param ep
	 *            end coordinate of the locus
	 * @return index of element
	 */
	private int getIndexOfElement(ArrayList<Locus> list, String chr,
			int startPos, int endPos) {
		int index = -1;

		for (Locus locus : list) {
			if (locus.equals(chr, startPos, endPos)) {
				index = list.indexOf(locus);
			}
		}

		return index;
	}
	/**
	 * inner class: Table Model for the compare datasets table.
	 * 
	 * This class creates a generic table model, which is used in the comparison
	 * table.
	 * 
	 * @author mkumar
	 * @since v1.0
	 */
	class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private Object[][] tableContents;
		private String[] headers;

		public MyTableModel(String[][] tableContents, String[] headers) {
			super();
			this.tableContents = tableContents;
			this.headers = headers;
		}

		/**
		 * Returns the number of columns in the table
		 */
		@Override
		public int getColumnCount() {
			return headers.length;
		}

		/**
		 * Returns the number of rows in the table
		 */
		@Override
		public int getRowCount() {
			return tableContents.length;
		}

		/**
		 * Used to display tick marks in the comparison table.
		 * Returns the ImageIcon or String object, based on which column
		 * it is.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			if (col < ignoreColumns) {
				return tableContents[row][col];
			} else {
				if (tableContents[row][col].equals("Y")) {
					return new ImageIcon(GlobalParameters.PATH_TO_TICK);
				} else if (tableContents[row][col].equals("N")) {
					return new ImageIcon();
				} else
					return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int col) {
			if (col < ignoreColumns) {
				return String.class;
			} else {
				return ImageIcon.class;
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public String getColumnName(int i) {
			return headers[i];
		}
	}

	// variable declaration
	private Map<String, ArrayList<Pair>> comparisonMap;
	private ArrayList<Locus> mergedLociList;
	private ArrayList<Locus[]> m_listOfLocusArray;
	private ArrayList<String> m_listOfFileNames;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel label;
	private JButton refreshButton;
	private JButton closeButton;
	private JFormattedTextField thresholdTextField;
	private int ignoreColumns= 0;

	// end of variable declaration
}
