package server;

public class UsersLoggedIn {
	
	private String[] userNames;
	private String updateTime;
	
	public UsersLoggedIn(String[] userNames, String updateTime) {
		this.userNames = userNames;
		this.updateTime = updateTime;
	}
	
	public String[] getUserNames() {
		return userNames;
	}
	
	public void setUserNames(String[] userNames) {
		this.userNames = userNames;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}