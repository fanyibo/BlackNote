package Database;

public class DBAddException extends Exception {

	private static final long serialVersionUID = -7823446397662255752L;
	
	public DBAddException(){
	}
	
	public DBAddException(String message){
		super(message);
	}
	
	public DBAddException(String message, Exception e){
		super(message, e);
	}

}
