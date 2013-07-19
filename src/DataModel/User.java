package DataModel;

/**
 * An interface used to model a user. Extends the UserEntity interface.
 * @author Ralph
 *
 */
public interface User extends UserEntity {
		
	public String getPass();
	
	public String getEmail();
	
}
