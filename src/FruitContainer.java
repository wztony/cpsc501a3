
public class FruitContainer {
	private Fruit[] fruits;
	
	public FruitContainer(Fruit[] fruits) {
		setFruits(fruits);
	}
	
	public FruitContainer() {
		
	}
	
	public Fruit[] getFruits() {
		return fruits;
	}
	public void setFruits(Fruit[] fruits) {
		this.fruits = fruits;
	}
}
