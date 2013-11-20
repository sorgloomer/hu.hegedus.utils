package hu.hegedus.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class does and caches property copying operations.
 * 
 * @author Hege
 * 
 */
public class BeansBean {

	/**
	 * This map holds the cached getter and setter method pairs for every
	 * matching properties in a given source-destination class pair.
	 */
	private Map<Pair<Class<?>, Class<?>>, Pair<Method, Method>[]> cache = new ConcurrentHashMap<>();

	/**
	 * Creates a new instance of the type of src, and copies it's properties
	 * into the new instance via the getters and setters.
	 * 
	 * @param src
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T clone(T src) {
		if (src == null)
			return null;
		return this.convert(src, (Class<T>) src.getClass());
	}

	/**
	 * Converts the object src to the given type. This function first creates a
	 * new instance of type classDst, and then copies the matching properties
	 * from src.
	 * 
	 * @param src
	 * @param classDst
	 * @return
	 */
	public <T> T convert(Object src, Class<T> classDst) {
		if (src == null)
			return null;
		Class<?> classSrc = src.getClass();
		try {
			T res = classDst.newInstance();
			this.copyProperties(src, res);
			return res;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not convert object of class '"
					+ classSrc.getSimpleName() + "' to class '"
					+ classDst.getSimpleName() + "'");
		}
	}

	/**
	 * Copies the matching properties from src to dst.
	 * 
	 * @param classSrc
	 * @param classDst
	 * @param src
	 * @param dst
	 */
	protected void copyProperties(Object src, Object dst) {
		Class<?> classSrc = src.getClass();
		Class<?> classDst = dst.getClass();
		Map<Pair<Class<?>, Class<?>>, Pair<Method, Method>[]> cache = this.cache;
		try {
			Pair<Class<?>, Class<?>> classes =
					new Pair<Class<?>, Class<?>>(classSrc, classDst);
			Pair<Method, Method>[] methodPairs = cache
					.get(classes);
			if (methodPairs == null) {
				synchronized (cache) {
					methodPairs = cache.get(classes);
					if (methodPairs == null) {
						methodPairs = this.generatePropertyPairs(
								classSrc,
								classDst);
						cache.put(classes, methodPairs);
					}
				}
			}
			for (Pair<Method, Method> mpair : methodPairs) {
				Object val = mpair.getFirst().invoke(src);
				mpair.getSecond().invoke(dst, val);
			}
		} catch (IntrospectionException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					"Could not copy properties of object of class '"
							+ classSrc.getSimpleName()
							+ "' to object of class class '"
							+ classDst.getSimpleName() + "'");
		}
	}

	/**
	 * Generates the getter-setter pairs for the matching properties of classes
	 * src and dst. For every writeable property of class dst, a readable
	 * porperty in the class src is searched for, and returned as a Method pair.
	 * If no suitable getter is found in src, than no pair is returned for the
	 * proeprty in dst.
	 * 
	 * @param src
	 * @param dst
	 * @return
	 * @throws IntrospectionException
	 */
	protected Pair<Method, Method>[] generatePropertyPairs(
			Class<?> src, Class<?> dst) throws IntrospectionException {
		BeanInfo srcInfo = Introspector.getBeanInfo(src);
		BeanInfo dstInfo = Introspector.getBeanInfo(dst);
		PropertyDescriptor[] srcDescs = srcInfo.getPropertyDescriptors();
		PropertyDescriptor[] dstDescs = dstInfo.getPropertyDescriptors();
		List<Pair<Method, Method>> result = new ArrayList<>();
		for (PropertyDescriptor dstDesc : dstDescs) {
			Method dstWrite = dstDesc.getWriteMethod();
			if (dstWrite != null) {
				for (PropertyDescriptor srcDesc : srcDescs) {
					Method srcRead = srcDesc.getReadMethod();
					if (srcRead != null) {
						String srcName = srcDesc.getDisplayName();
						String dstName = dstDesc.getDisplayName();
						Class<?> srcType = Primitives.getWrappedType(srcDesc
								.getPropertyType());
						Class<?> dstType = Primitives.getWrappedType(dstDesc
								.getPropertyType());
						if (srcName.equals(dstName)
								&& dstType.isAssignableFrom(srcType)) {
							result.add(new Pair<>(srcRead, dstWrite));
							// Write a property at most once.
							break;
						}
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		Pair<Method, Method>[] resultArr = (Pair<Method, Method>[]) result
				.toArray(new Pair<?, ?>[result.size()]);
		return resultArr;
	}
}
