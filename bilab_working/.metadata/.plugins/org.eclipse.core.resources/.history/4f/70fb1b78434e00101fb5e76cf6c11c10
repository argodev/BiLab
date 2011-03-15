package scigol;

public class Location {
  public Location()
  {
    line=column=-1;
    filename = "?";
  }
  
  public Location(int line, int col, String filename)
  {
    if ((filename == null) || (filename == ""))
      Debug.WriteLine("got filename:"+((filename==null)?"null":filename));
    Debug.Assert((filename != null) && (filename != ""));
    this.line=line; column=col; this.filename=filename;
  }
  
  public boolean isKnown()
  {
    return (line != -1); 
  }
  
  
  public String toString()
  {
    if (!isKnown() || ((line == 0) && (column == 0)))
      return filename;
    else
      return filename+":"+line+":"+column;
  }
  
  public int line;
  public int column;
  public String filename;
  
}
