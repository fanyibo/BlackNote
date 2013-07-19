package DataModel;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * An interface used to represent a note
 * @author Ralph
 *
 */
public interface Note extends FSObject {

	public int getAuthor();
	
	public DateTime getCreated();
	
	public DateTime getLastModified();
	
	public Map<String, String> getMetadata();
	
	public Set<Tag> getTags();
		
}
