package DataModel;

public class TagImpl implements Tag{

	private String name;
	
	
	public TagImpl(String name){
		this.name = name;
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	public boolean equals(Object o){
		if(o == null || !(o instanceof Tag)){
			return false;
		}
		
		Tag other = (Tag) o;
		
		return name.equals(other.getName());
	}
	
	
	public int hashCode(){
		return name.hashCode();
	}

}
