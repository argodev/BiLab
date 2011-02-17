package scigol;

public class InternalScigolError extends ScigolException {
  public InternalScigolError() {}
  public InternalScigolError(String s) { super(s); }
  public InternalScigolError(String s, Exception inner) { super(s,inner); }

}
