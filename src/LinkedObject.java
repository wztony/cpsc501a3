
public class LinkedObject {
	private int id;
	private LinkedObject linkedObject;
	
	public LinkedObject getLinkedObject() {
		return linkedObject;
	}
	public void setLinkedObject(LinkedObject linkedObject) {
		this.linkedObject = linkedObject;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
