package DataModel;

/**
 * An object that implements the FSObject interface. Should be instantiated in order
 * to represent the top level details of an FSObject when browsing the file system. The
 * interface contains all information necessary to display the object.
 * @author Ralph
 *
 */
public class FSObjectImpl implements FSObject {

	private final FSObjectType type;
	
	private final String name;
	
	private final int id;
	
	
	public FSObjectImpl(int id, String name, FSObjectType type){
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	
	@Override
	public FSObjectType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return id;
	}
	
	public String toString(){
		return id + ", " + name + ", " + type;
	}

}
