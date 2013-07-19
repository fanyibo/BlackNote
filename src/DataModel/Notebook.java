package DataModel;

import java.util.List;

/**
 * An interface used to represent a notebook
 * @author Ralph
 *
 */
public interface Notebook extends FSObject {

	public int getID();
		
	public List<FSObject> getChildren();
	
}
