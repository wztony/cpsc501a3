import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
	
	
	public void setIHM(IdentityHashMap<Object, Integer> ihm) {
		this.ihm = ihm;
	}
	
	public IdentityHashMap<Object, Integer> getIHM() {
		return ihm;
	}
	
	public void ihmPut(Object object) {
		ihm.put(object, ihm.size());
	}
	
	public int ihmGetInteger(Object object) {
		return ihm.get(object);
	}
	
	public Object createFruit(double weight, boolean seed) {
		Object object = null;
		try {
			String className = "Fruit";
			Class fruitClass = Class.forName(className);
			try {
				try {
					object = fruitClass.getConstructor(double.class, boolean.class).newInstance(weight, seed);
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
		return object;
	}
	
	public Object createBasicContainer(int[] array) {
		Object object = null;
		try {
			String className = "BasicContainer";
			Class fruitClass = Class.forName(className);
			try {
				try {
					object = fruitClass.getConstructor(int[].class).newInstance(array);
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
		return object;
	}
	
	public org.jdom2.Document serialize(Object obj){
		Element rootElement = new Element("serialized");
		Document doc = new Document(rootElement);
		
		Class objectClass = obj.getClass();
		String className = objectClass.getName();
		ihmPut(obj);
		int id = ihmGetInteger(obj);
		
		Element elementObject = new Element("object");
		elementObject.setAttribute(new Attribute("class", className));
		elementObject.setAttribute(new Attribute("id", String.valueOf(id)));
		
		
		Field[] fields = objectClass.getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			try {
				Object ob = field.get(obj);
				System.out.println("field name: " + field.getName());
				System.out.println("field class: " + ob.getClass());
				System.out.println("field type: " + field.getType());
				if(field.getType().isPrimitive()) {
					System.out.println(field.getName() + " is primitive");
					Element elementField = new Element("field");
					elementField.setAttribute(new Attribute("name", field.getName()));
					elementField.setAttribute(new Attribute("declaringclass", className));
					elementField.addContent(new Element("value").setText(String.valueOf(ob)));
					elementObject.addContent(elementField);
				}
				else if(ob.getClass().isArray()) {
					System.out.println(field.getName() + " is an array");
					
					Element elementField = new Element("field");
					elementField.setAttribute(new Attribute("name", field.getName()));
					elementField.setAttribute(new Attribute("declaringclass", className));
					
					ihmPut(ob);
					int arrayID = ihmGetInteger(ob);
					Element elementArray = new Element("object");
					elementArray.setAttribute("class", ob.getClass().getName());
					elementArray.setAttribute("id", String.valueOf(arrayID));
					elementArray.setAttribute("length", String.valueOf(Array.getLength(ob)));
					
					
					
					for(int i=0; i<Array.getLength(ob); i++) {
						Object value = Array.get(ob, i);
						if(value.getClass().isPrimitive() ||
								value.getClass() == java.lang.Character.class||
								value.getClass() == java.lang.Long.class||
								value.getClass() == java.lang.Integer.class||
								value.getClass() == java.lang.Boolean.class) {
							System.out.println("element " + i + " is primitive");
							elementArray.addContent(new Element("value").setText(String.valueOf(value)));
						}
					}
					elementField.addContent(elementArray);
					elementObject.addContent(elementField);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		doc.getRootElement().addContent(elementObject);
		
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter("Fruit.xml"));
			xmlOutput.output(doc, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		Element object = new Element("object");
		serializer.ihmPut(obj);
		object.setAttribute(new Attribute("class", className));
		object.setAttribute(new Attribute("id", String.valueOf(serializer.ihmGetInteger(obj))));
		
		Element field1 = new Element("field");
		field1.setAttribute(new Attribute("name", "weight"));
		field1.setAttribute(new Attribute("declaringclass", className));
		field1.addContent(new Element("value").setText(""));
		
		
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
		
		*/
		return doc;
	}
	
	public static void main(String[] args) {
		Serializer serializer = new Serializer();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your selection:");
		System.out.println("1 for simple object with primitive fields");
		System.out.println("2 for objects with references to each other");
		System.out.println("3 for object with an array of primitives");
		System.out.println("4 for object with an array of simple objects");
		System.out.println("5 for object with collection");
		System.out.println("q to exit");
		String s = scanner.nextLine();
		while(!s.equals("q")) {
			switch(s) {
			case "1":
				System.out.println("Enter its weight (double)");
				s = scanner.nextLine();
				double weight = Double.parseDouble(s);
				System.out.println("Enter if it has seeds (boolean)");
				s = scanner.nextLine();
				boolean seed = Boolean.parseBoolean(s);
				Object fruit = serializer.createFruit(weight, seed);
				System.out.println("Class name: " + fruit.getClass().getName());
				serializer.serialize(fruit);
				break;
			case "2":
				break;
			case "3":
				System.out.println("Enter the length of the int array");
				s = scanner.nextLine();
				int length = Integer.parseInt(s);
				int[] array = new int[length];
				for(int i=0; i<length; i++) {
					System.out.println("Enter value (int) for index " + i);
					s = scanner.nextLine();
					int value = Integer.parseInt(s);
					array[i] = value;
				}
				Object basicContainer = serializer.createBasicContainer(array);
				System.out.println("Class name: " + basicContainer.getClass().getName());
				serializer.serialize(basicContainer);
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
