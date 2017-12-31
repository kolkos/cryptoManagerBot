package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.Interval;

public class IntervalRepository {
	
	public IntervalRepository() {
		try {
			this.registerIntervals();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createIntervalTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS `interval` ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"period varchar(255) NOT NULL,"
				+ 	"value varchar(255) NOT NULL,"
				+ 	"text varchar(255) NOT NULL,"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private void emptyIntervalTable() throws Exception {
		String query = "TRUNCATE TABLE `interval`;";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private List<Interval> createIntervals(){
		List<Interval> intervals = new ArrayList<>();
		
		intervals.add(new Interval("1h","1m","1 minute"));
		intervals.add(new Interval("1h","5m","5 minutes"));
		intervals.add(new Interval("1h","10m","10 minutes"));
		
		intervals.add(new Interval("5h","5m","5 minutes"));
		intervals.add(new Interval("5h","10m","10 minutes"));
		intervals.add(new Interval("5h","1h","1 hour"));
		
		intervals.add(new Interval("12h","5m","5 minutes"));
		intervals.add(new Interval("12h","10m","10 minutes"));
		intervals.add(new Interval("12h","1h","1 hour"));
		
		intervals.add(new Interval("1d","1h","1 hour"));
		intervals.add(new Interval("1d","12h","12 hours"));
		
		intervals.add(new Interval("1w","12h","12 hours"));
		intervals.add(new Interval("1w","1d","1 day"));
		
		intervals.add(new Interval("4w","12h","12 hours"));
		intervals.add(new Interval("4w","1d","1 day"));
		intervals.add(new Interval("4w","1w","1 week"));
		
		intervals.add(new Interval("1y","1w","1 week"));
		intervals.add(new Interval("1y","4w","4 weeks"));
		
		
		return intervals;
	}
	
	private void saveInterval(Interval interval) throws Exception {
		String query = "INSERT INTO `interval` (period, value, text) VALUES (?, ?, ?)";
		Object[] parameters = new Object[] {
				interval.getPeriod(),
				interval.getValue(),
				interval.getText()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
		
	}
	
	private void registerIntervals() throws Exception {
		this.createIntervalTable();
		this.emptyIntervalTable();
		
		List<Interval> intervals = this.createIntervals();
		for(Interval interval : intervals) {
			this.saveInterval(interval);
		}
	}
	
	public List<Interval> getIntervalsForPeriod(String period) throws Exception{
		List<Interval> intervals = new ArrayList<>();
		
		String query = "SELECT * FROM `interval` WHERE period = ?;";
		Object[] parameters = new Object[] {period};
		
		MySQLController mysql = new MySQLController();
		mysql.executeSelectQuery(query, parameters);
		ResultSet resultSet = mysql.getResultSet();
		
		while(resultSet.next()) {
			Interval interval = new Interval();
			interval.setPeriod(resultSet.getString("period"));
			interval.setValue(resultSet.getString("value"));
			interval.setText(resultSet.getString("text"));
			
			intervals.add(interval);
			
		}
		
		
		
		return intervals;
	}
	
	
}
