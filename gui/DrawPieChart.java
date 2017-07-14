package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 * Creates a jFreeChart pie chart from the dataset.
 * 
 * @author mkumar
 * @since v1.0
 */
public class DrawPieChart extends JFrame {
	private static final Logger logger = Logger
			.getLogger(DrawPieChart.class);
	private static final long serialVersionUID = 1L;
	private PieDataset m_dataset;
	private String m_title;
	private JFreeChart m_chart;
	private ChartPanel m_chartPanel;

	public DrawPieChart(PieDataset pieDataset, String chartTitle) {
		logger.info("inside pie chart");
		m_title = chartTitle;
		this.m_dataset = pieDataset;
		setTitle("Pie Chart");
		m_chart = getChart();
		setMenuBar();
		setLayoutOfComponents();
	}
	
	/**
	 * generates a jfreechart pie chart.
	 * 
	 * @return pie chart.
	 */
	private JFreeChart getChart() {

		JFreeChart chart = ChartFactory.createPieChart(m_title, m_dataset,
				true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0} = {2} ({1})", new DecimalFormat("0"), new DecimalFormat(
						"0.00%")));
		return chart;

	}

	/**
	 * sets the menu bar for the frame containing pie chart.
	 */
	private void setMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Save(m_chart);
			}
		});

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DrawPieChart.this.dispose();
			}
		});

		file.add(save);
		file.add(exit);
		menuBar.add(file);
		setJMenuBar(menuBar);
	}

	/**
	 * sets the layout of the components on the jframe.
	 */
	private void setLayoutOfComponents() {
		m_chartPanel = new ChartPanel(m_chart);
		m_chartPanel.setPreferredSize(new Dimension(800, 400));
		setContentPane(m_chartPanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
