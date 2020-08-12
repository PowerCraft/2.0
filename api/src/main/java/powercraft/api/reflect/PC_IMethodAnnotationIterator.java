package powercraft.api.reflect;

import java.lang.annotation.Annotation;

public interface PC_IMethodAnnotationIterator<T extends Annotation> {

	public boolean onMethodWithAnnotation(PC_MethodWithAnnotation<T> methodWithAnnotation);

}
