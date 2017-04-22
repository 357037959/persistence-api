package com.ctvit.framework.core.model;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.F_APPEND;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

@SuppressWarnings({"unchecked","rawtypes"})
public class BeanPopulatorFactory {

	private final Log log = LogFactory.getLog(BeanPopulatorFactory.class);

	private static Map<Class, BeanPopulator> populators = new HashMap<Class, BeanPopulator>();

	private static final Map<String, String[]> primitiveMap = new HashMap<String, String[]>(8);

	private static final Map<Class<?>, String> transforms = new HashMap<Class<?>, String>(8);

	private static final Map<String, String[]> converterMap = new HashMap<String, String[]>(8);

	private static final BeanPopulatorFactory factory = new BeanPopulatorFactory();

	private static final boolean debug = false;

	static {
		// Unbox map: key:primitiveType name, value[box type,method,signature]
		primitiveMap.put("I", new String[] { "java/lang/Integer", "intValue", "()I", "(I)Ljava/lang/Integer;" });
		primitiveMap.put("J", new String[] { "java/lang/Long", "longValue", "()J", "(J)Ljava/lang/Long;" });
		primitiveMap.put("S", new String[] { "java/lang/Short", "shortValue", "()S", "(S)Ljava/lang/Short;" });
		primitiveMap.put("D", new String[] { "java/lang/Double", "doubleValue", "()D", "(D)Ljava/lang/Double;" });
		primitiveMap.put("F", new String[] { "java/lang/Float", "floatValue", "()F", "(F)Ljava/lang/Float;" });
		primitiveMap.put("Z", new String[] { "java/lang/Boolean", "booleanValue", "()Z", "(Z)Ljava/lang/Boolean;" });
		primitiveMap.put("C", new String[] { "java/lang/Character", "charValue", "()C", "(C)Ljava/lang/Charater;" });
		primitiveMap.put("B", new String[] { "java/lang/Byte", "byteValue", "()B", "(B)Ljava/lang/Byte" });

		// Converter map: key: type name, value[method,signature]
		converterMap.put("I", new String[] { "toI", "(Ljava/lang/Object;)I" });
		converterMap.put("J", new String[] { "toJ", "(Ljava/lang/Object;)J" });
		converterMap.put("S", new String[] { "toS", "(Ljava/lang/Object;)S" });
		converterMap.put("D", new String[] { "toD", "(Ljava/lang/Object;)D" });
		converterMap.put("F", new String[] { "toF", "(Ljava/lang/Object;)F" });
		converterMap.put("Z", new String[] { "toZ", "(Ljava/lang/Object;)Z" });
		converterMap.put("C", new String[] { "toC", "(Ljava/lang/Object;)C" });
		converterMap.put("B", new String[] { "toB", "(Ljava/lang/Object;)B" });

		converterMap.put("java/lang/String", new String[] { "toString", "(Ljava/lang/Object;)Ljava/lang/String;" });

		converterMap.put("java/lang/Integer", new String[] { "toInteger", "(Ljava/lang/Object;)Ljava/lang/Integer;" });
		converterMap.put("java/lang/Long", new String[] { "toLong", "(Ljava/lang/Object;)Ljava/lang/Long;" });
		converterMap.put("java/lang/Short", new String[] { "toShort", "(Ljava/lang/Object;)Ljava/lang/Short;" });
		converterMap.put("java/lang/Double", new String[] { "toDouble", "(Ljava/lang/Object;)Ljava/lang/Double;" });
		converterMap.put("java/lang/Float", new String[] { "toFloat", "(Ljava/lang/Object;)Ljava/lang/Float;" });
		converterMap.put("java/lang/Boolean", new String[] { "toBoolean", "(Ljava/lang/Object;)Ljava/lang/Boolean;" });
		converterMap.put("java/lang/Character", new String[] { "toCharacter", "(Ljava/lang/Object;)Ljava/lang/Character;" });
		converterMap.put("java/lang/Byte", new String[] { "toByte", "(Ljava/lang/Object;)Ljava/lang/Byte;" });

		// primitive to native type map
		transforms.put(byte.class, "B");
		transforms.put(char.class, "C");
		transforms.put(double.class, "D");
		transforms.put(float.class, "F");
		transforms.put(int.class, "I");
		transforms.put(long.class, "J");
		transforms.put(short.class, "S");
		transforms.put(boolean.class, "Z");
	}

	private BeanPopulatorFactory() {
	}

	public static <T> BeanPopulator<T> getPopulater(Class<T> type) {
		BeanPopulator<T> populator = populators.get(type);
		if (populator == null) {
			synchronized (type) {
				if (populators.get(type)==null) { // double null check
					try {
						populator = factory.generate(type);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						// invoke ClassLoader.defineClass(String name, byte[] b, int off,
						// int len, ProtectionDomain protectionDomain);
					} catch (IntrospectionException e) {
						e.printStackTrace();
						// fetch BeanInfo failed.
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						// invoke ClassLoader.defineClass(String name, byte[] b, int off,
						// int len, ProtectionDomain protectionDomain);
						// failed.
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						// invoke ClassLoader.defineClass(String name, byte[] b, int off,
						// int len, ProtectionDomain protectionDomain);
						// throws SecurityException or ClassFormatError
					} catch (InstantiationException e) {
						e.printStackTrace();
						// never new instance generated class failed.
					}
					populators.put(type, populator);
				}
			}
		}
		return populator;
	}

	/**
	 * Clear Populators Cache(prevent ClassLoader Memory Leak)
	 */
	public static void shutdown() {
		populators.clear();
	}

