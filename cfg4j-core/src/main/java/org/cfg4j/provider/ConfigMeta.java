package org.cfg4j.provider;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface ConfigMeta {

  String message() default "configKey and bindedFileName values cannot be null";

  String configKey() default "";

  String bindedFileName() default "";

}
