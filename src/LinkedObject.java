
public class LinkedObject {
	private int value;
	private LinkedObject linkedObject;
	
	public LinkedObject(int value) {
		setValue(value);
	}
	
	public LinkedObject() {
		
	}
	
	public LinkedObject getLinkedObject() {
		return linkedObject;
	}
	public void setLinkedObject(LinkedObject linkedObject) {
		this.linkedObject = linkedObject;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
