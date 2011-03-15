package scigol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// annotation to indicate that annotations should be obtained
// from an alternative entity, specified in string form
// (e.g. a fully qualified function or class name)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR,
        ElementType.TYPE, ElementType.PACKAGE, ElementType.PARAMETER })
public @interface redirect {
    String value();
}
