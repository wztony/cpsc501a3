import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Scanner;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;



public class Serializer {
	
	private String fileName;
	private IdentityHashMap<Object, Integer> ihm;
	private Element rootElement;
	
	public Serializer(String fileName) {
		this.fileName = fileName;
		ihm = new IdentityHashMap<Object, Integer>();
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
		if(!ihm.containsKey(object)) {
			ihmPut(object);
		}
		return ihm.get(object);
	}
	
	public Element getRootElement() {
		return rootElement;
	}

	public void setRootElement(Element rootElement) {
		this.rootElement = rootElement;
	}
	
	public void rootAddContent(Element elem) {
		rootElement.addContent(elem);
	}
	
	public Object createFruit(double weight, boolean seed) {
		Object object = null;
		try {
			String className = "Fruit";
			Class objectClass = Class.forName(className);
			try {
				try {
					object = objectClass.getConstructor(double.class, boolean.class).newInstance(weight, seed);
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
	
	public Object createLinkedObject(int value) {
		Object object = null;
		try {
			String className = "LinkedObject";
			Class objectClass = Class.forName(className);
			try {
				try {
					object = objectClass.getConstructor(int.class).newInstance(value);
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
			Class objectClass = Class.forName(className);
			try {
				try {
					object = objectClass.getConstructor(array.getClass()).newInstance(array);
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
	
	public Object createFruitContainer(Fruit[] array) {
		Object object = null;
		try {
			String className = "FruitContainer";
			Class objectClass = Class.forName(className);
			try {
				try {
					object = objectClass.getConstructor(Fruit[].class).newInstance((Object) array);			//https://stackoverflow.com/questions/26005924/instantiate-object-with-array-elements-as-parameters-java
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
	
	public Object createFruitCollection(ArrayList<Fruit> arrayList) {
		Object object = null;
		try {
			String className = "FruitCollection";
			Class objectClass = Class.forName(className);
			try {
				try {
					object = objectClass.getConstructor(ArrayList.class).newInstance(arrayList);
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
	
	
	public Element recSerialize(Object obj) {
		Class objectClass = obj.getClass();
		String className = objectClass.getName();
		int	id = ihmGetInteger(obj);
		
		Element elementObject = new Element("object");
		elementObject.setAttribute(new Attribute("class", className));
		elementObject.setAttribute(new Attribute("id", String.valueOf(id)));
		
		Field[] fields = objectClass.getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			try {
				Object ob = field.get(obj);
//				System.out.println("field name: " + field.getName());
//				System.out.println("field class: " + ob.getClass());
//				System.out.println("field type: " + field.getType());
				if(field.getType().isPrimitive()) {
//					System.out.println(field.getName() + " is primitive");
					Element elementField = new Element("field");
					elementField.setAttribute(new Attribute("name", field.getName()));
					elementField.setAttribute(new Attribute("declaringclass", className));
					elementField.addContent(new Element("value").setText(String.valueOf(ob)));
					elementObject.addContent(elementField);
				}
				else if(ob.getClass().isArray()) {
//					System.out.println(field.getName() + " is an array");
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
//							System.out.println("element " + i + " is primitive");
							elementArray.addContent(new Element("value").setText(String.valueOf(value)));
						}
						else {
//							System.out.println("element " + i + " is an object");
							ihmPut(value);
							elementArray.addContent(new Element("reference").setText(String.valueOf(ihmGetInteger(value))));
							rootAddContent(recSerialize(value));
						}
					}
					rootElement.addContent(elementArray);
					elementField.addContent(new Element("reference").setText(String.valueOf(arrayID)));
					elementObject.addContent(elementField);
				}
				else {
//					System.out.println(field.getName() + " is an object");
					if(!ihm.containsKey(ob)) {
						rootAddContent(recSerialize(ob));
					}

					Element elementField = new Element("field");
					elementField.setAttribute(new Attribute("name", field.getName()));
					elementField.setAttribute(new Attribute("declaringclass", className));
					
					int refID = ihmGetInteger(ob);
					Element refElem = new Element("reference");
					refElem.setText(String.valueOf(refID));
					elementField.addContent(refElem);
					elementObject.addContent(elementField);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return elementObject;
	}
	
	public org.jdom2.Document serialize(Object obj){
		rootElement = new Element("serialized");
		Document doc = new Document(rootElement);
		
		doc.getRootElement().addContent(recSerialize(obj));
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(fileName));
			xmlOutput.output(doc, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	public void displaySelections() {
		System.out.println("Enter your selection:");
		System.out.println("1 for simple object with primitive fields");
		System.out.println("2 for objects with references to each other");
		System.out.println("3 for object with an array of primitives");
		System.out.println("4 for object with an array of simple objects");
		System.out.println("5 for object with collection");
		System.out.println("q to exit");
	}
	
	public static void main(String[] args) {
		Serializer serializer = new Serializer("Fruit.xml");
		Scanner scanner = new Scanner(System.in);
		serializer.displaySelections();
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
//				System.out.println("Class name: " + fruit.getClass().getName());
				serializer.setFileName("Fruit.xml");
				serializer.serialize(fruit);
				break;
			case "2":
				System.out.println("Enter its value (int)");
				s = scanner.nextLine();
				int value1 = Integer.parseInt(s);
				Object linkedObject1 = serializer.createLinkedObject(value1);
				System.out.println("Enter its value (int)");
				s = scanner.nextLine();
				int value2 = Integer.parseInt(s);
				Object linkedObject2 = serializer.createLinkedObject(value2);
//				System.out.println("Class name: " + linkedObject1.getClass().getName());
//				System.out.println("Class name: " + linkedObject2.getClass().getName());
				try {
					Method setLinkedObject = Class.forName("LinkedObject").getDeclaredMethod("setLinkedObject", LinkedObject.class);
					setLinkedObject.setAccessible(true);
					try {
						setLinkedObject.invoke(linkedObject1, linkedObject2);
						setLinkedObject.invoke(linkedObject2, linkedObject1);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				serializer.setFileName("LinkedObject.xml");
				serializer.serialize(linkedObject1);
				break;
			case "3":
				System.out.println("Enter the length of the int array");
				s = scanner.nextLine();
				int length1 = Integer.parseInt(s);
				int[] array1 = new int[length1];
				for(int i=0; i<length1; i++) {
					System.out.println("Enter value (int) for index " + i);
					s = scanner.nextLine();
					int value = Integer.parseInt(s);
					array1[i] = value;
				}
				Object basicContainer = serializer.createBasicContainer(array1);
//				System.out.println("Class name: " + basicContainer.getClass().getName());
				serializer.setFileName("BasicContainer.xml");
				serializer.serialize(basicContainer);
				break;
			case "4":
				System.out.println("Enter the length of the object array");
				s = scanner.nextLine();
				int length2 = Integer.parseInt(s);
				Fruit[] array2 = new Fruit[length2];
				for(int i=0; i<length2; i++) {
					System.out.println("Enter its weight (double)");
					s = scanner.nextLine();
					double weight2 = Double.parseDouble(s);
					System.out.println("Enter if it has seeds (boolean)");
					s = scanner.nextLine();
					boolean seed2 = Boolean.parseBoolean(s);
					array2[i] = (Fruit) serializer.createFruit(weight2, seed2);
				}
				Object fruitContainer = serializer.createFruitContainer(array2);
//				System.out.println("Class name: " + fruitContainer.getClass().getName());
				serializer.setFileName("FruitContainer.xml");
				serializer.serialize(fruitContainer);
				break;
			case "5":
				System.out.println("Enter the length of the array list");
				s = scanner.nextLine();
				int length3 = Integer.parseInt(s);
				ArrayList<Fruit> array3= new ArrayList<Fruit>(length3);
				for(int i=0; i<length3; i++) {
					System.out.println("Enter its weight (double)");
					s = scanner.nextLine();
					double weight3 = Double.parseDouble(s);
					System.out.println("Enter if it has seeds (boolean)");
					s = scanner.nextLine();
					boolean seed3 = Boolean.parseBoolean(s);
					array3.add((Fruit) serializer.createFruit(weight3, seed3));
				}
				Object arrayList = serializer.createFruitCollection(array3);
//				System.out.println("Class name: " + arrayList.getClass().getName());
				serializer.setFileName("FruitCollection.xml");
				serializer.serialize(arrayList);
				break;
			default:
				System.out.println("Invalid selection, try again");
				break;
			}
			serializer.displaySelections();
			s = scanner.nextLine();
		}
		System.out.println("Exiting program.");
	}
}
