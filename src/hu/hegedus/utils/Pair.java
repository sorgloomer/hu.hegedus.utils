package hu.hegedus.utils;

import java.io.Serializable;
import java.util.Objects;

public class Pair<T, U> implements Serializable {
	private static final long serialVersionUID = 389764408763757396L;
	private transient int hashCode = 0;
	private T first;
	private U second;

	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public U getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = this.hashCode;
		if (hashCode != 0)
			return hashCode;
		this.hashCode = hashCode = Objects.hashCode(this.getFirst()) * prime
				+ Objects.hashCode(this.getSecond());
		return hashCode;
	}

	@Override
	public boolean equals(Object b) {
		if (b == this)
			return true;
		if (b == null)
			return false;
		if (!(b instanceof Pair))
			return false;
		Pair<?, ?> o = (Pair<?, ?>) b;
		return Objects.equals(this.getFirst(), o.getFirst())
				&& Objects.equals(this.getSecond(), o.getSecond());
	}

}
