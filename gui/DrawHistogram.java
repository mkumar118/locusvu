package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * This class creates a plot of type Histogram, used to create
 * the size distribution chart for all loci.
 * @author mkumar
 * @since v1.0
 */
public class DrawHistogram extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor, creates the GUI frame, puts chart on it, adds buttons
	 * @param histogramDatasetForSizeArray dataset for histogram
	 * @param title title of the window
	 */
	public DrawHistogram(IntervalXYDataset histogramDatasetForSizeArray, String title) {
		super(title);
		IntervalXYDataset dataset = histogramDatasetForSizeArray;
		final JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
		setMinimumSize(new Dimension(500,350));
		setPreferredSize(new Dimension(500,350));
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		// save menu item
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Save(chart);
			}
		});
		
		// exit menu item
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DrawHistogram.this.dispose();
			}
		});
		
		// layout of components
		fileMenu.add(saveMenuItem);
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		setVisible(true);
	}

	/**
	 * creates the histogram chart
	 * @param dataset dataset for size distribution
	 * @return histogram chart
	 */
	private JFreeChart createChart(IntervalXYDataset dataset) {
		JFreeChart chart = ChartFactory.createHistogram(
				"Distribution of sizes of loci", null, null, dataset,
				PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setForegroundAlpha(0.75f);
		chart.removeLegend();
		return chart;
	}
}