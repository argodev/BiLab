package scigol;
  
import antlr.CommonToken;
import antlr.Token;


public class CommonTokenWithLocation extends CommonToken 
{
  public CommonTokenWithLocation()
  {
    loc = new Location();
  }
  
  public CommonTokenWithLocation(Token tok) 
  {
    super(tok.getType(), tok.getText());
    setLine(tok.getLine());
    setColumn(tok.getColumn());
    if (tok.getFilename() != null)
      setFilename(tok.getFilename());
    else
      setFilename("?");
    
    if (!(tok instanceof CommonTokenWithLocation))
      loc = new Location();
    else
      loc = ((CommonTokenWithLocation)tok).loc;
  }
  
  public CommonTokenWithLocation(int t, String txt) 
  {
    super(t,txt);
    loc = new Location();
  }
  
  public CommonTokenWithLocation(String s) 
  {
    super(s);
    loc = new Location();
  }
  
  
  public void  setLine(int l)
  {
    if (loc == null) loc = new Location();
    loc.line = l;
    super.setLine(l);
  }

  public void  setColumn(int c)
  {
    if (loc == null) loc = new Location();
    loc.column = c;
    super.setColumn(c);
  }

  
  
  public void setFilename(String name)
  {
    Debug.Assert(name != null);
    if (loc == null) loc = new Location();
    loc.filename = name;
  }
  
  public String getFilename()
  {
    if ((loc != null) && (loc.filename != null))
      return loc.filename;
    else
      return "?";
  }
  
  
  
  public Location loc;
}

