package server;

import java.util.ArrayList;

public class Messanger {
	
	private ArrayList<Message> messages;
	
	public Messanger(){
		messages = new ArrayList<Message>();
	}
	
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public ArrayList<Message> getMessages() {
		return messages;
	}
	
	public long getLastMessageId() {
		if (messages.size() == 0) {
			return 0L;
		}
		return messages.get(messages.size()-1).getId();
	}
}