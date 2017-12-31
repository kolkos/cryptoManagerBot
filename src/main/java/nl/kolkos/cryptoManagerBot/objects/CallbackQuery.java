package nl.kolkos.cryptoManagerBot.objects;

import java.util.Date;

public class CallbackQuery {
	private long id;
	private Date date;
	private long chatId;
	private int msgId;
	private String userName;
	private String callbackData;
	private int handled = 0;
	private Date dateHandled;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public long getChatId() {
		return chatId;
	}
	public void setChatId(long chatId) {
		this.chatId = chatId;
	}
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCallbackData() {
		return callbackData;
	}
	public void setCallbackData(String callbackData) {
		this.callbackData = callbackData;
	}
	public int getHandled() {
		return handled;
	}
	public void setHandled(int handled) {
		this.handled = handled;
	}
	public Date getDateHandled() {
		return dateHandled;
	}
	public void setDateHandled(Date dateHandled) {
		this.dateHandled = dateHandled;
	}
	
	
	
}
