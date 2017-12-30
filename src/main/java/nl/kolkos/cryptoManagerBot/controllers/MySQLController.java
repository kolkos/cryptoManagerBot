package nl.kolkos.cryptoManagerBot.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.kolkos.cryptoManagerBot.services.ConfigService;

public class MySQLController {
	private static final Logger LOG = LogManager.getLogger(MySQLController.class);
	private String mysqlConnectUrl;
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	
	
	public String getMysqlConnectUrl() {
		return mysqlConnectUrl;
	}

	public void setMysqlConnectUrl(String mysqlConnectUrl) {
		this.mysqlConnectUrl = mysqlConnectUrl;
	}

	/**
	 * This method gets the database configuration from the config file
	 * @throws IOException 
	 */
	private void loadDatabaseConfig() throws IOException {
		LOG.trace("Load database configuration");
		
		ConfigService configService = new ConfigService();
		Properties properties = configService.loadConfig();
		
		String host = properties.getProperty("mysql.host");
		String port = properties.getProperty("mysql.port");
		String database = properties.getProperty("mysql.database");
		String user = properties.getProperty("mysql.user");
		String pass = properties.getProperty("mysql.pass");
		
		// now generate the url
		//String url = "jdbc:mysql://192.168.178.7/" + db + "?user=" + user + "&password=" + pass;
		String url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", host, port, database, user, pass);
		this.setMysqlConnectUrl(url);
		
		LOG.trace("Finished loading database configuration");		
	}
	
	/**
	 * Connect to the database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 */
	public void connectDatabase() throws SQLException, ClassNotFoundException, IOException {
		LOG.trace("Entering connectDatabase()");
		
		this.loadDatabaseConfig();
		
		Class.forName("com.mysql.jdbc.Driver");
		
		String url = this.getMysqlConnectUrl();
		
		LOG.trace("mysql url: {}", url);
		
		this.connect = DriverManager.getConnection(url);
		LOG.trace("Finished connectDatabase()");
	}
	
	/**
	 * Method to run a select query. This method runs prepared statements. The results are set to the resultSet variable
	 * @param query the SQL query to run
	 * @param parameters options for the prepared statement
	 * @throws Exception
	 */
	public void executeSelectQuery(String query, Object[] parameters) throws Exception {
		LOG.trace("Entering executeSelectQuery()");
		
		// String query, String[] parameters
		this.connectDatabase();
		
		try {
			LOG.trace("Executing prepared statement: {}", query);
			LOG.trace("Prepared parameters: {}", parameters);
			
			preparedStatement = connect.prepareStatement(query);
			
			int i = 1;
			for(Object x : parameters) {
				//System.out.println(x.toString());
				//preparedStatement.setString(i, x.toString());
				LOG.trace("Setting parameter {} => {}", i, x);
				preparedStatement.setObject(i, x);
				i++;
			}
			
			resultSet = preparedStatement.executeQuery();
			
			LOG.info("Query OK: ({})", query);
		} catch (Exception e) {
			LOG.fatal("Error executing query: '{}'", query);
    			LOG.fatal("Used parameters: '{}'", parameters);
			throw e;
        }
		LOG.trace("Finished executeSelectQuery()");
	}
	
	/**
	 * Method to execute a update/insert/delete statement to the database
	 * @param query prepared query to run
	 * @param parameters values for the prepared query
	 * @throws Exception
	 */
	public void executeUpdateQuery(String query, Object[] parameters) throws Exception {
		LOG.trace("Entering executeUpdateQuery()");
		
		//connect
		this.connectDatabase();
		
		try {
			LOG.trace("Executing prepared statement: {}", query);
			LOG.trace("Prepared parameters: {}", parameters);
			
			preparedStatement = connect.prepareStatement(query);
			
			int i = 1;
			for(Object x : parameters) {
				//System.out.println(x.toString());
				LOG.info("Setting parameter {} => {}", i, x);
				preparedStatement.setString(i, x.toString());
				i++;
			}
			
			// just execute
			preparedStatement.executeUpdate();
			LOG.info("Query OK: ({})", query);
		} catch (Exception e) {
        		LOG.fatal("Error executing query: '{}'", query);
        		LOG.fatal("Used parameters: '{}'", parameters);
			throw e;
        }
		LOG.trace("Finished executeUpdateQuery()");
	}
	
	/**
	 * Method to return the current resultset
	 * @return resultset with (or without) values of the selecct query
	 */
	public ResultSet getResultSet() {
		return this.resultSet;
	}

	/**
	 * Close the current call
	 */
	public void close() {
		LOG.trace("Entering close()");
		try {
			if (resultSet != null) {
				LOG.trace("Closing resultSet");
				resultSet.close();
			}

			if (statement != null) {
				LOG.trace("Closing statement");
				statement.close();
			}

			if (connect != null) {
				LOG.trace("Closing connection");
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.fatal("Error closing: {}", e);
		}
		LOG.trace("Entering close()");
	}
	
}
