package backend;

import gui.GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * This class connects to the remote database via the mysql interface.
 * 
 * @author mkumar
 * @since v1.0
 */
public class Database {
	private static final Logger logger = Logger.getLogger(Database.class);

	/**
	 * Establishes connection with the database.
	 * 
	 * @param gui
	 *            object of the class GUI.
	 * @param con
	 *            object of class Connection.
	 * @return statement following successful connection with the database.
	 */
	public Statement establishConnection(final GUI gui, Connection con) {
		Statement st = null;
		String hostname = "genome-mysql.cse.ucsc.edu";
		String database = null;
		String user = "genome";
		String password = "";
		String url = null;

		if (GlobalParameters.IS_HG18_ON_UCSC) {
			database = "hg18";
		} else if (GlobalParameters.IS_HG19_ON_UCSC) {
			database = "hg19";
		}

		url = "jdbc:mysql://" + hostname + "/" + database + "?user=" + user
				+ "&password=" + password;

		try {
			con = DriverManager.getConnection(url);
			logger.info("Connection established to database " + database);
			st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (final SQLException ex) {
			logger.error("Cannot establish connection to database " + database);
			JOptionPane.showMessageDialog(gui,
					"Unable to connect to remote database", "Error",
					JOptionPane.ERROR_MESSAGE);
			// restart application
			gui.restartApplication();
		}
		return st;
	}

	/**
	 * Method to safely close connection with the database
	 * 
	 * @param con
	 *            object of class Connection.
	 * @param st
	 *            statement object, following database connection.
	 */
	public void closeConnection(Connection con, Statement st) {
		try {
			logger.info("Connection to database closed");
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}

		} catch (SQLException ex) {
			logger.debug("Cannot close connection to database");
		}
	}

}
