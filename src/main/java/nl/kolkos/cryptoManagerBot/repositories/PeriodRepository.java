package nl.kolkos.cryptoManagerBot.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import nl.kolkos.cryptoManagerBot.controllers.MySQLController;
import nl.kolkos.cryptoManagerBot.objects.Period;

public class PeriodRepository {
	public PeriodRepository() {
		try {
			this.registerPeriods();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createPeriodTable() throws Exception {
		String query = "CREATE TABLE IF NOT EXISTS period ("
				+ 	"id bigint(11) NOT NULL AUTO_INCREMENT,"
				+ 	"value varchar(255) NOT NULL,"
				+ 	"text varchar(255) NOT NULL,"
				+ 	"PRIMARY KEY (id)"
				+ ");";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private List<Period> createPeriods() {
		List<Period> periods = new ArrayList<>();
		periods.add(new Period("1h", "Last hour"));
		periods.add(new Period("5h", "Last 5 hours"));
		periods.add(new Period("12h", "Last 12 hour"));
		periods.add(new Period("1d", "Last day"));
		periods.add(new Period("1w", "Last week"));
		periods.add(new Period("1y", "Last year"));
		
		return periods;
	}
	
	private void emptyPeriodTable() throws Exception {
		String query = "TRUNCATE TABLE period;";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
	}
	
	private void savePeriod(Period period) throws Exception {
		String query = "INSERT INTO period (value, text) VALUES (?, ?)";
		Object[] parameters = new Object[] {
				period.getValue(),
				period.getText()
		};
		MySQLController mysql = new MySQLController();
		mysql.executeUpdateQuery(query, parameters);
		mysql.close();
		
	}
	
	private void registerPeriods() throws Exception {
		this.createPeriodTable();
		this.emptyPeriodTable();
		
		List<Period> periods = this.createPeriods();
		for(Period period : periods) {
			this.savePeriod(period);
		}
	}
	
	public List<Period> getPeriods() throws Exception{
		List<Period> periods = new ArrayList<>();
		
		String query = "SELECT * FROM period";
		Object[] parameters = new Object[] {};
		
		MySQLController mysql = new MySQLController();
		mysql.executeSelectQuery(query, parameters);
		ResultSet resultSet = mysql.getResultSet();
		
		while(resultSet.next()) {
			Period period = new Period();
			period.setText(resultSet.getString("text"));
			period.setValue(resultSet.getString("value"));
			
			periods.add(period);
			
		}
		
		return periods;
	}
	
	
}
