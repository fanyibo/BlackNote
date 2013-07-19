package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import DataModel.FSObject;
import DataModel.FSObjectImpl;
import DataModel.FSObjectType;
import DataModel.Note;
import DataModel.Notebook;
import DataModel.NotebookImpl;
import DataModel.Permission;
import DataModel.Tag;
import DataModel.User;
import DataModel.UserEntity;
import DataModel.UserImpl;

/**
 * A simple (non-hibernate based) implementation of the database interface.
 * @author Ralph
 *
 */
final class DBAccessImpl implements DBAccess {

	private final static String SCHEMA_NAME = "InnoChallenge";
	
	/**
	 * The sql interface singleton.
	 */
	private SQLInterface sql;
	
	private int previousFSObjectID;
	
	
	DBAccessImpl() throws DBException{
		sql = SQLInterface.getSQLInterface();
		previousFSObjectID = getPreviousFSObjectID();
	}
	
	
	/**
	 * Returns the ID of the last FSObject created.
	 * @return the ID of the last FSObject added
	 * @throws DBException 
	 */
	private int getPreviousFSObjectID() throws DBException {
		String query = "select max(objectID) from " + SCHEMA_NAME + ".FSObjects";
		ResultSet rs = sql.getResultSafe(query);
		try {
			if(rs.next()){
				return rs.getInt(1);
			}
			return 0;
		} catch (SQLException e) {
			throw new DBException("An error occured while communicating with the database", e);
		}
	}


	@Override
	public synchronized int addNotebook(int parentNotebookID, Notebook notebook) throws DBAddException {
		try {
			// Make sure the parent notebook exists
			if(!notebookDoesExist(parentNotebookID)){
				throw new DBAddException();
			}
			
			// Add the object to the global file system object directory
			String update = "insert into " + SCHEMA_NAME + ".FSObjects " +
					"(objectID, objectName, typeName) values (" + (previousFSObjectID + 1) + 
					", '" + notebook.getName() + "', 'notebook')";
			sql.getResultSafe(update);
			previousFSObjectID++;
			
			// Add the notebook to the notebook table
			update = "insert into " + SCHEMA_NAME + ".Notebooks (notebookID) "
					+ "values (" + previousFSObjectID + ")";
			sql.getResultSafe(update);
			
			// Place the notebook under the specified parent notebook
			update = "insert into " + SCHEMA_NAME + ".FSStructure (parent, child) " +
					"values (" + parentNotebookID + ", " + previousFSObjectID + ")";
			sql.getResultSafe(update);
			
			// Return the ID of the notebook just added
			return previousFSObjectID;
		} 
		catch (SQLException e) {
			throw new DBAddException("Error communicating with database", e);
		}
	}
	
	
	/**
	 * Queries the database for a notebook of the ID number supplied.
	 * @return true if the ID corresponds to a notebook, false otherwise
	 * @throws SQLException 
	 */
	private boolean notebookDoesExist(int id) throws SQLException{
		String query = "select * from " + SCHEMA_NAME + ".Notebooks where notebookID = " + 
				id;
		ResultSet rs = sql.getResultSafe(query);
		
		if(rs.next()){
			return true;
		}
		
		return false;
	}


	@Override
	public synchronized Notebook getNotebook(int notebookID) throws DBException {
		// Get the details of the notebook
		String query = "select notebookID, objectName " +
				"from " + SCHEMA_NAME + ".Notebooks, " +
				SCHEMA_NAME + ".FSObjects " +
				"where notebookID = " + notebookID + " and " +
				"objectID = notebookID";
		
		ResultSet rs = sql.getResultSafe(query);
		
		String notebookName = null;
		try{
			if(!rs.next()){
				return null;
			}
			notebookName = rs.getString(2);
		}
		catch(SQLException e){
			throw new DBException(e);
		}
		
		// Get the notebook's children
		query = "select objectID, typeName, objectName " +
				"from " + SCHEMA_NAME + ".FSObjects fso, " +
				SCHEMA_NAME + ".FSStructure fss " +
				"where fss.parent = " + notebookID + " and " +
				"fss.child = fso.objectID " +
				"order by objectName";
		
		rs = sql.getResultSafe(query);
		List<FSObject> children = new LinkedList<FSObject>();
		try{
			while(rs.next()){
				int id = rs.getInt(1);
				FSObjectType type = FSObjectType.fromString(rs.getString(2));
				String name = rs.getString(3);
				FSObject obj = new FSObjectImpl(id, name, type);
				
				children.add(obj);
			}
		}
		catch(SQLException e){
			throw new DBException("Error communicating with DB", e);
		}
		
		// Create and return the new Notebook
		return new NotebookImpl(notebookID, notebookName, children);
	}


