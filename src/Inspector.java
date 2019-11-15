import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

public class Inspector {
	IdentityHashMap<Object, Integer> ihm = new IdentityHashMap();
	StringBuffer sb = new StringBuffer();
	
    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
        printStringBuffer();
    }

	private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		//set up how far to indent tabs
		if(ihm.containsKey(obj)) {
			return;
		}
		ihm.put(obj, ihm.size());
		String tabs = setupTabs(depth);
		if(c.isArray()) {
			recArray(c, obj, recursive, depth);
		}
		else {
			Class superClass = c.getSuperclass();
//			if(c != Object.class) {
//				inspectClass(superClass, obj, recursive, depth + 1);
//			}
			
//			recInterface(c, obj, recursive, depth + 1);
			
			//print class name
			String className = c.getName();
			concatLine(tabs + "[CLASS]");
			concatLine(tabs + "Class Name: " + className);
			
//			printConstructors(c, tabs);
//			printMethods(c, tabs);		
			printFields(c, obj, recursive, depth);
		}
	}
    
    
    public void printConstructors(Class c, String tabs) {
    	//get each constructor and use it to get and print name, parameters, and modifier
		Constructor[] classConstructor = c.getDeclaredConstructors();
		for(Constructor constructor : classConstructor) {
			try {
				if(!Modifier.isPublic(constructor.getModifiers())) {
					constructor.setAccessible(true);
				}
				concatLine(tabs + " Constructor Name: " + constructor.getName());
				Class[] constructorParameters = constructor.getParameterTypes();
				for(Class parameter : constructorParameters) {
					concatLine(tabs + "  Parameter Type: " + parameter.getName());
				}
				concatLine(tabs + "  Modifier: " + Modifier.toString(constructor.getModifiers()));
			} catch (SecurityException e) {
				concatLine("cannot set accessible");
			} 
		}
    }
    
    
    public void printMethods(Class c, String tabs) {
    	Method[] classMethod = c.getDeclaredMethods();
		for(Method method : classMethod) {
			if(!Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
			concatLine(tabs + " Method Name: " + method.getName());
			
			Class[] exceptionParameters = method.getExceptionTypes();
			for(Class parameter : exceptionParameters) {
				concatLine(tabs + "  Exception Type: " + parameter.getName());
			}
			
			concatLine(tabs + "  Return Type: " + method.getReturnType());
			
			Class[] methodParameters = method.getParameterTypes();
			for(Class parameter : methodParameters) {
				concatLine(tabs + "  Parameter Type: " + parameter.getName());
			}
			concatLine(tabs + "  Modifier: " + Modifier.toString(method.getModifiers()));
		}
    }
    
    
    public void printFields(Class c, Object obj, boolean recursive, int depth) {
    	String tabs = setupTabs(depth);
    	Field[] classField = c.getDeclaredFields();
		for(Field field : classField) {
			if(!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			
			concatLine(tabs + " Field Name: " + field.getName());
			concatLine(tabs + "  Type: " + field.getType().getName());
			concatLine(tabs + "  Modifier: " + Modifier.toString(field.getModifiers()));
			try {
				Object ob = field.get(obj);
				if(ob.equals(null)) {
					concatLine(tabs + "  Value: null");
				}
				if(isPrimitive(ob)||
						ob.getClass() == java.lang.String.class||
						ob.getClass() == java.lang.Long.class||
						ob.getClass() == java.lang.Integer.class||
						ob.getClass() == java.lang.Boolean.class||
						ob.getClass() == java.lang.Double.class||
						ob.getClass() == java.lang.Float.class||
						ob.getClass() == java.lang.Short.class||
						ob.getClass() == java.lang.Character.class||
						ob.getClass() == java.lang.Byte.class) {
					concatLine(tabs + "  Value: " + ob);
				}
				else {
					concatLine(tabs + "  Reference: " + ob.getClass().getName() + "@" + ob.hashCode());
					inspectClass(ob.getClass(), ob, recursive, depth+1);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
//				e.printStackTrace();
				concatLine(tabs + "  Value: null");
			}
			
		}
    }
    
    
    public void recInterface(Class c, Object obj, boolean recursive, int depth) {
    	String tabs = setupTabs(depth);
    	Class[] interfaces = c.getInterfaces();
    	for(Class intf : interfaces) {
			Class[] superInterfaces = intf.getInterfaces();
			for(Class sintf : superInterfaces) {
				if(sintf.isInterface()) {
					recInterface(intf, obj, recursive, depth + 1);
				}
			}
			concatLine(tabs + "[INTERFACE]");
			concatLine(tabs + "Interface Name: " + intf.getName());
			printMethods(intf, tabs);
			printFields(intf, obj, recursive, depth);
		}
    }
    
    
    public String setupTabs(int depth) {
    	String tabs = "";
    	for(int i=0; i<depth; i++) {
			tabs = tabs + "\t";
		}
    	return tabs;
    }
    
    
    public boolean isPrimitive(Object ob) {
    	return ob.getClass().isPrimitive() ||
				ob.getClass() == Integer.class ||
				ob.getClass() == Double.class ||
				ob.getClass() == Float.class ||
				ob.getClass() == Short.class ||
				ob.getClass() == Long.class ||
				ob.getClass() == Character.class ||
				ob.getClass() == Byte.class ||
				ob.getClass() == Boolean.class;
    }
    
    
    public void recArray(Class c, Object obj, boolean recursive, int depth) {
    	String tabs = setupTabs(depth);
    	concat(tabs + "Array Length: " + Array.getLength(obj) + "\n");
    	for(int i=0; i<Array.getLength(obj); i++) {
			if(i==0) {
				concat(tabs + "[\n");
			}
			if(i>0) {
				concat(tabs + ",\n");
			}
			try {
				Object value = Array.get(obj, i);
				if(value.getClass().isPrimitive() ||
						value.getClass() == java.lang.String.class||
						value.getClass() == java.lang.Long.class||
						value.getClass() == java.lang.Integer.class||
						value.getClass() == java.lang.Boolean.class) {
					concat("\t" + value.toString() + "\n");
				}
				else if(value.getClass().isArray()){
					recArray(value.getClass(), value, true, depth);
				}
				else {
					if(recursive) {
						inspectClass(value.getClass(), value, true, depth);
					}
					else {
						concat(value.getClass().getName() + "@" + value.hashCode());
					}
				}
			} catch (NullPointerException e) {
				concat("null");
//				e.printStackTrace();
			}
			if(i == Array.getLength(obj)-1) {
				concat(tabs + "]");
			}
		}
		concatLine("");
    }
    
    
    public void concatLine(String s) {
    	sb.append(s + "\n");
    }
    
    public void concat(String s) {
    	sb.append(s);
    }
    
    public void printStringBuffer() {
        System.out.println(sb.toString());
    }
    
    public String getStringBuffer() {
    	return sb.toString();
    }
    
}
