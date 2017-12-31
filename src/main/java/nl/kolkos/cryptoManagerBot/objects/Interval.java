package nl.kolkos.cryptoManagerBot.objects;

public class Interval {
	private long id;
	private String forPeriod;
	private String value;
	private String text;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getForPeriod() {
		return forPeriod;
	}
	public void setForPeriod(String forPeriod) {
		this.forPeriod = forPeriod;
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
