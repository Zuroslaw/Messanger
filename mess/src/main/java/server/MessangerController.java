package server;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;

import org.joda.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;
import java.util.Optional;
import java.util.Base64;

@RestController
public class MessangerController {
	
	/// Server logs:
	private static final String LOG_REQUEST_PERFORMED = "Request performed -- ";
	
	private static final String LOG_GET_USERS = LOG_REQUEST_PERFORMED + "getUsers()";
	private static final String LOG_GET_MESSANGER = LOG_REQUEST_PERFORMED + "getMessanger()";
	private static final String LOG_POST_MESSAGE = LOG_REQUEST_PERFORMED + "postMessage()";
	private static final String LOG_CLEAR_MESSAGES = LOG_REQUEST_PERFORMED + "clearMessages()";
	private static final String LOG_ADD_USER = LOG_REQUEST_PERFORMED + "addUser()";
	private static final String LOG_GET_USERS_AND_PASSWORDS = LOG_REQUEST_PERFORMED + "getUsersAndPasswords()";
	private static final String LOG_ADD_USER_FORBID = LOG_REQUEST_PERFORMED + "addUserForbid() -- UNAUTHORIZED ACCESS";
	private static final String LOG_GET_NEW_MESSAGES = LOG_REQUEST_PERFORMED + "getNewMessages()";
	private static final String LOG_USER_POST_MESSAGE = LOG_REQUEST_PERFORMED + "userPostMessage()";
	private static final String LOG_GET_LAST_MESSAGE_ID = LOG_REQUEST_PERFORMED + "getLastMessageId()";
	private static final String LOG_AUTHORIZE = LOG_REQUEST_PERFORMED + "authorize()";
	private static final String LOG_GET_UPDATE_INFO = LOG_REQUEST_PERFORMED + "getUpdateInfo()";
	private static final String LOG_LOG_OFF = LOG_REQUEST_PERFORMED + "logOff()";
	private static final String LOG_ADMIN_AUTHORIZE = LOG_REQUEST_PERFORMED + "adminAuthorize()";
	private static final String LOG_DELETE_USER = LOG_REQUEST_PERFORMED + "deleteUser()";
	private static final String LOG_KICK_USER = LOG_REQUEST_PERFORMED + "kickUser()";
	private static final String LOG_REQUEST_ACCOUNT = LOG_REQUEST_PERFORMED + "requestAccount()";
	private static final String LOG_GET_ACCOUNT_REQUESTS = LOG_REQUEST_PERFORMED + "getAccountRequests()";
	private static final String LOG_ACCEPT_REQUEST = LOG_REQUEST_PERFORMED + "acceptRequest()";
	private static final String LOG_REJECT_REQUEST = LOG_REQUEST_PERFORMED + "rejectRequest()";
	
	private static final String LOG_REQUEST_LIST_FULL = "	Request list is full. Cant add more";
	
	/// Model:
	private final User ADMIN = new User("admin", "admin");
	private Messanger messangerInstance; 
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<User> accountRequests = new ArrayList<User>();
	
	/// Utils:
	private final AtomicLong counter = new AtomicLong(0L);
	private String userUpdateTime;
	
	//private void instantiateMessanger() {
	//	messangerInstance = new Messanger();
	//}
	
	private String getTimeForLog() {
		return (new LocalTime()).toString("HH:mm:ss") + " : ";
	}
	
	/*private void setUpdateInfo(boolean hasNewMessages, boolean hasNewUsers) {
		for (User usr : users) {
			usr.setUpdateInfo(boolean hasNewMessages, boolean hasNewUsers);
		}
	}*/
	
	{
		users.add(ADMIN);
		messangerInstance = new Messanger();
		userUpdateTime = (new LocalTime()).toString("HH:mm:ss");
	}
	
	/**
	 **	PUBLIC REQUESTS:
	 */
	
	//GET LAST MESSAGE ID
	@RequestMapping(value = "/messages/last")
	public Object getLastMessageId() {
		
		System.out.println(getTimeForLog() + LOG_GET_LAST_MESSAGE_ID);
		
		return new Object() {
			private long id;
			public long getId() { return id; }
			private Object init(long id) { this.id = id; return this; }
		}.init(messangerInstance.getLastMessageId());
	}
	
	//AUTHORIZE
	@RequestMapping(value = "/users/authorize")
	public Object authorize(
				@RequestHeader("Authorization") String authorization64	) {
		
		System.out.println(getTimeForLog() + LOG_AUTHORIZE);
		Optional<User> user = users.stream()
								   .filter(u -> u.authorize(authorization64))
								   .findAny();
		user.ifPresent(usr -> {
			usr.setLoggedOn();
			userUpdateTime = (new LocalTime()).toString("HH:mm:ss");
			});
		return new Object(){
			private boolean status;
			public boolean getStatus() { return status; }
			private Object init(boolean status) { this.status = status; return this; }
		}.init(user.isPresent());
	}
	
