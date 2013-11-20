package hu.hegedus.utils;

import java.lang.reflect.Array;
import java.util.List;

public class Lists {

	/**
	 * Converts a list to an array of element type clazz. Instantiates an array
	 * of element type clazz and size list.size(), and returns with the filled
	 * array.
	 * 
	 * @param list
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(List<? extends T> list, Class<T> clazz) {
		T[] array = (T[]) Array.newInstance(clazz, list.size());
		return list.toArray(array);
	}
}
