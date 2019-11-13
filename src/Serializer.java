import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Scanner;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;



public class Serializer {
	
	
	private IdentityHashMap<Object, Integer> ihm;
	
	
	public Serializer() {
		ihm = new IdentityHashMap<Object, Integer>();
		
	}
	
	
	public void ihmPut(Object object) {
		ihm.put(object, ihm.size());
	}
	
	public int ihmGetInteger(Object object) {
		return ihm.get(object);
	}
	
	public static void main(String[] args) {
		Serializer serializer = new Serializer();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your selection:");
		System.out.println("1 for ");
		System.out.println("2 for ");
		System.out.println("3 for ");
		System.out.println("4 for ");
		System.out.println("5 for ");
		String s = scanner.nextLine();
		while(!s.equals("exit")) {
			switch(s) {
			case "1":
				
				try {
					String className = "Fruit";
					Class fruitClass = Class.forName(className);
					try {
						System.out.println("Enter its weight (double)");
						s = scanner.nextLine();
						double weight = Double.parseDouble(s);
						System.out.println("Enter if it has seeds (boolean)");
						s = scanner.nextLine();
						boolean seed = Boolean.parseBoolean(s);
						try {
							Object currentFruit = fruitClass.getConstructor(double.class, boolean.class).newInstance(weight, seed);
							
							Method getWeight = fruitClass.getDeclaredMethod("getWeight", null);
							getWeight.setAccessible(true);
							
							Element rootElement = new Element("serialized");
							Document doc = new Document(rootElement);
							
							Element object = new Element("object");
							serializer.ihmPut(currentFruit);
							object.setAttribute(new Attribute("class", className));
							object.setAttribute(new Attribute("id", String.valueOf(serializer.ihmGetInteger(currentFruit))));
							
							Element field1 = new Element("field");
							field1.setAttribute(new Attribute("name", "weight"));
							field1.setAttribute(new Attribute("declaringclass", currentFruit.getClass().toString()));
							field1.addContent(new Element("value").setText(String.valueOf(getWeight.invoke(currentFruit, null))));
							
							
							object.addContent(field1);
							
							doc.getRootElement().addContent(object);
							
							try {
								XMLOutputter xmlOutput = new XMLOutputter();
								
								xmlOutput.setFormat(Format.getPrettyFormat());
								xmlOutput.output(doc, new FileWriter("Fruit.xml"));
								xmlOutput.output(doc, System.out);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
					} catch (NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case "2":
				break;
			case "3":
				break;
			case "4":
				break;
			case "5":
				break;
			default:
				System.out.println("Invalid selection, try again");
				break;
			}
			s = scanner.nextLine();
		}

		System.out.println("Exiting program.");
	}
}
