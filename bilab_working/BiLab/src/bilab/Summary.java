package bilab;

import java.lang.annotation.*;

// documentation summary string
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE, ElementType.PARAMETER})
public @interface Summary {
  String value();
}
