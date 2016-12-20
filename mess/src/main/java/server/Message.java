package server;

import org.joda.time.LocalTime;

public class Message {
	
	private final long id;
	//Czas na serwerze?:
	private final String messageTime;
	private final String userName;
	private final String content;
	
	public Message(long id, String userName, String content) {
		this.id = id;
		this.userName = userName;
		this.content = content;
		this.messageTime = (new LocalTime()).toString("HH:mm:ss");
	}
	
	public long getId() {
		return id;
	}
	
	public String getMessageTime() {
		return messageTime;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getContent() {
		return content;
	}
}