package com.zuroslaw.messclient.restclient;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zuroslaw.messclient.model.FullUser;
import com.zuroslaw.messclient.model.Message;
import com.zuroslaw.messclient.model.User;

public class Requester {
	
	private String serviceURL;
	private ClientUser clientUser;
	private String userUpdateTime = "-1";
	private long lastMessageId;
	private boolean isReady = false;
	
	public static Requester initializeAndGet(String userName, String password, String serviceURL) throws Exception {
		Requester instance =  new Requester(new ClientUser(userName, password), serviceURL);
		boolean isAuthorized = instance.logIn();
		//System.out.println(isAuthorized);
		if (!isAuthorized) throw new AuthorizationException("Username and/or password don't match any user on server.");
		instance.requestLastMessageId();
		instance.isReady = true;
		return instance;
	}
	
	public static boolean checkRequesterReadiness(Requester instance) {
		return instance == null ? false : instance.isReady;
	}
	
	public static boolean requestAccount(String serviceURL, String userName, String password) throws Exception {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/requestaccount")
				.field("userName", userName)
				.field("password", password)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	private Requester(ClientUser clientUser, String serviceURL) {
		this.clientUser = clientUser;
		this.serviceURL = serviceURL;
	}
	
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	
	public long getLastMessageId() {
		return lastMessageId;
	}
	
	public void setLastMessageId(long lastMessageId) {
		this.lastMessageId =  lastMessageId;
	}
	
	public void requestLastMessageId() throws Exception {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/messages/last")
				.asJson();
		Integer result = (Integer) jsonResponse.getBody().getObject().get("id");
		lastMessageId = result.longValue();
	}
	
	public User[] getUsers() throws Exception {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/users")
				.header("Authorization", clientUser.getAuthorization64())
				.header("updateTime", userUpdateTime)
				.asJson();

		String serverUpdateTime = jsonResponse.getBody().getObject().getString("updateTime");
		//System.out.println(serverUpdateTime);
		if (serverUpdateTime.equals("upToDate")) return null;
		if (serverUpdateTime.equals("@noAuthorization")) throw new AuthorizationException("Invalid username and/or password");

		userUpdateTime = serverUpdateTime;
		JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("userNames");
		User[] result = new User[jsonArray.length()];
		if (result.length != 0) {
			for (int i = 0; i < result.length; i++) {
				String name = (String) jsonArray.get(i);
				result[i] = new User(name);
			}
		}
		return result;
	}
	
	public Message[] getNewMessages() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/messages/new")
				.header("Authorization", clientUser.getAuthorization64())
				.header("Id", Long.toString(lastMessageId))
				.asJson();
		JSONArray jsonArray = jsonResponse.getBody().getArray();
		Message[] result = new Message[jsonArray.length()];
		if (result.length != 0) {
			for (int i = 0; i < result.length; i++) {
				JSONObject jobj = /*(JSONObject)*/ jsonArray.getJSONObject(i);
				result[i] = new Message(
						jobj.getString("userName"),
						jobj.getString("content"),
						jobj.getLong("id"),
						jobj.getString("messageTime")
						);
			}
			this.setLastMessageId(result[result.length-1].getId());
		}		
		return result;
	}
	
	public void postMessage(String messageContent) throws UnirestException {
		Unirest.post(serviceURL + "/messages/post")
				.header("Authorization", clientUser.getAuthorization64())
				.header("messageContent", messageContent)
				.asJson();
	}
	
	public boolean logIn() throws Exception {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/users/authorize")
				.header("Authorization", clientUser.getAuthorization64())
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status"); 
	}
	
	public boolean logOff() {
		isReady = false;
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/users/logoff")
					.header("Authorization", this.clientUser.getAuthorization64())
					.asJson();
			return jsonResponse.getBody().getObject().getBoolean("status");
		} catch (UnirestException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean ping(String serviceURL) {
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/ping")
					.asJson();
			return jsonResponse.getBody().getObject().getBoolean("status");
		} catch (Exception e) {
			return false;
		}
	}
	
	//TODO: Unirest.post() to Unirest.get()
	
	//ADMIN:
	
	public boolean adminAuthorize() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/authorize")
				.header("Authorization", clientUser.getAuthorization64())
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	public FullUser[] getFullUsers() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/users")
				.header("Authorization", clientUser.getAuthorization64())
				.asJson();
		JSONArray jarray = jsonResponse.getBody().getArray();
		FullUser[] result = new FullUser[jarray.length()];
		for (int i = 0; i < jarray.length(); i++) {
			JSONObject jobj = jarray.getJSONObject(i);
			result[i] = new FullUser(
						jobj.getString("name"),
						jobj.getString("password"),
						jobj.getBoolean("logged")
					);
		}
		return result;
	}
	
	public boolean kickUser(String userName) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/kickuser")
				.header("Authorization", clientUser.getAuthorization64())
				.field("userName", userName)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	public boolean deleteUser(String userName) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/deleteuser")
				.header("Authorization", clientUser.getAuthorization64())
				.field("userName", userName)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	public boolean addUser(String userName, String password) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/adduser")
				.header("Authorization", clientUser.getAuthorization64())
				.field("userName", userName)
				.field("password", password)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	public FullUser[] getUserRequests() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/accountrequests")
				.header("Authorization", clientUser.getAuthorization64())
				.asJson();
		JSONArray jarray = jsonResponse.getBody().getArray();
		FullUser[] result = new FullUser[jarray.length()];
		for (int i = 0; i < jarray.length(); i++) {
			JSONObject jobj = jarray.getJSONObject(i);
			result[i] = new FullUser(
						jobj.getString("name"),
						jobj.getString("password"),
						jobj.getBoolean("logged")
					);
		}
		return result;
	}
	
	public boolean addUserFromRequest(String userName) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/acceptrequest")
				.header("Authorization", clientUser.getAuthorization64())
				.field("userName", userName)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
	
	public boolean rejectRequest(String userName) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceURL + "/admin/rejectrequest")
				.header("Authorization", clientUser.getAuthorization64())
				.field("userName", userName)
				.asJson();
		return jsonResponse.getBody().getObject().getBoolean("status");
	}
}
