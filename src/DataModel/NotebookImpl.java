package DataModel;

import java.util.List;

/**
 * A simple implementation of the notebook interface
 * @author Ralph
 *
 */
public final class NotebookImpl implements Notebook {

	private final int id;
	
	private final String name;
	
	private final List<FSObject> children;
	
	
	public NotebookImpl(int id, String name, List<FSObject> children){
		this.id = id;
		this.name = name;
		this.children = children;
	}
	
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<FSObject> getChildren() {
		return children;
	}

	@Override
	public FSObjectType getType() {
		return FSObjectType.NOTEBOOK;
	}
	
	
	public String toString(){
		String out = id + ", " + name + ": \n";
		for(FSObject obj: children){
			out += "\t" + obj.toString() + "\n";
		}
		return out;
	}

}
