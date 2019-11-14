
public class Fruit {
	private double weight;
	private boolean seed;
	
	public Fruit(double weight, boolean seed) {
		setWeight(weight);
		setSeed(seed);
	}
	
	public Fruit() {
		
	}
	
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public boolean getSeed() {
		return seed;
	}
	public void setSeed(boolean seed) {
		this.seed = seed;
	}
	
	
}
