package bilab;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import scigol.accessor;

/// convenience singleton to access to system-wide default notifier
public class Notify
{
  public Notify()
  {
  }

  @accessor
  public static INotifier get_Notifier() 
  { 
    return notifier;
  }
  
  @accessor
  public static void set_Notifier(INotifier value)
  {
    notifier=value; 
  }

  // convenience pass-through to notifier
  public static void startProgress(Object from, String task) 
  {
    notifier.StartProgress(from, task);
  }

  public static void progress(Object from, double percent)
  {
    notifier.Progress(from, percent);
  }

  public static void endProgress(Object from)
  {
    notifier.EndProgress(from);
  }

  public static void userStatus(Object from, String message)
  {
    notifier.UserStatus(from, message);
  }

  public static void userInfo(Object from, String message)
  {
    notifier.UserInfo(from, message);
  }

  public static void userWarning(Object from, String message)
  {
    notifier.UserWarning(from, message);
  }

  public static void userError(Object from, String message)
  {
    notifier.UserError(from, message);
  }

  public static void devStatus(Object from, String message)
  {
    notifier.DevStatus(from, message);
  }

  public static void devInfo(Object from, String message)
  {
    notifier.DevInfo(from, message);
  }

  public static void devWarning(Object from, String message)
  {
    notifier.DevWarning(from, message);
  }

  public static void devError(Object from, String message)
  {
    notifier.DevError(from, message);
  }

  // aliases for DevInfo
  public static void debug(Object from, String message)
  {
    devInfo(from, message);
  }

  public static void debug(String message)
  {
    devInfo(Notify.class, message);
  }
  
  // alias for DevError & throw exception
  public static void unimplemented(Object from)
  {
    devError(from, "unimplemented");
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();

    throw new BilabException("method of "+className+" invoked unimplemented");
  }

  public static void logInfo(Object from, String message)
  {
    notifier.LogInfo(from, message);
  }

  public static void logError(Object from, String message)
  {
    notifier.LogError(from, message);
  }

  private static INotifier notifier = null;
  
  static {
    if (notifier==null)
      notifier = new WriterNotifier(new BufferedWriter(new OutputStreamWriter(System.out)));
  } 
}