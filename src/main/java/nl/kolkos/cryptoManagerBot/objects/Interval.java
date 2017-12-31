package nl.kolkos.cryptoManagerBot.objects;

public class Interval {
	private long id;
	private String period;
	private String value;
	private String text;
	
	public Interval() {
		
	}
	
	public Interval(String period, String value, String text) {
		this.setPeriod(period);
		this.setValue(value);
		this.setText(text);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
