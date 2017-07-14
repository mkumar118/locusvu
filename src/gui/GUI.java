package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import backend.GlobalParameters;
import backend.Start;
import backend.Locus;
import backend.OpenBrowser;
import backend.ResultsTable;
import backend.Statistics;
import backend.TaskManager;

/**
 * This class is responsible for all GUI-related events.
 * 
 * @author mkumar
 * 
 */
public class GUI extends JFrame implements ClipboardOwner {
	private static final Logger logger = Logger.getLogger(GUI.class);
	private static final long serialVersionUID = 1L;
	TaskManager fetchData;

	/**
	 * Constructor for the GUI class.
	 */
	public GUI() {
		super();
		logger.info("inside GUI class");
		setTitle("LocusVu");

		// initialize
		init();
		createAndShowGUI();
	}

	/**
	 * Initialize all objects
	 */
	private void init() {
		m_resultsTableObject = new ResultsTable();
		m_stats = new Statistics();
		listOfFileNames = new ArrayList<String>();
		listOfLocusArray = new ArrayList<Locus[]>();
		listOfCytoBandArray = new ArrayList<String[]>();
		listOfGeneArray = new ArrayList<String[]>();
		listOfRepeatsNameArray = new ArrayList<String[]>();
		listOfRepeatsClassArray = new ArrayList<String[]>();
		listOfRepeatsFamilyArray = new ArrayList<String[]>();
		listOfOmimArray = new ArrayList<String[]>();
		listOfTables = new ArrayList<JTable>();		

		setMenuBar();
		setMenuBarActionListeners();
	}

	/**
	 * Creates the first window in the GUI
	 */
	private void createAndShowGUI() {

		this.setLayout(new MigLayout());
		this.setPreferredSize(new Dimension(800, 400));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Sets the menu bar in the GUI
	 */
	private void setMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		exportMenu = new JMenu("Export..");
		editMenu = new JMenu("Edit");
		viewMenu = new JMenu("View");
		compareMenu = new JMenu("Compare");
		plotMenu = new JMenu("Plot");
		barChartMenu = new JMenu("Bar Chart");
		pieChartMenu = new JMenu("Pie Chart");
		newFile = new JMenuItem("New File");
		save = new JMenuItem("Save");
		excelItem = new JMenuItem("As Excel");
		print = new JMenuItem("Print");
		restart = new JMenuItem("Restart");
		exit = new JMenuItem("Exit");
		settings = new JMenuItem("Settings");
		compare = new JMenuItem("Compare Datasets");
		sizeChart = new JMenuItem("Size Distribution");
		chrBarChart = new JMenuItem("Chromosome Distribution");
		chrPieChart = new JMenuItem("Chromosome Distribution");
		genePieChart = new JMenuItem("Genes");
		omimPieChart = new JMenuItem("OMIM");

		fileMenu.add(newFile);
		fileMenu.add(exit);

		editMenu.add(settings);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);

