package hu.hegedus.utils;

import java.util.HashMap;
import java.util.Map;

public class Primitives {
	private static Map<Class<?>, Class<?>> mapping;
	private static Map<Class<?>, Class<?>> inverseMapping;

	static {
		@SuppressWarnings("unchecked")
		Pair<Class<?>, Class<?>>[] ps = (Pair<Class<?>, Class<?>>[]) new Pair<?, ?>[] {
				new Pair<Class<?>, Class<?>>(boolean.class, Boolean.class),
				new Pair<Class<?>, Class<?>>(char.class, Character.class),
				new Pair<Class<?>, Class<?>>(byte.class, Byte.class),
				new Pair<Class<?>, Class<?>>(short.class, Short.class),
				new Pair<Class<?>, Class<?>>(int.class, Integer.class),
				new Pair<Class<?>, Class<?>>(long.class, Long.class),
				new Pair<Class<?>, Class<?>>(float.class, Float.class),
				new Pair<Class<?>, Class<?>>(double.class, Double.class)
		};
		mapping = new HashMap<Class<?>, Class<?>>();
		inverseMapping = new HashMap<Class<?>, Class<?>>();
		for (Pair<Class<?>, Class<?>> p : ps) {
			mapping.put(p.getFirst(), p.getSecond());
			inverseMapping.put(p.getSecond(), p.getFirst());
		}
	}

	public static Class<?> getWrappedType(Class<?> param) {
		Class<?> result = mapping.get(param);
		return (result == null) ? param : result;
	}

	public static Class<?> getPrimitiveType(Class<?> param) {
		return param.isPrimitive() ? param : inverseMapping.get(param);
	}
}
