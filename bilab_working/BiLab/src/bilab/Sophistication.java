package bilab;

import java.lang.annotation.*;

// annotation to indicate the target user
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface Sophistication {
 
  public static final int Normal = 1;	    // always show
  public static final int Advanced = 1;	  // show when advanced user flag set (e.g. for bioinformatics programmers)
  public static final int Developer = 1;	// show when developer flag set (i.e. meant for bilab developer use only)
  
  int value();
}
