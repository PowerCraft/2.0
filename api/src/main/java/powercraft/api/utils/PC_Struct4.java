package powercraft.api.utils;

import java.io.Serializable;

/**
 * Structure of 4 objects.
 * 
 * @author MightyPork
 * @copy (c) 2012
 * @param <T1> 1st object class
 * @param <T2> 2nd object class
 * @param <T3> 3rd object class
 * @param <T4> 4th object class
 */
public class PC_Struct4<T1, T2, T3, T4> implements Serializable {
	/**
	 * 1st object
	 */
	public T1 a;

	/**
	 * 2nd object
	 */
	public T2 b;

	/**
	 * 3rd object
	 */
	public T3 c;

	/**
	 * 4th object
	 */
	public T4 d;

	/**
	 * Make structure of 4 objects
	 * 
	 * @param objA 1st object
	 * @param objB 2nd object
	 * @param objC 3rd object
	 * @param objD 4th object
	 */
	public PC_Struct4(T1 objA, T2 objB, T3 objC, T4 objD) {
		a = objA;
		b = objB;
		c = objC;
		d = objD;
	}

	/**
	 * @return 1st object
	 */
	public T1 getA() {
		return a;
	}

	/**
	 * @return 2nd object
	 */
	public T2 getB() {
		return b;
	}

	/**
	 * @return 3rd object
	 */
	public T3 getC() {
		return c;
	}

	/**
	 * @return 4th object
	 */
	public T4 getD() {
		return d;
	}

	/**
	 * @return 1st object
	 */
	public T1 get1() {
		return a;
	}

	/**
	 * @return 2nd object
	 */
	public T2 get2() {
		return b;
	}

	/**
	 * @return 3rd object
	 */
	public T3 get3() {
		return c;
	}

	/**
	 * @return 4th object
	 */
	public T4 get4() {
		return d;
	}

	/**
	 * Set 1st object
	 * 
	 * @param obj 1st object
	 */
	public void setA(T1 obj) {
		a = obj;
	}

	/**
	 * Set 2nd object
	 * 
	 * @param obj 2nd object
	 */
	public void setB(T2 obj) {
		b = obj;
	}

	/**
	 * Set 3rd object
	 * 
	 * @param obj 3rd object
	 */
	public void setC(T3 obj) {
		c = obj;
	}

	/**
	 * Set 4th object
	 * 
	 * @param obj 4th object
	 */
	public void setD(T4 obj) {
		d = obj;
	}

	/**
	 * Set 1st object
	 * 
	 * @param obj 1st object
	 */
	public void set1(T1 obj) {
		a = obj;
	}

	/**
	 * Set 2nd object
	 * 
	 * @param obj 2nd object
	 */
	public void set2(T2 obj) {
		b = obj;
	}

	/**
	 * Set 3rd object
	 * 
	 * @param obj 3rd object
	 */
	public void set3(T3 obj) {
		c = obj;
	}

	/**
	 * Set 4th object
	 * 
	 * @param obj 4th object
	 */
	public void set4(T4 obj) {
		d = obj;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		PC_Struct4<?, ?, ?, ?> t = (PC_Struct4<?, ?, ?, ?>) obj;

		return (a == null ? t.a == null : a.equals(t.a)) && (b == null ? t.b == null : b.equals(t.b))
				&& (c == null ? t.c == null : c.equals(t.c)) && (d == null ? t.d == null : d.equals(t.d));

	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (a == null ? 0 : a.hashCode());
		hash += (b == null ? 0 : b.hashCode());
		hash += (c == null ? 0 : c.hashCode());
		hash += (d == null ? 0 : d.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		return "STRUCT {" + a + "," + b + "," + c + "," + d + "}";
	}

}
