package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import backend.GlobalParameters;

/**
 * This class contains methods that manage global settings for the tool, eg.
 * login preference, and tracks to query etc.
 * 
 * @author mkumar
 * 
 */
public class Settings extends JFrame implements ListSelectionListener {
	private static final Logger logger = Logger.getLogger(Settings.class);
	private static final long serialVersionUID = 1L;

	public Settings() {
		super();
		logger.info("inside settings");
		initComponents();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Initialize all components
	 */
	private void initComponents() {
		setTitle("Settings");

		setLoginPanel();
		setFindPanel();
		setUpdateOmimPanel();
		setLayoutOfComponents();
	}

	/**
	 * Sets the various items in the Login Panel in the settings menu
	 */
	private void setLoginPanel() {

		assemblyDropDown = new JComboBox(assemblyOptions);
		assemblyDropDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				assemblyActionPerformed(evt);
			}
		});
		setLayoutOfLoginPanel();
	}

	/**
	 * sets the layout of the login panel in the settings menu
	 */
	private void setLayoutOfLoginPanel() {

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Settings.this.dispose();
			}
		});
		loginPanel.setLayout(new MigLayout());
		loginPanel.add(new JLabel("Please select your login preference"),
				"wrap");
		addGap(loginPanel, 5);
		loginPanel.add(new JLabel("Host:"), "gap para, split 2");
		loginPanel.add(new JLabel("UCSC"), "wrap");
		loginPanel.add(new JLabel("Species:"), "gap para, split 2");
		loginPanel.add(new JLabel("Human"), "wrap");
		loginPanel.add(new JLabel("Assembly:"), "gap para, split 2");
		loginPanel.add(assemblyDropDown, "growx, wrap");
		addGap(loginPanel, 23);
		loginPanel.add(closeButton, "bottom, left");

	}

	/**
	 * set the Find panel
	 */
	private void setFindPanel() {
		getOmimLabel = new JLabel("Get OMIM data from locally stored files");
		createCheckBoxesInFindPanel();
		setLayoutOfFindPanel();
	}

	/**
	 * creates check boxes in find panel
	 */
	private void createCheckBoxesInFindPanel() {
		cytoBandCheckBox = new JCheckBox();
		geneCheckBox = new JCheckBox();
		repeatsCheckBox = new JCheckBox();
		omimCheckBox = new JCheckBox();

		cytoBandCheckBox.setText(GlobalParameters.STR_CYTOBAND);
		if (GlobalParameters.FIND_CYTOBAND)
			cytoBandCheckBox.setSelected(true);
		else
			cytoBandCheckBox.setSelected(false);
		cytoBandCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxInFindActionPerformed(arg0);
			}
		});

		geneCheckBox.setText(GlobalParameters.STR_GENE);
		if (GlobalParameters.FIND_GENE)
			geneCheckBox.setSelected(true);
		else
			geneCheckBox.setSelected(false);
		geneCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxInFindActionPerformed(arg0);
			}
		});

		repeatsCheckBox.setText(GlobalParameters.STR_REPEATS);
		if (GlobalParameters.FIND_REPEATS)
			repeatsCheckBox.setSelected(true);
		else
			repeatsCheckBox.setSelected(false);
		repeatsCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxInFindActionPerformed(arg0);
			}
		});

		omimCheckBox.setText(GlobalParameters.STR_OMIM);
		if (GlobalParameters.FIND_OMIM)
			omimCheckBox.setSelected(true);
		else
			omimCheckBox.setSelected(false);
		omimCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxInFindActionPerformed(arg0);
			}
		});
	}

	/**
	 * sets layout of components in find panel
	 */
	private void setLayoutOfFindPanel() {

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Settings.this.dispose();
			}
		});

		findPanel.setLayout(new MigLayout());
		findPanel.add(new JLabel("Please select the tracks you wish to query"),
				"wrap");
		addGap(findPanel, 5);
		findPanel.add(cytoBandCheckBox, "gap para, wrap");
		findPanel.add(geneCheckBox, "gap para, wrap");
		findPanel.add(repeatsCheckBox, "gap para, wrap");
		addGap(findPanel, 8);
		findPanel.add(getOmimLabel, "gap para, wrap");
		findPanel.add(omimCheckBox, "gap para, wrap");
		addGap(findPanel, 8);
		findPanel.add(closeButton, "bottom, left");
	}

	/**
	 * this method sets the update-OMIM panel.
	 */
	private void setUpdateOmimPanel() {

		mim2geneTextField = new JTextField(20);

		browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				browseButtonActionPerformed();
			}
		});

		updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateButtonActionPerformed();
			}
		});

		// set layout
		setLayoutOfUpdateOmimPanel();
	}

	/**
	 * sets the layout of the update-OMIM panel
	 */
	private void setLayoutOfUpdateOmimPanel() {

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Settings.this.dispose();
			}
		});

		updateOmimPanel.setLayout(new MigLayout());
		updateOmimPanel.add(new JLabel("Update OMIM"), "wrap");
		updateOmimPanel.add(new JLabel(
				"To update your locally stored OMIM object file to the"),
				"wrap");
		updateOmimPanel.add(new JLabel(
				"latest version or to create it for the first time,"), "wrap");
		updateOmimPanel.add(new JLabel(
				"please download the mim2gene.txt file from"), "wrap");
		updateOmimPanel.add(new JLabel(
				"http://omim.org/downloads followed by clicking on"), "wrap");
		updateOmimPanel.add(new JLabel(
				"the Browse button and pointing to its location."), "wrap");
		updateOmimPanel.add(new JLabel(
				"Finally, click the Update button to update."), "wrap");
		updateOmimPanel.add(new JLabel("(See the User Manual for details)"),
				"wrap");
		addGap(updateOmimPanel, 2);
		updateOmimPanel.add(new JLabel("File:"), "split 3");
		updateOmimPanel.add(mim2geneTextField);
		updateOmimPanel.add(browseButton, "wrap");
		updateOmimPanel.add(updateButton, "wrap");
		addGap(updateOmimPanel, 3);
		updateOmimPanel.add(closeButton);
	}

	/**
	 * browse button action performed, opens file chooser dialog
	 */
	private void browseButtonActionPerformed() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mim2geneFileLocation = new Mim2GeneFileChoosing()
						.getMim2GeneFileLocation();
			}
		});
	}

	/**
	 * action performed on the update button, calls the updateOMIMObject method
	 */
	private void updateButtonActionPerformed() {
		updateOMIMObject();
	}

	/**
	 * updates the OMIM object given the mim2gene.txt file
	 * 
	 * @param mim2geneFileLocation
	 *            location of the mim2gene.txt file on the disk
	 */
	private void updateOMIMObject() {
		Map<String, String> omimMap = new HashMap<String, String>();
		// open file
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					mim2geneFileLocation));
			// read file, line-by-line
			String line = null;
			while ((line = br.readLine()) != null) {
				// remove whitespaces at the ends
				line = line.trim();
				// split line by separator, assign to array
				String[] column = line.split("\t");
				// 1st element is mim number
				String mimNr = column[0];
				// 4th element is approved gene symbol
				String gene = column[3];
				// check whether gene symbol is already in map or not
				// if yes
				if (omimMap.containsKey(gene)) {
					// fetch corresponding value for this gene(key),
					// and add to existing value
					String oldMimNr = omimMap.get(gene);
					mimNr = mimNr + "," + oldMimNr;
				}
				// if not, do nothing

				// put in map
				omimMap.put(gene, mimNr);
			}
			// create serialized object for this map
			createSerializedObject(omimMap);
		} catch (Exception exception) {
			logger.error("error in updating OMIM object");
			JOptionPane
					.showMessageDialog(
							Settings.this,
							"OMIM object not updated. Please make sure you specified the correct path to mim2gene.txt",
							"Failure", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * creates a serialized object
	 */
	private void createSerializedObject(Map<String, String> omimMap) {
		try {
			// save gene : omim accession numbers map
			ObjectOutputStream out_accNrMap = new ObjectOutputStream(
					new FileOutputStream(GlobalParameters.PATH_OMIM_ACC_NR_MAP));
			out_accNrMap.writeObject(omimMap);

			JOptionPane.showMessageDialog(Settings.this, "Update successful",
					"Success", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception exception) {
			logger.error("IOException on object creation");
			JOptionPane
					.showMessageDialog(
							Settings.this,
							"OMIM object not updated. Please make sure you specified the correct path to mim2gene.txt",
							"Failure", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Adds a blank row (gap) to the layout to make it look nice
	 * 
	 * @param p
	 *            panel to which gap must be added
	 * @param n
	 *            number of gaps
	 */
	private void addGap(JPanel p, int n) {
		for (int i = 0; i < n; i++) {
			p.add(new JLabel(), "wrap");
		}
	}

	/**
	 * sets the layout of components in the settings panel.
	 */
	private void setLayoutOfComponents() {
		Display login = new Display(m_strLogin, loginPanel);
		Display find = new Display(m_strFind, findPanel);
		Display updateOmim = new Display(m_strupdateOmim, updateOmimPanel);
		Display[] displays = { login, find, updateOmim };

		listLeft = new JList(displays);
		listLeft.setSelectedIndex(0);
		leftScrollPane = new JScrollPane();
		leftScrollPane.getViewport().add(listLeft);
		rightScrollPane = new JScrollPane();
		rightScrollPane.getViewport().add(loginPanel);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				leftScrollPane, rightScrollPane);
		listLeft.addListSelectionListener(this);

		splitPane.setDividerLocation(150);
		add(splitPane);
		setPreferredSize(new Dimension(550, 350));
		pack();
	}

	/**
	 * contains instructions to display corresponding right scroll pane when
	 * item in left pane is selected
	 * 
	 * @param listEvent
	 *            event generated in list in left scroll pane
	 */
	@Override
	public void valueChanged(ListSelectionEvent listEvent) {
		JList list = (JList) listEvent.getSource();
		Display displayValue = (Display) list.getSelectedValue();
		rightScrollPane.getViewport().removeAll();
		rightScrollPane.getViewport().add(displayValue.getPanel());

	}

	/**
	 * Action performed when check boxes in Find panel are edited.
	 * 
	 * @param checkBoxItemEvent
	 *            event generated on check box
	 */
	private void checkBoxInFindActionPerformed(ItemEvent checkBoxItemEvent) {
		if (checkBoxItemEvent.getItemSelectable() == cytoBandCheckBox) {
			if (GlobalParameters.FIND_CYTOBAND) {
				GlobalParameters.FIND_CYTOBAND = false;
			} else {
				GlobalParameters.FIND_CYTOBAND = true;
			}
		} else if (checkBoxItemEvent.getItemSelectable() == geneCheckBox) {
			if (GlobalParameters.FIND_GENE) {
				GlobalParameters.FIND_GENE = false;
			} else {
				GlobalParameters.FIND_GENE = true;
			}
		} else if (checkBoxItemEvent.getItemSelectable() == repeatsCheckBox) {
			if (GlobalParameters.FIND_REPEATS) {
				GlobalParameters.FIND_REPEATS = false;
			} else {
				GlobalParameters.FIND_REPEATS = true;
			}
		} else if (checkBoxItemEvent.getItemSelectable() == omimCheckBox) {
			if (GlobalParameters.FIND_OMIM) {
				GlobalParameters.FIND_OMIM = false;
			} else {
				GlobalParameters.FIND_OMIM = true;
			}
		}
	}

	/**
	 * Action performed when assembly drop down menu is edited
	 * 
	 * @param assemblyEvent
	 */
	private void assemblyActionPerformed(ActionEvent assemblyEvent) {
		JComboBox cb = (JComboBox) assemblyEvent.getSource();
		String assembly = (String) cb.getSelectedItem();

		if (assembly.equals(assemblyOptions[0])) {
			GlobalParameters.IS_HG18_ON_UCSC = true;
			GlobalParameters.IS_HG19_ON_UCSC = false;
		} else if (assembly.equals(assemblyOptions[1])) {
			GlobalParameters.IS_HG18_ON_UCSC = false;
			GlobalParameters.IS_HG19_ON_UCSC = true;
		}
	}

	// variable declaration
	private JSplitPane splitPane;
	private JScrollPane leftScrollPane;
	private JScrollPane rightScrollPane;
	private JPanel loginPanel = new JPanel();
	private JPanel findPanel = new JPanel();
	private JPanel updateOmimPanel = new JPanel();
	private JList listLeft;
	private String m_strLogin = "Login";
	private String m_strFind = "Tracks";
	private String m_strupdateOmim = "Update OMIM";
	private JComboBox assemblyDropDown;
	private String[] assemblyOptions = { "hg18", "hg19" };
	private JCheckBox cytoBandCheckBox;
	private JCheckBox geneCheckBox;
	private JCheckBox repeatsCheckBox;
	private JLabel getOmimLabel;
	private JCheckBox omimCheckBox;
	private String mim2geneFileLocation;
	private JTextField mim2geneTextField;
	private JButton browseButton;
	private JButton updateButton;

	// end of variable declaration

	/**
	 * Inner-class for Display of settings panel, contains left and right
	 * scrollpanes.
	 * 
	 * @author mkumar
	 * 
	 */
	class Display {
		public Display(String n, JPanel p) {
			name = n;
			panel = p;
		}

		@Override
		public String toString() {
			return name;
		}

		public JPanel getPanel() {
			return panel;
		}

		String name;
		JPanel panel;
	}

	/**
	 * Inner-class of Settings class for FileChoosing Dialog.
	 * 
	 * This is an inner-class, which contains methods for popping up the File
	 * Chooser dialog and getting the location of the mim2gene.txt file on the
	 * disk.
	 * 
	 * @author mkumar
	 * 
	 */
	class Mim2GeneFileChoosing {

		/**
		 * Get the location of the mim2gene.txt file on the disk
		 * 
		 * @return location of the mim2gene file
		 */
		public String getMim2GeneFileLocation() {

			fileChooser = new JFileChooser();
			fileChooser.setPreferredSize(new Dimension(500, 300));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			int openVal = fileChooser.showOpenDialog(Settings.this);
			logger.info("file choosing - OPEN window successfully launched");
			if (openVal == JFileChooser.APPROVE_OPTION) {
				String selectedFileName = fileChooser.getSelectedFile()
						.getName();
				directory = fileChooser.getCurrentDirectory().toString();
				mim2geneFileLocation = directory + "/" + selectedFileName;
				mim2geneTextField.setText(mim2geneFileLocation);
			}
			if (openVal == JFileChooser.CANCEL_OPTION) {
			}

			pack();
			return mim2geneFileLocation;
		}

		// Variables declaration
		private JFileChooser fileChooser;
		private String directory;
		// End of variables declaration
	}

}
