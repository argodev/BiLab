package bilab;

import java.util.List;


// all classes capable of reading/writing  their state from/to a resource 
public interface IResourceIOProvider
{
  /* this is a marker interface for types that implement the following static methods */
  /*  (they are located via reflection) 
  
  @Summary("return a list of supported resource type names")
  public static List<String> getSupportedResourceTypes();
  
  @Summary("create an object by reading in a resource (in a supported format).  resourceType may be 'unknown' ")
  public static Object importResource(String resourceName, String resourceType);

  @Summary("create resource (in a supported format) from an object.")
  public static void exportResource(IResourceIOProvider object, String resourceName, String resourceType);

  */
}
