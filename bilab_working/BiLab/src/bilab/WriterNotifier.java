package bilab;

import java.io.IOException;
import java.io.Writer;


/// a INotifier that outputs to a Writer
public class WriterNotifier implements INotifier
{
  public WriterNotifier(Writer w)
  {  
    this.w = w; 
  }
  
  
  public void StartProgress(Object from, String task) 
  {
    try {
      String s = task+" [";
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  public void Progress(Object from, double percent)
  {
    try {
      String s = ".."+percent+"%%";
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  public void EndProgress(Object from)
  {
    try {
      String s = "..] done."+EOL;
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
    }
  }
  
  
  public void PushLevel(Object from)
  {
  }
  
  public void PopLevel(Object from)
  {
  }
  
  
  public void UserStatus(Object from, String message)
  {
    String s = "status: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserInfo(Object from, String message)
  {
    String s = "info: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserWarning(Object from, String message)
  {
    String s = "warning: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void UserError(Object from, String message)
  {
    String s = "error: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  
  public void DevStatus(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev status: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevInfo(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev info: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevWarning(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev warning: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void DevError(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "dev error: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  
  public void LogInfo(Object from, String message)
  {
    String s = "log info: "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public void LogError(Object from, String message)
  {
    String className = (from instanceof java.lang.Class)?((java.lang.Class)from).toString():from.getClass().toString();
    String s = "log error: "+className+" - "+message+EOL;
    try {
      w.write(s,0,s.length());
      w.flush();
    }
    catch (IOException e) {
      System.err.println(s);
    }
  }
  
  public static final String EOL = "\n";
  
  protected Writer w;
}
