package DataModel;

public enum Permission {

	WRITE;
	
	
	public static String asString(Permission p){
		switch (p){
			case WRITE: return "read";
			default: return "unknown";
		}
	}
}
