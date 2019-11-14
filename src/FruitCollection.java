import java.util.ArrayList;

public class FruitCollection {
	private ArrayList<Fruit> arrayList = new ArrayList<Fruit>();
	
	public FruitCollection(ArrayList<Fruit> arrayList) {
		this.arrayList = arrayList;
	}
	
	public ArrayList<Fruit> getArrayList() {
		return arrayList;
	}
	public void setArrayList(ArrayList<Fruit> arrayList) {
		this.arrayList = arrayList;
	}
}