		setJMenuBar(menuBar);
	}

	/**
	 * Sets action listeners for all menu bar events
	 */
	private void setMenuBarActionListeners() {
		// new
		newFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newFileActionPerformed();
			}
		});
		// save
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveFileActionPerformed();
			}
		});
		excelItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				excelItemActionPerformed();
			}
		});
		// print
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				printActionPerformed();
			}
		});
		// exit
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitActionPerformed();
			}
		});
		// restart application
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				restartApplication();
			}
		});		
		// compare datasets
		compare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compareActionPerformed();
			}
		});
		// size distribution
		sizeChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				sizeActionPerformed(listOfLocusArray
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
		// chromosomes bar chart
		chrBarChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chrBarChartActionPerformed(listOfLocusArray
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
		// chromosomes pie chart
		chrPieChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chrPieChartActionPerformed(listOfLocusArray
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
		// genes pie chart
		genePieChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				genePieChartActionPerformed(listOfGeneArray
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
		// omim pie chart
		omimPieChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				omimPieChartActionPerformed(listOfOmimArray
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
		// integrated settings pane
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Settings();
			}
		});
	}

	/**
	 * Displays a progress bar on the GUI while the tool is busy fetching
	 * information from the online database.
	 * 
	 */
	private void displayProgressBar() {
		logger.debug("inside displayProgressBar");
		progressBarLabel = new JLabel(
				"Please wait while LocusVu fetches information from the UCSC database..");
		progressBar = new JProgressBar();
		
		// add restart item to file menu
		fileMenu.remove(exit);
		fileMenu.add(restart);
		fileMenu.add(exit);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				restartApplication();
			}
		});

		// get %age of task completed
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(250, 25));

		// fetch data in the background
		new Thread(new TaskManager(this)).start();

		// layout
		p = new JPanel(new MigLayout());
		this.add(p);
		// hack to make the layout look nice
		if (tabbedPane.getTabCount() == 0) {
			addGapInLayout(p, 10);
		}

		p.add(progressBarLabel, "gap 150px 200px, wrap");
		p.add(progressBar, "gap 235px, split 2");
		p.add(cancelButton, "wrap");
		repaint();
		validate();
	}

	/**
	 * Displays the results table after all the information has been fetched
	 * from the database.
	 * 
	 * @param locusArray
	 *            an array of input loci.
	 * @param cytoBandArray
	 *            an array of cytoband information
	 * @param geneArray
	 *            an array of gene information
	 * @param repeatsNameArray
	 *            an array of repeats name information
	 * @param repeatsClassArray
	 *            an array of repeats class information
	 * @param repeatsFamilyArray
	 *            an array of repeats family information
	 * @param omimArray
	 *            an array of omim information
	 * @param st
	 *            statement object from the database connection
	 * 
	 * @see backend.GetCytoBand
	 * @see backend.GetGenes
	 * @see backend.GetRepeats
	 * @see backend.GetOmim
	 * @see backend.Database
	 * @see Statement
	 */
	public void displayResults(Locus[] locusArray, String[] cytoBandArray,
			String[] geneArray, String[] repeatsNameArray,
			String[] repeatsClassArray, String[] repeatsFamilyArray,
			String[] omimArray, Statement st) {
		logger.debug("inside GUI.displayResults");
		this.m_st = st;

		// do these only once
		if (tabbedPane.getTabCount() == 0) {
			addCheckBoxesToViewMenu();
			addItemsToMenuBar();
		}

		m_resultsTableObject.setTable(locusArray, cytoBandArray, geneArray,
				repeatsNameArray, repeatsClassArray, repeatsFamilyArray,
				omimArray);

		JTable table = m_resultsTableObject.getTable();
		setPopupMenu(table);
		addToLists(locusArray, cytoBandArray, geneArray, repeatsNameArray,
				repeatsClassArray, repeatsFamilyArray, omimArray, table);
		setLayoutOfResultsPanel(table);
	}

	/**
	 * Adds current dataset's arrays to the master list for all datasets.
	 * 
	 * Each time an input file is specified, the tool fetches information for
	 * that file from the remote database. All the fetched information is stored
	 * in respective arrays. To be able to open multiple datasets in tabs, a
	 * master list of arrays is maintained for each track type, e.g.
	 * listOfLocusArray contains all the locus arrays, where each locusArray
	 * contains the list of loci of that dataset.
	 * 
	 * 
	 * @param locusArray
	 *            array of loci, from input file.
	 * @param cytoBandArray
	 *            array of cytoband data.
	 * @param geneArray
	 *            array of gene data.
	 * @param repeatsNameArray
	 *            array of repeats name data.
	 * @param repeatsClassArray
	 *            array of repeats class data.
	 * @param repeatsFamilyArray
	 *            array of repeats family data.
	 * @param omimArray
	 *            array of omim data.
	 */
	private void addToLists(Locus[] locusArray, String[] cytoBandArray,
			String[] geneArray, String[] repeatsNameArray,
			String[] repeatsClassArray, String[] repeatsFamilyArray,
			String[] omimArray, JTable table) {

		listOfFileNames.add(selectedFileName);
		listOfLocusArray.add(locusArray);
		listOfCytoBandArray.add(cytoBandArray);
		listOfGeneArray.add(geneArray);
		listOfRepeatsNameArray.add(repeatsNameArray);
		listOfRepeatsClassArray.add(repeatsClassArray);
		listOfRepeatsFamilyArray.add(repeatsFamilyArray);
		listOfOmimArray.add(omimArray);
		listOfTables.add(table);
		
		// add all lists to a master array,
		// and pass this array to the Tabbed pane,
		// such that when a tab is closed, all entries
		// in all lists for that tab are permanently deleted
		// from memory too
		
		// and initialize this array for every new dataset
		// so this initialization statement should stay here
		arrayOfLists = new ArrayList[9];
		arrayOfLists[0] = listOfFileNames;
		arrayOfLists[1] = listOfLocusArray;
		arrayOfLists[2] = listOfCytoBandArray;
		arrayOfLists[3] = listOfGeneArray;
		arrayOfLists[4] = listOfRepeatsNameArray;
		arrayOfLists[5] = listOfRepeatsClassArray;
		arrayOfLists[6] = listOfRepeatsFamilyArray;
		arrayOfLists[7] = listOfOmimArray;
		arrayOfLists[8] = listOfTables;
		
	}

	/**
	 * Adds check boxes to the view menu.
	 * 
	 * Based on user specification for tracks to query, this method dynamically
	 * adds the check box for that track to the view menu option, such that the
	 * user can choose to view or not view that track in the results table.
	 */
	private void addCheckBoxesToViewMenu() {
		// initialize
		sizeCheckBox = new JCheckBoxMenuItem(GlobalParameters.STR_SIZE);
		cytoBandCheckBox = new JCheckBoxMenuItem(GlobalParameters.STR_CYTOBAND);
		geneCheckBox = new JCheckBoxMenuItem(GlobalParameters.STR_GENE);
		repeatsNameCheckBox = new JCheckBoxMenuItem(
				GlobalParameters.STR_REPEATS_NAME);
		repeatsClassCheckBox = new JCheckBoxMenuItem(
				GlobalParameters.STR_REPEATS_CLASS);
		repeatsFamilyCheckBox = new JCheckBoxMenuItem(
				GlobalParameters.STR_REPEATS_FAMILY);
		omimCheckBox = new JCheckBoxMenuItem(GlobalParameters.STR_OMIM);

		// set all true
		sizeCheckBox.setSelected(true);
		cytoBandCheckBox.setSelected(true);
		geneCheckBox.setSelected(true);
		repeatsNameCheckBox.setSelected(true);
		repeatsClassCheckBox.setSelected(true);
		repeatsFamilyCheckBox.setSelected(true);
		omimCheckBox.setSelected(true);

		// add size, ALWAYS
		viewMenu.add(sizeCheckBox);
		GlobalParameters.SHOW_SIZE = true;

		// add others based on user selection
		if (GlobalParameters.FIND_CYTOBAND) {
			viewMenu.add(cytoBandCheckBox);
			GlobalParameters.SHOW_CYTOBAND = true;
		}
		if (GlobalParameters.FIND_GENE) {
			viewMenu.add(geneCheckBox);
			GlobalParameters.SHOW_GENE = true;
		}
		if (GlobalParameters.FIND_REPEATS) {
			viewMenu.add(repeatsNameCheckBox);
			GlobalParameters.SHOW_REPEATS_NAME = true;
		}
		if (GlobalParameters.FIND_REPEATS) {
			viewMenu.add(repeatsClassCheckBox);
			GlobalParameters.SHOW_REPEATS_CLASS = true;
		}
		if (GlobalParameters.FIND_REPEATS) {
			viewMenu.add(repeatsFamilyCheckBox);
			GlobalParameters.SHOW_REPEATS_FAMILY = true;
		}
		if (GlobalParameters.FIND_OMIM) {
			viewMenu.add(omimCheckBox);
			GlobalParameters.SHOW_OMIM = true;
		}

		ItemListener checkBoxListener = new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				viewCheckBoxActionPerformed(evt);
			}
		};

		// add listeners on viewMenuitems
		sizeCheckBox.addItemListener(checkBoxListener);
		cytoBandCheckBox.addItemListener(checkBoxListener);
		geneCheckBox.addItemListener(checkBoxListener);
		repeatsNameCheckBox.addItemListener(checkBoxListener);
		repeatsClassCheckBox.addItemListener(checkBoxListener);
		repeatsFamilyCheckBox.addItemListener(checkBoxListener);
		omimCheckBox.addItemListener(checkBoxListener);
	}

	/**
	 * Adds items to the menu bar.
	 * 
	 */
	private void addItemsToMenuBar() {
		// first remove and later add exit and restart to fileMenu such that
		// they always appear towards the end
		fileMenu.remove(restart);
		fileMenu.remove(exit);
		fileMenu.add(save);
		fileMenu.add(exportMenu);
		exportMenu.add(excelItem);
		fileMenu.add(print);
		fileMenu.add(restart);
		fileMenu.add(exit);

		// remove edit menu
		menuBar.remove(editMenu);

		menuBar.add(viewMenu);
		menuBar.add(compareMenu);
		compareMenu.add(compare);

		menuBar.add(plotMenu);

		plotMenu.add(barChartMenu);
		barChartMenu.add(sizeChart);
		barChartMenu.add(chrBarChart);

		plotMenu.add(pieChartMenu);

		pieChartMenu.add(chrPieChart);
		if (GlobalParameters.FIND_GENE) {
			pieChartMenu.add(genePieChart);
		}
		if (GlobalParameters.FIND_OMIM) {
			pieChartMenu.add(omimPieChart);
		}
	}

	/**
	 * Sets the layout of the results table
	 * 
	 * @param table
	 *            the JTable which displays the contents of the results.
	 */
	private void setLayoutOfResultsPanel(JTable table) {

		// remove temporary panel that was added for effective 
		// layout of progress bar
		this.remove(p);		

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setPreferredSize(new Dimension(800, 400));
		
		tabbedPane.addTab(selectedFileName, scrollPane);

		ChangeListener tabbedPaneListener = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JTabbedPane pane = (JTabbedPane) arg0.getSource();
				GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX = pane
						.getSelectedIndex();
			}
		};

		if (tabbedPane.getTabCount() == 1) {
			this.add(tabbedPane, "grow, push, span");
			tabbedPane.addChangeListener(tabbedPaneListener);
		}

		repaint();
		validate();
	}

	/**
	 * Sets the popup menu on the JTable. Triggered by right clicking on a row.
	 * 
	 * @param table
	 *            the JTable on which popup menu is deployed.
	 */
	private void setPopupMenu(final JTable table) {
		// initialize items on popup menu
		popupNGenes = new JMenuItem("View neighboring genes");
		popupUcsc = new JMenuItem("View on UCSC");
		popupGenecards = new JMenuItem("View on Genecards.org");
		popupOmim = new JMenuItem("View on OMIM");
		popupCopy = new JMenu("Copy");
		popupCopyLocus = new JMenuItem("Locus");
		popupCopyCytoBand = new JMenuItem("CytoBand");
		popupCopyGene = new JMenuItem("Gene");
		popupCopyOmim = new JMenuItem("Omim");

		// action listener for view items
		ActionListener popupViewListener = new ActionListener() {
			public void actionPerformed(ActionEvent viewEvent) {
				popupViewActionPerformed(viewEvent);
			}
		};

		// action listener for copy items
		ActionListener popupCopyListener = new ActionListener() {
			public void actionPerformed(ActionEvent copyEvent) {
				popupCopyActionPerformed(copyEvent);

			}
		};

		// mouse adapter -- what to do on right click
		MouseAdapter mouseAdapter = new MouseAdapter() {
			// mouse pressed event
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.isPopupTrigger())
					firePopup(mouseEvent);
			}

			// mouse released event
			public void mouseReleased(MouseEvent mouseEvent) {
				if (mouseEvent.isPopupTrigger())
					firePopup(mouseEvent);
			}

			// popup event
			public void firePopup(MouseEvent mouseEvent) {
				popupMenu = new JPopupMenu();

				// add items to pop up menu
				popupMenu.add(popupNGenes);
				popupMenu.add(popupUcsc);
				popupMenu.add(popupGenecards);
				popupMenu.add(popupOmim);
				popupMenu.add(popupCopy);

				// add items to copy menu
				popupCopy.add(popupCopyLocus);
				popupCopy.add(popupCopyCytoBand);
				popupCopy.add(popupCopyGene);
				popupCopy.add(popupCopyOmim);

				// set default false for all items
				popupNGenes.setEnabled(false);
				popupGenecards.setEnabled(false);
				popupCopyGene.setEnabled(false);
				popupOmim.setEnabled(false);
				popupCopyOmim.setEnabled(false);

				// enable Neighboring genes popup item if the user has requested for
				// the Gene track
				if (GlobalParameters.FIND_GENE) {
					popupNGenes.setEnabled(true);
					// enable lookup on genecards.org if selected locus has a non-empty gene value
					if (listOfGeneArray
							.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[table
							.getSelectedRow()] != GlobalParameters.STR_EMPTY_VALUE) {
						popupGenecards.setEnabled(true);
						popupCopyGene.setEnabled(true);
					}
				}

				// enable omim popup item if the user has requested for omim data
				if (GlobalParameters.FIND_OMIM
						&& (listOfOmimArray
								.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[table
								.getSelectedRow()] != GlobalParameters.STR_EMPTY_VALUE)) {
					// enable lookup on omim.org if selected row has a non-empty value
					popupOmim.setEnabled(true);
					popupCopyOmim.setEnabled(true);
				}
				
				// show pop up menu	
				if (table.getModel().getRowCount() != 0
						&& table.getSelectedRow() != -1) {
					popupMenu.show(table, mouseEvent.getX(), mouseEvent.getY());
				}
			}
		};

		// add action listeners to all items
		popupNGenes.addActionListener(popupViewListener);
		popupUcsc.addActionListener(popupViewListener);
		popupGenecards.addActionListener(popupViewListener);
		popupOmim.addActionListener(popupViewListener);
		popupCopyLocus.addActionListener(popupCopyListener);
		popupCopyCytoBand.addActionListener(popupCopyListener);
		popupCopyGene.addActionListener(popupCopyListener);
		popupCopyOmim.addActionListener(popupCopyListener);
		// add mouse listener on the table
		table.addMouseListener(mouseAdapter);

	}

	/**
	 * This method contains instructions to display / hide columns in the table
	 * as they are selected / de-selected in the View menu.
	 * 
	 * @param viewCheckBoxEvent
	 *            event generated on the view menu check box action.
	 * 
	 */
	private void viewCheckBoxActionPerformed(ItemEvent viewCheckBoxEvent) {
		if (viewCheckBoxEvent.getItemSelectable() == sizeCheckBox) {
			if (GlobalParameters.SHOW_SIZE)
				GlobalParameters.SHOW_SIZE = false;
			else
				GlobalParameters.SHOW_SIZE = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == cytoBandCheckBox) {
			if (GlobalParameters.SHOW_CYTOBAND)
				GlobalParameters.SHOW_CYTOBAND = false;
			else
				GlobalParameters.SHOW_CYTOBAND = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == geneCheckBox) {
			if (GlobalParameters.SHOW_GENE)
				GlobalParameters.SHOW_GENE = false;
			else
				GlobalParameters.SHOW_GENE = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == repeatsNameCheckBox) {
			if (GlobalParameters.SHOW_REPEATS_NAME)
				GlobalParameters.SHOW_REPEATS_NAME = false;
			else
				GlobalParameters.SHOW_REPEATS_NAME = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == repeatsClassCheckBox) {
			if (GlobalParameters.SHOW_REPEATS_CLASS)
				GlobalParameters.SHOW_REPEATS_CLASS = false;
			else
				GlobalParameters.SHOW_REPEATS_CLASS = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == repeatsFamilyCheckBox) {
			if (GlobalParameters.SHOW_REPEATS_FAMILY)
				GlobalParameters.SHOW_REPEATS_FAMILY = false;
			else
				GlobalParameters.SHOW_REPEATS_FAMILY = true;
		}
		if (viewCheckBoxEvent.getItemSelectable() == omimCheckBox) {
			if (GlobalParameters.SHOW_OMIM)
				GlobalParameters.SHOW_OMIM = false;
			else
				GlobalParameters.SHOW_OMIM = true;
		}
		m_resultsTableObject.updateTablesForViewMenu();

	}

	/**
	 * 
	 * This method contains instructions for each item in the popup menu.
	 * 
	 * When a user clicks on a specific item in the popup menu, eg. on View
	 * Neighboring Genes, or View on UCSC, etc. this method contains
	 * instructions for the tool on how to proceed.
	 * <p>
	 * The View Neighboring Genes action invokes the appropriate class that
	 * handles fetching of information from the database.
	 * </p>
	 * <p>
	 * The View on UCSC calls the class for displaying the selected locus in the
	 * default browser's window.
	 * </p>
	 * <p>
	 * The View on Genecards.org calls the class for displaying the selected locus
	 * in the default browser's window.
	 * </p>
	 * <p>
	 * Go to Omim option calls the class, which redirects to the Omim database.
	 * 
	 * @param popupViewEvent
	 *            action event generated for each action on the popup menu.
	 */
	public void popupViewActionPerformed(ActionEvent popupViewEvent) {

		// if view neighboring genes is selected
		if (popupViewEvent.getActionCommand().equals(popupNGenes.getText())) {
			logger.debug("looking for Neighboring Genes");
			// call the respective neighboring genes class
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new NeighboringGenes(
							listOfLocusArray
									.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX),
							listOfGeneArray
									.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX),
							listOfTables
									.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)
									.getSelectedRow(), m_st);
				}
			});	
		}
		// if view on ucsc is selected
		else if (popupViewEvent.getActionCommand()
				.equals(popupUcsc.getText())) {
			logger.debug("going to UCSC");
			// call the appropriate class
			new OpenBrowser(
					listOfLocusArray
							.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX),
					listOfTables.get(
							GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)
							.getSelectedRow()).goToUCSC();
		}
		// if view on genecards.org is selected
		else if (popupViewEvent.getActionCommand().equals(
				popupGenecards.getText())) {
			logger.debug("going to Genecards.org");
			// call the appropriate class
			new OpenBrowser(
					listOfLocusArray
							.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX),
					listOfTables.get(
							GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)
							.getSelectedRow()).goToGenecards(listOfGeneArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
		}
		// if go to omim is selected
		else if (popupViewEvent.getActionCommand()
				.equals(popupOmim.getText())) {
			logger.debug("going to OMIM database");
			// go to omim database
			new OpenBrowser(
					listOfLocusArray
							.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX),
					listOfTables.get(
							GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)
							.getSelectedRow()).goToOMIM(listOfOmimArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
		}
	}

	/**
	 * 
	 * To copy user requested information from the table on to the clipboard.
	 * 
	 * @param popupCopyEvent
	 *            event generated when copy action is invoked on a table row.
	 */
	private void popupCopyActionPerformed(ActionEvent popupCopyEvent) {
		StringSelection sel = null;
		int rowIndex = listOfTables.get(
				GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX).getSelectedRow();
		// copy locus
		if (popupCopyEvent.getActionCommand().equals(popupCopyLocus.getText())) {
			String temp_locus = listOfLocusArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[rowIndex]
					.toString();
			sel = new StringSelection(temp_locus);
		}
		// copy cytoband
		else if (popupCopyEvent.getActionCommand().equals(
				popupCopyCytoBand.getText())) {
			String temp_cytoBand = listOfCytoBandArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[rowIndex];
			sel = new StringSelection(temp_cytoBand);
		}
		// copy gene
		else if (popupCopyEvent.getActionCommand().equals(
				popupCopyGene.getText())) {
			String temp_gene = listOfGeneArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[rowIndex];
			sel = new StringSelection(temp_gene);
		}
		// copy omim
		else if (popupCopyEvent.getActionCommand().equals(
				popupCopyOmim.getText())) {
			String temp_omim = listOfOmimArray
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX)[rowIndex];
			sel = new StringSelection(temp_omim);
		}
		// unknown copy command
		else {
			logger.info("unknown copy command selected!");
			sel = new StringSelection("unknown copy selected!");
		}
		
		// finally copy contents to clipboard
		CLIPBOARD.setContents(sel, this);
	}

	/**
	 * Instructions on what to do when the clipboard loses ownership.
	 * 
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// do nothing
	}

	/**
	 * Invokes the class which handles the File Chooser window.
	 * 
	 * This method calls the File Chooser window, and is invoked when the user
	 * chooses to select the input file in the GUI.
	 */
	private void newFileActionPerformed() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				inputFileLocation = new OpenFileChooserDialog()
						.invokeFileChooser();
			}
		});
	}

	/**
	 * 
	 * The method calls the Save class, which brings up the Save File dialog
	 * box.
	 */
	private void saveFileActionPerformed() {
		GlobalParameters.IS_OUTPUT_TXT = true;
		GlobalParameters.IS_OUTPUT_XLS = false;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Save(listOfTables
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
	}

	/**
	 * export table as an excel sheet
	 */
	private void excelItemActionPerformed() {
		GlobalParameters.IS_OUTPUT_TXT = false;
		GlobalParameters.IS_OUTPUT_XLS = true;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Save(listOfTables
						.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX));
			}
		});
	}

	/**
	 * This method prints the table in the selected tab
	 */
	private void printActionPerformed() {
		try {
			JTable table = listOfTables
					.get(GlobalParameters.TABBED_PANE_ACTIVE_TAB_INDEX);
			if (!table.print()) {
				logger.info("User cancelled printing");
			}
		} catch (PrinterException printex) {
			logger.info("unable not print: printerexception");
		}
	}
	
	/**
	 * method to restart the application
	 */
	public void restartApplication(){
		// TODO
		JOptionPane.showConfirmDialog(GUI.this, "Are you sure you want to restart the application?", "Confirm restart", JOptionPane.OK_CANCEL_OPTION);
		GUI.this.dispose();
		new Start();
		Start.launchTool();
	}

	/**
	 * Method to abort the program when exit is called.
	 */
	private void exitActionPerformed() {
		System.exit(ABORT);

	}

	/**
	 * This method invokes the Compare Datasets class when the Compare option is
	 * selected in the menu bar.
	 */
	private void compareActionPerformed() {
		new CompareDatasets(listOfLocusArray, listOfFileNames);
	}

	/**
	 * Plots a histogram of the size distribution.
	 * 
	 * @param locusArray
	 */
	private void sizeActionPerformed(final Locus[] locusArray) {
		new DrawHistogram(m_stats.getHistogramDatasetForSizeDist(m_stats
				.getSizeDist(locusArray)), "Size distribution");
	}

	/**
	 * Plots a bar chart for the chromosome distribution.
	 * 
	 * @param locusArray
	 */
	private void chrBarChartActionPerformed(Locus[] locusArray) {
		new DrawBarChart(m_stats.getBarDatasetForChrDist(m_stats
				.getChrDistForBarChart(locusArray)), "Chromosome Distribution", "Chr Nr", "Frequency");
	}

	/**
	 * Plots a pie chart for the chromosome distribution.
	 * 
	 * @param locusArray
	 */
	private void chrPieChartActionPerformed(Locus[] locusArray) {

		new DrawPieChart(m_stats.getPieDatasetForChrDist(m_stats
				.getChrDistForPieChart(locusArray)), "Chromosome Distribution");

	}

	/**
	 * Plots a pie chart which shows how many loci lie within a known gene.
	 * 
	 * @param geneArray
	 * 
	 */
	private void genePieChartActionPerformed(String[] geneArray) {

		new DrawPieChart(m_stats.getPieDatasetForGeneDist(m_stats
				.getGeneDist(geneArray)), "How many are within known genes");

	}

	/**
	 * Plots a pie chart which shows how many loci have a accession number in
	 * omim.
	 * 
	 * This method calls the PieChart class.
	 * 
	 * @see gui.DrawPieChart
	 */
	private void omimPieChartActionPerformed(String[] omimArray) {
		new DrawPieChart(m_stats.getPieDatasetForOmim(m_stats
				.getOmimDist(omimArray)),
				"How many are/are-not associated with an OMIM accession number");

	}

	/**
	 * Checks whether tabbed pane is enabled or not.
	 * 
	 * @return boolean true/false
	 */
	public boolean isTabbedPaneEnabled() {
		if (tabbedPane.getTabCount() > 0)
			return true;
		else
			return false;
	}

	/**
	 * This method returns a list of all JTables.
	 * 
	 * Each dataset has its own JTable.
	 * 
	 * @return array list of JTable.
	 */
	public ArrayList<JTable> getListOfTables() {
		return listOfTables;
	}

	/**
	 * This method returns the name of the input file.
	 * 
	 * @return input file name
	 */
	public String getInputFileName() {
		return inputFileLocation;
	}
	
	/**
	 * This method returns an array, which contains an arraylist as
	 * each of its element. Each arraylist, in turn, holds arrays as its
	 * elements, where one array contains
	 * data retrieved from the database for one dataset.
	 * 
	 * @return arrayOfLists an array containing all lists
	 * 
	 */
	@SuppressWarnings("rawtypes")
	protected ArrayList[] getArrayOfLists(){
		return arrayOfLists;
	}

	/**
	 * Adds a dummy JLabel, to make the layout look better
	 * 
	 * @param panel
	 *            the panel to which gap is added
	 * @param n
	 *            the number of times gap is added
	 */
	private void addGapInLayout(JPanel panel, int n) {
		for (int i = 0; i < n; i++) {
			panel.add(new JLabel(), "wrap");
		}
	}

	// variable declaration
	private JPanel p;
	// private JTabbedPane tabbedPane = new JTabbedPane();
    private JTabbedPane tabbedPane = new ClosableTabbedPane(GUI.this);
	private JMenu fileMenu;
	private JMenu exportMenu;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu compareMenu;
	private JMenu plotMenu;
	private JMenu barChartMenu;
	private JMenu pieChartMenu;
	private JMenuBar menuBar;
	private JMenuItem newFile;
	private JMenuItem save;
	private JMenuItem excelItem;
	private JMenuItem print;
	private JMenuItem restart;
	private JMenuItem exit;
	private JMenuItem settings;
	private JMenuItem compare;
	private JMenuItem sizeChart;
	private JMenuItem chrBarChart;
	private JMenuItem chrPieChart;
	private JMenuItem genePieChart;
	private JMenuItem omimPieChart;
	private JCheckBoxMenuItem sizeCheckBox;
	private JCheckBoxMenuItem cytoBandCheckBox;
	private JCheckBoxMenuItem geneCheckBox;
	private JCheckBoxMenuItem repeatsNameCheckBox;
	private JCheckBoxMenuItem repeatsClassCheckBox;
	private JCheckBoxMenuItem repeatsFamilyCheckBox;
	private JCheckBoxMenuItem omimCheckBox;
	private JPopupMenu popupMenu;
	private JMenuItem popupNGenes;
	private JMenuItem popupUcsc;
	private JMenuItem popupGenecards;
	private JMenuItem popupOmim;
	private JMenuItem popupCopy;
	private JMenuItem popupCopyLocus;
	private JMenuItem popupCopyCytoBand;
	private JMenuItem popupCopyGene;
	private JMenuItem popupCopyOmim;
	private JLabel progressBarLabel;
	public JProgressBar progressBar;
	private String inputFileLocation;
	private String selectedFileName;
	private ArrayList<String> listOfFileNames;
	private ArrayList<Locus[]> listOfLocusArray;
	private ArrayList<String[]> listOfCytoBandArray;
	private ArrayList<String[]> listOfGeneArray;
	private ArrayList<String[]> listOfRepeatsNameArray;
	private ArrayList<String[]> listOfRepeatsClassArray;
	private ArrayList<String[]> listOfRepeatsFamilyArray;
	private ArrayList<String[]> listOfOmimArray;
	private ArrayList<JTable> listOfTables;
	@SuppressWarnings("rawtypes")
	private ArrayList[] arrayOfLists;
	private Statistics m_stats;
	private ResultsTable m_resultsTableObject;
	private Statement m_st;
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit()
			.getSystemClipboard();
	// end of variable declaration

	/**
	 * Inner-class of GUI class for FileChoosing Dialog.
	 * 
	 * This is an inner-class, which contains methods for popping up the File
	 * Chooser dialog when the user selects a New file.
	 * 
	 * @author mkumar
	 * 
	 */
	class OpenFileChooserDialog {

		/**
		 * Gets the file name of the user specified input file.
		 * 
		 * @return input file name
		 */
		public String invokeFileChooser() {
			fileChooser = new JFileChooser();
			fileChooser.setPreferredSize(new Dimension(500, 300));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pack();

			int openVal = fileChooser.showOpenDialog(GUI.this);
			logger.info("file choosing - OPEN window successfully launched");
			if (openVal == JFileChooser.APPROVE_OPTION) {
				selectedFileName = fileChooser.getSelectedFile().getName();
				directory = fileChooser.getCurrentDirectory().toString();
				inputFileLocation = directory + "//" + selectedFileName;
				displayProgressBar();
			}
			if (openVal == JFileChooser.CANCEL_OPTION) {
			}

			
			return inputFileLocation;
		}

		// Variables declaration
		private JFileChooser fileChooser;
		private String directory;
		// End of variables declaration
	}
}
