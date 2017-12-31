package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.Command;

public class CommandRepository {
	private void createCommandTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS command ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ 	"chat_id bigint(11) NOT NULL,"
				+ 	"username varchar(255),"
				+ 	"command varchar(255),"
				+ 	"handled int(1) DEFAULT 0,"
				+ 	"timestamp_handled DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public void saveCommand(Command command) throws Exception {
		this.createCommandTable();
		String query = "INSERT INTO command (chat_id, username, command) VALUES (?, ?, ?);";
		Object[] parameters = new Object[] {
				command.getChatId(),
				command.getUserName(),
				command.getCommand()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public void updateCommand(Command command) throws Exception {
		this.createCommandTable();
		String query = "UPDATE command SET handled = ? WHERE id = ?;";
		Object[] parameters = new Object[] {
				command.getCommandHandled(),
				command.getId()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	public List<Command> getUnhandledCommands() throws Exception{
		List<Command> unhandledCommands = new ArrayList<>();
		
		String query = "SELECT * FROM command WHERE handled = 0;";
		Object[] parameters = new Object[] {};
		MySQLController mysql = new MySQLController();
		
		mysql.executeSelectQuery(query, parameters);
		ResultSet resultSet = mysql.getResultSet();
		while(resultSet.next()) {
			// create a new command object
			Command command = new Command();
			command.setId(resultSet.getLong("id"));
			command.setChatId(resultSet.getLong("chat_id"));
			command.setCommand(resultSet.getString("command"));
			
			// add to the list
			unhandledCommands.add(command);
		}
	
		mysql.close();
		return unhandledCommands;
	}
	
	
}
