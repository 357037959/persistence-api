package com.ctvit.framework.core.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ReflectUtils {
	private static final Map<String, Class<?>> primitives = new HashMap<String, Class<?>>(8);
	private static final Map<String, String> transforms = new HashMap<String, String>(8);

	private static Method DEFINE_CLASS;
	private static final ProtectionDomain PROTECTION_DOMAIN;

	static {
		primitives.put("byte", Byte.TYPE);
		primitives.put("char", Character.TYPE);
		primitives.put("double", Double.TYPE);
		primitives.put("float", Float.TYPE);
		primitives.put("int", Integer.TYPE);
		primitives.put("long", Long.TYPE);
		primitives.put("short", Short.TYPE);
		primitives.put("boolean", Boolean.TYPE);

		transforms.put("byte", "B");
		transforms.put("char", "C");
		transforms.put("double", "D");
		transforms.put("float", "F");
		transforms.put("int", "I");
		transforms.put("long", "J");
		transforms.put("short", "S");
		transforms.put("boolean", "Z");
	}

	static {
		PROTECTION_DOMAIN = AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
			@Override
			public ProtectionDomain run() {
				return ReflectUtils.class.getProtectionDomain();
			}
		});

		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				try {
					Class<?> loader = Class.forName("java.lang.ClassLoader"); // JVM crash w/o this
					DEFINE_CLASS = loader.getDeclaredMethod("defineClass",
							new Class[]{ String.class,
							byte[].class,
							Integer.TYPE,
							Integer.TYPE,
							ProtectionDomain.class });
					DEFINE_CLASS.setAccessible(true);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
				return null;
			}
		});
	}

	/**
	 * 
	 * @param className
	 * @param b
	 * @param loader
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Class defineClass(String className, byte[] b, ClassLoader loader) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object[] args = new Object[]{className, b, new Integer(0), new Integer(b.length), PROTECTION_DOMAIN };
		return (Class)DEFINE_CLASS.invoke(loader, args);
	}

	public static PropertyDescriptor[] getBeanProperties(Class type) {
		return getPropertiesHelper(type, true, true);
	}

	public static PropertyDescriptor[] getBeanGetters(Class type) {
		return getPropertiesHelper(type, true, false);
	}

	public static PropertyDescriptor[] getBeanSetters(Class type) {
		return getPropertiesHelper(type, false, true);
	}

	private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
		try {
			BeanInfo info = Introspector.getBeanInfo(type, Object.class);
			PropertyDescriptor[] all = info.getPropertyDescriptors();
			if (read && write) {
				return all;
			}
			List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>(all.length);
			for (int i = 0; i < all.length; i++) {
				PropertyDescriptor pd = all[i];
				if ((read && pd.getReadMethod() != null) ||
						(write && pd.getWriteMethod() != null)) {
					properties.add(pd);
				}
			}
			return properties.toArray(new PropertyDescriptor[properties.size()]);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}
}