	// Generate Class extends BeanPopulator.
	private <T> BeanPopulator<T> generate(Class<T> type) throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
	InvocationTargetException, InstantiationException {
		String sig = toSig(type); // to com/dota/xxx/Domain
		String descriptor = Type.getDescriptor(type); // to Lcom/dota/xxx/Domain;
		// throws IntrospectionException: can't fecth BeanInfo
		PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(type, Object.class).getPropertyDescriptors();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		if (debug) {
			log.debug("Tracing Generate:");
			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out, true));
			cv = new CheckClassAdapter(tcv);
		}

		String className = sig+"$$Converter"; // TODO namingStrategy
		cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, "com/dota/framework/domain/BeanPopulator", null);
		//		cw.visitSource("Bp001.java", null);
		MethodVisitor mv;
		mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		if (mv != null) {
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			// super();
			mv.visitMethodInsn(INVOKESPECIAL, "com/dota/framework/domain/BeanPopulator", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		// public <T> T toBean(Class<T> type, Map<String,Object> value);
		mv = cv.visitMethod(ACC_PUBLIC, "toBean", "(" + descriptor+"Ljava/util/Map;)" + descriptor, "(" + descriptor + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)" + descriptor,
				null);
		if (mv != null) {
			mv.visitCode();

			boolean jumped = false;
			// Iterate setters
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Method setter = propertyDescriptor.getWriteMethod();
				if (setter == null) {
					continue;
				}
				String setterName = setter.getName();
				String setterSign = Type.getMethodDescriptor(setter);
				String paramType = toSig(setter.getParameterTypes()[0]);
				Class<?> paramClass = setter.getParameterTypes()[0];
				String property = propertyDescriptor.getName();
				boolean primitive = paramClass.isPrimitive();

				// if (value.containsKey(property)) {
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn(property);
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z");
				Label label = new Label();
				mv.visitJumpInsn(IFEQ, label);

				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 0);
				if (!primitive) {
					mv.visitLdcInsn(Type.getType(paramClass));
				}
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn(property);
				mapGet(mv); // map.get(Object);

				if (primitive) { // to[IJSFDZCB], regualer converters
					mv.visitMethodInsn(INVOKEVIRTUAL, className, converterMap.get(paramType)[0], converterMap.get(paramType)[1]);
				} else { // convert(Class, Object); general converter
					mv.visitMethodInsn(INVOKEVIRTUAL, className, "convert", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;");
					mv.visitTypeInsn(CHECKCAST, paramType);
				}
				// bean.setProperty();
				mv.visitMethodInsn(INVOKEVIRTUAL, sig, setterName, setterSign);

				// } end if [value.containsKey(property)]
				mv.visitLabel(label);
				if (jumped) {
					mv.visitFrame(F_SAME, 0, null, 0, null);
				} else {
					mv.visitFrame(F_APPEND, 1, new Object[] { className }, 0, null);
					jumped = true;
				}
			}
			// return bean instance
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ARETURN);

			mv.visitMaxs(5, 3);
			mv.visitEnd();
		}
		// Fake toBean(); generic and abstract bridge
		mv = cv.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "toBean", "(Ljava/lang/Object;Ljava/util/Map;)Ljava/lang/Object;", null, null);
		if (mv != null) {
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, sig);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, className, "toBean", "(" + descriptor + "Ljava/util/Map;)" + descriptor);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		// public abstract Map<String, Object> toMap(Object bean);
		mv = cv.visitMethod(ACC_PUBLIC, "toMap", "(Ljava/lang/Object;)Ljava/util/Map;",
				"(Ljava/lang/Object;)Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
		if (mv != null) {
			mv.visitCode();
			// new HashMap();
			mv.visitTypeInsn(NEW, "java/util/HashMap");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
			mv.visitVarInsn(ASTORE, 2);

			// cast parameter value to Bean's Type
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, sig);
			mv.visitVarInsn(ASTORE, 3);

			// Iterate getters
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Method getter = propertyDescriptor.getReadMethod();
				if (getter == null) {
					continue;
				}
				String getterName = getter.getName();
				String getterSign = Type.getMethodDescriptor(getter);
				Class<?> returnClass = getter.getReturnType();
				String returnType = toSig(returnClass);
				String property = propertyDescriptor.getName();

				// bean.getXXX()
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn(property);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKEVIRTUAL, sig, getterName, getterSign);
				if (returnClass.isPrimitive()) { // box
					mv.visitMethodInsn(INVOKESTATIC, primitiveMap.get(returnType)[0], "valueOf", primitiveMap.get(returnType)[3]);
				}
				// java.util.Map.put(Object,Object);
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
				mv.visitInsn(POP);
			}

			// return result
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}
		cv.visitEnd();
		byte[] data = cw.toByteArray();
		// throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
		Class<BeanPopulator> clazz = ReflectUtils.defineClass(type.getName()+"$$Converter", data, getClassLoader(type)); // TODO namingStrategy
		// throws InstantiationException, IllegalAccessException
		return clazz.newInstance();
	}

	private ClassLoader getClassLoader(Class<?> type) {
		ClassLoader classLoader = null;
		if (classLoader == null) {
			classLoader = type.getClassLoader();
		}
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		if (classLoader == null) {
			throw new IllegalStateException("Cannot determine classloader");
		}
		return classLoader;
	}

	/**
	 * invoke java.util.Map.get(Object);
	 * 
	 * @param mv
	 */
	private void mapGet(MethodVisitor mv) {
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
	}

	/**
	 * type to java native type name.
	 * 
	 * e.g. int = I; long = J; java.lang.Object = java/lang/Object
	 * @param clazz
	 * @return
	 */
	private String toSig(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return transforms.get(clazz);
		} else {
			return Type.getType(clazz).getInternalName();
		}
	}
}
