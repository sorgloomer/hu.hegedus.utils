package hu.hegedus.utils;

public class Beans {

	private static BeansBean beansBean = new BeansBean();

	public static <T> T clone(T src) {
		return beansBean.clone(src);
	}

	public static <T> T convert(Object src, Class<T> dstClass) {
		return beansBean.convert(src, dstClass);
	}

	public static void copyProperties(Object src, Object dst) {
		beansBean.copyProperties(src, dst);
	}

	public static void flushCaches() {
		beansBean = new BeansBean();
	}
}
