package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Generates a jFreeChart bar chart from a dataset.
 * @author mkumar
 * @since v1.0
 */
public class DrawBarChart extends JFrame {
	private static final Logger logger = Logger
			.getLogger(DrawBarChart.class);
	private static final long serialVersionUID = 1L;
	private JFreeChart m_chart;
	private ChartPanel m_chartPanel;
	private DefaultCategoryDataset m_dataset;
	private String m_appTitle;
	private String m_xAxisLabel;
	private String m_yAxisLabel;

	public DrawBarChart(DefaultCategoryDataset dataset, String appTitle, String xAxisLabel, String yAxisLabel) {
		logger.debug("inside barchart");
		this.m_dataset = dataset;
		this.m_appTitle = appTitle;
		this.m_xAxisLabel = xAxisLabel;
		this.m_yAxisLabel = yAxisLabel;
		
		setTitle("Bar Chart");
		m_chart = getChart();		
		
		CategoryPlot catplot = m_chart.getCategoryPlot();
		NumberAxis rangeAxis = (NumberAxis)catplot.getRangeAxis();
		rangeAxis.setTickUnit(new NumberTickUnit(1.0));
		
		setMenuBar();
		setLayoutOfComponents();
	}
	
	/**
	 * sets the menu bar in the bar chart window.
	 */
	private void setMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Save(m_chart);
			}
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DrawBarChart.this.dispose();
			}
		});
		
		fileMenu.add(save);
		fileMenu.add(exit);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}
	
	/**
	 * sets the layout of components in the jframe for a bar chart.
	 */
	private void setLayoutOfComponents(){
		m_chartPanel = new ChartPanel(m_chart);
		m_chartPanel.setPreferredSize(new Dimension(630, 280));
		setContentPane(m_chartPanel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * creates the bar chart with the jfree chart library.
	 * @return bar chart
	 */
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createBarChart(m_appTitle, m_xAxisLabel, m_yAxisLabel, m_dataset, PlotOrientation.VERTICAL,
				false, true, false);
		chart.setBackgroundPaint(Color.white);
		chart.getTitle().setPaint(Color.blue);
		CategoryPlot p = chart.getCategoryPlot();
		p.setRangeGridlinePaint(Color.red);
		
		
		return chart;
	}
}