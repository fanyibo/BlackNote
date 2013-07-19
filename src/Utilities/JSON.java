package Utilities;

import java.util.Map.Entry;

import DataModel.FSObject;
import DataModel.Note;
import DataModel.Notebook;
import DataModel.Tag;

/**
 * Class used to convert objects into JSON objects to be transmitted
 * @author Ralph
 *
 */
public class JSON {

	// Static class
	private JSON(){
	}
	
	
	/**
	 * Encodes the inputed notebook as a JSON object.
	 * @param book the notebook to encode
	 * @return the encoding
	 */
	public static String encodeNotebookAsJSON(Notebook book){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"id\":" + book.getID() + ", ");
		sb.append("\"children\":[");
		
		boolean first = true;
		for(FSObject obj: book.getChildren()){
			if(!first){
				sb.append(", ");
			}
			sb.append("{");
			sb.append("\"id\":" + obj.getID() + ", ");
			sb.append("\"name\":\"" + obj.getName() + "\", ");
			sb.append("\"type\":\"" + obj.getType().toString() + "\"");
			sb.append("}");
			
			first = false;
		}
		
		sb.append("]");
		sb.append("}");
		
		return sb.toString();
	}
	
	
	/**
	 * Encodes the inputed note as a JSON string
	 * @param note the note to encode
	 * @return the JSON string
	 */
	public static String encodeNoteAsJSON(Note note){
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"id\":" + note.getID() + ", ");
		sb.append("\"name\":\"" + note.getName() + "\", ");
		sb.append("\"author\":" + note.getAuthor() + ", ");
		sb.append("\"created\":");		// TODO
		sb.append("\"modified\":");		// TODO
		
		sb.append("\"metadata\":[");
		boolean first = true;
		for(Entry<String, String> entry: note.getMetadata().entrySet()){
			if(!first){
				sb.append(", ");
			}
			
			sb.append("{");
			sb.append("\"field\":\"" + entry.getKey() + "\", ");
			sb.append("\"value\":\"" + entry.getValue() + "\"");
			sb.append("}");
			
			first = false;
		}
		sb.append("], ");
		
		sb.append("\"tags\":[");
		first = true;
		for(Tag tag: note.getTags()){
			if(!first){
				sb.append(", ");
			}
			sb.append("\"" + tag.getName() + "\"");
			first = false;
		}
		sb.append("]");
		
		sb.append("}");
		
		return sb.toString();
	}

}
