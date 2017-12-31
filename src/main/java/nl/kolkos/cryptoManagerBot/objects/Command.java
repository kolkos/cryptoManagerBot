package nl.kolkos.cryptoManagerBot.objects;

import java.util.Date;

public class Command {
	private long id;
	private long chatId;
	private String userName;
	private Date date;
	private String command;
	private int commandHandled = 0;
	private Date timestampHandled;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getChatId() {
		return chatId;
	}
	public void setChatId(long chatId) {
		this.chatId = chatId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getCommandHandled() {
		return commandHandled;
	}
	public void setCommandHandled(int commandHandled) {
		this.commandHandled = commandHandled;
	}
	public Date getTimestampHandled() {
		return timestampHandled;
	}
	public void setTimestampHandled(Date timestampHandled) {
		this.timestampHandled = timestampHandled;
	}
	
	
}
