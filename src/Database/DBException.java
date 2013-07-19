package Database;

public class DBException extends Exception {

	private static final long serialVersionUID = -8141291068248102080L;


	public DBException(){
	}
	
	public DBException(String message){
		super(message);
	}
	
	public DBException(String message, Exception e){
		super(message, e);
	}
	
	public DBException(Exception e){
		super(e);
	}

}
