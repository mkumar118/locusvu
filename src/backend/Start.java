package backend;

import gui.GUI;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class contains the main method, the entry point for the tool. Start here.
 * @author mkumar
 * @version 1.0
 * @since v1.0
 */
public class Start {
	private static final Logger logger = Logger.getLogger(Start.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure("./log/log4j.properties");
		logger.debug("**** start: "
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date()) + " ****");
		launchTool();
	}
	
	/**
	 * method to launch the tool
	 */
	public static void launchTool(){
		try {
			for (LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			logger.debug("error in launching tool" + ex.getMessage());
		} catch (InstantiationException ex) {
			logger.debug("error in launching tool" + ex.getMessage());
		} catch (IllegalAccessException ex) {
			logger.debug("error in launching tool" + ex.getMessage());
		} catch (UnsupportedLookAndFeelException ex) {
			logger.debug("error in launching tool" + ex.getMessage());
		}
		
		/* Create and display the form */
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				logger.info("LocusVu launched successfully");
				new GUI();
			}
			
		});
	}
	
}