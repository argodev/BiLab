package scigol;

import java.lang.annotation.*;
import java.util.*;


public class ScigolAnnotation implements Annotation
{
  public ScigolAnnotation(TypeSpec annotationInterface)
  {
    Debug.Assert(annotationInterface.isInterface());
    Debug.Assert( ((java.lang.Class)annotationInterface.getSysType()).isAnnotation() ); 
    
    this.annotationInterface = annotationInterface;
    
    members = new ArrayList();
  }
  
  
  public void setMembers(ArrayList members)
  {
    this.members = members;
    
    // check members against number & type of annotation 
    
  }
  
  
  public ArrayList getMembers()
  {
    return members;
  }
  
  
  
  public java.lang.Class<? extends Annotation> annotationType() 
  {
    return (java.lang.Class<Annotation>)annotationInterface.getSysType();
  }
  
  
  public boolean equals(Object obj)
  {
    return obj.getClass().isAssignableFrom( (java.lang.Class)annotationInterface.getSysType() );
  }
    
  public int hashCode()
  {
    //!!! might want to match the Java algorithm here...
    int code = toString().hashCode();
    for(Object o : members)
      code += o.hashCode();
    return code;
  }
   
  public String toString()
  {
    return ((java.lang.Class)annotationInterface.getSysType()).toString();
  }
  
  
  private TypeSpec annotationInterface;
  private ArrayList members;
}
