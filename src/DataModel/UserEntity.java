package DataModel;

/**
 * An interface used to represent a user entity. As of this writing, can be either
 * a user, or a user group
 * @author Ralph
 *
 */
public interface UserEntity {

	public int getID();
	
	public String getUserName();
}
