package com.ctvit.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ctvit.framework.core.code.Style;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameStyle {

    Style value() default Style.normal;

}
