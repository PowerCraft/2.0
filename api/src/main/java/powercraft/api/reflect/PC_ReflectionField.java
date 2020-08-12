package powercraft.api.reflect;

import java.lang.reflect.Field;

public class PC_ReflectionField<C, T> {

	public final Class<C> clasz;
	public final int index;
	public final Class<T> type;

	private Field field;

	public PC_ReflectionField(Class<C> clasz, int index, Class<T> type) {
		this.clasz = clasz;
		this.index = index;
		this.type = type;
	}

	public Field getField() {
		if (this.field == null) {
			this.field = PC_Reflection.findNearestBestField(this.clasz, this.index, this.type);
		}
		return this.field;
	}

	public T getValue(Object object) {
		return PC_Reflection.getValue(this, object);
	}

	public void setValue(Object object, Object value) {
		PC_Reflection.setValue(this, object, value);
	}

	public void setValueAndFinals(Object object, Object value) {
		PC_Reflection.setValueAndFinals(this, object, value);
	}

}