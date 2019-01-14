package com.autoabm.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface IdABM {
	
	String nombre() default "";
	boolean auto() default true;
	
}
