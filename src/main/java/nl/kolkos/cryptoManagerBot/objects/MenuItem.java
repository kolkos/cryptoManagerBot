package nl.kolkos.cryptoManagerBot.objects;

public class MenuItem {
	private long id;
	private String command;
	private String text;
	private String callbackData;
	
	public MenuItem(String command, String text, String callbackData) {
		this.setCommand(command);
		this.setText(text);
		this.setCallbackData(callbackData);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCallbackData() {
		return callbackData;
	}
	public void setCallbackData(String callbackData) {
		this.callbackData = callbackData;
	}
	
	
}
