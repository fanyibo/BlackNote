package DataModel;

/**
 * An object used to model a file system object. As of this writing, can be either a
 * notebook, or a note
 * @author Ralph
 *
 */
public interface FSObject {

	public FSObjectType getType();
	
	public String getName();
	
	public int getID();
	
}
