package bilab;

public class BilabException extends RuntimeException {
  
  public BilabException() {}
  
  public BilabException(String message) { super(message); }
	
  public BilabException(String message, Throwable cause) { super(message,cause); }
  
  public BilabException(Throwable cause) { super(cause); }
}
