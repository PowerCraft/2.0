package powercraft.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PC_ClientServerSync {

	String name() default "";

	boolean save() default true;

	boolean clientChangeAble() default true;

}
