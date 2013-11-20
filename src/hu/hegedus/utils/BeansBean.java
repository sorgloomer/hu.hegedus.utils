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

public class BeansBean {

	private Map<Pair<Class<?>, Class<?>>, Pair<Method, Method>[]> cache = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T clone(T src) {
		return this.convert(src, (Class<T>) src.getClass());
	}

	public <T> T convert(Object src, Class<T> classDst) {
		if (src == null)
			return null;
		Class<?> classSrc = src.getClass();
		try {
			T res = classDst.newInstance();
			this.copyProperties(src.getClass(), classDst, src, res);
			return res;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not convert object of class '"
					+ classSrc.getSimpleName() + "' to class '"
					+ classDst.getSimpleName() + "'");
		}
	}

	public void copyProperties(Object src, Object dst) {
		this.copyProperties(src.getClass(), dst.getClass(), src, dst);
	}

	protected void copyProperties(Class<?> classSrc, Class<?> classDst,
			Object src, Object dst) {
		try {
			Map<Pair<Class<?>, Class<?>>, Pair<Method, Method>[]> cache = this.cache;

			Pair<Class<?>, Class<?>> classes =
					new Pair<Class<?>, Class<?>>(classSrc, classDst);
			Pair<Method, Method>[] methodPairs = cache
					.get(classes);
			if (methodPairs == null) {
				synchronized (cache) {
					methodPairs = cache.get(classes);
					if (methodPairs == null) {
						methodPairs = this.generatePropertyPairs(
								classes.getFirst(),
								classes.getSecond());
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

	protected Pair<Method, Method>[] generatePropertyPairs(
			Class<?> src, Class<?> dst) throws IntrospectionException {
		BeanInfo srcInfo = Introspector.getBeanInfo(src);
		BeanInfo dstInfo = Introspector.getBeanInfo(dst);
		PropertyDescriptor[] srcDescs = srcInfo.getPropertyDescriptors();
		PropertyDescriptor[] dstDescs = dstInfo.getPropertyDescriptors();
		List<Pair<Method, Method>> result = new ArrayList<>();
		for (PropertyDescriptor srcDesc : srcDescs) {
			Method srcRead = srcDesc.getReadMethod();
			if (srcRead != null) {
				String srcName = srcDesc.getDisplayName();
				Class<?> srcType = Primitives.getWrappedType(srcDesc
						.getPropertyType());
				for (PropertyDescriptor dstDesc : dstDescs) {
					Method dstWrite = dstDesc.getWriteMethod();
					Class<?> dstType = Primitives.getWrappedType(dstDesc
							.getPropertyType());
					if (dstWrite != null
							&& srcName.equals(dstDesc.getDisplayName())
							&& dstType.isAssignableFrom(srcType)) {
						result.add(new Pair<>(srcRead, dstWrite));
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