	@Override
	public synchronized int addNote(int parentNotebookID, Note note,
			Map<UserEntity, Permission> perms) throws DBAddException,
			DBException {
		try {
			if (!notebookDoesExist(parentNotebookID)) {
				throw new DBAddException("Parent notebook does not exist");
			}

			int tempID = previousFSObjectID + 1;

			// Insert the note into the FSObject directory
			insertNoteIntoFSObjectTable(tempID, note);

			// Insert the note into the Notes table
			insertNoteIntoNotesTable(tempID, note);

			// Insert the note into the FSStructure table
			insertNoteIntoFSStructureTable(parentNotebookID, tempID);

			// Insert the note metadata into the NoteMetadata table
			insertNoteMetaData(tempID, note);

			// Insert the tags into the tags table
			insertNoteTagsIntoTable(tempID, note);

			// Insert the perms into the perms table
			insertNotePermsIntoTable(tempID, perms);

			previousFSObjectID++;
			return tempID;
		} 
		catch (SQLException e) {
			throw new DBException("Error communicating with DB", e);
		}
	}
	

	@Override
	public int updateNote(Note note, Map<UserEntity, Permission> perms)
			throws DBAddException, DBException {
		if (!noteDoesExist(note)) {
			return addNote(1, note, perms);
		}
		
		try{
			
			int tempID = note.getID();

			// Insert the note into the Notes table
			insertNoteIntoNotesTable(tempID, note);

			// Insert the note metadata into the NoteMetadata table
			insertNoteMetaData(tempID, note);

			// Insert the tags into the tags table
			insertNoteTagsIntoTable(tempID, note);

			// Insert the perms into the perms table
			insertNotePermsIntoTable(tempID, perms);
			
			return tempID;
		}
		catch(SQLException e){
			throw new DBException("Error communicating with DB", e);
		}

	}
	
	
	/**
	 * Returns true if the note exists, false otherwise
	 * @param note
	 * @return
	 */
	private boolean noteDoesExist(Note note){
		// TODO
		return true;
	}
	