	//REQUEST FOR ACCOUNT
	@RequestMapping(value = "/requestaccount")
	public Object requestAccount(
				@RequestParam("userName") String userName,
				@RequestParam("password") String password	) {
	
		System.out.println(getTimeForLog() + LOG_REQUEST_ACCOUNT);
		
		StatusObject result = new StatusObject(false);
		
		if (accountRequests.size() >= 1000) {
			System.out.println(getTimeForLog() + LOG_REQUEST_LIST_FULL);
			return result;
		}
		if (userName.isEmpty() || password.isEmpty() || userName.charAt(0) == '@') return result;
		
		if (accountRequests.stream()
				 .anyMatch(user -> 
						   user.getName().equals(userName))	) {
			return result;
		}
		
		if (users.stream()
				 .anyMatch(user -> 
						   user.getName().equals(userName))	) {
			return result;
		}
		
		accountRequests.add(new User(userName, password));
		return result.init(true);
		
	}
	
	/**
	 **	ADMIN REQUESTS:
	 */
	
	Optional<User> getUserByName(String userName) {
		return users.stream()
					.filter(u -> u.getName().equals(userName))
					.findAny();
	}
	
	public class StatusObject {
			private boolean status;
			public boolean getStatus() { return status; }
			public StatusObject init(boolean status) { this.status = status; return this; }
			public StatusObject(boolean status) { this.status = status; }
	}
	 
	//ADMIN AUTHORIZE
	@RequestMapping(value = "/admin/authorize")
	public Object adminAuthorize(
				@RequestHeader("Authorization") String authorization64 ) {
		
		System.out.println(getTimeForLog() + LOG_ADMIN_AUTHORIZE);
		
		return new Object() {
			private boolean status;
			public boolean getStatus() { return status; }
			public Object init(boolean status) { this.status = status; return this; }
		}.init(authorization64.equals("Basic YWRtaW46YWRtaW4="));
	}
	 
