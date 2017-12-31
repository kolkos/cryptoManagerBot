package nl.kolkos.cryptoManagerBot.objects;

public class Period {
	private long id;
	private String value;
	private String text;
	
	public Period() {
		
	}
	
	public Period(String value, String text) {
		this.setValue(value);
		this.setText(text);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
