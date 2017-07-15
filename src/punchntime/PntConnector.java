package punchntime;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class PntConnector {
	public PntConnector()
	{
		Properties prop = new Properties();
		FileInputStream input = null;
		try {

			input = new FileInputStream("config.ini");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			dbhost = prop.getProperty("dbhost");
			dbname = prop.getProperty("dbname");
			dbuser = prop.getProperty("dbuser");
			dbpass = prop.getProperty("dbpass");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
	}
	
	public Connection pntConnect()
	{
		Connection connection = null;
		try {
			System.out.println("jdbc:mysql://"+dbhost+"/"+dbname);
			connection = DriverManager
			.getConnection("jdbc:mysql://"+dbhost+"/"+dbname,dbuser , dbpass);

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		
		return connection;
	}

	private String dbhost, dbname, dbuser, dbpass;
}