	//GET MESSAGES:
	@RequestMapping(value = "/admin/messages", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public Message[] getMessanger() {
		
		System.out.println(getTimeForLog() + LOG_GET_MESSANGER);

		return messangerInstance.getMessages().stream().toArray(Message[]::new);
	}
	 
	//CLEAR:
	@RequestMapping(value = "/admin/clear", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public Object clearMessages() {		
	
		System.out.println(getTimeForLog() + LOG_CLEAR_MESSAGES);
		
		counter.set(0L);
		messangerInstance = new Messanger();
		return new Object() {
			public boolean getStatus() { return true; }
		};
	}
	
	//ADD USER:
	@RequestMapping(value = "/admin/adduser", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public StatusObject addUser(
				@RequestParam(value="userName") String userName,
				@RequestParam(value="password") String password		) {
		
		System.out.println(getTimeForLog() + LOG_ADD_USER);
		
		StatusObject result = new StatusObject(false);
		if (userName.isEmpty() || password.isEmpty() || userName.charAt(0) == '@') return result;
		
		if (users.stream()
				 .anyMatch(user -> 
						   user.getName().equals(userName))	) {
			return result;
		}
		User newUser = new User(userName, password);
		users.add(newUser);
		return result.init(true);
		
	}
	
	//ADD USER FROM REQUEST
	@RequestMapping(value = "/admin/acceptrequest", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public StatusObject acceptRequest(
				@RequestParam(value="userName") String userName	) {
		
		System.out.println(getTimeForLog() + LOG_ACCEPT_REQUEST);
		
		Optional<User> userRequest = accountRequests.stream()
							.filter(user -> user.getName().equals(userName))
							.findAny();
		userRequest.ifPresent( user -> {
								users.add(user);
								accountRequests.remove(user);
							});
		return new StatusObject(userRequest.isPresent());
		
	}
	
	//REJECT REQUEST
	@RequestMapping(value = "/admin/rejectrequest", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public StatusObject rejectRequest(
				@RequestParam(value="userName") String userName	) {
		
		System.out.println(getTimeForLog() + LOG_REJECT_REQUEST);
		
		Optional<User> userRequest = accountRequests.stream()
							.filter(user -> user.getName().equals(userName))
							.findAny();
		userRequest.ifPresent( user -> {
								accountRequests.remove(user);
							});
		return new StatusObject(userRequest.isPresent());
		
	}
	
	//DELETE USER:
	@RequestMapping(value = "/admin/deleteuser", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public Object deleteUser(
				@RequestParam(value="userName") String userName	) {
		
		System.out.println(getTimeForLog() + LOG_DELETE_USER);
		
		Optional<User> user = getUserByName(userName);
		
		user.ifPresent( usr -> {
			users.remove(usr); 
			userUpdateTime = (new LocalTime()).toString("HH:mm:ss");
		});
		
		return new Object() {
			private boolean status;
			public boolean getStatus() { return status; }
			public Object init(boolean status) { this.status = status; return this; }
		}.init(user.isPresent());
	}
	
	//KICK USER:
	@RequestMapping(value = "/admin/kickuser", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public Object kickUser(
				@RequestParam(value="userName") String userName	) {
		
		System.out.println(getTimeForLog() + LOG_KICK_USER);
		
		Optional<User> user = getUserByName(userName);
		
		user.ifPresent( usr -> {
			usr.setLoggedOff();
			userUpdateTime = (new LocalTime()).toString("HH:mm:ss");
		});
		
		return new Object() {
			private boolean status;
			public boolean getStatus() { return status; }
			public Object init(boolean status) { this.status = status; return this; }
		}.init(user.isPresent());
	}
	
	//GET USERS WITH PASSWORDS
	@RequestMapping(value = "/admin/users", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public User[] getUsersAndPasswords(
				@RequestParam(value="userName", defaultValue="@all") String userName	) {
		
		System.out.println(getTimeForLog() + LOG_GET_USERS_AND_PASSWORDS);
		
		return users.stream().toArray(User[]::new);
	}
	
	//GET ACCOUNT REQUESTS
	@RequestMapping(value = "/admin/accountrequests", headers = "Authorization=Basic YWRtaW46YWRtaW4=")
	public User[] getAccountRequests() {
		
		System.out.println(getTimeForLog() + LOG_GET_ACCOUNT_REQUESTS);
		
		return accountRequests.stream().toArray(User[]::new);
	}
	
	/**
	 **	USER REQUESTS:
	 */
	
	Optional<User> getLoggedUserFromAuthorization(String authorization64) {
		return users.stream()
					.filter(u -> u.authorize(authorization64) && u.isLogged())
					.findAny();
	}
	
	//GET USERS:
	@RequestMapping(value = "/users")
	public UsersLoggedIn getUsers(
				@RequestHeader("Authorization") String authorization64,				
				@RequestHeader("updateTime") String updateTime	) {
		
		System.out.println(getTimeForLog() + LOG_GET_USERS);
		
		Optional<User> user = getLoggedUserFromAuthorization(authorization64);
		if (!user.isPresent()) return new UsersLoggedIn(new String[0], "@noAuthorization");
		
		if (userUpdateTime.equals(updateTime))
			return new UsersLoggedIn(new String[0], "upToDate");
		
		String[] userNames = users.stream()
								.filter(User::isLogged)
								.map(User::getName)
								.toArray(String[]::new);
		return new UsersLoggedIn(userNames, userUpdateTime);
	}
	
	//LOG OFF
	@RequestMapping(value = "/users/logoff")
	public /*Optional<User>*/Object logOff(
				@RequestHeader("Authorization") String authorization64	) {
		
		System.out.println(getTimeForLog() + LOG_LOG_OFF);
		Optional<User> user = getLoggedUserFromAuthorization(authorization64);
		user.ifPresent(usr -> {
			usr.setLoggedOff();
			userUpdateTime = (new LocalTime()).toString("HH:mm:ss");
		});
		return new Object() {
			private boolean status;
			public boolean getStatus() { return status; }
			public Object init(boolean status) { this.status = status; return this; }
		}.init(user.isPresent());
	}
	
	//GET MESSAGES LATER THAN ID (NEW MESSAGES)
	@RequestMapping(value = "/messages/new")
	public Message[] getNewMessages(
				@RequestHeader("Authorization") String authorization64,
				@RequestHeader("Id") long id	) {
		
		System.out.println(getTimeForLog() + LOG_GET_NEW_MESSAGES);
		
		Optional<User> user = getLoggedUserFromAuthorization(authorization64);
		if (!user.isPresent() || id == messangerInstance.getLastMessageId()) {
			return new Message[0];
		}
		return messangerInstance.getMessages().stream()
							.filter(m -> m.getId() > id)
							.toArray(Message[]::new);
	}
	
	//POST MESSAGE
	@RequestMapping(value = "/messages/post")
	public Message postMessage(
					@RequestHeader("Authorization") String authorization64,
					@RequestHeader("messageContent") String messageContent	) {
						
		System.out.println(getTimeForLog() + LOG_POST_MESSAGE);
		
		Optional<User> user = getLoggedUserFromAuthorization(authorization64);
		
		if (!user.isPresent()) {
			return null;
		}
		
		Message msg = new Message(counter.incrementAndGet(), user.get().getName(), messageContent);
		messangerInstance.addMessage(msg);
		return msg;
	}
	
	//UPDATE INFO
	/*@RequestMapping(value = "/updateinfo")
	public UpdateInfo getUpdateInfo(
					@RequestHeader("Authorization") String authorization64,
					@RequestHeader("Id") long id	) {
						
		System.out.println(getTimeForLog() + LOG_GET_UPDATE_INFO);
		
		
	}*/
	 
	/**
	 **	FAIL REQUESTS:
	 */
	 
	/* //FORBIDDED ADD USER
	@RequestMapping(value = "users/adduser")
	public String addUserForbid(
				@RequestParam(value="userName") String userName,
				@RequestParam(value="password") String password		) {
		
		System.out.println(getTimeForLog() + LOG_ADD_USER_FORBID);
		return "Invalid username/password. Access forbidden";
	}*/
	
	/**
	 **	TEST REQUESTS:
	 */
	 
	@RequestMapping("/ping")
	public Object ping() {
		return new Object() {
			public boolean getStatus() { return true; }
		};
	}
	
	@RequestMapping("")
	public Object test() {
		System.out.println("POLACZYL SIE KURDE Z RESTEM MOJ PROGRAM SUPER");
		return "czesc";
	}
	
}













