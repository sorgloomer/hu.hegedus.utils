package hu.hegedus.utils.tests;

import hu.hegedus.utils.Beans;

public class BeansTest {
	public static class Point2D {
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		private int x, y;
	}

	public static class Point3D {
		private int x, y, z;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getZ() {
			return z;
		}

		public void setZ(int z) {
			this.z = z;
		}
	}

	public static class Wrapper {
		private Object x;

		public Object getX() {
			return x;
		}

		public void setX(Object x) {
			this.x = x;
		}
	}

	public static void main(String[] args) {
		Point2D o = new Point2D();
		o.setX(10);
		o.setY(20);

		Point2D p2 = Beans.clone(o);
		System.out.println("p2.x=" + p2.getX());
		System.out.println("p2.y=" + p2.getY());
		Point3D p3 = Beans.convert(o, Point3D.class);
		System.out.println("p3.x=" + p3.getX());
		System.out.println("p3.y=" + p3.getY());
		System.out.println("p3.z=" + p3.getZ());
		Wrapper w = Beans.convert(p3, Wrapper.class);
		System.out.println("w.x=" + w.getX());
	}
}
