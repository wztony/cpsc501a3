import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Deserializer {
	StringBuffer sb = new StringBuffer();
	private String filePrefix;
	
	public void printStringBuffer() {
		System.out.println(sb.toString());
    }
	
	public void setPrefix(int selection) {
		if(selection == 1) {
			setFilePrefix("Fruit");
		}
		else if(selection == 2) {
			setFilePrefix("LinkedObject");
		}
		else if(selection == 3) {
			setFilePrefix("BasicContainer");
		}
		else if(selection == 4) {
			setFilePrefix("FruitContainer");
		}
		else if(selection == 5) {
			setFilePrefix("FruitCollection");
		}
	}
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
	public String getFilePrefix() {
		return filePrefix;
	}
	
	private IdentityHashMap<Integer, Object> ihm;
	
	public Deserializer() {
		ihm = new IdentityHashMap<Integer, Object>();
	}
	
	public boolean ihmContainsKey(int value) {
		return ihm.containsKey(value);
	}
	
	public void ihmPut(Object object, int value) {
//		System.out.println("ihm storing object id " + value);
		ihm.put(value, object);
	}
	
	public Object ihmGetObject(int value) {
		return ihm.get(value);
	}
	
	
	/*public Object alGetObject(int value) {
		Iterator iterator = al.iterator();
		while(iterator.hasNext()) {
			Object o = iterator.next();
			if(ihm.get(o) == value) {
				System.out.println("object exists, getting ihm # " + ihm.get(o));
				return o;
			}
		}
		return null;
	}*/
	
	public Object recDeserialize(Element rootElement, Element childElement) {
		int id = Integer.valueOf(childElement.getAttributeValue("id"));
		if(ihm.containsKey(id)) {
//			System.out.println("child reference exists, no recursion");
			return ihmGetObject(id);
		}
		Object object = null;
		if(childElement.getAttributeValue("class").contains("[")) {
//			System.out.println("Object is an array");
			int length = Integer.valueOf(childElement.getAttributeValue("length"));
//			System.out.println("the array length is " + length);
			if(childElement.getAttributeValue("class").contains("[I")) {
				int[] intArray = (int[]) Array.newInstance(int.class, length);
				List values = childElement.getChildren();
				int i = 0;
				for(Object value : values) {
					Element intValue = (Element) value;
					Array.setInt(intArray, i, Integer.valueOf(intValue.getValue()));
					i++;
				}
				for(int j=0; j<intArray.length; j++) {
//					System.out.println("int array element " + Array.get(intArray, j));
				}
				object = (Object) intArray;
				ihmPut(object, id);
			}
			else if(childElement.getAttributeValue("class").contains("[LFruit")){
				Fruit[] fruitArray = (Fruit[]) Array.newInstance(Fruit.class, length);
				List values = childElement.getChildren();
				int refCounter = 0;
				for(Object value : values) {
					Element fruitReference = (Element) value;
					int fruitID = Integer.valueOf(fruitReference.getText());
//					System.out.println("fruit reference is " + fruitID);
					
					List children = rootElement.getChildren();
					for(Object child : children) {
						Element childElems = (Element) child;
//						System.out.println("reference child name " + childElems.getName());
						int childID = Integer.valueOf(childElems.getAttributeValue("id"));
//						System.out.printlfn("child id " + childID);
						if(childID == fruitID) {
							Array.set(fruitArray, refCounter, recDeserialize(rootElement, childElems));
							
						}
					}
					refCounter++;
				}
				for(int j=0; j<fruitArray.length; j++) {
//					System.out.println("fruit array element " + Array.get(fruitArray, j));
				}
				object = (Object) fruitArray;
				ihmPut(object, id);
			}
			else {
//				System.out.println("some other array");
				Object[] objectArray = (Object[]) Array.newInstance(Object.class, length);
//				System.out.println("some other array length " + Array.getLength(objectArray));
				List values = childElement.getChildren();
				int refCounter = 0;
				for(Object value : values) {
//					System.out.println("refCounter " + refCounter);
					Element fruitReference = (Element) value;
					int fruitID = Integer.valueOf(fruitReference.getText());
//					System.out.println("fruit reference is " + fruitID);
					
					List children = rootElement.getChildren();
					for(Object child : children) {
						Element childElems = (Element) child;
//						System.out.printkln("reference child name " + childElems.getName());
						int childID = Integer.valueOf(childElems.getAttributeValue("id"));
//						System.out.println("child id " + childID);
						if(childID == fruitID) {
//							System.out.println("child match");
							Array.set(objectArray, refCounter, (Object) recDeserialize(rootElement, childElems));
							
						}
					}
					refCounter++;
				}
				for(int j=0; j<objectArray.length; j++) {
//					System.out.println("fruit array element " + Array.get(objectArray, j));
				}
				object = (Object) objectArray;
				ihmPut(object, id);
			}
		}
		else {
			String className = childElement.getAttribute("class").getValue();
//			System.out.println("class name: " + className);
			Class objectClass;
			try {
				objectClass = Class.forName(className);
				try {
					object = objectClass.getConstructor().newInstance();
					ihmPut(object, id);
					List objectChildren = childElement.getChildren();
					for(Object objectField : objectChildren) {
						Element elementField = (Element) objectField;
						if(elementField.getName().equals("field")) {
							List vfs = elementField.getChildren();
							for(Object objectVF : vfs) {
								Element vf = (Element) objectVF;
								if(vf.getName().equals("value")) {
//									System.out.println("field is value");
									try {
//										System.out.println("field name: " + elementField.getAttribute("name").getValue());
										Field field = objectClass.getDeclaredField(elementField.getAttribute("name").getValue());
										field.setAccessible(true);
//										System.out.println("field type: " + field.getType());
										if(field.getType() == int.class) {
//											System.out.println("field value: " + Integer.valueOf(vf.getValue()));
											Field modifiersField = Field.class.getDeclaredField("modifiers");								//https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
											modifiersField.setAccessible(true);
											modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
											field.set(object, Integer.valueOf(vf.getValue()));
										}
										else if(field.getType() == double.class) {
//											System.out.println("field value: " + Double.valueOf(vf.getValue()));
											field.set(object, Double.valueOf(vf.getValue()));
										}
										else if(field.getType() == boolean.class) {
//											System.out.println("field value: " + Boolean.valueOf(vf.getValue()));
											field.set(object, Boolean.valueOf(vf.getValue()));
										}
										
									} catch (NoSuchFieldException e) {
										e.printStackTrace();
									}
								}
								else if(vf.getName().equals("reference")) {
									int referenceNumber = Integer.valueOf(vf.getText());
//									System.out.println("field is object reference " + referenceNumber);
									if(ihmContainsKey(referenceNumber)) {
//										System.out.println("reference object exists");
										Field field;
										try {
											field = objectClass.getDeclaredField(elementField.getAttribute("name").getValue());
											field.setAccessible(true);
											Field modifiersField = Field.class.getDeclaredField("modifiers");
											modifiersField.setAccessible(true);
											modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
											field.set(object, ihmGetObject(referenceNumber));
										} catch (NoSuchFieldException e) {
											e.printStackTrace();
										}
										
									}
									else {
//										System.out.println("reference object doesn't exist");
										List children = rootElement.getChildren();
										for(Object child : children) {
											Element childElems = (Element) child;
											int childID = Integer.valueOf(childElems.getAttributeValue("id"));
											if(childID == referenceNumber) {
												Field field;
												try {
													field = objectClass.getDeclaredField(elementField.getAttribute("name").getValue());
													field.setAccessible(true);
													field.set(object, recDeserialize(rootElement, childElems));
												} catch (NoSuchFieldException e) {
													e.printStackTrace();
												}
											}
										}
										
									}
								}
							}
						}
					}

					ihmPut(object, id);
					
					
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
	
	public Object deserialize(org.jdom2.Document document) {
		
		
		Element rootElement = document.getRootElement();
		List children = rootElement.getChildren();
		
		for(Object child : children) {
			Element childElement = (Element) child;
//			System.out.println("object id is: " + childElement.getAttributeValue("id"));
			Object fieldObject = recDeserialize(rootElement, childElement);
//			System.out.println("Done deserializing");
		}
		return ihm.get(0);
	}
	
	
	public static void main(String[] args) {
		Object object = null;
		int port = 4444;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Socket socket;
			while(true) {
				socket = serverSocket.accept();
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

				Deserializer deserializer = new Deserializer();
				try {
					object = inputStream.readObject();
					if(object == null) {
						socket.close();
						System.out.println("Received server close, halting main");
						return;
					}
					Inspector inspector = new Inspector();
					inspector.inspect(deserializer.deserialize((Document) object), true);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		deserializer.setPrefix(5);
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(new File(deserializer.getFilePrefix() + ".xml"));
			Object obj = deserializer.deserialize(document);
			new Inspector().inspect(obj, true);
	//		Serializer serializer = new Serializer("Reserialized.xml");
//			Serializer serializer = new Serializer("Reserialized" + deserializer.getFilePrefix() + ".xml");
//			serializer.serialize(obj);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		*/
		
	}

}
