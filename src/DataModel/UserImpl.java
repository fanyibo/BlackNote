package DataModel;

public class UserImpl implements User {

	private int id;
	
	private String userName, pass, email;
	
	
	public UserImpl(int id, String userName, String pass, String email){
		this.id = id;
		this.userName = userName;
		this.pass = pass;
		this.email = email;
	}
	
	
	@Override
	public int getID() {
		return id;
	}
	

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPass() {
		return pass;
	}

	@Override
	public String getEmail() {
		return email;
	}

}