	/**
	 * Inserts the note into the FSObjectTable
	 * @param id the id of the note
	 * @param note the note
	 */
	private void insertNoteIntoFSObjectTable(int id, Note note){
		String update = "insert into " + SCHEMA_NAME + ".FSObjects (objectID, objectName, " +
				"typeName) values (" + id + ", '" + note.getName() + "', " +
				"'note') on duplicate key objectName=values(objectName), typeName=values(" +
				"typeName)";
		sql.getResultSafe(update);
	}
	
	
	/**
	 * Inserts the note into the notes table
	 * @param id the id of the note
	 * @param note the note
	 */
	private void insertNoteIntoNotesTable(int id, Note note){
		String createdDate = dateToSQLString(note.getCreated());
		String modifiedDate = dateToSQLString(note.getLastModified());
		String update = "insert into " + SCHEMA_NAME + ".Notes (noteID, author, created, " +
				"lastModified) values (" + id + ", " + note.getAuthor() + 
				", STR_TO_DATE('" + createdDate + "', '%Y-%m-%d %H:%i'), " +
				" STR_TO_DATE('" + modifiedDate + "','%Y-%m-%d %H:%i')) on duplicate key " +
				"lastModified=values(lastModified)";
		
		sql.getResultSafe(update);
	}
	
	
	private String dateToSQLString(DateTime dt){
		return dt.getYear() + "-" + dt.getMonthOfYear() + "-"
				+ dt.getDayOfMonth() + " " + dt.getHourOfDay() + ":" + dt.getMinuteOfHour();
	}
	
	
	/**
	 * Inserts the note into the FSStructure table
	 * @param parentID the parent notebook's id (does NOT check for validity)
	 * @param id the id number of the note
	 */
	private void insertNoteIntoFSStructureTable(int parentID, int id){
		String update = "insert into " + SCHEMA_NAME + ".FSStructure (parent, child) values " +
				"(" + parentID + ", " + id + ") on duplicate key ignore";
		
		sql.getResultSafe(update);
	}
	
	
	/**
	 * Inserts the note's metadata into the metadata table
	 * @param id the notes id
	 * @param note the note
	 */
	private void insertNoteMetaData(int id, Note note){
		for(Entry<String, String> entry: note.getMetadata().entrySet()){
			String update = "insert into " + SCHEMA_NAME + ".NoteMetaData (noteID, " +
					"fieldName, fieldData) values (" + id + ", '" + entry.getKey() + "', '" + 
					entry.getValue() + "') on duplicate key update " +
					"fieldData=values(fieldData)";
			
			sql.getResultSafe(update);
		}
	}
	
	
	/**
	 * Inserts the note's tags into the tags table
	 * @param id the note's id
	 * @param note the note
	 * @throws SQLException 
	 */
	private void insertNoteTagsIntoTable(int id, Note note) throws SQLException{
		for (Tag t : note.getTags()) {
			String update;

			// Try to add the tag to the tag database. Ignore the exception that
			// occurs if there is a duplicate tag
			update = "insert into " + SCHEMA_NAME + ".Tags (tag) values ('"
					+ t.getName() + "') on duplicate key ignore";

			System.out.println(update);
			sql.getResultSafe(update);

			update = "insert into " + SCHEMA_NAME
					+ ".NoteTags (noteID, tag) values " + "(" + id + ", '"
					+ t.getName() + "') on duplicate key ignore";

			System.out.println(update);
			sql.getResultSafe(update);
		}
	}
	
	
	/**
	 * Inserts the perms into the perms table
	 * @param id the id of the note
	 * @param perms the map containing all the perms
	 */
	private void insertNotePermsIntoTable(int id, Map<UserEntity, Permission> perms) {
		if(perms == null){
			return;
		}
		
		for(Entry<UserEntity, Permission> e: perms.entrySet()){
			String update = "insert into " + SCHEMA_NAME + ".NotePermissions (noteID, " +
					"userEntityID, permission) values (" + id + ", " + e.getKey().getID() + 
					", '" + Permission.asString(e.getValue()) + "') on duplicate key " +
					"permission=values(permission)";
			
			sql.getResultSafe(update);
		}
	}


	@Override
	public synchronized Note getNote(int noteID) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public synchronized void addUser(User u) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public synchronized User getUser(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public User userDoesExist(String user, String pass) {
		// Validate user input
		if(!isSafeString(user) || !isSafeString(pass)){
			return null;
		}
		
		String query = "select u.userID, u.email from " + SCHEMA_NAME + 
				".Users u, " + SCHEMA_NAME + ".UserEntities ue where ue.userName = " 
				+ user + " and u.pass = " + pass + " and u.userID = ue.userEntityID";
		ResultSet rs = sql.getResultSafe(query);
		
		try{
			if(rs.next()){
				int userID = rs.getInt(1);
				String email = rs.getString(2);
				return new UserImpl(userID, user, pass, email);
			}
		}
		catch(SQLException e){
			return null;
		}

		return null;
	}


	@Override
	public synchronized Permission getPermission(int noteID, int userID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**Removes quotes from strings to avoid having the SQL syntax be invalidated.
	 * @param s the string to clean
	 * @return the clean string
	 */
	private String makeSafeString(String s){
		String temp = s.replace("'", "");
		return temp.replace("\"", "");
	}

	
	/**Checks if the inputed is safe. Used in the owner registration method.
	 * @param s the string to check
	 * @return true if the string is safe, false otherwise
	 */
	private boolean isSafeString(String s){
		if(s == null){
			return false;
		}
		return !s.contains("'") && !s.contains("\"");
	}


	@Override
	public Set<Note> getRecentlyUpdatedSince(DateTime start) {
		// TODO Auto-generated method stub
		return null;
	}

}
