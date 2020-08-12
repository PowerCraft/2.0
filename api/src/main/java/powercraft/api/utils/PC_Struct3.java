package powercraft.api.utils;

import java.io.Serializable;

public class PC_Struct3<T1, T2, T3> implements Serializable {
	public T1 a;

	public T2 b;

	public T3 c;

	public PC_Struct3(T1 objA, T2 objB, T3 objC) {
		a = objA;
		b = objB;
		c = objC;
	}

	public T1 getA() {
		return a;
	}

	public T2 getB() {
		return b;
	}

	public T3 getC() {
		return c;
	}

	public T1 get1() {
		return a;
	}

	public T2 get2() {
		return b;
	}

	public T3 get3() {
		return c;
	}

	public void setA(T1 obj) {
		a = obj;
	}

	public void setB(T2 obj) {
		b = obj;
	}

	public void setC(T3 obj) {
		c = obj;
	}

	public void set1(T1 obj) {
		a = obj;
	}

	public void set2(T2 obj) {
		b = obj;
	}

	public void set3(T3 obj) {
		c = obj;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		PC_Struct3<?, ?, ?> t = (PC_Struct3<?, ?, ?>) obj;
		return (a == null ? t.a == null : a.equals(t.a)) && (b == null ? t.b == null : b.equals(t.b))
				&& (c == null ? t.c == null : c.equals(t.c));
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (a == null ? 0 : a.hashCode());
		hash += (b == null ? 0 : b.hashCode());
		hash += (c == null ? 0 : c.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		return "STRUCT {" + a + "," + b + "," + c + "}";
	}
}