
package scigol;



  
public class Debug
{
  
  public static void WriteLine(String message)
  {
    System.out.println(message);
  }

  // shorthand
  public static void WL(String message)
  {
    System.out.println(message);
  }

  public static void Write(String message)
  {
    System.out.println(message);
  }

  public static void Assert(boolean c)
  {
    if (!c)
      throw new InternalScigolError("assertion failure");
  }

  public static void Assert(boolean c, String message)
  {
    if (!c)
      throw new InternalScigolError("assertion failure:"+message);
  }
  
  public static void Unimplemented()
  {
    throw new InternalScigolError("Unimplemented");
  }
  
  public static void Unimplemented(String s)
  {
    throw new InternalScigolError("Unimplemented:"+s);
  }

  public static void Depricated()
  {
    throw new InternalScigolError("Depricated");
  }

  public static void Depricated(String s)
  {
    throw new InternalScigolError("Depricated:"+s);
  }
  
  public static void Warning(String message)
  {
    System.out.println("Warning: "+message);
  }
  
  
  public static String stackTraceString()
  {
    try {
      throw new Exception("call stack:");
    } catch (Exception e) {
      return e.toString();//!!! check this actually includes the trace into
    }
  }
  
  
  public static void WriteStack()
  {
    System.out.println(stackTraceString());
  }
  
  
}

