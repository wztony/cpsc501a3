import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSerializer {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSerializeFruit() {
		boolean w = false;
		boolean s = false;
		Serializer serializer = new Serializer("testSerializeFruit");
		double weight = 12.1;
		boolean seed = true;
		Object fruit = serializer.createFruit(weight, seed);
		Document doc = serializer.serialize(fruit);
		
		Element serialized = doc.getRootElement();
		Element object = serialized.getChild("object");
		List fields = object.getChildren();
		for(Object f : fields) {
			Element field = (Element) f;
			if(field.getAttributeValue("name").equals("weight")) {
				Element value = field.getChild("value");
				double vWeight = Double.valueOf(value.getText());
				if(weight == vWeight) {
					w = true;
				}
			}
			else if(field.getAttributeValue("name").equals("seed")) {
				Element value = field.getChild("value");
				Boolean vSeed = Boolean.valueOf(value.getText());
				if(seed == vSeed) {
					s = true;
				}
			}
		}
		
		assertTrue(w&&s);
	}
	
	@Test
	void testSerializeLinkedObject() {
		boolean v1 = false;
		boolean r1 = false;
		boolean v2 = false;
		boolean r2 = false;
		Serializer serializer = new Serializer("testSerializeLinkedObject");
		int value1 = 123;
		int value2 = 456;

		LinkedObject lo1 = new LinkedObject(value1);
		LinkedObject lo2 = new LinkedObject(value2);
		
		lo1.setLinkedObject(lo2);
		lo2.setLinkedObject(lo1);
		
		Document doc = serializer.serialize(lo1);
		
		Element serialized = doc.getRootElement();
		List objects = serialized.getChildren();
		for(Object o : objects) {
			Element object = (Element) o;
			if(object.getAttributeValue("id").equals("0")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					if(field.getAttributeValue("name").equals("value")) {
						int vValue1 = Integer.valueOf(field.getChild("value").getText());
						if(value1 == vValue1) {
							v1 = true;
						}
					}
					else if(field.getAttributeValue("name").equals("linkedObject")) {
						int vRef1 = Integer.valueOf(field.getChild("reference").getText());
						if(vRef1 == 1) {
							r1 = true;
						}
					}
				}
			}
			else if(object.getAttributeValue("id").equals("1")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					if(field.getAttributeValue("name").equals("value")) {
						int vValue2 = Integer.valueOf(field.getChild("value").getText());
						if(value2 == vValue2) {
							v2 = true;
						}
					}
					else if(field.getAttributeValue("name").equals("linkedObject")) {
						int vRef2 = Integer.valueOf(field.getChild("reference").getText());
						if(vRef2 == 0) {
							r2 = true;
						}
					}
				}
			}
		}
		
		assertTrue(v1&&v2&&r1&&r2);
	}
	
	
	@Test
	void testSerializeBasicContainer() {
		boolean t = true;
		Serializer serializer = new Serializer("testSerializeBasicContainer");
		int[] array = {1,0,-1,1234};
		Object bc = serializer.createBasicContainer(array);
		Document doc = serializer.serialize(bc);
		
		Element serialized = doc.getRootElement();
		List objects = serialized.getChildren();
		for(Object o : objects) {
			Element object = (Element) o;
			if(object.getAttributeValue("id").equals("0")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					int ref = Integer.valueOf(field.getChild("reference").getText());
					if(ref!=1) {
						t = false;
					}
				}
			}
			else if(object.getAttributeValue("id").equals("1")) {
				List values = object.getChildren();
				for(Object v : values) {
					Element value = (Element) v;
					int arrayValue = Integer.valueOf(value.getText());
					if(arrayValue!=1&&arrayValue!=-1&&arrayValue!=0&&arrayValue!=1234) {
						t = false;
					}
				}
			}
		}
		
		assertTrue(t);
	}
	
	
	@Test
	void testSerializeFruitContainer() {
		boolean t = true;
		Serializer serializer = new Serializer("testSerializeFruitContainer");
		double w1 = 123.1;
		double w2 = 0;
		double w3 = 20;
		boolean s1 = true;
		boolean s2 = false;
		boolean s3 = true;
		Fruit fruit1 = new Fruit(w1, s1);
		Fruit fruit2 = new Fruit(w2, s2);
		Fruit fruit3 = new Fruit(w3, s3);

		Fruit[] fArray = {fruit1, fruit2, fruit3};
		

		Object fc = serializer.createFruitContainer(fArray);
		Document doc = serializer.serialize(fc);
		
		Element serialized = doc.getRootElement();
		List objects = serialized.getChildren();
		for(Object o : objects) {
			Element object = (Element) o;
			if(object.getAttributeValue("id").equals("0")) {
				Element field = object.getChild("field");
				int ref = Integer.valueOf(field.getChild("reference").getText());
				if(ref!=1) {
					t = false;
				}
				
			}
			else if(object.getAttributeValue("id").equals("1")) {
				List references = object.getChildren();
				for(Object r : references) {
					Element reference = (Element) r;
					int ref = Integer.valueOf(reference.getText());
					if(ref!=2&&ref!=3&&ref!=4) {
						t = false;
					}
				}
			}
			else if(object.getAttributeValue("id").equals("2")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					if(field.getAttributeValue("name").equals("weight")) {
						Element value = field.getChild("value");
						double vWeight = Double.valueOf(value.getText());
						if(w1 != vWeight) {
							t = false;
						}
					}
					else if(field.getAttributeValue("name").equals("seed")) {
						Element value = field.getChild("value");
						Boolean vSeed = Boolean.valueOf(value.getText());
						if(s1 != vSeed) {
							t = false;
						}
					}
				}
			}
			else if(object.getAttributeValue("id").equals("3")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					if(field.getAttributeValue("name").equals("weight")) {
						Element value = field.getChild("value");
						double vWeight = Double.valueOf(value.getText());
						if(w2 != vWeight) {
							t = false;
						}
					}
					else if(field.getAttributeValue("name").equals("seed")) {
						Element value = field.getChild("value");
						Boolean vSeed = Boolean.valueOf(value.getText());
						if(s2 != vSeed) {
							t = false;
						}
					}
				}
			}
			else if(object.getAttributeValue("id").equals("4")) {
				List fields = object.getChildren();
				for(Object f : fields) {
					Element field = (Element) f;
					if(field.getAttributeValue("name").equals("weight")) {
						Element value = field.getChild("value");
						double vWeight = Double.valueOf(value.getText());
						if(w3 != vWeight) {
							t = false;
						}
					}
					else if(field.getAttributeValue("name").equals("seed")) {
						Element value = field.getChild("value");
						Boolean vSeed = Boolean.valueOf(value.getText());
						if(s3 != vSeed) {
							t = false;
						}
					}
				}
			}
		}
		
		assertTrue(t);
	}

}
