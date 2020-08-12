package powercraft.api.reflect;

import java.lang.annotation.Annotation;

public interface PC_IFieldAnnotationIterator<T extends Annotation> {

	public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<T> fieldWithAnnotation);

}
