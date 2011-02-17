package bilab;

import java.lang.annotation.*;

// documentation URL
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE, ElementType.PARAMETER})
public @interface Doc {
  String value();
}
