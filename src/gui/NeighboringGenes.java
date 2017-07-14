package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import backend.GlobalParameters;
import backend.Locus;

/**
 * This class finds the nearest upstream - downstream neighboring genes for a
 * given locus and displays them on a UI frame.
 * 
 * @author mkumar since v1.0
 */
public class NeighboringGenes extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(NeighboringGenes.class);

	/**
	 * Constructor
	 * 
	 * @param locusArray
	 *            array of loci
	 * @param geneArray
	 *            array of gene data
	 * @param selectedRowIndex
	 *            index of the selected row in the UI
	 * @param statement
	 *            statement object for database connection
	 */
	public NeighboringGenes(Locus[] locusArray, String[] geneArray,
			int selectedRowIndex, Statement statement) {
		super();
		logger.info("inside Neighboring Genes");

		m_locusArray = locusArray;
		m_geneArray = geneArray;
		m_selectedRowIndex = selectedRowIndex;
		m_statement = statement;
		initComponents();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Initialize components, create text fields and buttons, and add action
	 * listeners on all events
	 */
	private void initComponents() {
		setTitle("Neighboring Genes");
		locusLabel = new JLabel(m_locusArray[m_selectedRowIndex].toString());
		getAtmostLabel = new JLabel("Show atmost");
		genesLabel = new JLabel("genes");
		withinLabel = new JLabel("that lie within");
		basepairsLabel = new JLabel("base pairs");
		maxGenesField = new JFormattedTextField();
		maxBasePairsField = new JFormattedTextField();
		refreshButton = new JButton("Refresh");
		upStreamLabel = new JLabel("Upstream");
		downStreamLabel = new JLabel("Downstream");
		upStreamScrollPane = new JScrollPane();
		downStreamScrollPane = new JScrollPane();
		upStreamTable = new JTable();
		downStreamTable = new JTable();
		closeButton = new JButton("Close");

		chr = m_locusArray[m_selectedRowIndex].getChr();
		startPos = m_locusArray[m_selectedRowIndex].getStartPos();
		endPos = m_locusArray[m_selectedRowIndex].getEndPos();

		// focus listener on maxGenesField, from user-specified value
		maxGenesField.setValue(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY);
		maxGenesField.setColumns(3);
		maxGenesField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fvt) {
			}

			public void focusLost(FocusEvent fvt) {
				String text = maxGenesField.getText();
				int maxNumOfGenes = Integer.parseInt(text);
				GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY = maxNumOfGenes;
				maxGenesField
						.setValue(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY);
			}
		});

		maxBasePairsField
				.setValue(GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES);
		maxBasePairsField.setColumns(10);
		maxBasePairsField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fvt) {
			}

			public void focusLost(FocusEvent fvt) {
				String text = maxBasePairsField.getText();
				int maxNumOfBasePairs = Integer.parseInt(text);
				GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES = maxNumOfBasePairs;
				maxBasePairsField
						.setValue(GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES);
			}
		});

		// refresh button action listener
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshButtonActionPerformed();
			}
		});

		// close button action listener
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NeighboringGenes.this.dispose();
			}
		});

		// here we add 20 so that even after elimination of
		// transcripts later, we still have MAX_NUMBER_OF_NEIGHBORING_GENES
		// unique values to display

		// upstream query
		queryUpStream = "SELECT txEnd, name2 FROM refGene WHERE chrom = \'"
				+ chr
				+ "\' AND txEnd BETWEEN "
				+ Integer.toString(startPos
						- GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES)
				+ " AND "
				+ startPos
				+ " ORDER BY txEnd DESC LIMIT "
				+ Integer
						.toString(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY + 20);
		// downstream query
		queryDownStream = "SELECT txStart, name2 FROM refGene WHERE chrom = \'"
				+ chr
				+ "\' AND txStart BETWEEN "
				+ endPos
				+ " AND "
				+ Integer.toString(endPos
						+ GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES)
				+ " ORDER BY txStart LIMIT "
				+ Integer
						.toString(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY + 20);

		m_upStreamArray = fetchFromServer(queryUpStream, startPos, "up");
		m_downStreamArray = fetchFromServer(queryDownStream, endPos, "down");
		populateTable(upStreamTable, m_upStreamArray);
		populateTable(downStreamTable, m_downStreamArray);
		makeTablePresentable(upStreamTable, upStreamScrollPane);
		makeTablePresentable(downStreamTable, downStreamScrollPane);
		setLayoutOfComponents();

	}

	/**
	 * Fetch results from database.
	 * 
	 * Results include the nearest gene name, and its distance from the respective
	 * start / end coordinate. These results are stored in a 2dArray and passed to the method
	 * which populates the cells in the table with these values.
	 * @param query sql query that is submitted to the database
	 * @param position start or end coordinate, whichever is applicable
	 * @param neighbor up or down, to specify whether upstream or downstream genes are being searched for
	 * @return 2D array containing neighboring genes data
	 */
	private String[][] fetchFromServer(String query, int position,
			String neighbor) {
		logger.info("fetching neighboring genes");
		String[][] array = new String[GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY][numOfColumnsInTable - 1];

		ResultSet rs = null;
		try {

			rs = m_statement.executeQuery(query);
			// reverse read the result set
			rs.beforeFirst();
			// if some result exists
			int j = 0;
			String currentEntry = null;
			String prevEntry = null;
			while (rs.next()) {
				// if the locus lies in some gene, then
				// do not display that gene name in the neighboring genes list
				if (rs.getString(2).equals(m_geneArray[m_selectedRowIndex])) {
					continue;
				}

				// show only unique genes in neighboring genes
				// do not store transcripts of genes
				if (j == 0) {
					String firstEntry = rs.getString(2);
					currentEntry = firstEntry;
					prevEntry = null;
					int distance = Math.abs(position
							- Integer.parseInt(rs.getString(1)));
					// start positions are 0-based, end positions are 1-based
					// hence need to add 1 to distance for all downstream
					// genes
					if (neighbor.equals("up"))
						array[j][0] = Integer.toString(distance);
					else
						array[j][0] = Integer.toString(distance + 1);
					array[j][1] = firstEntry;
					j++;

				} else {
					prevEntry = currentEntry;
					currentEntry = rs.getString(2);

					if (currentEntry.equals(prevEntry)) {
						continue;
					} else {
						int distance = Math.abs(position
								- Integer.parseInt(rs.getString(1)));
						// start positions are 0-based, end positions are
						// 1-based
						// hence need to add 1 to distance for all downstream
						// genes
						if (neighbor.equals("up"))
							array[j][0] = Integer.toString(distance);
						else
							array[j][0] = Integer.toString(distance + 1);
						array[j][1] = currentEntry;
						j++;
						
						// j should always be <
						// MAX_NUMBER_OF_NEIGHBORING_GENES_TO_DISPLAY,
						// but
						// we don't need to perform a manual check for it,
						// the
						// while
						// loop does that already (from LIMIT condition)
						if (j == GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY)
							break;
					}
				}
			}

		} catch (final SQLException sqlExcep) {
			logger.debug("cannot execute refGene upstream query");
			JOptionPane
			.showMessageDialog(
					NeighboringGenes.this,
					"Unable to fetch neighboring genes",
					"Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				logger.debug("cannot close resultset for neighboring genes query");
			}
		}
		return array;
	}

	/**
	 * Populate the cells in the GUI table with values from the 2dArray generated.
	 * Array contains gene name, and distance of gene from start / end coordinate
	 * @param table
	 * @param array 2darray containing values of cells
	 * @see gui.NeighboringGenes#fetchFromServer(String, int, String)
	 */
	private void populateTable(JTable table, String[][] array) {
		headers = new String[] { "Number", "Distance", "Gene" };
		// initialize tableContents
		tableContents = new String[GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY][numOfColumnsInTable];
		// table contents
		for (int i = 0; i < GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY; i++) {

			tableContents[i][0] = Integer.toString(i + 1);
			tableContents[i][1] = array[i][0];
			tableContents[i][2] = array[i][1];

		}
		// set the model for the table and disable user-editing of cells
		table.setModel(new DefaultTableModel(tableContents, headers) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	}

	/**
	 * make table look good
	 * @param table
	 * @param scrollPane scrollpane to which jtable is added
	 */
	private void makeTablePresentable(final JTable table, JScrollPane scrollPane) {

		// disable reordering of columns after display
		table.getTableHeader().setReorderingAllowed(false);
		// automatically adjust column widths in table based on text-width
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				adjustColumnWidths(table);
			}
		});
		table.revalidate();
		// align text CENTER in headers
		TableCellRenderer renderer = table.getTableHeader()
				.getDefaultRenderer();
		JLabel label = (JLabel) renderer;
		label.setHorizontalAlignment(JLabel.CENTER);
		// align text CENTER in cells
		for (int i = 0; i < numOfColumnsInTable; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			col.setCellRenderer(dtcr);
		}
		// add table to scrollpane
		scrollPane.setViewportView(table);
	}

	/**
	 * Adjust column widths to be as wide as the longest column entry for that
	 * respective column, for a given table.
	 * 
	 * @param table
	 */
	private void adjustColumnWidths(JTable table) {
		// strategy - get max width for cells in column and
		// make that the preferred width
		TableColumnModel columnModel = table.getColumnModel();
		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxwidth = 0;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = table.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(table,
						value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			} // for row

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
		} // for col
	}

	/**
	 * set layout of all UI components
	 */
	private void setLayoutOfComponents() {
		setLayout(new MigLayout());
		add(locusLabel, "wrap, center");
		add(getAtmostLabel, "split 7");
		add(maxGenesField);
		add(genesLabel);
		add(withinLabel);
		add(maxBasePairsField);
		add(basepairsLabel, "wrap");
		add(refreshButton, "wrap");
		add(upStreamLabel, "split 2, gap 85px 250px");
		add(downStreamLabel, "wrap");
		add(upStreamScrollPane, "split 2");
		add(downStreamScrollPane, "wrap");
		add(closeButton, "wrap");
		setPreferredSize(new Dimension(600, 300));
		pack();
		repaint();
	}

	/**
	 * action performed on the refresh button.
	 * 
	 * The tool takes new values from both text fields, and creates new sql queries
	 * with which data from the database is fetched.
	 */
	private void refreshButtonActionPerformed() {
		this.remove(upStreamScrollPane);
		this.remove(downStreamScrollPane);
		this.remove(closeButton);
		repaint();
		m_upStreamArray = null;
		m_downStreamArray = null;

		queryUpStream = "SELECT txEnd, name2 FROM refGene WHERE chrom = \'"
				+ chr
				+ "\' AND txEnd BETWEEN "
				+ Integer.toString(startPos
						- GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES)
				+ " AND "
				+ startPos
				+ " ORDER BY txEnd DESC LIMIT "
				+ Integer
						.toString(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY + 20);

		queryDownStream = "SELECT txStart, name2 FROM refGene WHERE chrom = \'"
				+ chr
				+ "\' AND txStart BETWEEN "
				+ endPos
				+ " AND "
				+ Integer.toString(endPos
						+ GlobalParameters.MAX_BASE_PAIRS_FOR_NEIGHBORING_GENES)
				+ " ORDER BY txStart LIMIT "
				+ Integer
						.toString(GlobalParameters.MAX_NR_OF_NEIGHBORING_GENES_TO_DISPLAY + 20);

		m_upStreamArray = fetchFromServer(queryUpStream, startPos, "up");
		m_downStreamArray = fetchFromServer(queryDownStream, endPos, "down");
		populateTable(upStreamTable, m_upStreamArray);
		populateTable(downStreamTable, m_downStreamArray);
		makeTablePresentable(upStreamTable, upStreamScrollPane);
		makeTablePresentable(downStreamTable, downStreamScrollPane);
		setLayoutOfComponents();
	}

	// variable declaration
	private Statement m_statement;
	private String chr;
	private int startPos;
	private int endPos;
	private String[][] m_upStreamArray;
	private String[][] m_downStreamArray;
	private JLabel locusLabel;
	private JLabel upStreamLabel;
	private JLabel downStreamLabel;
	private JScrollPane upStreamScrollPane;
	private JScrollPane downStreamScrollPane;
	private JTable upStreamTable;
	private JTable downStreamTable;
	private JButton closeButton;
	private Locus[] m_locusArray;
	private String[] m_geneArray;
	private int m_selectedRowIndex;
	private String[] headers;
	private String[][] tableContents;
	private int numOfColumnsInTable = 3;
	private JLabel getAtmostLabel;
	private JLabel genesLabel;
	private JLabel withinLabel;
	private JLabel basepairsLabel;
	/**
	 * Maximum number of genes the user wants to see
	 */
	private JFormattedTextField maxGenesField;
	/**
	 * The basepair range within which user wants to see all genes
	 */
	private JFormattedTextField maxBasePairsField;
	private JButton refreshButton;
	private String queryUpStream;
	private String queryDownStream;
	// end of variable declaration
}
