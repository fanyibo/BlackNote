package Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import DataModel.Note;
import DataModel.NoteImpl;
import DataModel.Tag;
import DataModel.TagImpl;
import Database.DBAccess;
import Database.DBAddException;
import Database.DBException;

public class Test {

	public static void main(String[] args) throws DBAddException, DBException {
		DateTime dt = new DateTime();
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("stock", "aapl");
		metadata.put("analyst", "tim");
		
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new TagImpl("Research"));
		tags.add(new TagImpl("Real Estate"));
		
		for(Tag t: tags){
			System.out.println(t.getName());
		}
		
		Note note = new NoteImpl.NoteBuilder().setId(5).setName("MyNote").setAuthor(1).
				setCreated(dt).setModified(dt).setMetadata(metadata).setTags(tags).build();
		
		DBAccess.Singleton.getInstance().addNote(1, note, null);
	}

}
