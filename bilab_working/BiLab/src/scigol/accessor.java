package scigol;

import java.lang.annotation.*;

// annotation to mark methods as property accessors
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD})
public @interface accessor {}
