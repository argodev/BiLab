package scigol;

import java.lang.annotation.*;

// annotation to indicate that annotations should be obtained
//  from an alternative entity, specified in string form
//  (e.g. a fully qualified function or class name)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE, ElementType.PARAMETER})
public @interface redirect {
  String value();
}
