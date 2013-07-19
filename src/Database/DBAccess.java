package Database;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import DataModel.Note;
import DataModel.Notebook;
import DataModel.Permission;
import DataModel.User;
import DataModel.UserEntity;

/**
 * External users of this package should use this interface to make calls to the
 * database. No arbitrary SQL should ever be executed
 * @author Ralph
 *
 */
public interface DBAccess {

	/**
	 * Adds a notebook as a child of the specified notebook. As of this writing, this
	 * method is NOT designed to recursively add whatever children the notebook may have
	 * @param parentNotebookID the ID number of the parent notebook
	 * @param notebook the new notebook
	 * @return the ID number of the notebook you just added
	 * @throws DBAddException
	 */
	public int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException;
	
	/**
	 * Gets the file system object with the inputed ID number
	 * @param notebookID the id of the notebook
	 * @return the notebook or null if none exists
	 */
	public Notebook getNotebook(int notebookID) throws DBException;
	
	/**
	 * Adds the inputed note to the database. Disregards whatever is in the id field of the
	 * note, and returns the id number under which the note was added
	 * @param parentNotebookID the id of the parent notebook
	 * @param note the note to add
	 * @return the id number the note was added under
	 * @throws DBException 
	 */
	public int addNote(int parentNotebookID, Note note, Map<UserEntity, Permission> perms) 
			throws DBAddException, DBException;
	
	/**
	 * Updates the inputed note in the database. If no such note exists, creates a new note.
	 * @param note the note to update
	 * @return the id of the note (the same as the note inputed if the note was updated, or
	 * the id of the new note that was just created)
	 * @throws DBAddException
	 * @throws DBException
	 */
	public int updateNote(Note note, Map<UserEntity, Permission> perms) 
			throws DBAddException, DBException;
	
	/**
	 * Gets the note that corresponds with the inputed note id number
	 * @param noteID the id number of the note
	 * @return the note that corresponds to that id or null if none can be found
	 * @throws DBException
	 */
	public Note getNote(int noteID) throws DBException;
	
	/**
	 * Adds 
	 * @param u the user object to add
	 */
	public void addUser(User u);
	
	/**
	 * Returns the user info for the given id
	 * @param id the id of the user
	 * @return the user object for that id
	 */
	public User getUser(int id);
	
	/**
	 * Checks the database if the inputed username and password match one in the
	 * database
	 * @param u the user object with the credentials encoded
	 * @return true if the credentials match, false otherwise
	 */
	public User userDoesExist(String user, String pass);
	
	/**
	 * For a given noteID and userID, returns what permissions the user has on the
	 * note.
	 * @param noteID
	 * @param userID
	 * @return
	 */
	public Permission getPermission(int noteID, int userID);
	
	/**
	 * Gets a set of all Notes that were updated since the time specified. Useful for
	 * sending notification emails.
	 * @param start the start time
	 * @return set of all notes updated since the inputed start time
	 */
	public Set<Note> getRecentlyUpdatedSince(DateTime start);
	
	
	/**
	 * Singleton provided for convenience.
	 * @author Ralph
	 *
	 */
	public class Singleton{
		
		private static DBAccess instance;
		
		public static DBAccess getInstance() throws DBException{
			if(instance == null){
				instance = new DBAccessImpl();
			}
			return instance;
		}
	}
	
}
