package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import backend.GlobalParameters;

/**
 * This class contains methods to create a GUI window which lets the user save
 * the results to a file on the disk.
 * 
 * @author mkumar
 * @since v1.0
 */
public class Save extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Save.class);

	/**
	 * Constructor, accepts JTable as argument
	 * 
	 * @param table
	 *            the table to be saved
	 */
	public Save(JTable table) {
		this.m_table = table;
		this.m_typeOfFile = "file";
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
	}

	/**
	 * Constructor, accepts JFreeChart as argument
	 * 
	 * @param chart
	 *            the chart to be saved
	 */
	public Save(JFreeChart chart) {
		this.m_chart = chart;
		this.m_typeOfFile = "chart";
		setLocationRelativeTo(null);
		initComponents();
		setVisible(true);
	}

	/**
	 * Initialize components
	 */
	private void initComponents() {

		setTitle("Save");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLabelsAndTextFields();
		setButtons();
		setLayoutOfComponents();
	}

	/**
	 * sets the Labels and Text fields on the UI
	 */
	private void setLabelsAndTextFields() {
		saveToDirLabel = new JLabel("Directory");
		fileNameLabel = new JLabel("File Name");
		directoryTextField = new JTextField();
		fileNameTextField = new JTextField();

		directoryTextField.setText(GlobalParameters.PATH_DEFAULT_OUTPUT_DIR);
		directoryTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fvt) {
				// do nothing
			}

			public void focusLost(FocusEvent fvt) {
				directoryTextFieldActionPerformed();
			}
		});

		if (m_typeOfFile == "file" && GlobalParameters.IS_OUTPUT_TXT) {
			fileNameTextField.setText(GlobalParameters.DEFAULT_OUTPUT_FILENAME
					+ ".txt");
		} else if (m_typeOfFile == "file" && GlobalParameters.IS_OUTPUT_XLS) {
			fileNameTextField.setText(GlobalParameters.DEFAULT_OUTPUT_FILENAME
					+ ".xls");
		} else if (m_typeOfFile == "chart") {
			fileNameTextField
					.setText(GlobalParameters.DEFAULT_OUTPUT_CHARTNAME);
		}
		fileNameTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fvt) {
				// do nothing
			}

			public void focusLost(FocusEvent fvt) {
				fileNameTextFieldActionPerformed();
			}
		});
	}

	/**
	 * sets the buttons on the UI
	 */
	private void setButtons() {
		browseButton = new JButton("Browse..");
		cancelButton = new JButton("Cancel");
		saveButton = new JButton("Save");

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				browseButtonActionPerformed();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed();
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveButtonActionPerformed();
			}
		});
	}

	/**
	 * sets the layout of all components on the UI
	 */
	private void setLayoutOfComponents() {
		setLayout(new MigLayout());
		directoryTextField.setColumns(50);
		fileNameTextField.setColumns(10);
		add(saveToDirLabel, "split 2");
		add(directoryTextField, "growx");
		add(browseButton, "wrap");
		add(fileNameLabel, "split 2");
		add(fileNameTextField, "wrap, growx");
		add(saveButton, "split 2");
		add(cancelButton, "wrap");
		setPreferredSize(new Dimension(350, 150));
		pack();
	}

	/**
	 * Action performed on the directory field, Output specified by user is
	 * passed to variable
	 */
	private void directoryTextFieldActionPerformed() {
		outputDir = directoryTextField.getText();
	}

	/**
	 * Action performed on the file name text field, file name given by user is
	 * passed to variable
	 */
	private void fileNameTextFieldActionPerformed() {
		outputFileName = fileNameTextField.getText();
	}

	/**
	 * Action performed on the browse button, File Dialog Inner-class
	 * opens up
	 */
	private void browseButtonActionPerformed() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SaveFileChooserDialog();
			}
		});
	}

	/**
	 * Action performed on the cancel button, window is disposed off
	 * 
	 */
	private void cancelButtonActionPerformed() {
		this.dispose();
	}

	/**
	 * Action performed on the save event, writeToFile method is called which
	 * contains instructions to save results to an output file
	 * 
	 */
	private void saveButtonActionPerformed() {
		if (outputDir == null) {
			outputDir = GlobalParameters.PATH_DEFAULT_OUTPUT_DIR;
		}
		if (outputFileName == null) {
			if (m_typeOfFile == "file" && GlobalParameters.IS_OUTPUT_TXT) {
				outputFileName = GlobalParameters.DEFAULT_OUTPUT_FILENAME
						+ ".txt";
			} else if (m_typeOfFile == "file" && GlobalParameters.IS_OUTPUT_TXT) {
				outputFileName = GlobalParameters.DEFAULT_OUTPUT_FILENAME
						+ ".xls";
			} else if (m_typeOfFile == "chart") {
				outputFileName = GlobalParameters.DEFAULT_OUTPUT_CHARTNAME;
			}
		}
		outputLocation = outputDir + "/" + outputFileName;
		if (m_typeOfFile == "file") {
			writeToFile(m_table, outputLocation);
		} else if (m_typeOfFile == "chart") {
			try {
				ChartUtilities.saveChartAsPNG(new File(outputLocation),
						m_chart, 800, 400);
			} catch (IOException ex) {
				System.out.println("error in saving chart");
			}
		}

		this.dispose();
	}

	/**
	 * Save contents of table to an output file
	 * 
	 * @param table
	 *            results table
	 * @param file
	 *            -location
	 */
	public void writeToFile(JTable table, String outputFileLocation) {
		logger.info("writing to output file");

		String sep;
		String newline;

		// separator is tab
		sep = "\t";
		newline = "\n";

		TableModel model = table.getModel();
		// delete file if it already exists
		if (new File(outputFileLocation).exists()) {
			new File(outputFileLocation).delete();
		}
		// write to file
		try {
			File file = new File(outputFileLocation);
			FileWriter out = new FileWriter(file);

			// write column headers
			for (int i = 0; i < model.getColumnCount(); i++) {
				out.write(model.getColumnName(i) + sep);
			}
			out.write(newline);

			// write table contents
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 0; j < model.getColumnCount(); j++) {
					out.write(model.getValueAt(i, j).toString() + sep);
				}
				out.write(newline);
			}

			out.close();
			logger.info("output file successfully created");
		} catch (IOException ioe) {
			logger.error("cannot write to output file:" + ioe.getMessage());
		}
	}

	// Variables declaration - do not modify
	private JButton browseButton;
	private JButton cancelButton;
	private JButton saveButton;
	private JLabel saveToDirLabel;
	private JLabel fileNameLabel;
	private JTextField directoryTextField;
	private JTextField fileNameTextField;
	private String outputLocation;
	private String outputDir;
	private String outputFileName;
	private JTable m_table;
	private JFreeChart m_chart;
	private String m_typeOfFile;
	// End of variables declaration

	/**
	 * Inner class for File chooser dialog
	 * 
	 * @author mkumar
	 * 
	 */
	class SaveFileChooserDialog {

		/**
		 * Constructor
		 */
		public SaveFileChooserDialog() {
			fileChooser = new JFileChooser();
			fileChooser.setPreferredSize(new Dimension(470, 280));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			initComponents();
		}

		/**
		 * Initialize components
		 */
		private void initComponents() {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int saveVal = fileChooser.showSaveDialog(Save.this);
			if (saveVal == JFileChooser.APPROVE_OPTION) {
				nameOfOutputDir = fileChooser.getSelectedFile().getName();
				nameOfParentDir = fileChooser.getCurrentDirectory().toString();
				saveLocationSelected = nameOfParentDir + "/" + nameOfOutputDir;
				outputDir = saveLocationSelected;
				directoryTextField.setText(outputDir);
				logger.debug("Save as location selected: "
						+ saveLocationSelected);

			}
			if (saveVal == JFileChooser.CANCEL_OPTION) {
				logger.info("no output file chosen");
			}
			pack();
		}

		/**
		 * Get save location as specified by the user
		 * 
		 * @return save location
		 */
		public String getSaveAsLocation() {
			return saveLocationSelected;
		}

		// Variables declaration
		private JFileChooser fileChooser;
		private String nameOfOutputDir;
		private String nameOfParentDir;
		private String saveLocationSelected;
		// End of variables declaration
	}
}
